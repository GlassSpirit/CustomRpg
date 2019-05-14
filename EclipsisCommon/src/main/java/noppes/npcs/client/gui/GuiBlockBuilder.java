package noppes.npcs.client.gui;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.client.Client;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.common.schematics.ISchematic;
import noppes.npcs.common.schematics.SchematicWrapper;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.common.objects.tiles.TileBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class GuiBlockBuilder extends GuiNPCInterface implements IGuiData, ICustomScrollListener, IScrollData, GuiYesNoCallback {
    private int x, y, z;
    private TileBuilder tile;
    private GuiCustomScroll scroll;
    private ISchematic selected = null;

    public GuiBlockBuilder(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        setBackground("menubg.png");
        xSize = 256;
        ySize = 216;
        closeOnEsc = true;
        tile = (TileBuilder) player.world.getTileEntity(new BlockPos(x, y, z));
    }

    @Override
    public void initPacket() {
        Client.sendData(EnumPacketServer.SchematicsTile, x, y, z);
    }

    @Override
    public void initGui() {
        super.initGui();

        if (scroll == null) {
            scroll = new GuiCustomScroll(this, 0);
            scroll.setSize(125, 208);
        }
        scroll.guiLeft = guiLeft + 4;
        scroll.guiTop = guiTop + 4;
        addScroll(scroll);

        if (selected != null) {
            int y = guiTop + 4;
            int size = selected.getWidth() * selected.getHeight() * selected.getLength();
            addButton(new GuiNpcButtonYesNo(3, guiLeft + 200, y, TileBuilder.DrawPos != null && tile.getPos().equals(TileBuilder.DrawPos)));
            addLabel(new GuiNpcLabel(3, "schematic.preview", guiLeft + 130, y + 5));

            addLabel(new GuiNpcLabel(0, I18n.translateToLocal("schematic.width") + ": " + selected.getWidth(), guiLeft + 130, y += 21));
            addLabel(new GuiNpcLabel(1, I18n.translateToLocal("schematic.length") + ": " + selected.getLength(), guiLeft + 130, y += 11));
            addLabel(new GuiNpcLabel(2, I18n.translateToLocal("schematic.height") + ": " + selected.getHeight(), guiLeft + 130, y += 11));


            addButton(new GuiNpcButtonYesNo(4, guiLeft + 200, y += 14, tile.enabled));
            addLabel(new GuiNpcLabel(4, I18n.translateToLocal("gui.enabled"), guiLeft + 130, y + 5));

            addButton(new GuiNpcButtonYesNo(7, guiLeft + 200, y += 22, tile.finished));
            addLabel(new GuiNpcLabel(7, I18n.translateToLocal("gui.finished"), guiLeft + 130, y + 5));

            addButton(new GuiNpcButtonYesNo(8, guiLeft + 200, y += 22, tile.started));
            addLabel(new GuiNpcLabel(8, I18n.translateToLocal("gui.started"), guiLeft + 130, y + 5));

            addTextField(new GuiNpcTextField(9, this, guiLeft + 200, y += 22, 50, 20, tile.yOffest + ""));
            addLabel(new GuiNpcLabel(9, I18n.translateToLocal("gui.yoffset"), guiLeft + 130, y + 5));
            getTextField(9).numbersOnly = true;
            getTextField(9).setMinMaxDefault(-10, 10, 0);

            addButton(new GuiNpcButton(5, guiLeft + 200, y += 22, 50, 20, new String[]{"0", "90", "180", "270"}, tile.rotation));
            addLabel(new GuiNpcLabel(5, I18n.translateToLocal("movement.rotation"), guiLeft + 130, y + 5));

            addButton(new GuiNpcButton(6, guiLeft + 130, y += 22, 120, 20, "availability.options"));

            addButton(new GuiNpcButton(10, guiLeft + 130, y += 22, 120, 20, "schematic.instantBuild"));
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 3) {
            GuiNpcButtonYesNo button = (GuiNpcButtonYesNo) guibutton;
            if (button.getBoolean()) {
                TileBuilder.SetDrawPos(new BlockPos(x, y, z));
                tile.setDrawSchematic(new SchematicWrapper(selected));
            } else {
                TileBuilder.SetDrawPos(null);
                tile.setDrawSchematic(null);
            }
        }
        if (guibutton.id == 4) {
            tile.enabled = ((GuiNpcButtonYesNo) guibutton).getBoolean();
        }
        if (guibutton.id == 5) {
            tile.rotation = ((GuiNpcButton) guibutton).getValue();
        }
        if (guibutton.id == 6) {
            setSubGui(new SubGuiNpcAvailability(tile.availability));
        }
        if (guibutton.id == 7) {
            tile.finished = ((GuiNpcButtonYesNo) guibutton).getBoolean();
            Client.sendData(EnumPacketServer.SchematicsSet, x, y, z, scroll.getSelected());
        }
        if (guibutton.id == 8) {
            tile.started = ((GuiNpcButtonYesNo) guibutton).getBoolean();
        }
        if (guibutton.id == 10) {
            GuiYesNo guiyesno = new GuiYesNo(this, "", I18n.translateToLocal("schematic.instantBuildText"), 0);
            displayGuiScreen(guiyesno);
        }
    }

    @Override
    public void save() {
        if (getTextField(9) != null) {
            tile.yOffest = getTextField(9).getInteger();
        }
        Client.sendData(EnumPacketServer.SchematicsTileSave, x, y, z, tile.writePartNBT(new NBTTagCompound()));
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        if (compound.hasKey("Width")) {
            List<IBlockState> states = new ArrayList<>();
            NBTTagList list = compound.getTagList("Data", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                states.add(NBTUtil.readBlockState(list.getCompoundTagAt(i)));
            }

            selected = new ISchematic() {

                @Override
                public short getWidth() {
                    return compound.getShort("Width");
                }

                @Override
                public int getTileEntitySize() {
                    return 0;
                }

                @Override
                public NBTTagCompound getTileEntity(int i) {
                    return null;
                }

                @Override
                public String getName() {
                    return compound.getString("SchematicName");
                }

                @Override
                public short getLength() {
                    return compound.getShort("Length");
                }

                @Override
                public short getHeight() {
                    return compound.getShort("Height");
                }

                @Override
                public IBlockState getBlockState(int i) {
                    return states.get(i);
                }

                @Override
                public IBlockState getBlockState(int x, int y, int z) {
                    return getBlockState((y * getLength() + z) * getWidth() + x);
                }

                @Override
                public NBTTagCompound getNBT() {
                    // TODO Auto-generated method stub
                    return null;
                }
            };

            if (TileBuilder.DrawPos != null && TileBuilder.DrawPos.equals(tile.getPos())) {
                SchematicWrapper wrapper = new SchematicWrapper(selected);
                wrapper.rotation = tile.rotation;
                tile.setDrawSchematic(wrapper);
            }
            scroll.setSelected(selected.getName());
            scroll.scrollTo(selected.getName());
        } else {
            tile.readPartNBT(compound);
        }
        initGui();
    }

    @Override
    public void confirmClicked(boolean flag, int i) {
        if (flag) {
            Client.sendData(EnumPacketServer.SchematicsBuild, x, y, z);
            close();
            selected = null;
        } else {
            NoppesUtil.openGUI(player, this);
        }
    }

    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
        if (!scroll.hasSelected())
            return;
        if (selected != null)
            getButton(3).setDisplay(0);
        TileBuilder.SetDrawPos(null);
        tile.setDrawSchematic(null);
        Client.sendData(EnumPacketServer.SchematicsSet, x, y, z, scroll.getSelected());
    }

    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        scroll.setList(list);
        if (selected != null)
            scroll.setSelected(selected.getName());
        initGui();
    }

    @Override
    public void setSelected(String selected) {

    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
}
