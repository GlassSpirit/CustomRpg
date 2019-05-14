package noppes.npcs.api

import net.minecraft.entity.Entity
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.eventhandler.EventBus
import noppes.npcs.api.block.IBlock
import noppes.npcs.api.entity.ICustomNpc
import noppes.npcs.api.entity.IEntity
import noppes.npcs.api.handler.*
import noppes.npcs.api.item.IItemStack

import java.io.File

/**
 * Note this API should only be used Server side not on the client
 */
interface NpcAPI {

    val iWorlds: Array<IWorld>

    val factions: IFactionHandler

    val recipes: IRecipeHandler

    val quests: IQuestHandler

    val dialogs: IDialogHandler

    val clones: ICloneHandler

    /**
     * @return Returns the .minecraft/customnpcs folder or [yourserverfolder]/customnpcs
     */
    val globalDir: File

    /**
     * @return Returns the .minecraft/saves/[yourworld]/customnpcs folder or [yourserverfolder]/[yourworld]/customnpcs
     */
    val worldDir: File

    /**
     * Doesnt spawn the npc in the world
     */
    fun createNPC(world: World): ICustomNpc<*>

    /**
     * Creates and spawns an npc
     */
    fun spawnNPC(world: World, x: Int, y: Int, z: Int): ICustomNpc<*>

    fun getIEntity(entity: Entity): IEntity<*>

    fun getIBlock(world: World, pos: BlockPos): IBlock

    fun getIContainer(inventory: IInventory): IContainer

    fun getIContainer(container: Container): IContainer

    fun getIItemStack(itemstack: ItemStack): IItemStack?

    fun getIWorld(world: WorldServer): IWorld

    fun getIWorld(dimensionId: Int): IWorld

    fun getINbt(compound: NBTTagCompound): INbt

    fun getIPos(x: Double, y: Double, z: Double): IPos

    fun getIDamageSource(damagesource: DamageSource): IDamageSource

    fun stringToNbt(str: String): INbt

    /**
     * Get player data even if they are offline
     *
     * @param uuid
     * @return
     */
    fun getRawPlayerData(uuid: String): INbt

    /**
     * Used by modders
     *
     * @return The event bus where you init CustomNPCEvents
     */
    fun events(): EventBus

    /**
     * Use to init your own /noppes subcommand
     */
    fun registerCommand(command: CommandNoppesBase)

    /**
     * @param permission  Permission node, best if it's lowercase and contains '.' (e.g. `"modid.subgroup.permission_id"`)
     * @param defaultType 0:ALL, 1:OP, 2:NONE. This determines who can use the permission by default everybody, only ops or nobody
     */
    fun registerPermissionNode(permission: String, defaultType: Int)

    fun hasPermissionNode(permission: String): Boolean

    /**
     * @param world   The world in which the command is executed
     * @param command The Command to execute
     * @return
     */
    fun executeCommand(world: IWorld, command: String): String

    companion object {
        private var instance: NpcAPI? = null

        @JvmStatic
        fun isAvailable(): Boolean {
            return Loader.isModLoaded("customnpcs")
        }

        @JvmStatic
        fun instance(): NpcAPI {
            if (instance != null)
                return instance!!

            try {
                val c = Class.forName("noppes.npcs.api.wrapper.WrapperNpcAPI")
                instance = c.getMethod("instance").invoke(null) as NpcAPI
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return instance!!
        }
    }
}
