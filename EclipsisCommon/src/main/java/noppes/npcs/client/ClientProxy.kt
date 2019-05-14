package noppes.npcs.client


import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.particle.ParticleFlame
import net.minecraft.client.particle.ParticleSmokeNormal
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.ITextureObject
import net.minecraft.client.renderer.texture.SimpleTexture
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.ReportedException
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import noppes.npcs.LogWriter
import noppes.npcs.ModelData
import noppes.npcs.ModelPartData
import noppes.npcs.PacketHandlerPlayer
import noppes.npcs.client.controllers.MusicController
import noppes.npcs.client.controllers.PresetController
import noppes.npcs.client.fx.EntityEnderFX
import noppes.npcs.client.gui.*
import noppes.npcs.client.gui.global.*
import noppes.npcs.client.gui.mainmenu.*
import noppes.npcs.client.gui.player.*
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionInv
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionStats
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionTalents
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeItem
import noppes.npcs.client.gui.roles.*
import noppes.npcs.client.gui.script.*
import noppes.npcs.client.model.*
import noppes.npcs.client.renderer.*
import noppes.npcs.common.CommonProxy
import noppes.npcs.common.CustomNpcs
import noppes.npcs.common.CustomNpcsConfig
import noppes.npcs.common.config.TrueTypeFont
import noppes.npcs.constants.EnumGuiType
import noppes.npcs.containers.*
import noppes.npcs.controllers.PixelmonHelper
import noppes.npcs.controllers.data.PlayerData
import noppes.npcs.common.entity.*
import org.lwjgl.input.Keyboard
import java.awt.Font
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ClientProxy : CommonProxy() {

    override val player: EntityPlayer?
        get() = Minecraft.getMinecraft().player

    override fun preInit(event: FMLPreInitializationEvent) {
        super.preInit(event)

        Font = FontContainer(CustomNpcsConfig.FontType, CustomNpcsConfig.FontSize)
        createFolders()
        (Minecraft.getMinecraft().resourceManager as IReloadableResourceManager).registerReloadListener(CustomNpcResourceListener())

        CustomNpcs.Channel.register(PacketHandlerClient())
        CustomNpcs.ChannelPlayer.register(PacketHandlerPlayer())
        MusicController()
        MinecraftForge.EVENT_BUS.register(ClientTickHandler())

        val mc = Minecraft.getMinecraft()

        QuestLog = KeyBinding("Quest Log", Keyboard.KEY_L, "key.categories.gameplay")

        if (CustomNpcsConfig.SceneButtonsEnabled) {
            Scene1 = KeyBinding("Scene1 start/pause", Keyboard.KEY_NUMPAD1, "key.categories.gameplay")
            Scene2 = KeyBinding("Scene2 start/pause", Keyboard.KEY_NUMPAD2, "key.categories.gameplay")
            Scene3 = KeyBinding("Scene3 start/pause", Keyboard.KEY_NUMPAD3, "key.categories.gameplay")
            SceneReset = KeyBinding("Scene reset", Keyboard.KEY_NUMPAD0, "key.categories.gameplay")

            ClientRegistry.registerKeyBinding(Scene1)
            ClientRegistry.registerKeyBinding(Scene2)
            ClientRegistry.registerKeyBinding(Scene3)
            ClientRegistry.registerKeyBinding(SceneReset)
        }

        ClientRegistry.registerKeyBinding(QuestLog)
        mc.gameSettings.loadOptions()

        PresetController(CustomNpcs.Dir)

        PixelmonHelper.loadClient()
    }

    override fun init(event: FMLInitializationEvent) {
        super.init(event)

        MinecraftForge.EVENT_BUS.register(ClientEventHandler())

        RenderingRegistry.registerEntityRenderingHandler(EntityNpcPony::class.java, RenderNPCPony())
        RenderingRegistry.registerEntityRenderingHandler(EntityNpcCrystal::class.java, RenderNpcCrystal(ModelNpcCrystal(0.5f)))
        RenderingRegistry.registerEntityRenderingHandler(EntityNpcDragon::class.java, RenderNpcDragon(ModelNpcDragon(0.0f), 0.5f))
        RenderingRegistry.registerEntityRenderingHandler(EntityNpcSlime::class.java, RenderNpcSlime(ModelNpcSlime(16), ModelNpcSlime(0), 0.25f))
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectile::class.java, RenderProjectile())
        RenderingRegistry.registerEntityRenderingHandler(EntityCustomNpc::class.java, RenderCustomNpc(ModelPlayerAlt(0f, false)))
        RenderingRegistry.registerEntityRenderingHandler(EntityNPC64x32::class.java, RenderCustomNpc(ModelBipedAlt(0f)))
        RenderingRegistry.registerEntityRenderingHandler(EntityNPCGolem::class.java, RenderNPCInterface(ModelNPCGolem(0f), 0f))
        RenderingRegistry.registerEntityRenderingHandler(EntityNpcAlex::class.java, RenderCustomNpc(ModelPlayerAlt(0f, true)))
        RenderingRegistry.registerEntityRenderingHandler(EntityNpcClassicPlayer::class.java, RenderCustomNpc(ModelClassicPlayer(0f)))

        /*Minecraft.getMinecraft().itemColors.registerItemColorHandler(IItemColor { stack, tintIndex -> 0x8B4513 }, NpcObjects.mounter, NpcObjects.mobCloner, NpcObjects.pather, NpcObjects.scripter, NpcObjects.wand, NpcObjects.teleporter)

        Minecraft.getMinecraft().itemColors.registerItemColorHandler(IItemColor { stack, tintIndex ->
            val item = NpcAPI.instance()!!.getIItemStack(stack)
            if (stack.item === NpcObjects.scriptedItem) {
                (item as IItemScripted).color
            } else -1
        }, NpcObjects.scriptedItem)*/
    }

    override fun getPlayerData(player: EntityPlayer): PlayerData? {
        if (player.uniqueID === Minecraft.getMinecraft().player.uniqueID) {
            if (playerData.player !== player)
                playerData.player = player
            return playerData
        }
        return null
    }

    private fun createFolders() {
        val file = File(CustomNpcs.Dir, "assets/customnpcs")
        if (!file.exists())
            file.mkdirs()

        var check = File(file, "sounds")
        if (!check.exists())
            check.mkdir()

        val json = File(file, "sounds.json")
        if (!json.exists()) {
            try {
                json.createNewFile()
                val writer = BufferedWriter(FileWriter(json))
                writer.write("{\n\n}")
                writer.close()
            } catch (e: IOException) {
            }

        }

        check = File(file, "textures")
        if (!check.exists())
            check.mkdir()

    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        if (ID > EnumGuiType.values().size)
            return null
        val gui = EnumGuiType.values()[ID]
        val npc = NoppesUtil.getLastNpc()
        val container = this.getContainer(gui, player, x, y, z, npc)
        return getGui(npc, gui, container, x, y, z)
    }

    private fun getGui(npc: EntityNPCInterface?, gui: EnumGuiType, container: Container?, x: Int, y: Int, z: Int): GuiScreen? {
        if (gui == EnumGuiType.CustomChest) {
            return GuiCustomChest(container as ContainerCustomChest)
        } else if (gui == EnumGuiType.MainMenuDisplay) {
            if (npc != null)
                return GuiNpcDisplay(npc)
            else
                Minecraft.getMinecraft().player.sendMessage(TextComponentString("Unable to find npc"))
        } else if (gui == EnumGuiType.MainMenuStats)
            return GuiNpcStats(npc)
        else if (gui == EnumGuiType.MainMenuInv)
            return GuiNPCInv(npc, container as ContainerNPCInv)
        else if (gui == EnumGuiType.MainMenuAdvanced)
            return GuiNpcAdvanced(npc)
        else if (gui == EnumGuiType.QuestReward)
            return GuiNpcQuestReward(npc, container as ContainerNpcQuestReward)
        else if (gui == EnumGuiType.QuestItem)
            return GuiNpcQuestTypeItem(npc, container as ContainerNpcQuestTypeItem)
        else if (gui == EnumGuiType.MovingPath)
            return GuiNpcPather(npc!!)
        else if (gui == EnumGuiType.ManageFactions)
            return GuiNPCManageFactions(npc)
        else if (gui == EnumGuiType.ManageLinked)
            return GuiNPCManageLinkedNpc(npc)
        else if (gui == EnumGuiType.BuilderBlock)
            return GuiBlockBuilder(x, y, z)
        else if (gui == EnumGuiType.ManageTransport)
            return GuiNPCManageTransporters(npc)
        else if (gui == EnumGuiType.ManageRecipes)
            return GuiNpcManageRecipes(npc, container as ContainerManageRecipes)
        else if (gui == EnumGuiType.ManageDialogs)
            return GuiNPCManageDialogs(npc)
        else if (gui == EnumGuiType.ManageQuests)
            return GuiNPCManageQuest(npc)
        else if (gui == EnumGuiType.ManageBanks)
            return GuiNPCManageBanks(npc, container as ContainerManageBanks)
        else if (gui == EnumGuiType.MainMenuGlobal)
            return GuiNPCGlobalMainMenu(npc)
        else if (gui == EnumGuiType.MainMenuAI)
            return GuiNpcAI(npc)
        else if (gui == EnumGuiType.PlayerAnvil)
            return GuiNpcCarpentryBench(container as ContainerCarpentryBench)
        else if (gui == EnumGuiType.PlayerFollowerHire)
            return GuiNpcFollowerHire(npc, container as ContainerNPCFollowerHire)
        else if (gui == EnumGuiType.PlayerFollower)
            return GuiNpcFollower(npc, container as ContainerNPCFollower)
        else if (gui == EnumGuiType.PlayerTrader)
            return GuiNPCTrader(npc, container as ContainerNPCTrader)
        else if (gui == EnumGuiType.PlayerBankSmall || gui == EnumGuiType.PlayerBankUnlock || gui == EnumGuiType.PlayerBankUprade || gui == EnumGuiType.PlayerBankLarge)
            return GuiNPCBankChest(npc, container as ContainerNPCBankInterface)
        else if (gui == EnumGuiType.PlayerTransporter)
            return GuiTransportSelection(npc)
        else if (gui == EnumGuiType.Script)
            return GuiScript(npc!!)
        else if (gui == EnumGuiType.ScriptBlock)
            return GuiScriptBlock(x, y, z)
        else if (gui == EnumGuiType.ScriptItem)
            return GuiScriptItem(Minecraft.getMinecraft().player)
        else if (gui == EnumGuiType.ScriptDoor)
            return GuiScriptDoor(x, y, z)
        else if (gui == EnumGuiType.ScriptPlayers)
            return GuiScriptGlobal()
        else if (gui == EnumGuiType.SetupFollower)
            return GuiNpcFollowerSetup(npc, container as ContainerNPCFollowerSetup)
        else if (gui == EnumGuiType.SetupItemGiver)
            return GuiNpcItemGiver(npc, container as ContainerNpcItemGiver)
        else if (gui == EnumGuiType.SetupTrader)
            return GuiNpcTraderSetup(npc, container as ContainerNPCTraderSetup)
        else if (gui == EnumGuiType.SetupTransporter)
            return GuiNpcTransporter(npc)
        else if (gui == EnumGuiType.SetupBank)
            return GuiNpcBankSetup(npc)
        else if (gui == EnumGuiType.NpcRemote && Minecraft.getMinecraft().currentScreen == null)
            return GuiNpcRemoteEditor()
        else if (gui == EnumGuiType.PlayerMailman)
            return GuiMailmanWrite(container as ContainerMail?, x == 1, y == 1)
        else if (gui == EnumGuiType.PlayerMailbox)
            return GuiMailbox()
        else if (gui == EnumGuiType.MerchantAdd)
            return GuiMerchantAdd()
        else if (gui == EnumGuiType.NpcDimensions)
            return GuiNpcDimension()
        else if (gui == EnumGuiType.Border)
            return GuiBorderBlock(x, y, z)
        else if (gui == EnumGuiType.RedstoneBlock)
            return GuiNpcRedstoneBlock(x, y, z)
        else if (gui == EnumGuiType.MobSpawner)
            return GuiNpcMobSpawner(x, y, z)
        else if (gui == EnumGuiType.CopyBlock)
            return GuiBlockCopy(x, y, z)
        else if (gui == EnumGuiType.MobSpawnerMounter)
            return GuiNpcMobSpawnerMounter(x, y, z)
        else if (gui == EnumGuiType.Waypoint)
            return GuiNpcWaypoint(x, y, z)
        else if (gui == EnumGuiType.Companion)
            return GuiNpcCompanionStats(npc)
        else if (gui == EnumGuiType.CompanionTalent)
            return GuiNpcCompanionTalents(npc)
        else if (gui == EnumGuiType.CompanionInv)
            return GuiNpcCompanionInv(npc, container as ContainerNPCCompanion)
        else if (gui == EnumGuiType.NbtBook)
            return GuiNbtBook(x, y, z)
        return null
    }

    override fun openGui(i: Int, j: Int, k: Int, gui: EnumGuiType, player: EntityPlayer) {
        val minecraft = Minecraft.getMinecraft()
        if (minecraft.player !== player)
            return

        val guiscreen = getGui(null, gui, null, i, j, k)


        if (guiscreen != null) {
            minecraft.displayGuiScreen(guiscreen)
        }
    }

    override fun openGui(npc: EntityNPCInterface, gui: EnumGuiType) {
        openGui(npc, gui, 0, 0, 0)
    }

    override fun openGui(npc: EntityNPCInterface, gui: EnumGuiType, x: Int, y: Int, z: Int) {
        val container = this.getContainer(gui, Minecraft().player, x, y, z, npc)
        val guiscreen = getGui(npc, gui, container, x, y, z)

        if (guiscreen != null) {
            Minecraft().displayGuiScreen(guiscreen)
        }
    }

    override fun openGui(player: EntityPlayer, guiscreen: Any) {
        if (!player.world.isRemote || guiscreen !is GuiScreen)
            return

        Minecraft().displayGuiScreen(guiscreen)
    }

    override fun spawnParticle(player: EntityLivingBase, string: String, vararg ob: Any) {
        if (string == "Block") {
            val pos = ob[0] as BlockPos
            val id = ob[1] as Int
            val block = Block.getBlockById(id and 4095)
            Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(pos, block.getStateFromMeta(id shr 12 and 255))
        } else if (string == "ModelData") {
            val data = ob[0] as ModelData
            val particles = ob[1] as ModelPartData
            val npc = player as EntityCustomNpc
            val height = npc.yOffset + data.bodyY
            val rand = npc.rng
            for (i in 0..1) {
                val fx = EntityEnderFX(npc, (rand.nextDouble() - 0.5) * player.width.toDouble(), rand.nextDouble() * player.height.toDouble() - height - 0.25, (rand.nextDouble() - 0.5) * player.width.toDouble(), (rand.nextDouble() - 0.5) * 2.0, -rand.nextDouble(), (rand.nextDouble() - 0.5) * 2.0, particles)
                Minecraft().effectRenderer.addEffect(fx)
            }
        }
    }

    override fun hasClient(): Boolean {
        return true
    }

    override fun spawnParticle(type: EnumParticleTypes, x: Double, y: Double, z: Double,
                               motionX: Double, motionY: Double, motionZ: Double, scale: Float) {
        val mc = Minecraft()
        val xx = mc.renderViewEntity!!.posX - x
        val yy = mc.renderViewEntity!!.posY - y
        val zz = mc.renderViewEntity!!.posZ - z
        if (xx * xx + yy * yy + zz * zz > 256)
            return

        val fx = mc.effectRenderer.spawnEffectParticle(type.particleID, x, y, z, motionX, motionY, motionZ) ?: return

        if (type == EnumParticleTypes.FLAME) {
            ObfuscationReflectionHelper.setPrivateValue(ParticleFlame::class.java, fx as ParticleFlame, scale, 0)
        } else if (type == EnumParticleTypes.SMOKE_NORMAL) {
            ObfuscationReflectionHelper.setPrivateValue(ParticleSmokeNormal::class.java, fx as ParticleSmokeNormal, scale, 0)
        }
    }

    class FontContainer(fontType: String, fontSize: Int) {
        var useCustomFont = true
        var textFont: TrueTypeFont

        val name: String
            get() = if (!useCustomFont) "Minecraft" else textFont.fontName

        init {
            textFont = TrueTypeFont(Font(fontType, java.awt.Font.PLAIN, fontSize), 1f)
            useCustomFont = !fontType.equals("minecraft", ignoreCase = true)
            try {
                if (!useCustomFont || fontType.isEmpty() || fontType.equals("default", ignoreCase = true))
                    textFont = TrueTypeFont(ResourceLocation("customnpcs", "opensans.ttf"), fontSize, 1f)
            } catch (e: Exception) {
                LogWriter.info("Failed loading font so using Arial")
            }
        }

        fun height(text: String): Int {
            return if (useCustomFont) textFont.height(text) else Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT
        }

        fun width(text: String): Int {
            return if (useCustomFont) textFont.width(text) else Minecraft.getMinecraft().fontRenderer.getStringWidth(text)
        }

        fun copy(): FontContainer {
            val font = FontContainer(textFont.fontName, textFont.font.size)
            font.textFont = textFont
            font.useCustomFont = useCustomFont
            return font
        }

        fun drawString(text: String, x: Int, y: Int, color: Int) {
            if (useCustomFont) {
                textFont.draw(text, x.toFloat(), y.toFloat(), color)
            } else {
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, x.toFloat(), y.toFloat(), color)
            }
        }

        fun clear() {
            textFont.dispose()
        }
    }

    companion object {
        var playerData = PlayerData()

        lateinit var QuestLog: KeyBinding

        lateinit var Scene1: KeyBinding
        lateinit var Scene2: KeyBinding
        lateinit var Scene3: KeyBinding
        lateinit var SceneReset: KeyBinding

        lateinit var Font: FontContainer

        fun bindTexture(location: ResourceLocation?) {
            try {
                if (location == null)
                    return
                val manager = Minecraft.getMinecraft().textureManager
                var ob: ITextureObject? = manager.getTexture(location)
                if (ob == null) {
                    ob = SimpleTexture(location)
                    manager.loadTexture(location, ob)
                }
                GlStateManager.bindTexture(ob.glTextureId)
            } catch (ex: NullPointerException) {

            } catch (ex: ReportedException) {
            }
        }
    }
}
