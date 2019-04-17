package noppes.npcs.client.gui;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;

public class GuiNbtBook extends GuiNPCInterface implements IGuiData {

    private int x, y, z;
    private TileEntity tile;
    private IBlockState state;
    private ItemStack blockStack;

    private int entityId;
    private Entity entity;

    private NBTTagCompound originalCompound;
    private NBTTagCompound compound;

    private String faultyText = null;
    private String errorMessage = null;

    public GuiNbtBook(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        setBackground("menubg.png");
        xSize = 256;
        ySize = 216;
    }

    @Override
    public void initGui() {
        super.initGui();
        int y = guiTop + 40;
        if (state != null) {
            addLabel(new GuiNpcLabel(11, "x: " + x + ", y: " + y + ", z: " + z, guiLeft + 60, guiTop + 6));
            addLabel(new GuiNpcLabel(12, "id: " + Block.REGISTRY.getNameForObject(state.getBlock()), guiLeft + 60, guiTop + 16));
        }
        if (entity != null) {
            addLabel(new GuiNpcLabel(12, "id: " + EntityRegistry.getEntry(entity.getClass()).getRegistryName(), guiLeft + 60, guiTop + 6));
        }

        addButton(new GuiNpcButton(0, guiLeft + 38, guiTop + 144, 180, 20, "nbt.edit"));
        getButton(0).enabled = compound != null && !compound.isEmpty();

        addLabel(new GuiNpcLabel(0, "", guiLeft + 4, guiTop + 167));
        addLabel(new GuiNpcLabel(1, "", guiLeft + 4, guiTop + 177));

        addButton(new GuiNpcButton(66, guiLeft + 128, guiTop + 190, 120, 20, "gui.close"));
        addButton(new GuiNpcButton(67, guiLeft + 4, guiTop + 190, 120, 20, "gui.save"));

        if (errorMessage != null) {
            getButton(67).enabled = false;
            int i = errorMessage.indexOf(" at: ");
            if (i > 0) {
                getLabel(0).label = errorMessage.substring(0, i);
                getLabel(1).label = errorMessage.substring(i);
            } else {
                getLabel(0).label = errorMessage;
            }
        }
        if (getButton(67).enabled && originalCompound != null) {
            getButton(67).enabled = !originalCompound.equals(compound);
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        int id = guibutton.id;
        if (id == 0) {
            if (faultyText != null) {
                setSubGui(new SubGuiNpcTextArea(compound.toString(), faultyText).enableHighlighting());
            } else {
                setSubGui(new SubGuiNpcTextArea(compound.toString()).enableHighlighting());
            }
        }
        if (id == 67) {
            getLabel(0).label = "Saved";
            if (compound.equals(originalCompound))
                return;
            if (tile == null) {
                Client.sendData(EnumPacketServer.NbtBookSaveEntity, entityId, compound);
                return;
            } else {
                Client.sendData(EnumPacketServer.NbtBookSaveBlock, x, y, z, compound);
            }
            originalCompound = compound.copy();
            getButton(67).enabled = false;
        }
        if (id == 66) {
            close();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (hasSubGui())
            return;

        if (state != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(guiLeft + 4, guiTop + 4, 0);
            GlStateManager.scale(3, 3, 3);
            RenderHelper.enableGUIStandardItemLighting();
            itemRender.renderItemAndEffectIntoGUI(blockStack, 0, 0);
            itemRender.renderItemOverlays(fontRenderer, blockStack, 0, 0);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }

        if (entity instanceof EntityLivingBase) {
            drawNpc((EntityLivingBase) entity, 20, 80, 1, 0);
        }
    }

    @Override
    public void closeSubGui(SubGuiInterface gui) {
        super.closeSubGui(gui);

        if (gui instanceof SubGuiNpcTextArea) {
            try {
                compound = JsonToNBT.getTagFromJson(((SubGuiNpcTextArea) gui).text);
                errorMessage = faultyText = null;
            } catch (NBTException e) {
                errorMessage = e.getLocalizedMessage();
                faultyText = ((SubGuiNpcTextArea) gui).text;
            }
            initGui();
        }
    }

    @Override
    public void save() {
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        if (compound.hasKey("EntityId")) {
            entityId = compound.getInteger("EntityId");
            entity = player.world.getEntityByID(entityId);
        } else {
            tile = player.world.getTileEntity(new BlockPos(x, y, z));
            state = player.world.getBlockState(new BlockPos(x, y, z));
            blockStack = state.getBlock().getItem(player.world, new BlockPos(x, y, z), state);
        }

        originalCompound = compound.getCompoundTag("Data");
        this.compound = originalCompound.copy();
        initGui();
    }
}
