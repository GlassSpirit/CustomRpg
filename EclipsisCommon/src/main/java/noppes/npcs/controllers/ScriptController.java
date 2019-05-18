package noppes.npcs.controllers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.api.wrapper.WorldWrapper;
import noppes.npcs.controllers.data.ForgeScriptData;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.NBTJsonUtil.JsonException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptController {

    public static ScriptController Instance;
    public static boolean HasStart = false;
    private ScriptEngineManager manager;
    public Map<String, String> languages = new HashMap<String, String>();
    public Map<String, ScriptEngineFactory> factories = new HashMap<String, ScriptEngineFactory>();
    public Map<String, String> scripts = new HashMap<String, String>();
    public PlayerScriptData playerScripts = new PlayerScriptData(null);
    public ForgeScriptData forgeScripts = new ForgeScriptData();
    public long lastLoaded = 0;
    public long lastPlayerUpdate = 0;
    public File dir;
    public NBTTagCompound compound = new NBTTagCompound();

    private boolean loaded = false;
    public boolean shouldSave = false;

    public ScriptController() {
        loaded = false;
        Instance = this;
        System.setProperty("nashorn.args", CustomNpcs.NashorArguments);
        manager = new ScriptEngineManager();
        try {
            Class c = Class.forName("org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory");
            ScriptEngineFactory factory = (ScriptEngineFactory) c.newInstance();
            factory.getScriptEngine();
            manager.registerEngineName("kotlin", factory);
            manager.registerEngineExtension("ktl", factory);
            manager.registerEngineMimeType("application/kotlin", factory);
            languages.put(factory.getLanguageName(), ".ktl");
            factories.put(factory.getLanguageName().toLowerCase(), factory);
        } catch (Throwable e) {

        }
        LogWriter.info("Script Engines Available:");
        for (ScriptEngineFactory fac : manager.getEngineFactories()) {
            try {
                if (fac.getExtensions().isEmpty())
                    continue;

                if (!(fac.getScriptEngine() instanceof Invocable) && !fac.getLanguageName().equals("lua"))
                    continue;
                String ext = "." + fac.getExtensions().get(0).toLowerCase();
                LogWriter.info(fac.getLanguageName() + ": " + ext);
                languages.put(fac.getLanguageName(), ext);
                factories.put(fac.getLanguageName().toLowerCase(), fac);
            } catch (Throwable e) {

            }
        }
    }

    public void loadCategories() {
        dir = new File(CustomNpcs.getWorldSaveDirectory(), "scripts");
        if (!dir.exists())
            dir.mkdirs();
        if (!worldDataFile().exists())
            shouldSave = true;
        WorldWrapper.tempData.clear();

        scripts.clear();
        for (String language : languages.keySet()) {
            String ext = languages.get(language);
            File scriptDir = new File(dir, language.toLowerCase());
            if (!scriptDir.exists())
                scriptDir.mkdir();
            else
                loadDir(scriptDir, "", ext);
        }
        lastLoaded = System.currentTimeMillis();
    }

    private void loadDir(File dir, String name, String ext) {
        for (File file : dir.listFiles()) {
            String filename = name + file.getName().toLowerCase();
            if (file.isDirectory()) {
                loadDir(file, filename + "/", ext);
                continue;
            }
            if (!filename.endsWith(ext))
                continue;
            try {
                scripts.put(filename, readFile(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean loadStoredData() {
        this.compound = new NBTTagCompound();
        File file = worldDataFile();
        try {
            if (!file.exists())
                return false;
            this.compound = NBTJsonUtil.LoadFile(file);
            shouldSave = false;
        } catch (Exception e) {
            LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
            return false;
        }
        return true;
    }

    private File worldDataFile() {
        return new File(dir, "world_data.json");
    }

    private File playerScriptsFile() {
        return new File(dir, "player_scripts.json");
    }

    private File forgeScriptsFile() {
        return new File(dir, "forge_scripts.json");
    }

    public boolean loadPlayerScripts() {
        this.playerScripts.clear();
        File file = playerScriptsFile();
        try {
            if (!file.exists())
                return false;
            this.playerScripts.readFromNBT(NBTJsonUtil.LoadFile(file));
        } catch (Exception e) {
            LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
            return false;
        }
        return true;
    }

    public void setPlayerScripts(NBTTagCompound compound) {
        this.playerScripts.readFromNBT(compound);

        File file = playerScriptsFile();
        try {
            NBTJsonUtil.SaveFile(file, compound);
            lastPlayerUpdate = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonException e) {
            e.printStackTrace();
        }
    }

    public boolean loadForgeScripts() {
        this.forgeScripts.clear();
        File file = forgeScriptsFile();
        try {
            if (!file.exists())
                return false;
            this.forgeScripts.readFromNBT(NBTJsonUtil.LoadFile(file));
        } catch (Exception e) {
            LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
            return false;
        }
        return true;
    }

    public void setForgeScripts(NBTTagCompound compound) {
        this.forgeScripts.readFromNBT(compound);

        File file = forgeScriptsFile();
        try {
            NBTJsonUtil.SaveFile(file, compound);
            this.forgeScripts.lastInited = -1;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonException e) {
            e.printStackTrace();
        }
    }

    private String readFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    public ScriptEngine getEngineByName(String language) {
        ScriptEngineFactory fac = factories.get(language.toLowerCase());
        if (fac == null)
            return null;
        return fac.getScriptEngine();
    }

    public NBTTagList nbtLanguages() {
        NBTTagList list = new NBTTagList();
        for (String language : languages.keySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            NBTTagList scripts = new NBTTagList();
            for (String script : getScripts(language)) {
                scripts.appendTag(new NBTTagString(script));
            }
            compound.setTag("Scripts", scripts);
            compound.setString("Language", language);
            list.appendTag(compound);
        }
        return list;
    }

    private List<String> getScripts(String language) {
        List<String> list = new ArrayList<String>();
        String ext = languages.get(language);
        if (ext == null)
            return list;
        for (String script : scripts.keySet()) {
            if (script.endsWith(ext)) {
                list.add(script);
            }
        }
        return list;
    }

    @SubscribeEvent
    public void saveWorld(WorldEvent.Save event) {
        if (!shouldSave || event.getWorld().isRemote || event.getWorld() != event.getWorld().getMinecraftServer().worlds[0])
            return;

        try {
            NBTJsonUtil.SaveFile(worldDataFile(), compound.copy());
        } catch (Exception e) {
            LogWriter.except(e);
        }

        shouldSave = false;
    }
}
