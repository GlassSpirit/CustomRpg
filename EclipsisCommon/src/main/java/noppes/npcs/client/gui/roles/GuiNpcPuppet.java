package noppes.npcs.client.gui.roles;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.common.entity.EntityCustomNpc;
import noppes.npcs.roles.JobPuppet;
import noppes.npcs.roles.JobPuppet.PartConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class GuiNpcPuppet extends GuiNPCInterface implements ISliderListener, ICustomScrollListener {

    private GuiScreen parent;
    private JobPuppet job;
    private String selectedName;
    private boolean isStart = true;

    public HashMap<String, PartConfig> data = new HashMap<String, PartConfig>();

    private GuiCustomScroll scroll;

    public GuiNpcPuppet(GuiScreen parent, EntityCustomNpc npc) {
        super(npc);
        this.parent = parent;
        ySize = 230;
        xSize = 400;
        job = (JobPuppet) npc.jobInterface;
        closeOnEsc = true;
    }

    @Override
    public void initGui() {
        super.initGui();

        int y = guiTop;
        addButton(new GuiNpcButton(30, guiLeft + 110, y += 14, 60, 20, new String[]{"gui.yes", "gui.no"}, job.whileStanding ? 0 : 1));
        addLabel(new GuiNpcLabel(30, "puppet.standing", guiLeft + 10, y + 5, 0xFFFFFF));
        addButton(new GuiNpcButton(31, guiLeft + 110, y += 22, 60, 20, new String[]{"gui.yes", "gui.no"}, job.whileMoving ? 0 : 1));
        addLabel(new GuiNpcLabel(31, "puppet.walking", guiLeft + 10, y + 5, 0xFFFFFF));
        addButton(new GuiNpcButton(32, guiLeft + 110, y += 22, 60, 20, new String[]{"gui.yes", "gui.no"}, job.whileAttacking ? 0 : 1));
        addLabel(new GuiNpcLabel(32, "puppet.attacking", guiLeft + 10, y + 5, 0xFFFFFF));
        addButton(new GuiNpcButton(33, guiLeft + 110, y += 22, 60, 20, new String[]{"gui.yes", "gui.no"}, job.animate ? 0 : 1));
        addLabel(new GuiNpcLabel(33, "puppet.animation", guiLeft + 10, y + 5, 0xFFFFFF));

        if (job.animate) {
            addButton(new GuiButtonBiDirectional(34, guiLeft + 240, y, 60, 20, new String[]{"1", "2", "3", "4", "5", "6", "7", "8"}, job.animationSpeed));
            addLabel(new GuiNpcLabel(34, "stats.speed", guiLeft + 190, y + 5, 0xFFFFFF));
        }

        y += 34;

        HashMap<String, PartConfig> data = new HashMap<String, PartConfig>();
        if (isStart) {
            data.put("model.head", job.head);
            data.put("model.body", job.body);
            data.put("model.larm", job.larm);
            data.put("model.rarm", job.rarm);
            data.put("model.lleg", job.lleg);
            data.put("model.rleg", job.rleg);
        } else {
            data.put("model.head", job.head2);
            data.put("model.body", job.body2);
            data.put("model.larm", job.larm2);
            data.put("model.rarm", job.rarm2);
            data.put("model.lleg", job.lleg2);
            data.put("model.rleg", job.rleg2);
        }

        this.data = data;

        if (scroll == null) {
            scroll = new GuiCustomScroll(this, 0);
        }
        scroll.setList(new ArrayList<String>(data.keySet()));
        scroll.guiLeft = guiLeft + 10;
        scroll.guiTop = y;
        scroll.setSize(80, 100);
        addScroll(scroll);

        if (selectedName != null) {
            scroll.setSelected(selectedName);
            drawSlider(y, data.get(selectedName));
        }

        addButton(new GuiNpcButton(66, guiLeft + xSize - 22, guiTop, 20, 20, "X"));

        if (job.animate) {
            addButton(new GuiNpcButton(67, guiLeft + 10, y + 110, 70, 20, "gui.start"));
            addButton(new GuiNpcButton(68, guiLeft + 90, y + 110, 70, 20, "gui.end"));

            getButton(67).enabled = !isStart;
            getButton(68).enabled = isStart;
        }
    }

    private void drawSlider(int y, PartConfig config) {
        addButton(new GuiNpcButton(29, guiLeft + 140, y, 80, 20, new String[]{"gui.enabled", "gui.disabled"}, config.disabled ? 1 : 0));
        y += 22;
        addLabel(new GuiNpcLabel(10, "X", guiLeft + 100, y + 5, 0xFFFFFF));
        addSlider(new GuiNpcSlider(this, 10, guiLeft + 120, y, (config.rotationX + 1) / 2));
        y += 22;
        addLabel(new GuiNpcLabel(11, "Y", guiLeft + 100, y + 5, 0xFFFFFF));
        addSlider(new GuiNpcSlider(this, 11, guiLeft + 120, y, (config.rotationY + 1) / 2));
        y += 22;
        addLabel(new GuiNpcLabel(12, "Z", guiLeft + 100, y + 5, 0xFFFFFF));
        addSlider(new GuiNpcSlider(this, 12, guiLeft + 120, y, (config.rotationZ + 1) / 2));
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        drawNpc(320, 200);
        super.drawScreen(i, j, f);
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        if (!(btn instanceof GuiNpcButton))
            return;

        GuiNpcButton button = (GuiNpcButton) btn;
        if (btn.id == 29) {
            data.get(selectedName).disabled = button.getValue() == 1;
        }
        if (btn.id == 30) {
            job.whileStanding = button.getValue() == 0;
        }
        if (btn.id == 31) {
            job.whileMoving = button.getValue() == 0;
        }
        if (btn.id == 32) {
            job.whileAttacking = button.getValue() == 0;
        }
        if (btn.id == 33) {
            job.animate = button.getValue() == 0;
            isStart = true;
            initGui();
        }
        if (btn.id == 34) {
            job.animationSpeed = button.getValue();
        }
        if (btn.id == 66) {
            close();
        }
        if (btn.id == 67) {
            isStart = true;
            initGui();
        }
        if (btn.id == 68) {
            isStart = false;
            initGui();
        }
    }

    @Override
    public void close() {
        this.mc.displayGuiScreen(parent);
        Client.sendData(EnumPacketServer.JobSave, job.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void mouseDragged(GuiNpcSlider slider) {
        int percent = (int) ((slider.sliderValue) * 360);
        slider.setString(percent + "%");
        PartConfig part = data.get(selectedName);
        if (slider.id == 10)
            part.rotationX = (slider.sliderValue - 0.5f) * 2;
        if (slider.id == 11)
            part.rotationY = (slider.sliderValue - 0.5f) * 2;
        if (slider.id == 12)
            part.rotationZ = (slider.sliderValue - 0.5f) * 2;
        npc.updateHitbox();
    }

    @Override
    public void mousePressed(GuiNpcSlider slider) {

    }

    @Override
    public void mouseReleased(GuiNpcSlider slider) {
    }

    @Override
    public void save() {

    }

    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        selectedName = guiCustomScroll.getSelected();
        initGui();
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
}
