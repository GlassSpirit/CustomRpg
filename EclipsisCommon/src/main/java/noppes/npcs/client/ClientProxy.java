package noppes.npcs.client;


import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabFactions;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabQuests;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabVanilla;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import noppes.npcs.*;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemScripted;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.controllers.PresetController;
import noppes.npcs.client.fx.EntityEnderFX;
import noppes.npcs.client.gui.*;
import noppes.npcs.client.gui.global.*;
import noppes.npcs.client.gui.mainmenu.*;
import noppes.npcs.client.gui.player.*;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionInv;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionStats;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionTalents;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeItem;
import noppes.npcs.client.gui.roles.*;
import noppes.npcs.client.gui.script.*;
import noppes.npcs.client.model.*;
import noppes.npcs.client.renderer.*;
import noppes.npcs.config.TrueTypeFont;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.*;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.*;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class ClientProxy extends CommonProxy {
    public static PlayerData playerData = new PlayerData();

    public static KeyBinding QuestLog;

    public static KeyBinding Scene1;
    public static KeyBinding SceneReset;
    public static KeyBinding Scene2;
    public static KeyBinding Scene3;

    public static FontContainer Font;

    @Override
    public void load() {
        Font = new FontContainer(CustomNpcs.FontType, CustomNpcs.FontSize);
        createFolders();
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new CustomNpcResourceListener());

        CustomNpcs.Channel.register(new PacketHandlerClient());
        CustomNpcs.ChannelPlayer.register(new PacketHandlerPlayer());
        new MusicController();
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());

        Minecraft mc = Minecraft.getMinecraft();

        QuestLog = new KeyBinding("Quest Log", Keyboard.KEY_L, "key.categories.gameplay");

        if (CustomNpcs.SceneButtonsEnabled) {
            Scene1 = new KeyBinding("Scene1 start/pause", Keyboard.KEY_NUMPAD1, "key.categories.gameplay");
            Scene2 = new KeyBinding("Scene2 start/pause", Keyboard.KEY_NUMPAD2, "key.categories.gameplay");
            Scene3 = new KeyBinding("Scene3 start/pause", Keyboard.KEY_NUMPAD3, "key.categories.gameplay");
            SceneReset = new KeyBinding("Scene reset", Keyboard.KEY_NUMPAD0, "key.categories.gameplay");

            ClientRegistry.registerKeyBinding(Scene1);
            ClientRegistry.registerKeyBinding(Scene2);
            ClientRegistry.registerKeyBinding(Scene3);
            ClientRegistry.registerKeyBinding(SceneReset);
        }

        ClientRegistry.registerKeyBinding(QuestLog);
        mc.gameSettings.loadOptions();

        new PresetController(CustomNpcs.Dir);

        if (CustomNpcs.EnableUpdateChecker) {
            VersionChecker checker = new VersionChecker();
            checker.start();
        }
        PixelmonHelper.loadClient();
    }

    @Override
    public PlayerData getPlayerData(EntityPlayer player) {
        if (player.getUniqueID() == Minecraft.getMinecraft().player.getUniqueID()) {
            if (playerData.player != player)
                playerData.player = player;
            return playerData;
        }
        return null;
    }

    @Override
    public void postload() {

        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        if (CustomNpcs.InventoryGuiEnabled) {
            MinecraftForge.EVENT_BUS.register(new TabRegistry());

            if (TabRegistry.getTabList().isEmpty()) {
                TabRegistry.registerTab(new InventoryTabVanilla());
            }
            TabRegistry.registerTab(new InventoryTabFactions());
            TabRegistry.registerTab(new InventoryTabQuests());
        }
        RenderingRegistry.registerEntityRenderingHandler(EntityNpcPony.class, new RenderNPCPony());
        RenderingRegistry.registerEntityRenderingHandler(EntityNpcCrystal.class, new RenderNpcCrystal(new ModelNpcCrystal(0.5F)));
        RenderingRegistry.registerEntityRenderingHandler(EntityNpcDragon.class, new RenderNpcDragon(new ModelNpcDragon(0.0F), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityNpcSlime.class, new RenderNpcSlime(new ModelNpcSlime(16), new ModelNpcSlime(0), 0.25F));
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectile.class, new RenderProjectile());
        RenderingRegistry.registerEntityRenderingHandler(EntityCustomNpc.class, new RenderCustomNpc(new ModelPlayerAlt(0, false)));
        RenderingRegistry.registerEntityRenderingHandler(EntityNPC64x32.class, new RenderCustomNpc(new ModelBipedAlt(0)));
        RenderingRegistry.registerEntityRenderingHandler(EntityNPCGolem.class, new RenderNPCInterface(new ModelNPCGolem(0), 0));
        RenderingRegistry.registerEntityRenderingHandler(EntityNpcAlex.class, new RenderCustomNpc(new ModelPlayerAlt(0, true)));
        RenderingRegistry.registerEntityRenderingHandler(EntityNpcClassicPlayer.class, new RenderCustomNpc(new ModelClassicPlayer(0)));

        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> 0x8B4513, CustomItems.mount, CustomItems.cloner, CustomItems.moving, CustomItems.scripter, CustomItems.wand, CustomItems.teleporter);

        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            IItemStack item = NpcAPI.Instance().getIItemStack(stack);
            if (stack.getItem() == CustomItems.scripted_item) {
                return ((IItemScripted) item).getColor();
            }
            return -1;
        }, CustomItems.scripted_item);
    }

    private void createFolders() {
        File file = new File(CustomNpcs.Dir, "assets/customnpcs");
        if (!file.exists())
            file.mkdirs();

        File check = new File(file, "sounds");
        if (!check.exists())
            check.mkdir();

        File json = new File(file, "sounds.json");
        if (!json.exists()) {
            try {
                json.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(json));
                writer.write("{\n\n}");
                writer.close();
            } catch (IOException e) {
            }
        }

        check = new File(file, "textures");
        if (!check.exists())
            check.mkdir();

    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        if (ID > EnumGuiType.values().length)
            return null;
        EnumGuiType gui = EnumGuiType.values()[ID];
        EntityNPCInterface npc = NoppesUtil.getLastNpc();
        Container container = this.getContainer(gui, player, x, y, z, npc);
        return getGui(npc, gui, container, x, y, z);
    }

    private GuiScreen getGui(EntityNPCInterface npc, EnumGuiType gui, Container container, int x, int y, int z) {
        if (gui == EnumGuiType.CustomChest) {
            return new GuiCustomChest((ContainerCustomChest) container);
        }
        if (gui == EnumGuiType.MainMenuDisplay) {
            if (npc != null)
                return new GuiNpcDisplay(npc);
            else
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Unable to find npc"));
        } else if (gui == EnumGuiType.MainMenuStats)
            return new GuiNpcStats(npc);

        else if (gui == EnumGuiType.MainMenuInv)
            return new GuiNPCInv(npc, (ContainerNPCInv) container);

        else if (gui == EnumGuiType.MainMenuAdvanced)
            return new GuiNpcAdvanced(npc);

        else if (gui == EnumGuiType.QuestReward)
            return new GuiNpcQuestReward(npc, (ContainerNpcQuestReward) container);

        else if (gui == EnumGuiType.QuestItem)
            return new GuiNpcQuestTypeItem(npc, (ContainerNpcQuestTypeItem) container);

        else if (gui == EnumGuiType.MovingPath)
            return new GuiNpcPather(npc);

        else if (gui == EnumGuiType.ManageFactions)
            return new GuiNPCManageFactions(npc);

        else if (gui == EnumGuiType.ManageLinked)
            return new GuiNPCManageLinkedNpc(npc);

        else if (gui == EnumGuiType.BuilderBlock)
            return new GuiBlockBuilder(x, y, z);

        else if (gui == EnumGuiType.ManageTransport)
            return new GuiNPCManageTransporters(npc);

        else if (gui == EnumGuiType.ManageRecipes)
            return new GuiNpcManageRecipes(npc, (ContainerManageRecipes) container);

        else if (gui == EnumGuiType.ManageDialogs)
            return new GuiNPCManageDialogs(npc);

        else if (gui == EnumGuiType.ManageQuests)
            return new GuiNPCManageQuest(npc);

        else if (gui == EnumGuiType.ManageBanks)
            return new GuiNPCManageBanks(npc, (ContainerManageBanks) container);

        else if (gui == EnumGuiType.MainMenuGlobal)
            return new GuiNPCGlobalMainMenu(npc);

        else if (gui == EnumGuiType.MainMenuAI)
            return new GuiNpcAI(npc);

        else if (gui == EnumGuiType.PlayerAnvil)
            return new GuiNpcCarpentryBench((ContainerCarpentryBench) container);

        else if (gui == EnumGuiType.PlayerFollowerHire)
            return new GuiNpcFollowerHire(npc, (ContainerNPCFollowerHire) container);

        else if (gui == EnumGuiType.PlayerFollower)
            return new GuiNpcFollower(npc, (ContainerNPCFollower) container);

        else if (gui == EnumGuiType.PlayerTrader)
            return new GuiNPCTrader(npc, (ContainerNPCTrader) container);

        else if (gui == EnumGuiType.PlayerBankSmall || gui == EnumGuiType.PlayerBankUnlock || gui == EnumGuiType.PlayerBankUprade || gui == EnumGuiType.PlayerBankLarge)
            return new GuiNPCBankChest(npc, (ContainerNPCBankInterface) container);

        else if (gui == EnumGuiType.PlayerTransporter)
            return new GuiTransportSelection(npc);

        else if (gui == EnumGuiType.Script)
            return new GuiScript(npc);

        else if (gui == EnumGuiType.ScriptBlock)
            return new GuiScriptBlock(x, y, z);

        else if (gui == EnumGuiType.ScriptItem)
            return new GuiScriptItem(Minecraft.getMinecraft().player);

        else if (gui == EnumGuiType.ScriptDoor)
            return new GuiScriptDoor(x, y, z);

        else if (gui == EnumGuiType.ScriptPlayers)
            return new GuiScriptGlobal();

        else if (gui == EnumGuiType.SetupFollower)
            return new GuiNpcFollowerSetup(npc, (ContainerNPCFollowerSetup) container);

        else if (gui == EnumGuiType.SetupItemGiver)
            return new GuiNpcItemGiver(npc, (ContainerNpcItemGiver) container);

        else if (gui == EnumGuiType.SetupTrader)
            return new GuiNpcTraderSetup(npc, (ContainerNPCTraderSetup) container);

        else if (gui == EnumGuiType.SetupTransporter)
            return new GuiNpcTransporter(npc);

        else if (gui == EnumGuiType.SetupBank)
            return new GuiNpcBankSetup(npc);

        else if (gui == EnumGuiType.NpcRemote && Minecraft.getMinecraft().currentScreen == null)
            return new GuiNpcRemoteEditor();

        else if (gui == EnumGuiType.PlayerMailman)
            return new GuiMailmanWrite((ContainerMail) container, x == 1, y == 1);

        else if (gui == EnumGuiType.PlayerMailbox)
            return new GuiMailbox();

        else if (gui == EnumGuiType.MerchantAdd)
            return new GuiMerchantAdd();

        else if (gui == EnumGuiType.NpcDimensions)
            return new GuiNpcDimension();

        else if (gui == EnumGuiType.Border)
            return new GuiBorderBlock(x, y, z);

        else if (gui == EnumGuiType.RedstoneBlock)
            return new GuiNpcRedstoneBlock(x, y, z);

        else if (gui == EnumGuiType.MobSpawner)
            return new GuiNpcMobSpawner(x, y, z);

        else if (gui == EnumGuiType.CopyBlock)
            return new GuiBlockCopy(x, y, z);

        else if (gui == EnumGuiType.MobSpawnerMounter)
            return new GuiNpcMobSpawnerMounter(x, y, z);

        else if (gui == EnumGuiType.Waypoint)
            return new GuiNpcWaypoint(x, y, z);

        else if (gui == EnumGuiType.Companion)
            return new GuiNpcCompanionStats(npc);

        else if (gui == EnumGuiType.CompanionTalent)
            return new GuiNpcCompanionTalents(npc);

        else if (gui == EnumGuiType.CompanionInv)
            return new GuiNpcCompanionInv(npc, (ContainerNPCCompanion) container);

        else if (gui == EnumGuiType.NbtBook)
            return new GuiNbtBook(x, y, z);
        return null;
    }

    @Override
    public void openGui(int i, int j, int k, EnumGuiType gui, EntityPlayer player) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player != player)
            return;

        GuiScreen guiscreen = getGui(null, gui, null, i, j, k);


        if (guiscreen != null) {
            minecraft.displayGuiScreen(guiscreen);
        }
    }

    @Override
    public void openGui(EntityNPCInterface npc, EnumGuiType gui) {
        openGui(npc, gui, 0, 0, 0);
    }

    public void openGui(EntityNPCInterface npc, EnumGuiType gui, int x, int y, int z) {
        Minecraft minecraft = Minecraft.getMinecraft();

        Container container = this.getContainer(gui, minecraft.player, x, y, z, npc);
        GuiScreen guiscreen = getGui(npc, gui, container, x, y, z);

        if (guiscreen != null) {
            minecraft.displayGuiScreen(guiscreen);
        }
    }

    public void openGui(EntityPlayer player, Object guiscreen) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (!player.world.isRemote || !(guiscreen instanceof GuiScreen))
            return;

        if (guiscreen != null) {
            minecraft.displayGuiScreen((GuiScreen) guiscreen);
        }
    }

    @Override
    public void spawnParticle(EntityLivingBase player, String string, Object... ob) {
        if (string.equals("Block")) {
            BlockPos pos = (BlockPos) ob[0];
            int id = (Integer) ob[1];
            Block block = Block.getBlockById(id & 4095);
            Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(pos, block.getStateFromMeta(id >> 12 & 255));
        } else if (string.equals("ModelData")) {
            ModelData data = (ModelData) ob[0];
            ModelPartData particles = (ModelPartData) ob[1];
            EntityCustomNpc npc = (EntityCustomNpc) player;
            Minecraft minecraft = Minecraft.getMinecraft();
            double height = npc.getYOffset() + data.getBodyY();
            Random rand = npc.getRNG();
            //if(particles.type == 0){
            for (int i = 0; i < 2; i++) {
                EntityEnderFX fx = new EntityEnderFX(npc, (rand.nextDouble() - 0.5D) * (double) player.width, (rand.nextDouble() * (double) player.height) - height - 0.25D, (rand.nextDouble() - 0.5D) * (double) player.width, (rand.nextDouble() - 0.5D) * 2D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2D, particles);
                minecraft.effectRenderer.addEffect(fx);
            }

            //}
//			else if(particles.type == 1){
//	        	for(int i = 0; i < 2; i++){
//		            double x = player.posX + (rand.nextDouble() - 0.5D) * 0.9;
//		            double y = (player.posY + rand.nextDouble() * 1.9) - 0.25D - height;
//		            double z = player.posZ + (rand.nextDouble() - 0.5D) * 0.9;
//
//
//		            double f = (rand.nextDouble() - 0.5D) * 2D;
//		            double f1 =  -rand.nextDouble();
//		            double f2 = (rand.nextDouble() - 0.5D) * 2D;
//
//		            minecraft.effectRenderer.addEffect(new EntityRainbowFX(player.world, x, y, z, f, f1, f2));
//	        	}
//			}
        }
    }

    public boolean hasClient() {
        return true;
    }

    public EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().player;
    }

    public static void bindTexture(ResourceLocation location) {
        try {
            if (location == null)
                return;
            TextureManager manager = Minecraft.getMinecraft().getTextureManager();
            ITextureObject ob = manager.getTexture(location);
            if (ob == null) {
                ob = new SimpleTexture(location);
                manager.loadTexture(location, ob);
            }
            GlStateManager.bindTexture(ob.getGlTextureId());
        } catch (NullPointerException ex) {

        } catch (ReportedException ex) {

        }
    }

    @Override
    public void spawnParticle(EnumParticleTypes particle, double x, double y, double z,
                              double motionX, double motionY, double motionZ, float scale) {
        Minecraft mc = Minecraft.getMinecraft();
        double xx = mc.getRenderViewEntity().posX - x;
        double yy = mc.getRenderViewEntity().posY - y;
        double zz = mc.getRenderViewEntity().posZ - z;
        if (xx * xx + yy * yy + zz * zz > 256)
            return;

        Particle fx = mc.effectRenderer.spawnEffectParticle(particle.getParticleID(), x, y, z, motionX, motionY, motionZ);
        if (fx == null)
            return;

        if (particle == EnumParticleTypes.FLAME) {
            ObfuscationReflectionHelper.setPrivateValue(ParticleFlame.class, (ParticleFlame) fx, scale, 0);
        } else if (particle == EnumParticleTypes.SMOKE_NORMAL) {
            ObfuscationReflectionHelper.setPrivateValue(ParticleSmokeNormal.class, (ParticleSmokeNormal) fx, scale, 0);
        }
    }

    public static class FontContainer {
        private TrueTypeFont textFont = null;
        public boolean useCustomFont = true;

        private FontContainer() {

        }

        public FontContainer(String fontType, int fontSize) {
            textFont = new TrueTypeFont(new Font(fontType, java.awt.Font.PLAIN, fontSize), 1f);
            useCustomFont = !fontType.equalsIgnoreCase("minecraft");
            try {
                if (!useCustomFont || fontType.isEmpty() || fontType.equalsIgnoreCase("default"))
                    textFont = new TrueTypeFont(new ResourceLocation("customnpcs", "opensans.ttf"), fontSize, 1f);
            } catch (Exception e) {
                LogWriter.info("Failed loading font so using Arial");
            }
        }

        public int height(String text) {
            if (useCustomFont)
                return textFont.height(text);
            return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
        }

        public int width(String text) {
            if (useCustomFont)
                return textFont.width(text);
            return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
        }

        public FontContainer copy() {
            FontContainer font = new FontContainer();
            font.textFont = textFont;
            font.useCustomFont = useCustomFont;
            return font;
        }

        public void drawString(String text, int x, int y, int color) {
            if (useCustomFont) {
                textFont.draw(text, x, y, color);
            } else {
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, x, y, color);
            }
        }

        public String getName() {
            if (!useCustomFont)
                return "Minecraft";
            return textFont.getFontName();
        }

        public void clear() {
            if (textFont != null)
                textFont.dispose();
        }
    }
}
