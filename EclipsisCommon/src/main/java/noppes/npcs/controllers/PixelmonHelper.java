package noppes.npcs.controllers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.Loader;
import noppes.npcs.LogWriter;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PixelmonHelper {
    public static boolean Enabled = false;

    private static Constructor getPartyStorage;
    private static Constructor getPcStorage;
    private static Method getPokemonData;

    //Client
    private static Method getPixelmonModel = null;
    private static Class modelSetupClass;
    private static Method modelSetupMethod;

    public static void load() {
        Enabled = Loader.isModLoaded("pixelmon");
        if (!Enabled)
            return;

        try {
            Class c = Class.forName("com.pixelmonmod.pixelmon.api.storage.PartyStorage");
            getPartyStorage = c.getConstructor(UUID.class);

            c = Class.forName("com.pixelmonmod.pixelmon.api.storage.PCStorage");
            getPcStorage = c.getConstructor(UUID.class);

            c = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.Entity1Base");
            getPixelmonModel = c.getMethod("getPokemonData");
        } catch (Exception e) {
            LogWriter.except(e);
            Enabled = false;
        }
    }

    public static void loadClient() {
        if (!Enabled)
            return;

        try {
            Class c = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.Entity2Client");
            getPixelmonModel = c.getMethod("getModel");

            modelSetupClass = Class.forName("com.pixelmonmod.pixelmon.client.models.PixelmonModelSmd");
            modelSetupMethod = modelSetupClass.getMethod("setupForRender", c);
        } catch (Exception e) {
            LogWriter.except(e);
            Enabled = false;
        }
    }

    public static List<String> getPixelmonList() {
        List<String> list = new ArrayList<String>();
        if (!Enabled)
            return list;
        try {
            Class c = Class.forName("com.pixelmonmod.pixelmon.enums.EnumPokemonModel");
            Object[] array = c.getEnumConstants();
            for (Object ob : array)
                list.add(ob.toString());

        } catch (Exception e) {
            LogManager.getLogger().error("getPixelmonList", e);
        }
        return list;
    }

    public static boolean isPixelmon(Entity entity) {
        if (!Enabled)
            return false;
        String s = EntityList.getEntityString(entity);
        if (s == null)
            return false;
        return s.contains("Pixelmon");
    }

    public static String getName(EntityLivingBase entity) {
        if (!Enabled || !isPixelmon(entity))
            return "";
        try {
            Method m = entity.getClass().getMethod("getName");
            return m.invoke(entity).toString();
        } catch (Exception e) {
            LogManager.getLogger().error("getName", e);
        }
        return "";
    }

    public static Object getModel(EntityLivingBase entity) {
        try {
            return getPixelmonModel.invoke(entity);
        } catch (Exception e) {
            LogManager.getLogger().error("getModel", e);
        }
        return null;
    }

    public static void setupModel(EntityLivingBase entity, Object model) {
        try {
            if (modelSetupClass.isAssignableFrom(model.getClass())) {
                modelSetupMethod.invoke(model, entity);
            }
        } catch (Exception e) {
            LogManager.getLogger().error("setupModel", e);
        }
    }

    public static Object getPokemonData(Entity entity) {
        try {
            return getPokemonData.invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getParty(UUID uuid) {
        try {
            return getPartyStorage.newInstance(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getPc(UUID uuid) {
        try {
            return getPartyStorage.newInstance(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
