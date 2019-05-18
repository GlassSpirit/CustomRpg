package noppes.npcs.client.gui.model;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.NPCRendererHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityFakeLiving;
import noppes.npcs.entity.EntityNPCInterface;

import java.lang.reflect.Method;
import java.util.*;

public class GuiCreationExtra extends GuiCreationScreenInterface implements ICustomScrollListener {

    private final String[] ignoredTags = {"CanBreakDoors", "Bred", "PlayerCreated", "HasReproduced"};
    private final String[] booleanTags = {};

    private GuiCustomScroll scroll;
    private Map<String, GuiType> data = new HashMap<String, GuiType>();

    private GuiType selected;

    public GuiCreationExtra(EntityNPCInterface npc) {
        super(npc);
        active = 2;
    }

    @Override
    public void initGui() {
        super.initGui();
        if (entity == null) {
            openGui(new GuiCreationParts(npc));
            return;
        }

        if (scroll == null) {
            data = getData(entity);
            scroll = new GuiCustomScroll(this, 0);
            List<String> list = new ArrayList<String>(data.keySet());
            scroll.setList(list);
            if (list.isEmpty())
                return;
            scroll.setSelected(list.get(0));
        }
        selected = data.get(scroll.getSelected());
        if (selected == null)
            return;
        scroll.guiLeft = guiLeft;
        scroll.guiTop = guiTop + 46;
        scroll.setSize(100, ySize - 74);
        addScroll(scroll);
        selected.initGui();
    }

    public Map<String, GuiType> getData(EntityLivingBase entity) {
        Map<String, GuiType> data = new HashMap<String, GuiType>();
        NBTTagCompound compound = getExtras(entity);
        Set<String> keys = compound.getKeySet();
        for (String name : keys) {
            if (isIgnored(name))
                continue;
            NBTBase base = compound.getTag(name);
            if (name.equals("Age")) {
                data.put("Child", new GuiTypeBoolean("Child", entity.isChild()));
            } else if (name.equals("Color") && base.getId() == 1) {
                data.put("Color", new GuiTypeByte("Color", compound.getByte("Color")));
            } else if (base.getId() == 1) {
                byte b = ((NBTTagByte) base).getByte();
                if (b != 0 && b != 1)
                    continue;
                if (playerdata.extra.hasKey(name))
                    b = playerdata.extra.getByte(name);
                data.put(name, new GuiTypeBoolean(name, b == 1));
            }
        }
        if (PixelmonHelper.isPixelmon(entity)) {
            data.put("Model", new GuiTypePixelmon("Model"));
        }
        if (EntityList.getEntityString(entity).equals("tgvstyle.Dog")) {
            data.put("Breed", new GuiTypeDoggyStyle("Breed"));
        }
        return data;
    }

    private boolean isIgnored(String tag) {
        for (String s : ignoredTags)
            if (s.equals(tag))
                return true;
        return false;
    }

    private void updateTexture() {
        EntityLivingBase entity = playerdata.getEntity(npc);
        RenderLivingBase render = (RenderLivingBase) mc.getRenderManager().getEntityRenderObject(entity);
        npc.display.setSkinTexture(NPCRendererHelper.getTexture(render, entity));
    }

    private NBTTagCompound getExtras(EntityLivingBase entity) {
        NBTTagCompound fake = new NBTTagCompound();
        new EntityFakeLiving(entity.world).writeEntityToNBT(fake);

        NBTTagCompound compound = new NBTTagCompound();
        entity.writeEntityToNBT(compound);
        Set<String> keys = fake.getKeySet();
        for (String name : keys)
            compound.removeTag(name);

        return compound;
    }

    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
        if (scroll.id == 0)
            initGui();
        else if (selected != null) {
            selected.scrollClicked(i, j, k, scroll);
        }
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        if (selected != null)
            selected.actionPerformed(btn);
    }

    abstract class GuiType {
        public String name;

        public GuiType(String name) {
            this.name = name;
        }

        public void initGui() {
        }

        public void actionPerformed(GuiButton button) {
        }

        public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
        }

    }

    class GuiTypeBoolean extends GuiType {
        private boolean bo;

        public GuiTypeBoolean(String name, boolean bo) {
            super(name);
            this.bo = bo;
        }

        @Override
        public void initGui() {
            addButton(new GuiNpcButtonYesNo(11, guiLeft + 120, guiTop + 50, 60, 20, bo));
        }

        @Override
        public void actionPerformed(GuiButton button) {
            if (button.id != 11)
                return;
            bo = ((GuiNpcButtonYesNo) button).getBoolean();
            if (name.equals("Child")) {
                playerdata.extra.setInteger("Age", bo ? -24000 : 0);
                playerdata.clearEntity();
            } else {
                playerdata.extra.setBoolean(name, bo);
                playerdata.clearEntity();
                updateTexture();
            }
        }

    }

    class GuiTypeByte extends GuiType {
        private byte b;

        public GuiTypeByte(String name, byte b) {
            super(name);
            this.b = b;
        }

        @Override
        public void initGui() {
            addButton(new GuiButtonBiDirectional(11, guiLeft + 120, guiTop + 45, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"}, b));
        }

        @Override
        public void actionPerformed(GuiButton button) {
            if (button.id != 11)
                return;
            playerdata.extra.setByte(name, (byte) ((GuiNpcButton) button).getValue());
            playerdata.clearEntity();
            updateTexture();
        }

    }

    class GuiTypePixelmon extends GuiType {

        public GuiTypePixelmon(String name) {
            super(name);
        }

        @Override
        public void initGui() {
            GuiCustomScroll scroll = new GuiCustomScroll(GuiCreationExtra.this, 1);
            scroll.setSize(120, 200);
            scroll.guiLeft = guiLeft + 120;
            scroll.guiTop = guiTop + 20;
            addScroll(scroll);

            scroll.setList(PixelmonHelper.getPixelmonList());
            scroll.setSelected(PixelmonHelper.getName(entity));
        }

        @Override
        public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
            String name = scroll.getSelected();
            playerdata.setExtra(entity, "name", name);
            updateTexture();
        }

    }

    class GuiTypeDoggyStyle extends GuiType {
        public GuiTypeDoggyStyle(String name) {
            super(name);
        }

        @Override
        public void initGui() {
            Enum breed = null;
            try {
                Method method = entity.getClass().getMethod("getBreedID");
                breed = (Enum) method.invoke(entity);
            } catch (Exception e) {

            }
            addButton(new GuiButtonBiDirectional(11, guiLeft + 120, guiTop + 45, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26"}, breed.ordinal()));
        }

        @Override
        public void actionPerformed(GuiButton button) {
            if (button.id != 11)
                return;
            int breed = ((GuiNpcButton) button).getValue();
            EntityLivingBase entity = playerdata.getEntity(npc);
            playerdata.setExtra(entity, "breed", ((GuiNpcButton) button).getValue() + "");
            updateTexture();
        }
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
}
