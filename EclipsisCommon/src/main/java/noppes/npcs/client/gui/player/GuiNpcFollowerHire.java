package noppes.npcs.client.gui.player;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.constants.EnumPlayerPacket;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleFollower;

public class GuiNpcFollowerHire extends GuiContainerNPCInterface {
    private final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/followerhire.png");
    private EntityNPCInterface npc;
    private ContainerNPCFollowerHire container;
    private RoleFollower role;

    public GuiNpcFollowerHire(EntityNPCInterface npc, ContainerNPCFollowerHire container) {
        super(npc, container);
        this.container = container;
        this.npc = npc;
        role = (RoleFollower) npc.roleInterface;
        closeOnEsc = true;
    }

    @Override
    public void initGui() {
        super.initGui();
        addButton(new GuiNpcButton(5, guiLeft + 26, guiTop + 60, 50, 20, I18n.translateToLocal("follower.hire")));
    }

    @Override
    public void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);
        if (guibutton.id == 5) {
            NoppesUtilPlayer.sendData(EnumPlayerPacket.FollowerHire);
            close();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        //fontRenderer.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(resource);
        int l = (width - xSize) / 2;
        int i1 = (height - ySize) / 2;
        drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
        int index = 0;
        for (int slot = 0; slot < role.inventory.items.size(); slot++) {
            ItemStack itemstack = role.inventory.items.get(slot);
            if (NoppesUtilServer.IsItemStackNull(itemstack))
                continue;
            int days = 1;
            if (role.rates.containsKey(slot))
                days = role.rates.get(slot);


            int yOffset = index * 26;

            int x = guiLeft + 78;
            int y = guiTop + yOffset + 10;
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();
            itemRender.renderItemAndEffectIntoGUI(itemstack, x + 11, y);
            itemRender.renderItemOverlays(fontRenderer, itemstack, x + 11, y);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();

            String daysS = days + " " + ((days == 1) ? I18n.translateToLocal("follower.day") : I18n.translateToLocal("follower.days"));
            fontRenderer.drawString(" = " + daysS, x + 27, y + 4, CustomNpcResourceListener.DefaultTextColor);

            if (this.isPointInRegion(x - guiLeft + 11, y - guiTop, 16, 16, mouseX, mouseY)) {
                this.renderToolTip(itemstack, mouseX, mouseY);
            }
            //fontRenderer.drawString(quantity, x + 0 + (12-fontRenderer.getStringWidth(quantity))/2, y + 4, 0x404040);

            index++;
        }

    }

    @Override
    public void save() {
        return;
    }
}
