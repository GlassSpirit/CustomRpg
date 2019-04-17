package noppes.npcs.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import noppes.npcs.util.NaturalOrderComparator;

import java.util.*;

public class GuiNPCStringSlot extends GuiSlot {

    private List<String> list; /* synthetic field */
    public String selected;
    public HashSet<String> selectedList;
    private boolean multiSelect;
    private GuiNPCInterface parent;
    public int size;

    public GuiNPCStringSlot(Collection<String> list, GuiNPCInterface parent, boolean multiSelect, int size) {
        super(Minecraft.getMinecraft(), parent.width, parent.height, 32, parent.height - 64, size);
        selectedList = new HashSet<String>();
        this.parent = parent;
        this.list = new ArrayList(list);
        Collections.sort(this.list, new NaturalOrderComparator());
        this.multiSelect = multiSelect;
        this.size = size;
    }

    public void setList(List<String> list) {
        Collections.sort(list, new NaturalOrderComparator());
        this.list = list;
        selected = "";
    }

    @Override
    protected int getSize() {
        return list.size();
    }

    @Override
    protected void elementClicked(int i, boolean flag, int j, int k) {
//        GuiSelectWorld.onElementSelected(parentWorldGui, i);
//        boolean flag1 = GuiSelectWorld.getSelectedWorld(parentWorldGui) >= 0 && GuiSelectWorld.getSelectedWorld(parentWorldGui) < getSize();
//        GuiSelectWorld.getSelectButton(parentWorldGui).enabled = flag1;
//        GuiSelectWorld.getRenameButton(parentWorldGui).enabled = flag1;
//        GuiSelectWorld.getDeleteButton(parentWorldGui).enabled = flag1;
//        if(flag && flag1)
//        {
//            parentWorldGui.selectWorld(i);
//        }
        if (selected != null && selected.equals(list.get(i)) && flag) {
            parent.doubleClicked();
        }
        selected = list.get(i);
        if (selectedList.contains(selected))
            selectedList.remove(selected);
        else
            selectedList.add(selected);
        parent.elementClicked();
    }

    @Override
    protected boolean isSelected(int i) {
        if (!multiSelect) {
            if (selected == null)
                return false;
            return selected.equals(list.get(i));
        } else {
            return selectedList.contains(list.get(i));
        }
    }

    @Override
    protected int getContentHeight() {
        return list.size() * size;
    }

    @Override
    protected void drawBackground() {
        parent.drawDefaultBackground();
    }

    @Override
    protected void drawSlot(int i, int j, int k, int l, int var6, int var7, float partialTick) {
        String s = list.get(i);
        //if(!parent.drawSlot(i, j, k, l, tessellator, s))
        parent.drawString(parent.getFontRenderer(), s, j + 50, k + 3, 0xFFFFFF);
    }

    public void clear() {
        list.clear();
    }

}
