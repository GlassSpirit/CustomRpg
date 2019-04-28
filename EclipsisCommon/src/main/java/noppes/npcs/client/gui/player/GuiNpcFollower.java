package noppes.npcs.client.gui.player;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.IGuiData;
import noppes.npcs.constants.EnumPlayerPacket;
import noppes.npcs.containers.ContainerNPCFollower;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleFollower;

public class GuiNpcFollower extends GuiContainerNPCInterface implements IGuiData {
    private final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/follower.png");
    private EntityNPCInterface npc;
    private RoleFollower role;

    public GuiNpcFollower(EntityNPCInterface npc, ContainerNPCFollower container) {
        super(npc, container);
        this.npc = npc;
        role = (RoleFollower) npc.roleInterface;
        closeOnEsc = true;

        NoppesUtilPlayer.sendData(EnumPlayerPacket.RoleGet);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        addButton(new GuiNpcButton(4, guiLeft + 100, guiTop + 110, 50, 20, new String[]{I18n.format("follower.waiting"), I18n.format(
                "follower.following")}, role.isFollowing ? 1 : 0));
        if (!role.infiniteDays)
            addButton(new GuiNpcButton(5, guiLeft + 8, guiTop + 30, 50, 20, I18n.format("follower.hire")));
    }

    @Override
    public void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);
        int id = guibutton.id;
        if (id == 4) {
            NoppesUtilPlayer.sendData(EnumPlayerPacket.FollowerState);
        }
        if (id == 5) {
            NoppesUtilPlayer.sendData(EnumPlayerPacket.FollowerExtend);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRenderer.drawString(I18n.format("follower.health") + ": " + npc.getHealth() + "/" + npc.getMaxHealth(), 62, 70, CustomNpcResourceListener.DefaultTextColor);
        if (!role.infiniteDays) {
            if (role.getDays() <= 1)
                fontRenderer.drawString(I18n.format("follower.daysleft") + ": " + I18n.format("follower.lastday"), 62, 94, CustomNpcResourceListener.DefaultTextColor);
            else
                fontRenderer.drawString(I18n.format("follower.daysleft") + ": " + (role.getDays() - 1), 62, 94, CustomNpcResourceListener.DefaultTextColor);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(resource);
        int l = guiLeft;
        int i1 = guiTop;
        drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
        int index = 0;
        if (!role.infiniteDays) {
            for (int slot = 0; slot < role.inventory.items.size(); slot++) {
                ItemStack itemstack = role.inventory.items.get(slot);
                if (NoppesUtilServer.IsItemStackNull(itemstack))
                    continue;
                int days = 1;
                if (role.rates.containsKey(slot))
                    days = role.rates.get(slot);

                int yOffset = index * 20;

                int x = guiLeft + 68;
                int y = guiTop + yOffset + 4;
                GlStateManager.enableRescaleNormal();
                RenderHelper.enableGUIStandardItemLighting();
                itemRender.renderItemAndEffectIntoGUI(itemstack, x + 11, y);
                itemRender.renderItemOverlays(fontRenderer, itemstack, x + 11, y);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();

                String daysS = days + " " + ((days == 1) ? I18n.format("follower.day") : I18n.format("follower.days"));
                fontRenderer.drawString(" = " + daysS, x + 27, y + 4, CustomNpcResourceListener.DefaultTextColor);
                //fontRenderer.drawString(quantity, x + 0 + (12-fontRenderer.getStringWidth(quantity))/2, y + 4, 0x404040);

                if (this.isPointInRegion(x - guiLeft + 11, y - guiTop, 16, 16, mouseX, mouseY)) {
                    this.renderToolTip(itemstack, mouseX, mouseY);
                }
                index++;
            }
        }
        this.drawNpc(33, 131);
    }

    @Override
    public void save() {

    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        npc.roleInterface.readFromNBT(compound);
        initGui();
    }
}
