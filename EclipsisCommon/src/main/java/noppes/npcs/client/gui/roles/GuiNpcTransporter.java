package noppes.npcs.client.gui.roles;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.util.HashMap;
import java.util.Vector;

public class GuiNpcTransporter extends GuiNPCInterface2 implements IScrollData, IGuiData {
    private GuiCustomScroll scroll;
    public TransportLocation location = new TransportLocation();
    private HashMap<String, Integer> data = new HashMap<String, Integer>();

    public GuiNpcTransporter(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public void initPacket() {
        Client.sendData(EnumPacketServer.TransportCategoriesGet);
        Client.sendData(EnumPacketServer.TransportGetLocation);
    }

    @Override
    public void initGui() {
        super.initGui();
        Vector<String> list = new Vector<String>();
        list.addAll(data.keySet());

        if (scroll == null) {
            scroll = new GuiCustomScroll(this, 0);
            scroll.setSize(143, 208);
        }
        scroll.guiLeft = guiLeft + 214;
        scroll.guiTop = guiTop + 4;

        addScroll(scroll);
        addLabel(new GuiNpcLabel(0, "gui.name", guiLeft + 4, height + 8));
        addTextField(new GuiNpcTextField(0, this, fontRenderer, guiLeft + 60, guiTop + 3, 140, 20, location.name));
        addButton(new GuiNpcButton(0, guiLeft + 4, guiTop + 31, new String[]{"transporter.discovered", "transporter.start", "transporter.interaction"}, location.type));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 0) {
            location.type = button.getValue();
        }
    }

    @Override
    public void save() {
        if (!scroll.hasSelected())
            return;

        String name = getTextField(0).getText();
        if (!name.isEmpty())
            location.name = name;

        location.pos = new BlockPos(player);
        location.dimension = player.dimension;

        int cat = data.get(scroll.getSelected());
        Client.sendData(EnumPacketServer.TransportSave, cat, location.writeNBT());
    }

    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        this.data = data;
        this.scroll.setList(list);
    }

    @Override
    public void setSelected(String selected) {
        scroll.setSelected(selected);
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        TransportLocation loc = new TransportLocation();
        loc.readNBT(compound);
        location = loc;
        initGui();
    }


}
