package noppes.npcs.client.gui.model;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.controllers.Preset;
import noppes.npcs.client.controllers.PresetController;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.List;

public class GuiCreationLoad extends GuiCreationScreenInterface implements ICustomScrollListener {

    private List<String> list = new ArrayList<String>();
    private GuiCustomScroll scroll;

    public GuiCreationLoad(EntityNPCInterface npc) {
        super(npc);
        active = 5;
        xOffset = 60;
        PresetController.instance.load();
    }

    @Override
    public void initGui() {
        super.initGui();
        if (scroll == null) {
            scroll = new GuiCustomScroll(this, 0);
        }
        list.clear();
        for (Preset preset : PresetController.instance.presets.values())
            list.add(preset.name);
        scroll.setList(list);
        scroll.guiLeft = guiLeft;
        scroll.guiTop = guiTop + 45;
        scroll.setSize(100, ySize - 96);

        addScroll(scroll);

        addButton(new GuiNpcButton(10, guiLeft, guiTop + ySize - 46, 120, 20, "gui.remove"));
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        if (btn.id == 10 && scroll.hasSelected()) {
            PresetController.instance.removePreset(scroll.getSelected());
            initGui();
        }
    }

    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
        Preset preset = PresetController.instance.getPreset(scroll.getSelected());
        playerdata.readFromNBT(preset.data.writeToNBT());
        initGui();
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
}
