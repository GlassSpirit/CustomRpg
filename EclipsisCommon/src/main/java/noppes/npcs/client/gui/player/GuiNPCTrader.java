package noppes.npcs.client.gui.player;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.containers.ContainerNPCTrader;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleTrader;

public class GuiNPCTrader extends GuiContainerNPCInterface {
    private final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/trader.png");
    private final ResourceLocation slot = new ResourceLocation("customnpcs", "textures/gui/slot.png");
    private RoleTrader role;
    private ContainerNPCTrader container;

    public GuiNPCTrader(EntityNPCInterface npc, ContainerNPCTrader container) {
        super(npc, container);
        this.container = container;
        role = (RoleTrader) npc.roleInterface;
        closeOnEsc = true;
        ySize = 224;
        xSize = 223;
        this.title = "role.trader";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        this.drawWorldBackground(0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(resource);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        GlStateManager.enableRescaleNormal();

        mc.renderEngine.bindTexture(slot);
        for (int slot = 0; slot < 18; slot++) {
            int x = guiLeft + slot % 3 * 72 + 10;
            int y = guiTop + slot / 3 * 21 + 6;

            ItemStack item = role.inventoryCurrency.items.get(slot);
            ItemStack item2 = role.inventoryCurrency.items.get(slot + 18);
            if (NoppesUtilServer.IsItemStackNull(item)) {
                item = item2;
                item2 = ItemStack.EMPTY;
            }
            if (NoppesUtilPlayer.compareItems(item, item2, false, false)) {
                item = item.copy();
                item.setCount(item.getCount() + item2.getCount());
                item2 = ItemStack.EMPTY;
            }

            ItemStack sold = role.inventorySold.items.get(slot);
            GlStateManager.color(1, 1, 1, 1);
            mc.renderEngine.bindTexture(this.slot);
            drawTexturedModalRect(x + 42, y, 0, 0, 18, 18);
            if (!NoppesUtilServer.IsItemStackNull(item) && !NoppesUtilServer.IsItemStackNull(sold)) {

                RenderHelper.enableGUIStandardItemLighting();
                if (!NoppesUtilServer.IsItemStackNull(item2)) {
                    itemRender.renderItemAndEffectIntoGUI(item2, x, y + 1);
                    itemRender.renderItemOverlays(fontRenderer, item2, x, y + 1);
                }
                itemRender.renderItemAndEffectIntoGUI(item, x + 18, y + 1);
                itemRender.renderItemOverlays(fontRenderer, item, x + 18, y + 1);
                RenderHelper.disableStandardItemLighting();

                fontRenderer.drawString("=", x + 36, y + 5, CustomNpcResourceListener.DefaultTextColor);

            }
        }
        GlStateManager.disableRescaleNormal();
        super.drawGuiContainerBackgroundLayer(f, i, j);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        for (int slot = 0; slot < 18; slot++) {
            int x = slot % 3 * 72 + 10;
            int y = slot / 3 * 21 + 6;

            ItemStack item = role.inventoryCurrency.items.get(slot);
            ItemStack item2 = role.inventoryCurrency.items.get(slot + 18);
            if (NoppesUtilServer.IsItemStackNull(item)) {
                item = item2;
                item2 = ItemStack.EMPTY;
            }
            if (NoppesUtilPlayer.compareItems(item, item2, role.ignoreDamage, role.ignoreNBT)) {
                item = item.copy();
                item.setCount(item.getCount() + item2.getCount());
                item2 = ItemStack.EMPTY;
            }
            ItemStack sold = role.inventorySold.items.get(slot);
            if (NoppesUtilServer.IsItemStackNull(sold))
                continue;

            if (this.isPointInRegion(x + 43, y + 1, 16, 16, par1, par2)) {
                if (!container.canBuy(item, item2, player)) {
                    GlStateManager.translate(0, 0, 300);
                    if (!item.isEmpty() && !NoppesUtilPlayer.compareItems(player, item, role.ignoreDamage, role.ignoreNBT))
                        this.drawGradientRect(x + 17, y, x + 35, y + 18, 0x70771010, 0x70771010);
                    if (!item2.isEmpty() && !NoppesUtilPlayer.compareItems(player, item2, role.ignoreDamage, role.ignoreNBT))
                        this.drawGradientRect(x - 1, y, x + 17, y + 18, 0x70771010, 0x70771010);

                    String title = I18n.translateToLocal("trader.insufficient");
                    this.fontRenderer.drawString(title, (xSize - fontRenderer.getStringWidth(title)) / 2, 131, 0xDD0000);
                    GlStateManager.translate(0, 0, -300);
                } else {
                    String title = I18n.translateToLocal("trader.sufficient");
                    this.fontRenderer.drawString(title, (xSize - fontRenderer.getStringWidth(title)) / 2, 131, 0x00DD00);
                }
            }

            if (this.isPointInRegion(x, y, 16, 16, par1, par2) && !NoppesUtilServer.IsItemStackNull(item2)) {
                this.renderToolTip(item2, par1 - guiLeft, par2 - guiTop);
            }
            if (this.isPointInRegion(x + 18, y, 16, 16, par1, par2)) {
                this.renderToolTip(item, par1 - guiLeft, par2 - guiTop);
            }
        }
    }

    @Override
    public void save() {
    }
}
