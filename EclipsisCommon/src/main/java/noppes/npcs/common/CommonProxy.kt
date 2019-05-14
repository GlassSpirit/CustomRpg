package noppes.npcs.common

import com.teamwizardry.librarianlib.features.network.Channel
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.RangedAttribute
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.common.ForgeModContainer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.IGuiHandler
import net.minecraftforge.fml.common.network.NetworkRegistry
import noppes.npcs.*
import noppes.npcs.api.wrapper.ItemStackWrapper
import noppes.npcs.api.wrapper.WrapperEntityData
import noppes.npcs.common.objects.CustomEntities
import noppes.npcs.common.objects.NpcObjects
import noppes.npcs.constants.EnumGuiType
import noppes.npcs.containers.*
import noppes.npcs.controllers.ChunkController
import noppes.npcs.controllers.PixelmonHelper
import noppes.npcs.controllers.RecipeController
import noppes.npcs.controllers.ScriptController
import noppes.npcs.controllers.data.MarkData
import noppes.npcs.controllers.data.PlayerData
import noppes.npcs.common.entity.EntityNPCInterface
import java.io.File

open class CommonProxy : IGuiHandler {

    lateinit var channel: Channel

    open val player: EntityPlayer?
        get() = null

    open fun preInit(event: FMLPreInitializationEvent) {
        channel = Channel(NetworkRegistry.INSTANCE.newSimpleChannel(CustomNpcs.MODID))

        CustomNpcs.Channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("CustomNPCs")
        CustomNpcs.ChannelPlayer = NetworkRegistry.INSTANCE.newEventDrivenChannel("CustomNPCsPlayer")

        CustomNpcs.Dir = File(File(event.modConfigurationDirectory, ".."), "customnpcs")
        CustomNpcs.Dir.mkdir()

        CustomNpcsConfig.loadConfig(event.modConfigurationDirectory)
        if (CustomNpcsConfig.NpcNavRange < 16) {
            CustomNpcsConfig.NpcNavRange = 16
        }

        //Init blocks, items, entities
        NpcObjects

        CapabilityManager.INSTANCE.register<PlayerData>(PlayerData::class.java, object : Capability.IStorage<PlayerData> {
            override fun writeNBT(capability: Capability<PlayerData>, instance: PlayerData, side: EnumFacing): NBTBase? {
                return null
            }

            override fun readNBT(capability: Capability<PlayerData>, instance: PlayerData, side: EnumFacing, nbt: NBTBase) {}
        }, PlayerData::class.java)

        CapabilityManager.INSTANCE.register<WrapperEntityData>(WrapperEntityData::class.java, object : Capability.IStorage<WrapperEntityData> {
            override fun writeNBT(capability: Capability<WrapperEntityData>, instance: WrapperEntityData, side: EnumFacing): NBTBase? {
                return null
            }

            override fun readNBT(capability: Capability<WrapperEntityData>, instance: WrapperEntityData, side: EnumFacing, nbt: NBTBase) {}
        }, WrapperEntityData::class.java)

        CapabilityManager.INSTANCE.register<MarkData>(MarkData::class.java, object : Capability.IStorage<MarkData> {
            override fun writeNBT(capability: Capability<MarkData>, instance: MarkData, side: EnumFacing): NBTBase? {
                return null
            }

            override fun readNBT(capability: Capability<MarkData>, instance: MarkData, side: EnumFacing, nbt: NBTBase) {}
        }, MarkData::class.java)

        CapabilityManager.INSTANCE.register(ItemStackWrapper::class.java, object : Capability.IStorage<ItemStackWrapper> {
            override fun writeNBT(capability: Capability<ItemStackWrapper>, instance: ItemStackWrapper, side: EnumFacing): NBTBase? {
                return null
            }

            override fun readNBT(capability: Capability<ItemStackWrapper>, instance: ItemStackWrapper, side: EnumFacing, nbt: NBTBase) {
            }
        }) { null }

        NetworkRegistry.INSTANCE.registerGuiHandler(CustomNpcs, this)

        MinecraftForge.EVENT_BUS.register(ServerEventsHandler())

        if (CustomNpcsConfig.EnableScripting) {
            val controller = ScriptController()
            if (controller.languages.isNotEmpty()) {
                MinecraftForge.EVENT_BUS.register(controller)
                MinecraftForge.EVENT_BUS.register(ScriptPlayerEventHandler().registerForgeEvents())
                MinecraftForge.EVENT_BUS.register(ScriptItemEventHandler())
            }
        }

        MinecraftForge.EVENT_BUS.register(ServerTickHandler())
        MinecraftForge.EVENT_BUS.register(CustomEntities())
        MinecraftForge.EVENT_BUS.register(CustomNpcs.proxy)

        PixelmonHelper.load()

        ForgeChunkManager.setForcedChunkLoadingCallback(CustomNpcs, ChunkController())

        ObfuscationReflectionHelper.setPrivateValue(RangedAttribute::class.java, SharedMonsterAttributes.MAX_HEALTH as RangedAttribute, java.lang.Double.MAX_VALUE, "field_111118_b")

        CustomNpcs.Channel.register(PacketHandlerServer())
        CustomNpcs.ChannelPlayer.register(PacketHandlerPlayer())
    }

    open fun init(event: FMLInitializationEvent) {
        ForgeModContainer.fullBoundingBoxLadders = true
        RecipeController()
        CustomNpcsPermissions()
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        if (ID > EnumGuiType.values().size)
            return null
        val npc = NoppesUtilServer.getEditingNpc(player)
        val gui = EnumGuiType.values()[ID]
        return getContainer(gui, player, x, y, z, npc)
    }

    fun getContainer(gui: EnumGuiType, player: EntityPlayer, x: Int, y: Int, z: Int, npc: EntityNPCInterface): Container? {
        if (gui == EnumGuiType.CustomChest)
            return ContainerCustomChest(player, x)

        if (gui == EnumGuiType.MainMenuInv)
            return ContainerNPCInv(npc, player)

        if (gui == EnumGuiType.PlayerAnvil)
            return ContainerCarpentryBench(player.inventory, player.world, BlockPos(x, y, z))

        if (gui == EnumGuiType.PlayerBankSmall)
            return ContainerNPCBankSmall(player, x, y)

        if (gui == EnumGuiType.PlayerBankUnlock)
            return ContainerNPCBankUnlock(player, x, y)

        if (gui == EnumGuiType.PlayerBankUprade)
            return ContainerNPCBankUpgrade(player, x, y)

        if (gui == EnumGuiType.PlayerBankLarge)
            return ContainerNPCBankLarge(player, x, y)

        if (gui == EnumGuiType.PlayerFollowerHire)
            return ContainerNPCFollowerHire(npc, player)

        if (gui == EnumGuiType.PlayerFollower)
            return ContainerNPCFollower(npc, player)

        if (gui == EnumGuiType.PlayerTrader)
            return ContainerNPCTrader(npc, player)

        if (gui == EnumGuiType.SetupItemGiver)
            return ContainerNpcItemGiver(npc, player)

        if (gui == EnumGuiType.SetupTrader)
            return ContainerNPCTraderSetup(npc, player)

        if (gui == EnumGuiType.SetupFollower)
            return ContainerNPCFollowerSetup(npc, player)

        if (gui == EnumGuiType.QuestReward)
            return ContainerNpcQuestReward(player)

        if (gui == EnumGuiType.QuestItem)
            return ContainerNpcQuestTypeItem(player)

        if (gui == EnumGuiType.ManageRecipes)
            return ContainerManageRecipes(player, x)

        if (gui == EnumGuiType.ManageBanks)
            return ContainerManageBanks(player)

        if (gui == EnumGuiType.MerchantAdd)
            return ContainerMerchantAdd(player, ServerEventsHandler.Merchant, player.world)

        if (gui == EnumGuiType.PlayerMailman)
            return ContainerMail(player, x == 1, y == 1)

        return if (gui == EnumGuiType.CompanionInv) ContainerNPCCompanion(npc, player) else null

    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        return null
    }

    open fun openGui(npc: EntityNPCInterface, gui: EnumGuiType) {

    }

    open fun openGui(npc: EntityNPCInterface, gui: EnumGuiType, x: Int, y: Int, z: Int) {

    }

    open fun openGui(i: Int, j: Int, k: Int, gui: EnumGuiType, player: EntityPlayer) {

    }

    open fun openGui(player: EntityPlayer, guiscreen: Any) {

    }

    open fun spawnParticle(player: EntityLivingBase, string: String, vararg ob: Any) {

    }

    open fun hasClient(): Boolean {
        return false
    }

    open fun spawnParticle(type: EnumParticleTypes, x: Double, y: Double, z: Double, motionX: Double, motionY: Double, motionZ: Double, scale: Float) {
    }

    open fun getPlayerData(player: EntityPlayer): PlayerData? {
        return null
    }
}
