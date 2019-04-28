package noppes.npcs.controllers;

import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.schematics.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SchematicController {
    public static SchematicController Instance = new SchematicController();

    private SchematicWrapper building = null;
    private ICommandSender buildStarter = null;
    private int buildingPercentage = 0;

    public List<String> included = Arrays.asList("archery_range.schematic", "bakery.schematic", "barn.schematic", "building_site.schematic", "chapel.schematic", "church.schematic", "gate.schematic", "glassworks.schematic", "guard_Tower.schematic", "guild_house.schematic",
            "house.schematic", "house_small.schematic", "inn.schematic", "library.schematic", "lighthouse.schematic", "mill.schematic", "observatory.schematic", "ship.schematic", "shop.schematic", "stall.schematic", "stall2.schematic", "stall3.schematic",
            "tier_house1.schematic", "tier_house2.schematic", "tier_house3.schematic", "tower.schematic", "wall.schematic", "wall_corner.schematic");


    public List<String> list() {
        List<String> list = new ArrayList<>();
        list.addAll(included);
        for (File file : getDir().listFiles()) {
            String name = file.getName();
            if (name.toLowerCase().endsWith(".schematic") || name.toLowerCase().endsWith(".blueprint")) {
                list.add(name);
            }
        }
        Collections.sort(list);
        return list;
    }

    public File getDir() {
        File dir = new File(CustomNpcs.INSTANCE.getWorldSaveDirectory(), "schematics");
        if (!dir.exists())
            dir.mkdir();

        return dir;
    }

    public void info(ICommandSender sender) {
        if (building == null) {
            sendMessage(sender, "Nothing is being build");
        } else {
            sendMessage(sender, "Already building: " + building.schema.getName() + " - " + building.getPercentage() + "%");
            if (buildStarter != null)
                sendMessage(sender, "Build started by: " + buildStarter.getName());
        }
    }

    private void sendMessage(ICommandSender sender, String message) {
        if (sender == null)
            return;
        sender.sendMessage(new TextComponentString(message));
    }


    public void stop(ICommandSender sender) {
        if (building == null || !building.isBuilding) {
            sendMessage(sender, "Not building");
        } else {
            sendMessage(sender, "Stopped building: " + building.schema.getName());
            building = null;
        }

    }

    public void build(SchematicWrapper schem, ICommandSender sender) {
        if (building != null && building.isBuilding) {
            info(sender);
            return;
        }
        buildingPercentage = 0;
        building = schem;
        building.isBuilding = true;

        buildStarter = sender;
    }

    public void updateBuilding() {
        if (building == null)
            return;
        building.build();
        if (buildStarter != null && building.getPercentage() - buildingPercentage >= 10) {
            sendMessage(buildStarter, "Building at " + building.getPercentage() + "%");
            buildingPercentage = building.getPercentage();
        }
        if (!building.isBuilding) {
            if (buildStarter != null)
                sendMessage(buildStarter, "Building finished");
            building = null;
        }
    }

    public SchematicWrapper load(String name) {
        InputStream stream = null;
        if (included.contains(name)) {
            stream = MinecraftServer.class.getResourceAsStream("/assets/customnpcs/schematics/" + name);
        }
        if (stream == null) {
            File file = new File(getDir(), name);
            if (!file.exists()) {
                return null;
            }
            try {
                stream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                return null;
            }
        }
        try {
            NBTTagCompound compound = CompressedStreamTools.readCompressed(stream);
            stream.close();
            if (name.toLowerCase().endsWith(".blueprint")) {
                Blueprint bp = BlueprintUtil.readBlueprintFromNBT(compound);
                bp.setName(name);
                return new SchematicWrapper(bp);
            }
            Schematic schema = new Schematic(name);
            schema.load(compound);
            return new SchematicWrapper(schema);
        } catch (IOException e) {
            LogWriter.except(e);
        }
        return null;
    }

    public void save(ICommandSender sender, String name, int type, BlockPos pos, short height, short width, short length) {
        name = name.replace(" ", "_");
        if (included.contains(name))
            return;

        World world = sender.getEntityWorld();
        File file = null;
        ISchematic schema = null;
        if (type == 0) {
            file = new File(getDir(), name + ".schematic");
            schema = Schematic.Create(world, name, pos, height, width, length);
        } else if (type == 1) {
            file = new File(getDir(), name + ".blueprint");
            schema = BlueprintUtil.createBlueprint(world, pos, width, length, height);
        }

        NoppesUtilServer.NotifyOPs("Schematic " + name + " succesfully created");
        try {
            CompressedStreamTools.writeCompressed(schema.getNBT(), new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
