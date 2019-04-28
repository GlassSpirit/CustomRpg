package noppes.npcs.client.gui.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.ModelData;
import noppes.npcs.client.Client;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.mainmenu.GuiNpcDisplay;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.input.Keyboard;

public abstract class GuiCreationScreenInterface extends GuiNPCInterface implements ISubGuiListener, ISliderListener {
    public static String Message = "";
    public EntityLivingBase entity;

    private boolean saving = false;
    protected boolean hasSaving = true;
    public int active = 0;

    private EntityPlayer player;
    public int xOffset = 0;
    public ModelData playerdata;

    protected NBTTagCompound original = new NBTTagCompound();

    private static float rotation = 0.5f;

    public GuiCreationScreenInterface(EntityNPCInterface npc) {
        super(npc);
        playerdata = ((EntityCustomNpc) npc).modelData;
        original = playerdata.writeToNBT();
        xSize = 400;
        ySize = 240;
        xOffset = 140;

        player = Minecraft.getMinecraft().player;
        this.closeOnEsc = true;
    }

    @Override
    public void initGui() {
        super.initGui();
        entity = playerdata.getEntity(npc);
        Keyboard.enableRepeatEvents(true);

        addButton(new GuiNpcButton(1, guiLeft + 62, guiTop, 60, 20, "gui.entity"));
        if (entity == null)
            addButton(new GuiNpcButton(2, guiLeft, guiTop + 23, 60, 20, "gui.parts"));
        else if (!(entity instanceof EntityNPCInterface)) {
            GuiCreationExtra gui = new GuiCreationExtra(npc);
            gui.playerdata = playerdata;
            if (!gui.getData(entity).isEmpty())
                addButton(new GuiNpcButton(2, guiLeft, guiTop + 23, 60, 20, "gui.extra"));
            else if (active == 2) {
                mc.displayGuiScreen(new GuiCreationEntities(npc));
                return;
            }
        }
        if (entity == null)
            addButton(new GuiNpcButton(3, guiLeft + 62, guiTop + 23, 60, 20, "gui.scale"));
        if (hasSaving) {
            addButton(new GuiNpcButton(4, guiLeft, guiTop + ySize - 24, 60, 20, "gui.save"));
            addButton(new GuiNpcButton(5, guiLeft + 62, guiTop + ySize - 24, 60, 20, "gui.init"));
        }
        if (getButton(active) == null) {
            openGui(new GuiCreationEntities(npc));
            return;
        }
        getButton(active).enabled = false;
        addButton(new GuiNpcButton(66, guiLeft + xSize - 20, guiTop, 20, 20, "X"));

        addLabel(new GuiNpcLabel(0, Message, guiLeft + 120, guiTop + ySize - 10, 0xff0000));
        getLabel(0).center(xSize - 120);

        addSlider(new GuiNpcSlider(this, 500, guiLeft + xOffset + 142, guiTop + 210, 120, 20, rotation));
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        if (btn.id == 1) {
            openGui(new GuiCreationEntities(npc));
        }
        if (btn.id == 2) {
            if (entity == null)
                openGui(new GuiCreationParts(npc));
            else
                openGui(new GuiCreationExtra(npc));
        }
        if (btn.id == 3) {
            openGui(new GuiCreationScale(npc));
        }
        if (btn.id == 4) {
            this.setSubGui(new GuiPresetSave(this, playerdata));
        }
        if (btn.id == 5) {
            openGui(new GuiCreationLoad(npc));
        }
        if (btn.id == 66) {
            save();
            NoppesUtil.openGUI(player, new GuiNpcDisplay(npc));
        }
    }

    @Override
    public void mouseClicked(int i, int j, int k) {
        if (!saving)
            super.mouseClicked(i, j, k);
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawScreen(x, y, f);
        entity = playerdata.getEntity(npc);
        EntityLivingBase entity = this.entity;
        if (entity == null)
            entity = this.npc;
        else
            EntityUtil.Copy(npc, entity);

        drawNpc(entity, xOffset + 200, 200, 2, (int) (rotation * 360 - 180));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void save() {
        NBTTagCompound newCompound = playerdata.writeToNBT();
        Client.sendData(EnumPacketServer.MainmenuDisplaySave, npc.display.writeToNBT(new NBTTagCompound()));
        Client.sendData(EnumPacketServer.ModelDataSave, newCompound);

    }

    public void openGui(GuiScreen gui) {
        mc.displayGuiScreen(gui);
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        initGui();
    }

    @Override
    public void mouseDragged(GuiNpcSlider slider) {
        if (slider.id == 500) {
            rotation = slider.sliderValue;
            slider.setString("" + (int) (rotation * 360));
        }
    }

    @Override
    public void mousePressed(GuiNpcSlider slider) {

    }

    @Override
    public void mouseReleased(GuiNpcSlider slider) {

    }
}
