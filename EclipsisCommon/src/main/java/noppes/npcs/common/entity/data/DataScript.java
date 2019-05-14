package noppes.npcs.common.entity.data;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.EventHooks;
import noppes.npcs.util.NBTTags;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class DataScript implements IScriptHandler {
    private List<ScriptContainer> scripts = new ArrayList<ScriptContainer>();

    private String scriptLanguage = "ECMAScript";
    private EntityNPCInterface npc;
    private boolean enabled = false;

    public long lastInited = -1;

    public DataScript(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public void readFromNBT(NBTTagCompound compound) {
        scripts = NBTTags.GetScript(compound.getTagList("Scripts", 10), this);
        scriptLanguage = compound.getString("ScriptLanguage");
        enabled = compound.getBoolean("ScriptEnabled");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("Scripts", NBTTags.NBTScript(scripts));
        compound.setString("ScriptLanguage", scriptLanguage);
        compound.setBoolean("ScriptEnabled", enabled);
        return compound;
    }

    @Override
    public void runScript(EnumScriptType type, Event event) {
        if (!isEnabled())
            return;
        if (ScriptController.Instance.lastLoaded > lastInited) {
            lastInited = ScriptController.Instance.lastLoaded;
            if (type != EnumScriptType.INIT)
                EventHooks.onNPCInit(npc);
        }
        for (ScriptContainer script : scripts) {
            script.run(type, event);
        }
    }

    public boolean isEnabled() {
        return enabled && ScriptController.HasStart && !npc.world.isRemote;
    }

    @Override
    public boolean isClient() {
        return npc.isRemote();
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
        BlockPos pos = npc.getPosition();
        return MoreObjects.toStringHelper(npc).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
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
