package com.joniski.kibtech.entity.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.jetbrains.annotations.Debug;

import com.joniski.kibtech.KibTech;
import com.joniski.kibtech.block.ModBlocks;
import com.joniski.kibtech.block.custom.RobotStation;
import com.joniski.kibtech.block.custom.RobotStationEntity;
import com.joniski.kibtech.component.ModDataComponents;
import com.joniski.kibtech.component.PowerRecord;
import com.joniski.kibtech.enums.RobotWorkType;
import com.joniski.kibtech.item.ModItems;
import com.joniski.kibtech.item.custom.BatteryItem;
import com.joniski.kibtech.item.custom.RobotWandItem;
import com.joniski.kibtech.item.custom.BatteryItem;
import com.joniski.kibtech.menus.custom.RobotMenu;
import com.joniski.kibtech.util.TreeUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner.BlockType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.entity.EntityType.Builder;

public class RobotEntity extends Animal{

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    protected Tiers maxToolTier = Tiers.WOOD;
    protected float moveSpeed = 0.75f;
    // use UUID instead of storing entity, easier for packet and saving nbt data 
    private UUID followEntityUUID;
    public BlockPos searchStart = null;
    public BlockPos searchEnd;
    private BlockPos station;
    private List<BlockPos> targetTree;
    public Item dropItem = ModItems.COPPER_ROBOT_ITEM.asItem();

    public boolean goStation = false;
    public boolean charging = false; // < will make these not needed with state machine or atleast enum of some kinds.
    private boolean moving = false;

    public int maxArea = 5;
    private RobotWorkType workType = RobotWorkType.NONE;

   // Slot 1: Battery; Slot 2: Tool
    public final ItemStackHandler inventory = new ItemStackHandler(6){
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            if (slot <= 1){
                return 1;
            }else{
                return stack.getMaxStackSize();
            }
        };


        @Override
        protected void onContentsChanged(int slot) {
            if(!level().isClientSide()){
                if (slot == 1){
                    setJob(getStackInSlot(slot));
                }
            }
        };

        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0 && stack.getItem() instanceof BatteryItem){
                return true;
            }

            if (slot == 1){
                if (stack.getItem() instanceof TieredItem tieredItem){
                    if(tieredItem.getTier().getAttackDamageBonus() > maxToolTier.getAttackDamageBonus()){
                        return false;
                    }

                }
                
                if (stack.getItem() instanceof AxeItem){
                    return true;
                }
                if (stack.getItem() instanceof HoeItem){
                    return true;
                }
            }


            if (slot > 1){
                return true;
            }

            return false;
        };

        protected void onLoad() {
            setJob(getStackInSlot(1));
        };

        public void setJob(ItemStack stack){
            if (stack == null){
                workType = RobotWorkType.NONE;
                return;
            }


            if (stack.getItem() instanceof AxeItem axe){
                workType = RobotWorkType.LUMBERJACK;
                return;
            }
            if (stack.getItem() instanceof PickaxeItem pickaxe){
                workType = RobotWorkType.MINER;
                return;
            }
            if (stack.getItem() instanceof HoeItem hoe){
                workType = RobotWorkType.FARMER;
                return;
            }

            workType = RobotWorkType.NONE;
        }
    };

    public RobotEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("inventory", inventory.serializeNBT(this.level().registryAccess()));

        if (followEntityUUID != null){
            tag.putString("follower", followEntityUUID.toString());
        }

        if (searchStart != null){
            List<Integer> start = new ArrayList<Integer>();
            List<Integer> end = new ArrayList<Integer>();
            start.add(searchStart.getX());
            end.add(searchEnd.getX());
            start.add(searchStart.getY());
            end.add(searchEnd.getY());
            start.add(searchStart.getZ());
            end.add(searchEnd.getZ());

            tag.putIntArray("searchStart", start);
            tag.putIntArray("searchEnd", end);
        }

        if (getStation() != null){
            List<Integer> stationPos = new ArrayList<Integer>();
            stationPos.add(station.getX());
            stationPos.add(station.getY());
            stationPos.add(station.getZ());
            tag.putIntArray("station", stationPos);
        }
    }

    public BlockPos getStation(){
        if (station == null){
            station = null;
            return null;
        }

        if (!(level().getBlockState(station).getBlock() instanceof RobotStation)){
            station = null;
        }

        return station;
    }

    public void setStation(BlockPos newStation){
        if ((level().getBlockState(newStation).getBlock() instanceof RobotStation)){
            goStation = false;
            stopMoving();
            station = newStation;
        }  
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        inventory.deserializeNBT(this.level().registryAccess(), tag.getCompound("inventory"));

        if (tag.get("follower") != null){
            String uuid = tag.getString("follower");
            if (uuid != null){
                followEntityUUID = (UUID.fromString(uuid));
            }
        }

        if (tag.get("searchStart") != null){
            int[] start = tag.getIntArray("searchStart");
            if (start.length >= 3){
                searchStart = new BlockPos(start[0], start[1], start[2]);
            }

        }

        if (tag.get("searchEnd") != null){
            int[] end = tag.getIntArray("searchEnd");
            if (end.length >= 3){
                searchEnd = new BlockPos(end[0], end[1], end[2]);
            }
        }

        if (tag.get("station") != null){
            int[] pos = tag.getIntArray("station");
            if (pos.length >= 3){
                setStation(new BlockPos(pos[0], pos[1], pos[2]));
            }
        }
    }

    @Override
    protected void registerGoals() {
      //  this.goalSelector.addGoal(0, new TemptGoal(this, 1, stack -> stack.is(ModItems.WEAK_BATTERY), false));

    }

    public static AttributeSupplier.Builder createAttributes(){
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 4d)
                .add(Attributes.MOVEMENT_SPEED, 0.3d)
                .add(Attributes.FOLLOW_RANGE, 10d)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 2);
    }


    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!player.level().isClientSide()){
            if (player instanceof ServerPlayer serverPlayer) {
                if (player.getItemInHand(hand).getItem() instanceof RobotWandItem  wand){
                    wand.setRobotTarget(this);
                    return InteractionResult.SUCCESS;
                }

                serverPlayer.openMenu(new SimpleMenuProvider((windId, inv, p) -> new RobotMenu(windId, inv, getId()) , Component.literal("Robot Settings")), buf -> buf.writeInt(getId()));
            }

        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        dropContents();
    }

    @Override
    public boolean isFood(ItemStack arg0) {
        return false;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel arg0, AgeableMob arg1) {
        return null;
    }

    private void setupAnimationStates(){
        if (this.idleAnimationTimeout <= 0){
            this.idleAnimationTimeout = 60;
            this.idleAnimationState.start(this.tickCount);
        }else{
            --this.idleAnimationTimeout;
        }

    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()){
            setupAnimationStates();
        }else{
            work();
        }   
    }

    public void setFollowEntity(Entity e){
        stopMoving();
        if (e == null){
            followEntityUUID = null;
            return;
        }

        followEntityUUID = e.getUUID();
    }

    public void stopMoving(){
        moving = false;
        goStation = false;
        getNavigation().stop();
    }

    public Entity getFollowEntity(){
        if (followEntityUUID == null){
            return null;
        }

        return level().getPlayerByUUID(followEntityUUID);
    }

    // TODO: CHANGE TO STATE MACHINE
    public void work(){
        ItemStack battStack = inventory.getStackInSlot(0);
        if (!(battStack.getItem() instanceof BatteryItem battery)){
            stopMoving();
            charging = false;
            return;
        }

        PowerRecord power = battStack.get(ModDataComponents.POWER_COMPONENT);
        if (power == null){
            stopMoving();
            charging = false;
            return;
        }

        if (power.power() > 1){
            battStack.set(ModDataComponents.POWER_COMPONENT, new PowerRecord(power.power()-1));
        }else{
            charging = false;
            stopMoving();
        }

        Entity followEntity = getFollowEntity();

        if (followEntity != null){
            follow();
            return;
        }

        if (getStation() != null && power.power() < (battery.getMaxPower() / 10) && !charging && (level().getBlockEntity(getStation()) instanceof RobotStationEntity e)){
            if (e.getEnergyStorage().getEnergyStored() >= 100){
                stopMoving();
                charging = true;
                goToStation();
            }
        }

        if (charging && distanceToSqr(getStation().getCenter()) < 2){
            if (!(level().getBlockEntity(getStation()) instanceof RobotStationEntity e)){
                return;
            }

            if (e.getEnergyStorage().getEnergyStored() < 100){
                charging = false;
                return;
            }

            int amountThere = e.getEnergyStorage().extractEnergy(35, true);

            if (amountThere == 0){
                charging = false;
                return;
            }

            int amountTook = battery.charge(battStack, amountThere);
            e.getEnergyStorage().extractEnergy(amountTook, false);

            if (battery.isFull(battStack)){
                charging = false;
            }
            return;
        }

        if (charging && distanceToSqr(getStation().getCenter()) >= 2 && !moving){
            goToStation();
            return;
        }

        int amountFull = 0;
        for (int i = 0; i < inventory.getSlots(); ++i){
            if (inventory.getStackInSlot(i).getCount() > 0){
                amountFull += 1;
            }
        }

        if (getStation() != null && !goStation){
            int stationAmount = 0;
            if (level().getBlockEntity(getStation()) instanceof RobotStationEntity stationEntity){
                for (int i = 0; i < stationEntity.inventory.getSlots(); ++ i){
                    if (stationEntity.inventory.getStackInSlot(i).getCount() > 0){
                        stationAmount += 1;
                    }
                }

                if (amountFull == inventory.getSlots() && stationAmount < stationEntity.inventory.getSlots()){
                    stopMoving();
                    goToStation();
                    return;
                }

                if (amountFull == inventory.getSlots() && stationAmount == stationEntity.inventory.getSlots()){
                    stopMoving();
                    return;
                }
            }
        }
        
        if (getStation() == null && amountFull == inventory.getSlots()){
            stopMoving();
            return;
        }

        if (goStation){
            goToStation();
            return;
        }

        if (workType == RobotWorkType.FARMER){
            farm();
            return;
        }

        if (workType == RobotWorkType.LUMBERJACK){
            lumberjack();
            return;
        } 


    }

    public void follow(){
        if (distanceTo(getFollowEntity()) < 2){
            return;
        }

        if (getNavigation().isDone() || getNavigation().isStuck()){
            moving = false;
        }

        if (moving == false){
            getNavigation().moveTo(getFollowEntity(), moveSpeed);
            moving = true;
        }
    }

    public void goToStation(){
        if (!moving){
            if (getStation() == null){
                return;
            }

            stopMoving();
            moving = true;
            goStation = true;

            getNavigation().moveTo(getStation().getX(), getStation().getY(), getStation().getZ(), moveSpeed);
            return;
        }

        if (getNavigation().isStuck()){
            stopMoving();
            return;
        }

        if (getNavigation().isDone()){
            stopMoving();

            RobotStationEntity robotStationEntity = (RobotStationEntity)level().getBlockEntity(getStation());
            for (int i = 2; i < inventory.getSlots(); ++i){
                for (int v = 0; v < robotStationEntity.inventory.getSlots(); ++ v){
                    if (inventory.getStackInSlot(i).getCount() == 0){
                        continue;
                    }
                    inventory.setStackInSlot(i, robotStationEntity.inventory.insertItem(v, inventory.getStackInSlot(i), false));
                }
            }

            return;
        }

        if (getNavigation().getPath() == null){
            stopMoving();
            return;
        }
    }


    public void farm(){
        if (searchStart == null || searchEnd == null){
            return;
        }

        if(!moving){
            List<BlockPos> validBlocks = getValidBlocks(level(), searchStart, searchEnd, workType);
            BlockPos closest = getClosestBlock(validBlocks, blockPosition());

            if (closest == null){
                stopMoving();
                return;
            }

            getNavigation().moveTo(closest.getX(), closest.getY(), closest.getZ(), moveSpeed);
            moving = true;
            return;
        }

        if (getNavigation().isStuck()){
            moving = false;
            return;
        }

        if (getNavigation().getTargetPos() == null){
            stopMoving();
            return;
        }

        if (getNavigation().isDone()){
            BlockPos targetPos = getNavigation().getTargetPos();
            BlockState oldBlockState = level().getBlockState(targetPos);

            if (!(oldBlockState.getBlock() instanceof CropBlock block)){
                return;
            }

            LootParams.Builder lootparams = new LootParams.Builder((ServerLevel)level()).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(targetPos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY);
            List<ItemStack> drops = oldBlockState.getDrops(lootparams);

            for(int y = 0; y < drops.size(); ++y){
                if (block.getCloneItemStack(level(), targetPos, oldBlockState).getItem() == drops.get(y).getItem()){
                    drops.get(y).setCount(drops.get(y).getCount()-1);
                }

                for(int i = 1; i < inventory.getSlots(); ++ i){
                    drops.set(y, inventory.insertItem(i, drops.get(y), false));
                }
            }

            SimpleContainer dropContainer = new SimpleContainer(drops.size());
            for (int i = 0; i < dropContainer.getContainerSize(); ++ i){
                dropContainer.setItem(i, drops.get(i));
            }

            Containers.dropContents(level(), getOnPos(), dropContainer);

            level().destroyBlock(targetPos, false);

            oldBlockState = block.getStateForAge(0);
            level().setBlock(targetPos, oldBlockState, 0);
            moving = false;
            return;
        }

        if (getNavigation().getPath() == null){
            moving = false;
            return;
        }

    }

    public void lumberjack(){
        if (searchStart == null || searchEnd == null){
            return;
        }

        if (!moving){
            List<BlockPos> tree = findClosestTreeInArea(getCommandSenderWorld(), searchStart, searchEnd, blockPosition());
            if (tree.size() > 0){
                BlockPos closestBlock = getClosestBlock(tree, blockPosition());
                closestBlock = getNearestClearBlock(level(), closestBlock, blockPosition());

                targetTree = tree;
                getNavigation().moveTo(closestBlock.getX(), closestBlock.getY(), closestBlock.getZ(), 1, moveSpeed);
                moving = true;
            }

            return;
        }

        if (getNavigation().isStuck()){
            moving = false;
            return;
        }

        if (getNavigation().getTargetPos() == null){
            stopMoving();
            return;
        }

        if (getNavigation().isDone()){
            if (targetTree == null){
                moving = false;
                return;
            }
            
            for(BlockPos log: targetTree){
                BlockState oldBlockState = level().getBlockState(log);

                LootParams.Builder lootparams = new LootParams.Builder((ServerLevel)level()).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(log)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY);
                List<ItemStack> drops = oldBlockState.getDrops(lootparams);

                for(int y = 0; y < drops.size(); ++y){
                    for(int i = 1; i < inventory.getSlots(); ++ i){
                        drops.set(y, inventory.insertItem(i, drops.get(y), false));
                    }
                }
                
                SimpleContainer dropContainer = new SimpleContainer(drops.size());
                for (int i = 0; i < dropContainer.getContainerSize(); ++ i){
                    dropContainer.setItem(i, drops.get(i));
                }

                Containers.dropContents(level(), getOnPos(), dropContainer);


                level().destroyBlock(log, false);

                targetTree = null;
            }

            moving = false;
            return;
        }

        if (getNavigation().getPath() == null){
            moving = false;
            return;
        }
    }

    public static List<BlockPos> findClosestTreeInArea(Level level, BlockPos start, BlockPos end, BlockPos current){
        List<BlockPos> logs = new ArrayList<BlockPos>();

        List<List<BlockPos>> trees = new ArrayList<List<BlockPos>>();

        for(int y = start.getY(); y < end.getY(); ++ y){
            for(int x = start.getX(); x < end.getX(); ++ x){
                for(int z = start.getZ(); z < end.getZ(); ++ z){
                    BlockPos newPos = new BlockPos(x, y, z);

                    List<BlockPos> tree = TreeUtil.getTree(level, newPos);
                    if (tree.size() >= 3){
                        boolean found = false;
                        for(List<BlockPos> treeX : trees){
                            if (treeX.equals(tree)){
                                found = true;
                                break;
                            }
                        }

                        if (found == false){
                            trees.add(tree);
                        }
                    }
                }
            }
        }

        double closestTreeDist = 9999;
        for(List<BlockPos> tree : trees){
            if (tree.get(0).distToCenterSqr(current.getCenter()) < closestTreeDist){
                closestTreeDist = tree.get(0).distToCenterSqr(current.getCenter());
                logs = tree;
            }
        }

        return logs;
    }
    
    public static List<BlockPos> getValidBlocks(Level level, BlockPos start, BlockPos end, RobotWorkType workType){
        List<BlockPos> blocks = new ArrayList<BlockPos>();

        for(int y = start.getY(); y < end.getY(); ++ y){
            for(int x = start.getX(); x < end.getX(); ++ x){
                for(int z = start.getZ(); z < end.getZ(); ++ z){
                    BlockPos newPos = new BlockPos(x, y, z);
                    if (isValidWorkBlock(level, newPos, workType)){
                        blocks.add(newPos);
                    }
                }
            }
        }

        return blocks;
    }

    public static BlockPos getClosestBlock(List<BlockPos> blocks, BlockPos from){
        BlockPos closest = null;
        if (blocks.size() == 0){
            return closest;
        }

        closest = blocks.get(0);
        double closestDist = blocks.get(0).distToCenterSqr(from.getCenter());

        for (BlockPos pos : blocks){
            double dist = pos.distToCenterSqr(from.getCenter());
            if (dist < closestDist){
                closestDist = dist;
                closest = pos;
            }
        }

        return closest;
    }

    public static Boolean isValidWorkBlock(Level level, BlockPos blockPos, RobotWorkType workType){
        if (blockPos == null){
            return false;
        }

        if (workType == RobotWorkType.NONE){
            return false;
        }

        if (workType == RobotWorkType.LUMBERJACK){
            for (TagKey tag : level.getBlockState(blockPos).getTags().toList()){
                if (tag.toString().contains("minecraft:logs")){
                    return true;
                }
            }
        }

        if (workType == RobotWorkType.FARMER){
            if (level.getBlockState(blockPos).getBlock() instanceof CropBlock crop){
                if (crop.isMaxAge(level.getBlockState(blockPos))){
                    return true;
                }
            }
        }

        return false;
    }

    public void dropContents() {
        SimpleContainer drops = new SimpleContainer(inventory.getSlots()+1);
        for (int i = 0; i < drops.getContainerSize()-1; ++ i){
            drops.setItem(i, inventory.getStackInSlot(i));
        }

        ItemStack robotItem = new ItemStack(dropItem,1);
        drops.setItem(drops.getContainerSize()-1, robotItem);

        Containers.dropContents(level(), getOnPos(), drops);
    }

    public static BlockPos getNearestClearBlock(Level level, BlockPos blockPos, BlockPos current){
        BlockPos closestPos = null;
        double distance = 999999;

        if (!level.getBlockState(blockPos.above()).isSolidRender(level, blockPos.above())){
            closestPos = blockPos.above();
            distance = blockPos.above().distToCenterSqr(current.getCenter());
        }

        if(!level.getBlockState(blockPos.south()).isSolidRender(level, blockPos.south())){
            if (blockPos.south().distToCenterSqr(current.getCenter()) < distance){
                closestPos = blockPos.south();
                distance = blockPos.south().distToCenterSqr(current.getCenter());
            }
        }

        if(!level.getBlockState(blockPos.east()).isSolidRender(level, blockPos.east())){
            if (blockPos.east().distToCenterSqr(current.getCenter()) < distance){
                closestPos = blockPos.east();
                distance = blockPos.east().distToCenterSqr(current.getCenter());
            }
        }

        if(!level.getBlockState(blockPos.west()).isSolidRender(level, blockPos.west())){
            if (blockPos.west().distToCenterSqr(current.getCenter()) < distance){
                closestPos = blockPos.west();
                distance = blockPos.west().distToCenterSqr(current.getCenter());
            }
        }

        if(!level.getBlockState(blockPos.north()).isSolidRender(level, blockPos.north())){
            if (blockPos.north().distToCenterSqr(current.getCenter()) < distance){
                closestPos = blockPos.north();
                distance = blockPos.north().distToCenterSqr(current.getCenter());
            }
        }

        if(!level.getBlockState(blockPos.below()).isSolidRender(level, blockPos.below())){
            if (blockPos.below().distToCenterSqr(current.getCenter()) < distance){
                closestPos = blockPos.below();
                distance = blockPos.below().distToCenterSqr(current.getCenter());
            }
        }

        return closestPos;
    }
}
    