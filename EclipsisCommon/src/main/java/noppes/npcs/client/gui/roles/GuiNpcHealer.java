package noppes.npcs.client.gui.roles;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.roles.JobHealer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GuiNpcHealer extends GuiNPCInterface2 {
    private JobHealer job;
    private GuiCustomScroll scroll1;
    private GuiCustomScroll scroll2;
    private HashMap<String, Integer> potions;
    private HashMap<String, String> displays;
    private int potency = 0; //dummy value

    public GuiNpcHealer(EntityNPCInterface npc) {
        super(npc);
        job = (JobHealer) npc.jobInterface;
        potions = new HashMap<String, Integer>();
        displays = new HashMap<String, String>();

        Iterator<Potion> ita = Potion.REGISTRY.iterator();
        while (ita.hasNext()) {
            Potion p = ita.next();
            potions.put(p.getName(), Potion.getIdFromPotion(p));
        }
    }

    public void initGui() {
        super.initGui();

        addLabel(new GuiNpcLabel(1, "beacon.range", guiLeft + 10, guiTop + 9));
        addTextField(new GuiNpcTextField(1, this, this.fontRenderer, guiLeft + 80, guiTop + 4, 40, 20, job.range + ""));
        getTextField(1).numbersOnly = true;
        getTextField(1).setMinMaxDefault(1, 64, 16);

        addLabel(new GuiNpcLabel(4, "stats.speed", guiLeft + 140, guiTop + 9));
        addTextField(new GuiNpcTextField(4, this, this.fontRenderer, guiLeft + 220, guiTop + 4, 40, 20, potency + ""));
        getTextField(4).numbersOnly = true;
        getTextField(4).setMinMaxDefault(10, Integer.MAX_VALUE, 20);

        addLabel(new GuiNpcLabel(3, "beacon.affect", guiLeft + 10, guiTop + 31));
        addButton(new GuiNpcButton(3, guiLeft + 56, guiTop + 26, 80, 20, new String[]{"faction.friendly", "faction.unfriendly", "spawner.all"}, job.type));

        addLabel(new GuiNpcLabel(2, "beacon.potency", guiLeft + 140, guiTop + 31));
        addTextField(new GuiNpcTextField(2, this, this.fontRenderer, guiLeft + 220, guiTop + 26, 40, 20, potency + ""));
        getTextField(2).numbersOnly = true;
        getTextField(2).setMinMaxDefault(0, 3, 0);

        if (scroll1 == null) {
            scroll1 = new GuiCustomScroll(this, 0);
            scroll1.setSize(175, 154);
        }
        scroll1.guiLeft = guiLeft + 4;
        scroll1.guiTop = guiTop + 58;
        this.addScroll(scroll1);
        addLabel(new GuiNpcLabel(11, "beacon.availableEffects", guiLeft + 4, guiTop + 48));

        if (scroll2 == null) {
            scroll2 = new GuiCustomScroll(this, 1);
            scroll2.setSize(175, 154);
        }
        scroll2.guiLeft = guiLeft + 235;
        scroll2.guiTop = guiTop + 58;
        this.addScroll(scroll2);
        addLabel(new GuiNpcLabel(12, "beacon.currentEffects", guiLeft + 235, guiTop + 48));

        List<String> all = new ArrayList<String>();
        for (String names : potions.keySet()) {
            if (!job.effects.containsKey(potions.get(names))) all.add(names);
            else
                displays.put(I18n.format(names) + " " + I18n.format("enchantment.level." + (job.effects.get(potions.get(names)) + 1)), names);
        }
        scroll1.setList(all);
        List<String> applied = new ArrayList<String>(displays.keySet());
        scroll2.setList(applied);

        addButton(new GuiNpcButton(11, guiLeft + 180, guiTop + 80, 55, 20, ">"));
        addButton(new GuiNpcButton(12, guiLeft + 180, guiTop + 102, 55, 20, "<"));

        addButton(new GuiNpcButton(13, guiLeft + 180, guiTop + 130, 55, 20, ">>"));
        addButton(new GuiNpcButton(14, guiLeft + 180, guiTop + 152, 55, 20, "<<"));

    }

    @Override
    public void elementClicked() {

    }

    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;

        if (button.id == 3) {
            job.type = (byte) button.getValue();
        }
        if (button.id == 11) {
            if (scroll1.hasSelected()) {
                job.effects.put(potions.get(scroll1.getSelected()), getTextField(2).getInteger());
                scroll1.selected = -1;
                scroll2.selected = -1;
                initGui();
            }
        }
        if (button.id == 12) {
            if (scroll2.hasSelected()) {
                job.effects.remove(potions.get(displays.remove(scroll2.getSelected())));
                scroll1.selected = -1;
                scroll2.selected = -1;
                initGui();
            }
        }
        if (button.id == 13) {
            job.effects.clear();

            List<String> all = new ArrayList<String>();

            Iterator<Potion> ita = Potion.REGISTRY.iterator();
            while (ita.hasNext()) {
                Potion p = ita.next();
                job.effects.put(Potion.getIdFromPotion(p), potency);
            }
            scroll1.selected = -1;
            scroll2.selected = -1;
            initGui();
        }
        if (button.id == 14) {
            job.effects.clear();
            displays.clear();
            scroll1.selected = -1;
            scroll2.selected = -1;
            initGui();
        }
    }


    @Override
    public void save() {
        job.range = getTextField(1).getInteger();
        potency = getTextField(2).getInteger();
        job.speed = getTextField(4).getInteger();
        Client.sendData(EnumPacketServer.JobSave, job.writeToNBT(new NBTTagCompound()));
    }


}
