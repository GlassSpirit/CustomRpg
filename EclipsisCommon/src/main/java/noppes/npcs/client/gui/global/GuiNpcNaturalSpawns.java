package noppes.npcs.client.gui.global;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.GuiNpcMobSpawnerSelector;
import noppes.npcs.client.gui.SubGuiNpcBiomes;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.controllers.data.SpawnData;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.util.HashMap;
import java.util.Vector;

public class GuiNpcNaturalSpawns extends GuiNPCInterface2 implements IGuiData, IScrollData, ITextfieldListener, ICustomScrollListener, ISliderListener {

    private GuiCustomScroll scroll;
    private HashMap<String, Integer> data = new HashMap<String, Integer>();

    private SpawnData spawn = new SpawnData();

    public GuiNpcNaturalSpawns(EntityNPCInterface npc) {
        super(npc);
        Client.sendData(EnumPacketServer.NaturalSpawnGetAll);
    }

    @Override
    public void initGui() {
        super.initGui();
        if (scroll == null) {
            scroll = new GuiCustomScroll(this, 0);
            scroll.setSize(143, 208);
        }
        scroll.guiLeft = guiLeft + 214;
        scroll.guiTop = guiTop + 4;
        this.addScroll(scroll);

        this.addButton(new GuiNpcButton(1, guiLeft + 358, guiTop + 38, 58, 20, "gui.add"));
        this.addButton(new GuiNpcButton(2, guiLeft + 358, guiTop + 61, 58, 20, "gui.remove"));

        if (spawn.id >= 0)
            showSpawn();

    }

    private void showSpawn() {
        addLabel(new GuiNpcLabel(1, "gui.title", guiLeft + 4, guiTop + 8));
        addTextField(new GuiNpcTextField(1, this, this.fontRenderer, guiLeft + 60, guiTop + 3, 140, 20, spawn.name));

        addLabel(new GuiNpcLabel(3, "spawning.biomes", guiLeft + 4, guiTop + 30));
        addButton(new GuiNpcButton(3, guiLeft + 120, guiTop + 25, 50, 20, "selectServer.edit"));

        addSlider(new GuiNpcSlider(this, 4, guiLeft + 4, guiTop + 47, 180, 20, (float) spawn.itemWeight / 100));

        int y = guiTop + 70;
        this.addButton(new GuiNpcButton(25, guiLeft + 14, y, 20, 20, "X"));
        addLabel(new GuiNpcLabel(5, "1:", guiLeft + 4, y + 5));
        this.addButton(new GuiNpcButton(5, guiLeft + 36, y, 170, 20, getTitle(spawn.compound1)));

        addLabel(new GuiNpcLabel(26, "gui.type", guiLeft + 4, guiTop + 100));
        addButton(new GuiNpcButton(27, guiLeft + 70, guiTop + 93, 120, 20, new String[]{"spawner.any", "spawner.dark", "spawner.light"}, spawn.type));
    }

    private String getTitle(NBTTagCompound compound) {
        if (compound != null && compound.hasKey("ClonedName"))
            return compound.getString("ClonedName");

        return "gui.selectnpc";
    }

    @Override
    public void buttonEvent(GuiButton guibutton) {
        int id = guibutton.id;
        if (id == 1) {
            save();
            String name = I18n.translateToLocal("gui.new");
            while (data.containsKey(name))
                name += "_";

            SpawnData spawn = new SpawnData();
            spawn.name = name;
            Client.sendData(EnumPacketServer.NaturalSpawnSave, spawn.writeNBT(new NBTTagCompound()));

        }
        if (id == 2) {
            if (data.containsKey(scroll.getSelected())) {
                Client.sendData(EnumPacketServer.NaturalSpawnRemove, spawn.id);
                spawn = new SpawnData();
                scroll.clear();
            }
        }
        if (id == 3) {
            setSubGui(new SubGuiNpcBiomes(spawn));
        }
        if (id == 5) {
            setSubGui(new GuiNpcMobSpawnerSelector());
        }
        if (id == 25) {
            spawn.compound1 = new NBTTagCompound();
            initGui();
        }
        if (id == 27) {
            spawn.type = ((GuiNpcButton) guibutton).getValue();
        }
    }


    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        String name = guiNpcTextField.getText();
        if (name.isEmpty() || data.containsKey(name)) {
            guiNpcTextField.setText(spawn.name);
        } else {
            String old = spawn.name;
            data.remove(old);
            spawn.name = name;
            data.put(spawn.name, spawn.id);
            scroll.replace(old, spawn.name);
        }
    }

    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        String name = scroll.getSelected();
        this.data = data;
        scroll.setList(list);

        if (name != null)
            scroll.setSelected(name);
        initGui();
    }

    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
            save();
            String selected = scroll.getSelected();
            spawn = new SpawnData();
            Client.sendData(EnumPacketServer.NaturalSpawnGet, data.get(selected));
        }
    }

    @Override
    public void save() {
        GuiNpcTextField.unfocus();
        if (spawn.id >= 0)
            Client.sendData(EnumPacketServer.NaturalSpawnSave, spawn.writeNBT(new NBTTagCompound()));
    }

    @Override
    public void setSelected(String selected) {

    }

    public void closeSubGui(SubGuiInterface gui) {
        super.closeSubGui(gui);
        if (gui instanceof GuiNpcMobSpawnerSelector) {
            GuiNpcMobSpawnerSelector selector = (GuiNpcMobSpawnerSelector) gui;
            NBTTagCompound compound = selector.getCompound();
            if (compound != null)
                spawn.compound1 = compound;
            initGui();
        }
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        spawn.readNBT(compound);
        setSelected(spawn.name);
        initGui();
    }

    @Override
    public void mouseDragged(GuiNpcSlider guiNpcSlider) {
        guiNpcSlider.displayString = I18n.translateToLocal("spawning.weightedChance") + ": " + (int) (guiNpcSlider.sliderValue * 100);
    }

    @Override
    public void mousePressed(GuiNpcSlider guiNpcSlider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(GuiNpcSlider guiNpcSlider) {
        spawn.itemWeight = (int) (guiNpcSlider.sliderValue * 100);
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }

}
