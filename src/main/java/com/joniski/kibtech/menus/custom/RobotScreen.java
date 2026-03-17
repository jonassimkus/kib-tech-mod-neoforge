package com.joniski.kibtech.menus.custom;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.units.qual.min;

import com.joniski.kibtech.KibTech;
import com.joniski.kibtech.block.custom.SolarPanelEntity;
import com.joniski.kibtech.entity.custom.DiamondRobotEntity;
import com.joniski.kibtech.entity.custom.IronRobotEntity;
import com.joniski.kibtech.entity.custom.NetheriteRobotEntity;
import com.joniski.kibtech.item.custom.BatteryItem;
import com.joniski.kibtech.packets.RobotFollowerPacket;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ContainerScreenEvent.Render;
import net.neoforged.neoforge.network.PacketDistributor;

public class RobotScreen extends AbstractContainerScreen<RobotMenu>{

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(KibTech.MODID, "textures/gui/robot/robot_gui.png");

    private static final ResourceLocation COPPER_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(KibTech.MODID, "textures/gui/robot/copper_gui.png");

    private static final ResourceLocation IRON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(KibTech.MODID, "textures/gui/robot/iron_gui.png");

    private static final ResourceLocation DIAMOND_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(KibTech.MODID, "textures/gui/robot/diamond_gui.png");


    private static final ResourceLocation NETHERITE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(KibTech.MODID, "textures/gui/robot/netherite_gui.png");


    Player player;
    String currentMenu ;

    public RobotScreen(RobotMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        player = playerInventory.player;
        currentMenu = "settings";
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (currentMenu == "settings"){
            guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        }else if(currentMenu == "container"){
            if (menu.entity instanceof NetheriteRobotEntity){
                RenderSystem.setShaderTexture(0, NETHERITE_TEXTURE);
                guiGraphics.blit(NETHERITE_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
            }
            else if (menu.entity instanceof DiamondRobotEntity){
                RenderSystem.setShaderTexture(0, DIAMOND_TEXTURE);
                guiGraphics.blit(DIAMOND_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
            }
            else if (menu.entity instanceof IronRobotEntity){
                RenderSystem.setShaderTexture(0, IRON_TEXTURE);
                guiGraphics.blit(IRON_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
            }else{
                RenderSystem.setShaderTexture(0, COPPER_TEXTURE);
                guiGraphics.blit(COPPER_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

            }

        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY + 2, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY + 2, 4210752, false);

        if (currentMenu == "settings"){
            ItemStack batteryStack = menu.entity.inventory.getStackInSlot(0);
            if (batteryStack != null && batteryStack.getItem() instanceof BatteryItem batteryItem){
                int color;
                float percentage = ((float)batteryItem.getPower(batteryStack) / (float)batteryItem.getMaxPower()) * 100;
                if (percentage > 50){
                    color = 0xA1FF52;
                }else if (percentage > 10){
                    color = 0xFFA530;
                }else{
                    color = 0xAD0000;
                }

                guiGraphics.drawString(font, batteryItem.getPower(batteryStack) + "/" + batteryItem.getMaxPower() + " KE", 8, 40, color);
            }else{
                guiGraphics.drawString(font, "0%", 8, 40, 0xAD0000);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        double x = (width - imageWidth) / 2;
        double y  = (height - imageHeight) / 2;

        double offsetX = mouseX - x;
        double offsetY = mouseY - y;

        if (offsetY >= 6 && offsetY <= 16){
            if (offsetX >= 158 && offsetX <= 168){
                currentMenu = "settings";
            }

            if (offsetX >= 145 &&offsetX <= 155){
                currentMenu = "container";
            }

             if (offsetX >= 131 &&offsetX <= 142){
                // Packet that makes sure server knows entity is following you now, or if its not.
                PacketDistributor.sendToServer(new RobotFollowerPacket(menu.entity.getId(), player.getStringUUID()));
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public boolean isHovering(Slot slot, int x, int y, int width, int height, double mouseX, double mouseY) {
        if (!isSlotShown(slot.index)){
            return false;
        }

        int i = this.leftPos;
        int j = this.topPos;
        mouseX -= (double)i;
        mouseY -= (double)j;
        return mouseX >= (double)(x - 1) && mouseX < (double)(x + width + 1) && mouseY >= (double)(y - 1) && mouseY < (double)(y + height + 1);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (slot != null) {
            slotId = slot.index;
        }

        if (isSlotShown(slotId)){
            this.minecraft.gameMode.handleInventoryMouseClick(this.menu.containerId, slotId, mouseButton, type, this.minecraft.player);
        }else{

        }
    }


    @Override
    protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, String countString) {
        if (isSlotShown(slot.index) == false){
            return;
        }

        super.renderSlotContents(guiGraphics, itemstack, slot, countString);
    }

    @Override
    protected void renderSlotHighlight(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY, float partialTick) {
        if (isSlotShown(slot.index) == false){
            return;
        }

        super.renderSlotHighlight(guiGraphics, slot, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        if (isSlotShown(slot.index) == false){
            return;
        }

        super.renderSlot(guiGraphics, slot);
    }

    public boolean isSlotShown(int slotIndex){
        if (currentMenu == "settings"){
            if (slotIndex >= 38){
                return false;
            }
        }
        if (currentMenu == "container"){
            if (slotIndex < 38 && slotIndex >= 36){
                return false;
            }
        }

        return true;
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x1, int y1) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if(isSlotShown(this.hoveredSlot.index)){
                ItemStack itemstack = this.hoveredSlot.getItem();
                guiGraphics.renderTooltip(this.font, this.getTooltipFromContainerItem(itemstack), itemstack.getTooltipImage(), itemstack, x1, y1);
            }
        }

        double x = (width - imageWidth) / 2;
        double y  = (height - imageHeight) / 2;

        double offsetX = x1 - x;
        double offsetY = y1 - y;

        if (offsetY >= 6 && offsetY <= 16){
            if (offsetX >= 158 && offsetX <= 168){
                guiGraphics.renderTooltip(font, (Component)Component.literal("Settings"), (int)x1, (int)y1);
            }

            if (offsetX >= 145 &&offsetX <= 155){
                guiGraphics.renderTooltip(font, (Component)Component.literal("Inventory"), (int)x1, (int)y1);
            }

             if (offsetX >= 131 &&offsetX <= 142){
                guiGraphics.renderTooltip(font, (Component)Component.literal("Follow Player"), (int)x1, (int)y1);
            }
        }
    }
    
}
