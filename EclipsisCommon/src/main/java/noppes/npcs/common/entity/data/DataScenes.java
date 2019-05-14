package noppes.npcs.common.entity.data;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.common.entity.EntityProjectile;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.util.ValueUtil;

import java.util.*;

public class DataScenes {
    private EntityNPCInterface npc;

    public List<SceneContainer> scenes = new ArrayList<>();

    public static Map<String, SceneState> StartedScenes = new HashMap<>();
    public static List<SceneContainer> ScenesToRun = new ArrayList<>();

    private EntityLivingBase owner = null;
    private String ownerScene = null;

    public DataScenes(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (SceneContainer scene : scenes) {
            list.appendTag(scene.writeToNBT(new NBTTagCompound()));
        }
        compound.setTag("Scenes", list);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("Scenes", 10);
        List<SceneContainer> scenes = new ArrayList<>();
        for (int i = 0; i < list.tagCount(); i++) {
            SceneContainer scene = new SceneContainer();
            scene.readFromNBT(list.getCompoundTagAt(i));
            scenes.add(scene);
        }
        this.scenes = scenes;
    }

    public EntityLivingBase getOwner() {
        return owner;
    }

    public static void Toggle(ICommandSender sender, String id) {
        SceneState state = StartedScenes.get(id.toLowerCase());
        if (state == null || state.paused) {
            Start(sender, id);
        } else {
            state.paused = true;
            NoppesUtilServer.NotifyOPs("Paused scene %s at %s", id, state.ticks);
        }
    }

    public static void Start(ICommandSender sender, String id) {
        SceneState state = StartedScenes.get(id.toLowerCase());
        if (state == null) {
            NoppesUtilServer.NotifyOPs("Started scene %s", id);
            StartedScenes.put(id.toLowerCase(), new SceneState());
        } else if (state.paused) {
            state.paused = false;
            NoppesUtilServer.NotifyOPs("Started scene %s from %s", id, state.ticks);
        }

    }

    public static void Pause(ICommandSender sender, String id) {
        if (id == null) {
            for (SceneState state : StartedScenes.values()) {
                state.paused = true;
            }
            NoppesUtilServer.NotifyOPs("Paused all scenes");
        } else {
            SceneState state = StartedScenes.get(id.toLowerCase());
            state.paused = true;
            NoppesUtilServer.NotifyOPs("Paused scene %s at %s", id, state.ticks);
        }
    }

    public static void Reset(ICommandSender sender, String id) {
        if (id == null) {
            StartedScenes = new HashMap<>();
            NoppesUtilServer.NotifyOPs("Reset all scene");
        } else if (StartedScenes.remove(id.toLowerCase()) == null)
            sender.sendMessage(new TextComponentTranslation("Unknown scene %s ", id));
        else
            NoppesUtilServer.NotifyOPs("Reset scene %s", id);
    }

    public void update() {
        for (SceneContainer scene : scenes) {
            if (scene.validState())
                ScenesToRun.add(scene);
        }
        if (owner != null && !StartedScenes.containsKey(ownerScene.toLowerCase())) {
            owner = null;
            ownerScene = null;
        }
    }

    public static class SceneState {
        public boolean paused = false;
        public int ticks = -1;
    }

    public class SceneContainer {
        public int btn = 0;
        public String name = "";
        public String lines = "";
        public boolean enabled = false;

        public int ticks = -1;
        private SceneState state = null;

        private List<SceneEvent> events = new ArrayList<>();

        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            compound.setBoolean("Enabled", enabled);
            compound.setString("Name", name);
            compound.setString("Lines", lines);
            compound.setInteger("Button", btn);
            compound.setInteger("Ticks", ticks);
            return compound;
        }

        public boolean validState() {
            if (!enabled)
                return false;
            if (state != null) {
                if (StartedScenes.containsValue(state))
                    return !state.paused;
                state = null;
            }
            state = StartedScenes.get(name.toLowerCase());
            if (state == null)
                state = StartedScenes.get(btn + "btn");
            if (state != null)
                return !state.paused;
            return false;
        }

        public void readFromNBT(NBTTagCompound compound) {
            enabled = compound.getBoolean("Enabled");
            name = compound.getString("Name");
            lines = compound.getString("Lines");
            btn = compound.getInteger("Button");
            ticks = compound.getInteger("Ticks");

            ArrayList<SceneEvent> events = new ArrayList<>();
            for (String line : lines.split("\r\n|\r|\n")) {
                SceneEvent event = SceneEvent.parse(line);
                if (event != null)
                    events.add(event);
            }
            Collections.sort(events);
            this.events = events;
        }

        public void update() {
            if (!enabled || events.isEmpty() || state == null)
                return;
            for (SceneEvent event : events) {
                if (event.ticks > state.ticks)
                    break;
                if (event.ticks == state.ticks) {
                    try {
                        handle(event);
                    } catch (Exception e) {

                    }
                }
            }
            ticks = state.ticks;
        }

        private void handle(SceneEvent event) throws Exception {
            if (event.type == SceneType.MOVE) {
                String[] param = event.param.split(" ");
                while (param.length > 1) {
                    boolean move = false;
                    if (param[0].startsWith("to")) {
                        move = true;
                    } else if (!param[0].startsWith("tp")) {
                        break;
                    }

                    BlockPos pos = null;
                    if (param[0].startsWith("@")) {
                        EntityLivingBase entitylivingbase = CommandBase.getEntity(npc.getServer(), npc, param[0], EntityLivingBase.class);
                        if (entitylivingbase != null)
                            pos = entitylivingbase.getPosition();
                        param = Arrays.copyOfRange(param, 2, param.length);
                    } else if (param.length < 4) {
                        return;
                    } else {
                        pos = CommandBase.parseBlockPos(npc, param, 1, false);
                        param = Arrays.copyOfRange(param, 4, param.length);
                    }
                    if (pos == null)
                        continue;
                    npc.ais.setStartPos(pos);
                    npc.getNavigator().clearPath();
                    if (move) {
                        Path pathentity = npc.getNavigator().getPathToPos(pos);
                        npc.getNavigator().setPath(pathentity, 1);
                    } else if (!npc.isInRange(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 2))
                        npc.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                }
            } else if (event.type == SceneType.SAY) {
                npc.saySurrounding(new Line(event.param));
            } else if (event.type == SceneType.ROTATE) {
                npc.lookAi.resetTask();
                if (event.param.startsWith("@")) {
                    EntityLivingBase entitylivingbase = CommandBase.getEntity(npc.getServer(), npc, event.param, EntityLivingBase.class);
                    npc.lookAi.rotate(npc.world.getClosestPlayerToEntity(entitylivingbase, 30));
                } else {
                    npc.lookAi.rotate(Integer.parseInt(event.param));
                }
            } else if (event.type == SceneType.EQUIP) {
                String[] args = event.param.split(" ");
                if (args.length < 2)
                    return;
                IItemStack itemstack = null;
                if (!args[1].equalsIgnoreCase("none")) {
                    Item item = CommandBase.getItemByText(npc, args[1]);
                    int i = args.length >= 3 ? CommandBase.parseInt(args[2], 1, 64) : 1;
                    int j = args.length >= 4 ? CommandBase.parseInt(args[3]) : 0;
                    itemstack = NpcAPI.instance().getIItemStack(new ItemStack(item, i, j));
                }

                if (args[0].equalsIgnoreCase("main"))
                    npc.inventory.weapons.put(0, itemstack);
                else if (args[0].equalsIgnoreCase("off"))
                    npc.inventory.weapons.put(2, itemstack);
                else if (args[0].equalsIgnoreCase("proj"))
                    npc.inventory.weapons.put(1, itemstack);
                else if (args[0].equalsIgnoreCase("head"))
                    npc.inventory.armor.put(0, itemstack);
                else if (args[0].equalsIgnoreCase("body"))
                    npc.inventory.armor.put(1, itemstack);
                else if (args[0].equalsIgnoreCase("legs"))
                    npc.inventory.armor.put(2, itemstack);
                else if (args[0].equalsIgnoreCase("boots"))
                    npc.inventory.armor.put(3, itemstack);
            } else if (event.type == SceneType.ATTACK) {
                if (event.param.equals("none"))
                    npc.setAttackTarget(null);
                else {
                    EntityLivingBase entity = CommandBase.getEntity(npc.getServer(), npc, event.param, EntityLivingBase.class);
                    if (entity != null)
                        npc.setAttackTarget(entity);
                }
            } else if (event.type == SceneType.THROW) {
                String[] args = event.param.split(" ");
                EntityLivingBase entity = CommandBase.getEntity(npc.getServer(), npc, args[0], EntityLivingBase.class);
                if (entity == null)
                    return;
                float damage = Float.parseFloat(args[1]);
                if (damage <= 0)
                    damage = 0.01f;
                ItemStack stack = ItemStackWrapper.MCItem(npc.inventory.getProjectile());
                if (args.length > 2) {
                    Item item = CommandBase.getItemByText(npc, args[2]);
                    stack = new ItemStack(item, 1, 0);
                }
                EntityProjectile projectile = npc.shoot(entity, 100, stack, false);
                projectile.damage = damage;
            } else if (event.type == SceneType.ANIMATE) {
                npc.animateAi.temp = AnimationType.NORMAL;
                if (event.param.equalsIgnoreCase("sleep"))
                    npc.animateAi.temp = AnimationType.SLEEP;
                else if (event.param.equalsIgnoreCase("sneak"))
                    npc.ais.animationType = AnimationType.SNEAK;
                else if (event.param.equalsIgnoreCase("normal"))
                    npc.ais.animationType = AnimationType.NORMAL;
                else if (event.param.equalsIgnoreCase("sit"))
                    npc.animateAi.temp = AnimationType.SIT;
                else if (event.param.equalsIgnoreCase("crawl"))
                    npc.ais.animationType = AnimationType.CRAWL;
                else if (event.param.equalsIgnoreCase("bow"))
                    npc.animateAi.temp = AnimationType.BOW;
                else if (event.param.equalsIgnoreCase("yes"))
                    npc.animateAi.temp = AnimationType.YES;
                else if (event.param.equalsIgnoreCase("no"))
                    npc.animateAi.temp = AnimationType.NO;
            } else if (event.type == SceneType.COMMAND) {
                NoppesUtilServer.runCommand(npc, npc.getName(), event.param, null);
            } else if (event.type == SceneType.STATS) {
                int i = event.param.indexOf(" ");
                if (i <= 0)
                    return;
                String type = event.param.substring(0, i).toLowerCase();
                String value = event.param.substring(i).trim();
                try {
                    if (type.equals("walking_speed")) {
                        npc.ais.setWalkingSpeed(ValueUtil.CorrectInt(Integer.parseInt(value), 0, 10));
                    } else if (type.equals("size")) {
                        npc.display.setSize(ValueUtil.CorrectInt(Integer.parseInt(value), 1, 30));
                    } else {
                        NoppesUtilServer.NotifyOPs("Unknown scene stat: " + type);
                    }
                } catch (NumberFormatException e) {
                    NoppesUtilServer.NotifyOPs("Unknown scene stat " + type + " value: " + value);
                }

            } else if (event.type == SceneType.FACTION) {
                npc.setFaction(Integer.parseInt(event.param));
            } else if (event.type == SceneType.FOLLOW) {
                if (event.param.equalsIgnoreCase("none")) {
                    owner = null;
                    ownerScene = null;
                } else {
                    EntityLivingBase entity = CommandBase.getEntity(npc.getServer(), npc, event.param, EntityLivingBase.class);
                    if (entity == null)
                        return;
                    owner = entity;
                    ownerScene = name;
                }
            }
        }
    }

    public static class SceneEvent implements Comparable<SceneEvent> {
        public int ticks = 0;
        public SceneType type;
        public String param = "";


        @Override
        public String toString() {
            return ticks + " " + type.name() + " " + param;
        }

        public static SceneEvent parse(String str) {
            SceneEvent event = new SceneEvent();
            int i = str.indexOf(" ");
            if (i <= 0)
                return null;
            try {
                event.ticks = Integer.parseInt(str.substring(0, i));
                str = str.substring(i + 1);
            } catch (NumberFormatException ex) {
                return null;
            }
            i = str.indexOf(" ");
            if (i <= 0)
                return null;
            String name = str.substring(0, i);
            for (SceneType type : SceneType.values()) {
                if (name.equalsIgnoreCase(type.name()))
                    event.type = type;
            }
            if (event.type == null)
                return null;
            event.param = str.substring(i + 1);

            return event;

        }

        @Override
        public int compareTo(SceneEvent o) {
            return ticks - o.ticks;
        }
    }

    public enum SceneType {
        ANIMATE, MOVE, FACTION, COMMAND, EQUIP, THROW, ATTACK, FOLLOW, SAY, ROTATE, STATS
    }

    public void addScene(String name) {
        if (name.isEmpty())
            return;
        SceneContainer scene = new SceneContainer();
        scene.name = name;
        scenes.add(scene);
    }
}
