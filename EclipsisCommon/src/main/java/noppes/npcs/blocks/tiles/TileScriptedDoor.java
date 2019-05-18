package noppes.npcs.blocks.tiles;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.wrapper.BlockScriptedDoorWrapper;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptBlockHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.entity.data.DataTimers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class TileScriptedDoor extends TileDoor implements ITickable, IScriptBlockHandler {
    public List<ScriptContainer> scripts = new ArrayList<ScriptContainer>();
    public boolean shouldRefreshData = false;

    public String scriptLanguage = "ECMAScript";
    public boolean enabled = false;

    private IBlock blockDummy = null;
    public DataTimers timers = new DataTimers(this);

    public long lastInited = -1;

    private short ticksExisted = 0;

    public int newPower = 0; //used for block redstone event
    public int prevPower = 0; //used for block redstone event

    public float blockHardness = 5;
    public float blockResistance = 10;

    public IBlock getBlock() {
        if (blockDummy == null)
            blockDummy = new BlockScriptedDoorWrapper(getWorld(), getBlockType(), getPos());
        return blockDummy;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        setNBT(compound);
        timers.readFromNBT(compound);
    }

    public void setNBT(NBTTagCompound compound) {
        scripts = NBTTags.GetScript(compound.getTagList("Scripts", 10), this);
        scriptLanguage = compound.getString("ScriptLanguage");
        enabled = compound.getBoolean("ScriptEnabled");
        prevPower = compound.getInteger("BlockPrevPower");

        if (compound.hasKey("BlockHardness")) {
            blockHardness = compound.getFloat("BlockHardness");
            blockResistance = compound.getFloat("BlockResistance");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        getNBT(compound);
        timers.writeToNBT(compound);
        return super.writeToNBT(compound);
    }

    public NBTTagCompound getNBT(NBTTagCompound compound) {
        compound.setTag("Scripts", NBTTags.NBTScript(scripts));
        compound.setString("ScriptLanguage", scriptLanguage);
        compound.setBoolean("ScriptEnabled", enabled);
        compound.setInteger("BlockPrevPower", prevPower);
        compound.setFloat("BlockHardness", blockHardness);
        compound.setFloat("BlockResistance", blockResistance);
        return compound;
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

    private boolean isEnabled() {
        return enabled && ScriptController.HasStart && !world.isRemote;
    }

    @Override
    public void update() {
        super.update();
        ticksExisted++;

        if (prevPower != newPower) {
            EventHooks.onScriptBlockRedstonePower(this, prevPower, newPower);
            prevPower = newPower;
        }

        timers.update();
        if (ticksExisted >= 10) {
            EventHooks.onScriptBlockUpdate(this);
            ticksExisted = 0;
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
    public String noticeString() {
        BlockPos pos = getPos();
        return MoreObjects.toStringHelper(this).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
    }

    @Override
    public Map<Long, String> getConsoleText() {
        Map<Long, String> map = new TreeMap<Long, String>();
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
}
