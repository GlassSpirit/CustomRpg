package noppes.npcs.client.gui.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.NPCRendererHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.common.entity.EntityNPC64x32;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.common.entity.EntityNpcAlex;
import noppes.npcs.common.entity.EntityNpcClassicPlayer;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class GuiCreationEntities extends GuiCreationScreenInterface implements ICustomScrollListener {

    public HashMap<String, Class<? extends EntityLivingBase>> data = new HashMap<String, Class<? extends EntityLivingBase>>();
    private List<String> list;
    private GuiCustomScroll scroll;
    private boolean resetToSelected = true;

    public GuiCreationEntities(EntityNPCInterface npc) {
        super(npc);
        for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
            String name = ent.getName();
            Class<? extends Entity> c = ent.getEntityClass();
            try {
                if (EntityLiving.class.isAssignableFrom(c) && c.getConstructor(World.class) != null && !Modifier.isAbstract(c.getModifiers())) {
                    if (Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(c) instanceof RenderLivingBase) {
                        String s = name;
                        if (s.toLowerCase().contains("customnpc"))
                            continue;
                        data.put(name, c.asSubclass(EntityLivingBase.class));
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
            }
        }
        data.put("NPC 64x32", EntityNPC64x32.class);
        data.put("NPC Alex Arms", EntityNpcAlex.class);
        data.put("NPC Classic Player", EntityNpcClassicPlayer.class);
        list = new ArrayList<String>(data.keySet());
        list.add("NPC");
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        active = 1;
        xOffset = 60;
    }

    @Override
    public void initGui() {
        super.initGui();
        addButton(new GuiNpcButton(10, guiLeft, guiTop + 46, 120, 20, "Reset To NPC"));
        if (scroll == null) {
            scroll = new GuiCustomScroll(this, 0);
            scroll.setUnsortedList(list);
        }
        scroll.guiLeft = guiLeft;
        scroll.guiTop = guiTop + 68;
        scroll.setSize(100, ySize - 96);

        String selected = "NPC";
        if (entity != null) {
            for (Entry<String, Class<? extends EntityLivingBase>> en : data.entrySet()) {
                if (en.getValue().toString().equals(entity.getClass().toString())) {
                    selected = en.getKey();
                }
            }
        }
        scroll.setSelected(selected);

        if (resetToSelected) {
            scroll.scrollTo(scroll.getSelected());
            resetToSelected = false;
        }
        addScroll(scroll);
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        if (btn.id == 10) {
            playerdata.setEntityClass(null);
            resetToSelected = true;
            initGui();
        }
    }

    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
        playerdata.setEntityClass(data.get(scroll.getSelected()));
        Entity entity = playerdata.getEntity(npc);
        if (entity != null) {
            RenderLivingBase render = (RenderLivingBase) mc.getRenderManager().getEntityClassRenderObject(entity.getClass());
            if (!NPCRendererHelper.getTexture(render, entity).equals(TextureMap.LOCATION_MISSING_TEXTURE.toString())) {
                npc.display.setSkinTexture(NPCRendererHelper.getTexture(render, entity));
            }
        } else {
            npc.display.setSkinTexture("customnpcs:textures/entity/humanmale/steve.png");
        }
        initGui();
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }

}
