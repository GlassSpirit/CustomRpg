package noppes.npcs.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.objects.blocks.tiles.TileCopy;

public class GuiBlockCopy extends GuiNPCInterface implements IGuiData, ITextfieldListener {
    private int x, y, z;
    private TileCopy tile;

    public GuiBlockCopy(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        setBackground("menubg.png");
        xSize = 256;
        ySize = 216;
        closeOnEsc = true;
        tile = (TileCopy) player.world.getTileEntity(new BlockPos(x, y, z));

        Client.sendData(EnumPacketServer.GetTileEntity, x, y, z);
    }

    @Override
    public void initPacket() {

    }

    @Override
    public void initGui() {
        super.initGui();
        int y = guiTop + 4;
        addTextField(new GuiNpcTextField(0, this, guiLeft + 104, y, 50, 20, tile.height + ""));
        addLabel(new GuiNpcLabel(0, "schematic.height", guiLeft + 5, y + 5));
        getTextField(0).numbersOnly = true;
        getTextField(0).setMinMaxDefault(0, 100, 10);

        addTextField(new GuiNpcTextField(1, this, guiLeft + 104, y += 23, 50, 20, tile.width + ""));
        addLabel(new GuiNpcLabel(1, "schematic.width", guiLeft + 5, y + 5));
        getTextField(1).numbersOnly = true;
        getTextField(1).setMinMaxDefault(0, 100, 10);

        addTextField(new GuiNpcTextField(2, this, guiLeft + 104, y += 23, 50, 20, tile.length + ""));
        addLabel(new GuiNpcLabel(2, "schematic.length", guiLeft + 5, y + 5));
        getTextField(2).numbersOnly = true;
        getTextField(2).setMinMaxDefault(0, 100, 10);

        addTextField(new GuiNpcTextField(5, this, guiLeft + 104, y += 23, 100, 20, ""));
        addLabel(new GuiNpcLabel(5, "gui.name", guiLeft + 5, y + 5));

        addButton(new GuiNpcButton(6, guiLeft + 5, y += 23, 200, 20, 0, "copyBlock.schematic", "copyBlock.blueprint"));

        addButton(new GuiNpcButton(0, guiLeft + 5, y += 30, 60, 20, "gui.save"));
        addButton(new GuiNpcButton(1, guiLeft + 67, y, 60, 20, "gui.cancel"));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 0) {
            NBTTagCompound compound = new NBTTagCompound();
            tile.writeToNBT(compound);
            Client.sendData(EnumPacketServer.SchematicStore, getTextField(5).getText(), getButton(6).getValue(), compound);
            close();
        }
        if (guibutton.id == 1) {
            close();
        }
    }

    @Override
    public void save() {
        NBTTagCompound compound = new NBTTagCompound();
        tile.writeToNBT(compound);
        Client.sendData(EnumPacketServer.SaveTileEntity, compound);
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        tile.readFromNBT(compound);
        initGui();
    }

    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 0)
            tile.height = (short) textfield.getInteger();
        if (textfield.id == 1)
            tile.width = (short) textfield.getInteger();
        if (textfield.id == 2)
            tile.length = (short) textfield.getInteger();
    }
}
