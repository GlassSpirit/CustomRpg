package noppes.npcs.api.wrapper

import net.minecraft.entity.Entity
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.fml.common.eventhandler.EventBus
import net.minecraftforge.server.permission.DefaultPermissionLevel
import net.minecraftforge.server.permission.PermissionAPI
import noppes.npcs.NoppesUtilServer
import noppes.npcs.api.*
import noppes.npcs.api.block.IBlock
import noppes.npcs.api.entity.ICustomNpc
import noppes.npcs.api.entity.IEntity
import noppes.npcs.api.handler.*
import noppes.npcs.api.item.IItemStack
import noppes.npcs.common.CustomNpcs
import noppes.npcs.common.entity.EntityCustomNpc
import noppes.npcs.common.entity.EntityNPCInterface
import noppes.npcs.controllers.*
import noppes.npcs.controllers.data.PlayerData
import noppes.npcs.util.LRUHashMap
import noppes.npcs.util.NBTJsonUtil
import noppes.npcs.util.NBTJsonUtil.JsonException
import java.io.File

object WrapperNpcAPI : NpcAPI {

    @JvmField
    val EVENT_BUS = EventBus()

    private val worldCache = LRUHashMap<Int, WorldWrapper>(10)

    override val iWorlds: Array<IWorld>
        get() = CustomNpcs.Server.worlds.map { getIWorld(it) }.toTypedArray()

    override val factions: IFactionHandler
        get() = FactionController.instance

    override val recipes: IRecipeHandler
        get() = RecipeController.instance

    override val quests: IQuestHandler
        get() = QuestController.instance

    override val dialogs: IDialogHandler
        get() = DialogController.instance

    override val clones: ICloneHandler
        get() = ServerCloneController.Instance

    override val globalDir: File
        get() = CustomNpcs.Dir

    override val worldDir: File
        get() = CustomNpcs.worldSaveDirectory

    fun clearCache() {
        worldCache.clear()
        BlockWrapper.clearCache()
    }

    override fun getIEntity(entity: Entity): IEntity<*> {
        return if (entity is EntityNPCInterface)
            entity.wrappedNPC
        else {
            WrapperEntityData.get(entity)
        }
    }

    override fun createNPC(world: World): ICustomNpc<*> {
        val npc = EntityCustomNpc(world)
        return npc.wrappedNPC
    }


    override fun registerPermissionNode(permission: String, defaultType: Int) {
        if (defaultType < 0 || defaultType > 2) {
            throw CustomNPCsException("Default type cant be smaller than 0 or larger than 2")
        }
        if (hasPermissionNode(permission)) {
            throw CustomNPCsException("Permission already exists")
        }
        val level = DefaultPermissionLevel.values()[defaultType]
        PermissionAPI.registerNode(permission, level, permission)
    }

    override fun hasPermissionNode(permission: String): Boolean {
        return PermissionAPI.getPermissionHandler().registeredNodes.contains(permission)
    }

    override fun spawnNPC(world: World, x: Int, y: Int, z: Int): ICustomNpc<*> {
        val npc = EntityCustomNpc(world)
        npc.setPositionAndRotation(x + 0.5, y.toDouble(), z + 0.5, 0f, 0f)
        npc.ais.setStartPos(BlockPos(x, y, z))
        npc.health = npc.maxHealth
        world.spawnEntity(npc)
        return npc.wrappedNPC
    }

    override fun events(): EventBus {
        return EVENT_BUS
    }

    override fun getIBlock(world: World, pos: BlockPos): IBlock {
        return BlockWrapper.createNew(world, pos, world.getBlockState(pos))
    }

    override fun getIItemStack(itemstack: ItemStack): IItemStack? {
        return if (itemstack.isEmpty) ItemStackWrapper.AIR else itemstack.getCapability(ItemStackWrapper.ITEMSCRIPTEDDATA_CAPABILITY, null)
    }

    override fun getIWorld(world: WorldServer): IWorld {
        val w: WorldWrapper? = worldCache[world.provider.dimension]
        if (w != null) {
            w.world = world
            return w
        }
        worldCache[world.provider.dimension] = WorldWrapper.createNew(world)
        return worldCache[world.provider.dimension]!!
    }

    override fun getIWorld(dimensionId: Int): IWorld {
        for (world in CustomNpcs.Server.worlds) {
            if (world.provider.dimension == dimensionId)
                return getIWorld(world)
        }
        throw CustomNPCsException("Unknown dimension id: $dimensionId")
    }

    override fun getIContainer(inventory: IInventory): IContainer {
        return ContainerWrapper(inventory)
    }

    override fun getIContainer(container: Container): IContainer {
        return ContainerWrapper(container)
    }

    override fun getIPos(x: Double, y: Double, z: Double): IPos {
        return BlockPosWrapper(BlockPos(x, y, z))
    }

    override fun registerCommand(command: CommandNoppesBase) {
        CustomNpcs.NoppesCommand.registerCommand(command)
    }

    override fun getINbt(compound: NBTTagCompound): INbt {
        return NBTWrapper(compound)
    }

    override fun stringToNbt(str: String): INbt {
        if (str.isEmpty())
            throw CustomNPCsException("Cant cast empty string to nbt")
        try {
            return getINbt(NBTJsonUtil.Convert(str))
        } catch (e: JsonException) {
            throw CustomNPCsException(e, "Failed converting $str")
        }

    }

    override fun getIDamageSource(damagesource: DamageSource): IDamageSource {
        return DamageSourceWrapper(damagesource)
    }

    override fun executeCommand(world: IWorld, command: String): String {
        val player = EntityNPCInterface.CommandPlayer
        player.setWorld(world.mcWorld)
        player.setPosition(0.0, 0.0, 0.0)
        return NoppesUtilServer.runCommand(world.mcWorld, BlockPos.ORIGIN, "API", command, null, player)
    }

    override fun getRawPlayerData(uuid: String): INbt {
        return getINbt(PlayerData.loadPlayerData(uuid))
    }

}
