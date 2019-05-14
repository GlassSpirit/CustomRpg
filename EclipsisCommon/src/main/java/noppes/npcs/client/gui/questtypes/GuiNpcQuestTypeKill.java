package noppes.npcs.client.gui.questtypes;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.quests.QuestKill;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.TreeMap;

public class GuiNpcQuestTypeKill extends SubGuiInterface implements ITextfieldListener, ICustomScrollListener {
    private GuiScreen parent;
    private GuiCustomScroll scroll;

    private QuestKill quest;

    private GuiNpcTextField lastSelected;

    public GuiNpcQuestTypeKill(EntityNPCInterface npc, Quest q, GuiScreen parent) {
        this.npc = npc;
        this.parent = parent;
        title = "Quest Kill Setup";

        quest = (QuestKill) q.questInterface;

        setBackground("menubg.png");
        xSize = 356;
        ySize = 216;
        closeOnEsc = true;
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = 0;
        addLabel(new GuiNpcLabel(0, "You can fill in npc or player names too", guiLeft + 4, guiTop + 50));
        for (String name : quest.targets.keySet()) {
            this.addTextField(new GuiNpcTextField(i, this, fontRenderer, guiLeft + 4, guiTop + 70 + i * 22, 180, 20, name));
            this.addTextField(new GuiNpcTextField(i + 3, this, fontRenderer, guiLeft + 186, guiTop + 70 + i * 22, 24, 20, quest.targets.get(name) + ""));
            this.getTextField(i + 3).numbersOnly = true;
            this.getTextField(i + 3).setMinMaxDefault(1, Integer.MAX_VALUE, 1);
            i++;
        }

        for (; i < 3; i++) {
            this.addTextField(new GuiNpcTextField(i, this, fontRenderer, guiLeft + 4, guiTop + 70 + i * 22, 180, 20, ""));
            this.addTextField(new GuiNpcTextField(i + 3, this, fontRenderer, guiLeft + 186, guiTop + 70 + i * 22, 24, 20, "1"));
            this.getTextField(i + 3).numbersOnly = true;
            this.getTextField(i + 3).setMinMaxDefault(1, Integer.MAX_VALUE, 1);
        }
        ArrayList<String> list = new ArrayList<>();
        for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
            Class<? extends Entity> c = ent.getEntityClass();
            String name = ent.getName();
            try {
                if (EntityLivingBase.class.isAssignableFrom(c) && !EntityNPCInterface.class.isAssignableFrom(c) && c.getConstructor(World.class) != null && !Modifier.isAbstract(c.getModifiers()))
                    list.add(name);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
            }
        }
        if (scroll == null)
            scroll = new GuiCustomScroll(this, 0);
        scroll.setList(list);
        scroll.setSize(130, 198);
        scroll.guiLeft = guiLeft + 220;
        scroll.guiTop = guiTop + 14;
        addScroll(scroll);
        this.addButton(new GuiNpcButton(0, guiLeft + 4, guiTop + 140, 98, 20, "gui.back"));

        scroll.visible = lastSelected != null;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);
        if (guibutton.id == 0) {
            close();
        }
    }

    @Override
    public void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        scroll.visible = lastSelected != null;
    }

    @Override
    public void save() {
    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        if (guiNpcTextField.id < 3)
            lastSelected = guiNpcTextField;

        saveTargets();
    }

    private void saveTargets() {
        TreeMap<String, Integer> map = new TreeMap<>();
        for (int i = 0; i < 3; i++) {
            String name = getTextField(i).getText();
            if (name.isEmpty())
                continue;
            map.put(name, getTextField(i + 3).getInteger());
        }
        quest.targets = map;
    }

    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (lastSelected == null)
            return;
        lastSelected.setText(guiCustomScroll.getSelected());
        saveTargets();
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }

}
