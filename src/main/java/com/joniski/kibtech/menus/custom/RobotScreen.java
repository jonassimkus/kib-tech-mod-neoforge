package com.joniski.kibtech.menus.custom;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.units.qual.min;

import com.joniski.kibtech.KibTech;
import com.joniski.kibtech.block.custom.SolarPanelEntity;
import com.joniski.kibtech.packets.RobotFollowerPacket;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.event.ContainerScreenEvent.Render;
import net.neoforged.neoforge.network.PacketDistributor;

public class RobotScreen extends AbstractContainerScreen<RobotMenu>{

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(KibTech.MODID, "textures/gui/robot/robot_gui.png");

    Player player;

    public RobotScreen(RobotMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        player = playerInventory.player;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
          guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){

        double x = (width - imageWidth) / 2;
        double y  = (height - imageHeight) / 2;

        double offsetX = mouseX - x;
        double offsetY = mouseY - y;

        if (offsetX >= 150 && offsetX <= 172){
            if (offsetY >= 62 && offsetY <= 77){
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

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        super.slotClicked(slot, slotId, mouseButton, type);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x1, int y1) {
        super.renderTooltip(guiGraphics, x1, y1);

        double x = (width - imageWidth) / 2;
        double y  = (height - imageHeight) / 2;

        double offsetX = x1 - x;
        double offsetY = y1 - y;

        if (offsetX >= 150 && offsetX <= 172){
            if (offsetY >= 62 && offsetY <= 77){
                guiGraphics.renderTooltip(font, (Component)Component.literal("Follow You"), (int)x1, (int)y1);
            }
        }
    }
    
}
