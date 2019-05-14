package noppes.npcs;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.relauncher.Side;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.event.ForgeEvent;
import noppes.npcs.api.event.ItemEvent;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.common.CustomNpcs;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.common.objects.NpcObjects;
import noppes.npcs.common.objects.items.ItemNbtBook;
import noppes.npcs.common.objects.items.ItemScripted;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerScriptData;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScriptPlayerEventHandler {

    @SubscribeEvent
    public void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.side != Side.SERVER || event.phase != Phase.START)
            return;
        EntityPlayer player = event.player;
        PlayerData data = PlayerData.get(player);

        if (player.ticksExisted % 10 == 0) {
            EventHooks.onPlayerTick(data.scriptData);
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack item = player.inventory.getStackInSlot(i);
                if (!item.isEmpty() && item.getItem() == NpcObjects.scriptedItem) {
                    ItemScriptedWrapper isw = (ItemScriptedWrapper) NpcAPI.instance().getIItemStack(item);
                    EventHooks.onScriptItemUpdate(isw, player);
                    if (isw.updateClient) {
                        isw.updateClient = false;
                        Server.sendData((EntityPlayerMP) player, EnumPacketClient.UPDATE_ITEM, i, isw.getMCNbt());
                    }
                }
            }
        }
        if (data.playerLevel != player.experienceLevel) {
            EventHooks.onPlayerLevelUp(data.scriptData, data.playerLevel - player.experienceLevel);
            data.playerLevel = player.experienceLevel;
        }
        data.timers.update();
    }

    @SubscribeEvent
    public void invoke(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntityPlayer().world.isRemote || event.getHand() != EnumHand.MAIN_HAND || !(event.getWorld() instanceof WorldServer))
            return;

        PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
        PlayerEvent.AttackEvent ev = new PlayerEvent.AttackEvent(handler.getPlayer(), 2, NpcAPI.instance().getIBlock(event.getWorld(), event.getPos()));
        event.setCanceled(EventHooks.onPlayerAttack(handler, ev));

        if (event.getItemStack().getItem() == NpcObjects.scriptedItem && !event.isCanceled()) {
            ItemScriptedWrapper isw = ItemScripted.Companion.getWrapper(event.getItemStack());
            ItemEvent.AttackEvent eve = new ItemEvent.AttackEvent(isw, handler.getPlayer(), 2, NpcAPI.instance().getIBlock(event.getWorld(), event.getPos()));
            eve.setCanceled(event.isCanceled());
            event.setCanceled(EventHooks.onScriptItemAttack(isw, eve));
        }
    }

    @SubscribeEvent
    public void invoke(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntityPlayer().world.isRemote || event.getHand() != EnumHand.MAIN_HAND || !(event.getWorld() instanceof WorldServer))
            return;
        if (event.getItemStack().getItem() == NpcObjects.nbtBook) {
            ((ItemNbtBook) event.getItemStack().getItem()).blockEvent(event);
            event.setCanceled(true);
            return;
        }

        PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
        handler.hadInteract = true;
        PlayerEvent.InteractEvent ev = new PlayerEvent.InteractEvent(handler.getPlayer(), 2, NpcAPI.instance().getIBlock(event.getWorld(), event.getPos()));
        event.setCanceled(EventHooks.onPlayerInteract(handler, ev));

        if (event.getItemStack().getItem() == NpcObjects.scriptedItem && !event.isCanceled()) {
            ItemScriptedWrapper isw = ItemScripted.Companion.getWrapper(event.getItemStack());
            ItemEvent.InteractEvent eve = new ItemEvent.InteractEvent(isw, handler.getPlayer(), 2, NpcAPI.instance().getIBlock(event.getWorld(), event.getPos()));
            event.setCanceled(EventHooks.onScriptItemInteract(isw, eve));
        }
    }

    @SubscribeEvent
    public void invoke(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntityPlayer().world.isRemote || event.getHand() != EnumHand.MAIN_HAND || !(event.getWorld() instanceof WorldServer))
            return;
        if (event.getItemStack().getItem() == NpcObjects.nbtBook) {
            ((ItemNbtBook) event.getItemStack().getItem()).entityEvent(event);
            event.setCanceled(true);
            return;
        }
        PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
        PlayerEvent.InteractEvent ev = new PlayerEvent.InteractEvent(handler.getPlayer(), 1, NpcAPI.instance().getIEntity(event.getTarget()));
        event.setCanceled(EventHooks.onPlayerInteract(handler, ev));

        if (event.getItemStack().getItem() == NpcObjects.scriptedItem && !event.isCanceled()) {
            ItemScriptedWrapper isw = ItemScripted.Companion.getWrapper(event.getItemStack());
            ItemEvent.InteractEvent eve = new ItemEvent.InteractEvent(isw, handler.getPlayer(), 1, NpcAPI.instance().getIEntity(event.getTarget()));
            event.setCanceled(EventHooks.onScriptItemInteract(isw, eve));
        }
    }

    @SubscribeEvent
    public void invoke(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntityPlayer().world.isRemote || event.getHand() != EnumHand.MAIN_HAND || !(event.getWorld() instanceof WorldServer))
            return;

        if (event.getEntityPlayer().isCreative() && event.getEntityPlayer().isSneaking() && event.getItemStack().getItem() == NpcObjects.scriptedItem) {
            NoppesUtilServer.sendOpenGui(event.getEntityPlayer(), EnumGuiType.ScriptItem, null);
            return;
        }
        PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
        if (handler.hadInteract) {
            handler.hadInteract = false;
            return;
        }
        PlayerEvent.InteractEvent ev = new PlayerEvent.InteractEvent(handler.getPlayer(), 0, null);
        event.setCanceled(EventHooks.onPlayerInteract(handler, ev));

        if (event.getItemStack().getItem() == NpcObjects.scriptedItem && !event.isCanceled()) {
            ItemScriptedWrapper isw = ItemScripted.Companion.getWrapper(event.getItemStack());
            ItemEvent.InteractEvent eve = new ItemEvent.InteractEvent(isw, handler.getPlayer(), 0, null);
            event.setCanceled(EventHooks.onScriptItemInteract(isw, eve));
        }
    }

    @SubscribeEvent
    public void invoke(ArrowLooseEvent event) {
        if (event.getEntityPlayer().world.isRemote || !(event.getWorld() instanceof WorldServer))
            return;
        PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
        PlayerEvent.RangedLaunchedEvent ev = new PlayerEvent.RangedLaunchedEvent(handler.getPlayer());
        event.setCanceled(EventHooks.onPlayerRanged(handler, ev));
    }

    @SubscribeEvent
    public void invoke(BlockEvent.BreakEvent event) {
        if (event.getPlayer().world.isRemote || !(event.getWorld() instanceof WorldServer))
            return;
        PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
        PlayerEvent.BreakEvent ev = new PlayerEvent.BreakEvent(handler.getPlayer(),
                NpcAPI.instance().getIBlock(event.getWorld(), event.getPos()), event.getExpToDrop());
        event.setCanceled(EventHooks.onPlayerBreak(handler, ev));
        event.setExpToDrop(ev.exp);
    }

    @SubscribeEvent
    public void invoke(ItemTossEvent event) {
        if (!(event.getPlayer().world instanceof WorldServer))
            return;
        PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
        event.setCanceled(EventHooks.onPlayerToss(handler, event.getEntityItem()));
    }

    @SubscribeEvent
    public void invoke(EntityItemPickupEvent event) {
        if (!(event.getEntityPlayer().world instanceof WorldServer))
            return;
        PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
        event.setCanceled(EventHooks.onPlayerPickUp(handler, event.getItem()));
    }

    @SubscribeEvent
    public void invoke(PlayerContainerEvent.Open event) {
        if (!(event.getEntityPlayer().world instanceof WorldServer))
            return;
        PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
        EventHooks.onPlayerContainerOpen(handler, event.getContainer());
    }

    @SubscribeEvent
    public void invoke(PlayerContainerEvent.Close event) {
        if (!(event.getEntityPlayer().world instanceof WorldServer))
            return;
        PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
        EventHooks.onPlayerContainerClose(handler, event.getContainer());
    }

    @SubscribeEvent
    public void invoke(LivingDeathEvent event) {
        if (!(event.getEntityLiving().world instanceof WorldServer))
            return;
        Entity source = NoppesUtilServer.GetDamageSourcee(event.getSource());
        if (event.getEntityLiving() instanceof EntityPlayer) {
            PlayerScriptData handler = PlayerData.get((EntityPlayer) event.getEntityLiving()).scriptData;
            EventHooks.onPlayerDeath(handler, event.getSource(), source);
        }
        if (source instanceof EntityPlayer) {
            PlayerScriptData handler = PlayerData.get((EntityPlayer) source).scriptData;
            EventHooks.onPlayerKills(handler, event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public void invoke(LivingHurtEvent event) {
        if (!(event.getEntityLiving().world instanceof WorldServer))
            return;
        Entity source = NoppesUtilServer.GetDamageSourcee(event.getSource());
        if (event.getEntityLiving() instanceof EntityPlayer) {
            PlayerScriptData handler = PlayerData.get((EntityPlayer) event.getEntityLiving()).scriptData;
            PlayerEvent.DamagedEvent pevent = new PlayerEvent.DamagedEvent(handler.getPlayer(), source, event.getAmount(), event.getSource());
            event.setCanceled(EventHooks.onPlayerDamaged(handler, pevent));
            event.setAmount(pevent.damage);
        }

        if (source instanceof EntityPlayer) {
            PlayerScriptData handler = PlayerData.get((EntityPlayer) source).scriptData;
            PlayerEvent.DamagedEntityEvent pevent = new PlayerEvent.DamagedEntityEvent(handler.getPlayer(), event.getEntityLiving(), event.getAmount(), event.getSource());
            event.setCanceled(EventHooks.onPlayerDamagedEntity(handler, pevent));
            event.setAmount(pevent.damage);
        }
    }

    @SubscribeEvent
    public void invoke(LivingAttackEvent event) {
        if (!(event.getEntityLiving().world instanceof WorldServer))
            return;
        Entity source = NoppesUtilServer.GetDamageSourcee(event.getSource());

        if (source instanceof EntityPlayer) {
            PlayerScriptData handler = PlayerData.get((EntityPlayer) source).scriptData;
            ItemStack item = ((EntityPlayer) source).getHeldItemMainhand();
            IEntity target = NpcAPI.instance().getIEntity(event.getEntityLiving());
            PlayerEvent.AttackEvent ev = new PlayerEvent.AttackEvent(handler.getPlayer(), 1, target);
            event.setCanceled(EventHooks.onPlayerAttack(handler, ev));
            if (item.getItem() == NpcObjects.scriptedItem && !event.isCanceled()) {
                ItemScriptedWrapper isw = ItemScripted.Companion.getWrapper(item);
                ItemEvent.AttackEvent eve = new ItemEvent.AttackEvent(isw, handler.getPlayer(), 1, target);
                eve.setCanceled(event.isCanceled());
                event.setCanceled(EventHooks.onScriptItemAttack(isw, eve));
            }
        }
    }

    @SubscribeEvent
    public void invoke(PlayerLoggedInEvent event) {
        if (!(event.player.world instanceof WorldServer))
            return;
        PlayerScriptData handler = PlayerData.get(event.player).scriptData;
        EventHooks.onPlayerLogin(handler);
    }

    @SubscribeEvent
    public void invoke(PlayerLoggedOutEvent event) {
        if (!(event.player.world instanceof WorldServer))
            return;
        PlayerScriptData handler = PlayerData.get(event.player).scriptData;
        EventHooks.onPlayerLogout(handler);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void invoke(ServerChatEvent event) {
        if (!(event.getPlayer().world instanceof WorldServer) || event.getPlayer() == EntityNPCInterface.ChatEventPlayer)
            return;
        PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
        String message = event.getMessage();
        PlayerEvent.ChatEvent ev = new PlayerEvent.ChatEvent(handler.getPlayer(), event.getMessage());
        EventHooks.onPlayerChat(handler, ev);
        event.setCanceled(ev.isCanceled());
        if (!message.equals(ev.message)) {
            TextComponentTranslation chat = new TextComponentTranslation("");
            chat.appendSibling(ForgeHooks.newChatWithLinks(ev.message));
            event.setComponent(chat);

        }
    }

    class ForgeEventHandler {
        @SubscribeEvent
        public void forgeEntity(Event event) {
            if (CustomNpcs.INSTANCE.getServer() == null || !ScriptController.Instance.forgeScripts.isEnabled()) {
                return;
            }

            if (event instanceof EntityEvent) {
                EntityEvent ev = (EntityEvent) event;
                if (ev.getEntity() == null || !(ev.getEntity().world instanceof WorldServer))
                    return;
                EventHooks.onForgeEntityEvent(ev);
                return;
            }
            if (event instanceof WorldEvent) {
                WorldEvent ev = (WorldEvent) event;
                if (!(ev.getWorld() instanceof WorldServer)) {
                    return;
                }
                EventHooks.onForgeWorldEvent(ev);
                return;
            }
            if (event instanceof TickEvent) {
                if (((TickEvent) event).side == Side.CLIENT)
                    return;
            }
            if (event instanceof net.minecraftforge.fml.common.gameevent.PlayerEvent) {
                net.minecraftforge.fml.common.gameevent.PlayerEvent ev = (net.minecraftforge.fml.common.gameevent.PlayerEvent) event;
                if (!(ev.player.world instanceof WorldServer))
                    return;
            }
            EventHooks.onForgeEvent(new ForgeEvent(event), event);
        }
    }

    public ScriptPlayerEventHandler registerForgeEvents() {
        ForgeEventHandler handler = new ForgeEventHandler();
        try {
            Method m = handler.getClass().getMethod("forgeEntity", Event.class);
            Method register = MinecraftForge.EVENT_BUS.getClass().getDeclaredMethod("register", Class.class, Object.class, Method.class, ModContainer.class);
            register.setAccessible(true);
            List<ClassInfo> list = new ArrayList<>(ClassPath.from(this.getClass().getClassLoader()).getTopLevelClassesRecursive("net.minecraftforge.event"));
            list.addAll(ClassPath.from(this.getClass().getClassLoader()).getTopLevelClassesRecursive("net.minecraftforge.fml.common"));
            for (ClassInfo info : list) {
                String name = info.getName();
                if (name.startsWith("net.minecraftforge.event.terraingen")) {
                    continue;
                }
                Class infoClass = info.load();
                List<Class> classes = new ArrayList<>(Arrays.asList(infoClass.getDeclaredClasses()));
                if (classes.isEmpty()) {
                    classes.add(infoClass);
                }
                for (Class c : classes) {
                    if (GenericEvent.class.isAssignableFrom(c) || EntityEvent.EntityConstructing.class.isAssignableFrom(c) || WorldEvent.PotentialSpawns.class.isAssignableFrom(c)
                            || TickEvent.RenderTickEvent.class.isAssignableFrom(c) || TickEvent.ClientTickEvent.class.isAssignableFrom(c)
                            || GetCollisionBoxesEvent.class.isAssignableFrom(c) || ClientCustomPacketEvent.class.isAssignableFrom(c) || ItemTooltipEvent.class.isAssignableFrom(c)) {
                        continue;
                    }
                    if (Event.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers()) && Modifier.isPublic(c.getModifiers())) {
                        register.invoke(MinecraftForge.EVENT_BUS, c, handler, m, Loader.instance().activeModContainer());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
}
