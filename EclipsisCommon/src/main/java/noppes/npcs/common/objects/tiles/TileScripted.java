package noppes.npcs.common.objects.tiles;

import com.google.common.base.MoreObjects;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.EventHooks;
import noppes.npcs.util.NBTTags;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.TextBlock;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.block.ITextPlane;
import noppes.npcs.api.wrapper.BlockScriptedWrapper;
import noppes.npcs.common.objects.NpcObjects;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptBlockHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.common.entity.data.DataTimers;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class TileScripted extends TileNpcEntity implements ITickable, IScriptBlockHandler {
    public List<ScriptContainer> scripts = new ArrayList<>();

    public String scriptLanguage = "ECMAScript";
    public boolean enabled = false;

    private IBlock blockDummy = null;
    public DataTimers timers = new DataTimers(this);

    public long lastInited = -1;

    private short ticksExisted = 0;

    public ItemStack itemModel = new ItemStack(NpcObjects.scriptedBlock);
    public Block blockModel = null;

    public boolean needsClientUpdate = false;

    public int powering = 0;
    public int activePowering = 0;
    public int newPower = 0; //used for block redstone event
    public int prevPower = 0; //used for block redstone event

    public boolean isPassible = false;
    public boolean isLadder = false;
    public int lightValue = 0;

    public float blockHardness = 5;
    public float blockResistance = 10;

    public int rotationX = 0, rotationY = 0, rotationZ = 0;
    public float scaleX = 1, scaleY = 1, scaleZ = 1;

    public TileEntity renderTile;
    public boolean renderTileErrored = true;
    public ITickable renderTileUpdate = null;

    public TextPlane text1 = new TextPlane();
    public TextPlane text2 = new TextPlane();
    public TextPlane text3 = new TextPlane();
    public TextPlane text4 = new TextPlane();
    public TextPlane text5 = new TextPlane();
    public TextPlane text6 = new TextPlane();

    @Override
    public IBlock getBlock() {
        if (blockDummy == null)
            blockDummy = new BlockScriptedWrapper(getWorld(), this.getBlockType(), getPos());
        return blockDummy;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        setNBT(compound);
        setDisplayNBT(compound);
        timers.readFromNBT(compound);
    }

    public void setNBT(NBTTagCompound compound) {
        scripts = NBTTags.GetScript(compound.getTagList("Scripts", 10), this);
        scriptLanguage = compound.getString("ScriptLanguage");
        enabled = compound.getBoolean("ScriptEnabled");
        activePowering = powering = compound.getInteger("BlockPowering");
        prevPower = compound.getInteger("BlockPrevPower");

        if (compound.hasKey("BlockHardness")) {
            blockHardness = compound.getFloat("BlockHardness");
            blockResistance = compound.getFloat("BlockResistance");
        }

    }

    public void setDisplayNBT(NBTTagCompound compound) {
        itemModel = new ItemStack(compound.getCompoundTag("ScriptBlockModel"));
        if (itemModel.isEmpty())
            itemModel = new ItemStack(NpcObjects.scriptedBlock);
        if (compound.hasKey("ScriptBlockModelBlock"))
            blockModel = Block.getBlockFromName(compound.getString("ScriptBlockModelBlock"));
        renderTileUpdate = null;
        renderTile = null;
        renderTileErrored = false;

        lightValue = compound.getInteger("LightValue");
        isLadder = compound.getBoolean("IsLadder");
        isPassible = compound.getBoolean("IsPassible");

        rotationX = compound.getInteger("RotationX");
        rotationY = compound.getInteger("RotationY");
        rotationZ = compound.getInteger("RotationZ");

        scaleX = compound.getFloat("ScaleX");
        scaleY = compound.getFloat("ScaleY");
        scaleZ = compound.getFloat("ScaleZ");

        if (scaleX <= 0)
            scaleX = 1;
        if (scaleY <= 0)
            scaleY = 1;
        if (scaleZ <= 0)
            scaleZ = 1;

        if (compound.hasKey("Text3")) {
            text1.setNBT(compound.getCompoundTag("Text1"));
            text2.setNBT(compound.getCompoundTag("Text2"));
            text3.setNBT(compound.getCompoundTag("Text3"));
            text4.setNBT(compound.getCompoundTag("Text4"));
            text5.setNBT(compound.getCompoundTag("Text5"));
            text6.setNBT(compound.getCompoundTag("Text6"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        getNBT(compound);
        getDisplayNBT(compound);
        timers.writeToNBT(compound);
        return super.writeToNBT(compound);
    }

    public NBTTagCompound getNBT(NBTTagCompound compound) {
        compound.setTag("Scripts", NBTTags.NBTScript(scripts));
        compound.setString("ScriptLanguage", scriptLanguage);
        compound.setBoolean("ScriptEnabled", enabled);
        compound.setInteger("BlockPowering", powering);
        compound.setInteger("BlockPrevPower", prevPower);
        compound.setFloat("BlockHardness", blockHardness);
        compound.setFloat("BlockResistance", blockResistance);
        return compound;
    }

    public NBTTagCompound getDisplayNBT(NBTTagCompound compound) {
        NBTTagCompound itemcompound = new NBTTagCompound();
        itemModel.writeToNBT(itemcompound);
        if (blockModel != null) {
            ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(blockModel);
            compound.setString("ScriptBlockModelBlock", resourcelocation == null ? "" : resourcelocation.toString());
        }
        compound.setTag("ScriptBlockModel", itemcompound);
        compound.setInteger("LightValue", lightValue);
        compound.setBoolean("IsLadder", isLadder);
        compound.setBoolean("IsPassible", isPassible);

        compound.setInteger("RotationX", rotationX);
        compound.setInteger("RotationY", rotationY);
        compound.setInteger("RotationZ", rotationZ);

        compound.setFloat("ScaleX", scaleX);
        compound.setFloat("ScaleY", scaleY);
        compound.setFloat("ScaleZ", scaleZ);

        compound.setTag("Text1", text1.getNBT());
        compound.setTag("Text2", text2.getNBT());
        compound.setTag("Text3", text3.getNBT());
        compound.setTag("Text4", text4.getNBT());
        compound.setTag("Text5", text5.getNBT());
        compound.setTag("Text6", text6.getNBT());

        return compound;
    }

    private boolean isEnabled() {
        return enabled && ScriptController.HasStart && !world.isRemote;
    }

    @Override
    public void update() {
        if (renderTileUpdate != null) {
            try {
                renderTileUpdate.update();
            } catch (Exception e) {
                renderTileUpdate = null;
            }
        }
        ticksExisted++;
        if (prevPower != newPower && powering <= 0) {
            EventHooks.onScriptBlockRedstonePower(this, prevPower, newPower);
            prevPower = newPower;
        }

        timers.update();
        if (ticksExisted >= 10) {
            EventHooks.onScriptBlockUpdate(this);
            ticksExisted = 0;
            if (needsClientUpdate) {
                markDirty();
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
                needsClientUpdate = false;
            }
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        int light = lightValue;
        setDisplayNBT(tag);
        if (light != lightValue)
            world.checkLight(pos);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("x", this.pos.getX());
        compound.setInteger("y", this.pos.getY());
        compound.setInteger("z", this.pos.getZ());
        getDisplayNBT(compound);
        return compound;
    }

    public void setItemModel(ItemStack item, Block b) {
        if (item == null || item.isEmpty()) {
            item = new ItemStack(NpcObjects.scriptedBlock);
        }
        if (NoppesUtilPlayer.compareItems(item, itemModel, false, false) && b != blockModel)
            return;

        itemModel = item;
        blockModel = b;
        needsClientUpdate = true;
    }

    public void setLightValue(int value) {
        if (value == lightValue)
            return;
        lightValue = ValueUtil.CorrectInt(value, 0, 15);
        needsClientUpdate = true;
    }

    public void setRedstonePower(int strength) {
        if (powering == strength)
            return;
        //using activePowering to prevent the RedstonePower script event from going crazy
        prevPower = activePowering = ValueUtil.CorrectInt(strength, 0, 15);
        world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
        powering = activePowering;
    }

    public void setScale(float x, float y, float z) {
        if (scaleX == x && scaleY == y && scaleZ == z)
            return;
        scaleX = ValueUtil.correctFloat(x, 0, 10);
        scaleY = ValueUtil.correctFloat(y, 0, 10);
        scaleZ = ValueUtil.correctFloat(z, 0, 10);
        needsClientUpdate = true;
    }

    public void setRotation(int x, int y, int z) {
        if (rotationX == x && rotationY == y && rotationZ == z)
            return;
        rotationX = ValueUtil.CorrectInt(x, 0, 359);
        rotationY = ValueUtil.CorrectInt(y, 0, 359);
        rotationZ = ValueUtil.CorrectInt(z, 0, 359);
        needsClientUpdate = true;
    }

    @Override
    public void runScript(EnumScriptType type, Event event) {
        if (!isEnabled())
            return;
        if (ScriptController.Instance.lastLoaded > lastInited) {
            lastInited = ScriptController.Instance.lastLoaded;
            if (type != EnumScriptType.INIT)
                EventHooks.onScriptBlockInit(this);
        }

        for (ScriptContainer script : scripts) {
            script.run(type, event);
        }
    }

    @Override
    public boolean isClient() {
        return getWorld().isRemote;
    }

    @Override
    public boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean bo) {
        enabled = bo;
    }

    @Override
    public String noticeString() {
        BlockPos pos = getPos();
        return MoreObjects.toStringHelper(this).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
    }

    @Override
    public String getLanguage() {
        return scriptLanguage;
    }

    @Override
    public void setLanguage(String lang) {
        scriptLanguage = lang;
    }

    @Override
    public List<ScriptContainer> getScripts() {
        return scripts;
    }

    @Override
    public Map<Long, String> getConsoleText() {
        Map<Long, String> map = new TreeMap<>();
        int tab = 0;
        for (ScriptContainer script : getScripts()) {
            tab++;
            for (Entry<Long, String> entry : script.console.entrySet()) {
                map.put(entry.getKey(), " tab " + tab + ":\n" + entry.getValue());
            }
        }
        return map;
    }

    @Override
    public void clearConsole() {
        for (ScriptContainer script : getScripts()) {
            script.console.clear();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
        return Block.FULL_BLOCK_AABB.offset(getPos());
    }

    public class TextPlane implements ITextPlane {
        public boolean textHasChanged = true;
        public TextBlock textBlock;

        public String text = "";
        public int rotationX = 0, rotationY = 0, rotationZ = 0;
        public float offsetX = 0, offsetY = 0f, offsetZ = 0.5f;
        public float scale = 1;

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void setText(String text) {
            if (this.text.equals(text))
                return;
            this.text = text;
            textHasChanged = true;
            needsClientUpdate = true;
        }

        @Override
        public int getRotationX() {
            return rotationX;
        }

        @Override
        public int getRotationY() {
            return rotationY;
        }

        @Override
        public int getRotationZ() {
            return rotationZ;
        }

        @Override
        public void setRotationX(int x) {
            x = ValueUtil.CorrectInt(x % 360, 0, 359);
            if (rotationX == x)
                return;
            rotationX = x;
            needsClientUpdate = true;
        }

        @Override
        public void setRotationY(int y) {
            y = ValueUtil.CorrectInt(y % 360, 0, 359);
            if (rotationY == y)
                return;
            rotationY = y;
            needsClientUpdate = true;
        }

        @Override
        public void setRotationZ(int z) {
            z = ValueUtil.CorrectInt(z % 360, 0, 359);
            if (rotationZ == z)
                return;
            rotationZ = z;
            needsClientUpdate = true;
        }

        @Override
        public float getOffsetX() {
            return offsetX;
        }

        @Override
        public float getOffsetY() {
            return offsetY;
        }

        @Override
        public float getOffsetZ() {
            return offsetZ;
        }

        @Override
        public void setOffsetX(float x) {
            x = ValueUtil.correctFloat(x, -1, 1);
            if (offsetX == x)
                return;
            offsetX = x;
            needsClientUpdate = true;
        }

        @Override
        public void setOffsetY(float y) {
            y = ValueUtil.correctFloat(y, -1, 1);
            if (offsetY == y)
                return;
            offsetY = y;
            needsClientUpdate = true;
        }

        @Override
        public void setOffsetZ(float z) {
            z = ValueUtil.correctFloat(z, -1, 1);
            if (rotationZ == z)
                return;
            offsetZ = z;
            needsClientUpdate = true;
        }

        @Override
        public float getScale() {
            return scale;
        }

        @Override
        public void setScale(float scale) {
            if (this.scale == scale)
                return;
            this.scale = scale;
            needsClientUpdate = true;
        }

        public NBTTagCompound getNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("Text", text);
            compound.setInteger("RotationX", rotationX);
            compound.setInteger("RotationY", rotationY);
            compound.setInteger("RotationZ", rotationZ);
            compound.setFloat("OffsetX", offsetX);
            compound.setFloat("OffsetY", offsetY);
            compound.setFloat("OffsetZ", offsetZ);
            compound.setFloat("Scale", scale);
            return compound;
        }

        public void setNBT(NBTTagCompound compound) {
            setText(compound.getString("Text"));
            rotationX = compound.getInteger("RotationX");
            rotationY = compound.getInteger("RotationY");
            rotationZ = compound.getInteger("RotationZ");
            offsetX = compound.getFloat("OffsetX");
            offsetY = compound.getFloat("OffsetY");
            offsetZ = compound.getFloat("OffsetZ");
            scale = compound.getFloat("Scale");
        }
    }
}
