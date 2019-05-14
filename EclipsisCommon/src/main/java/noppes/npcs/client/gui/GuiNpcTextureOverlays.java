package noppes.npcs.client.gui;

import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.common.entity.EntityNPCInterface;

public class GuiNpcTextureOverlays extends GuiNpcSelectionInterface {
    public GuiNpcTextureOverlays(EntityNPCInterface npc, GuiScreen parent) {
        super(npc, parent, npc.display.getOverlayTexture().isEmpty() ? "customnpcs:textures/overlays/" : npc.display.getOverlayTexture());
        title = "Select Overlay";
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        int index = npc.display.getOverlayTexture().lastIndexOf("/");
        if (index > 0) {
            String asset = npc.display.getOverlayTexture().substring(index + 1);
            if (npc.display.getOverlayTexture().equals(assets.getAsset(asset)))
                slot.selected = asset;
        }
    }


    @Override
    public void drawScreen(int i, int j, float f) {
        int l = -50;
        int i1 = (height / 2) + 30;
        drawNpc(npc, l, i1, 2, 0);

        super.drawScreen(i, j, f);
    }

    @Override
    public void elementClicked() {
        if (dataTextures.contains(slot.selected) && slot.selected != null) {
            npc.display.setOverlayTexture(assets.getAsset(slot.selected));
        }
    }

    public void save() {
    }

    @Override
    public String[] getExtension() {
        return new String[]{"png"};
    }


}
