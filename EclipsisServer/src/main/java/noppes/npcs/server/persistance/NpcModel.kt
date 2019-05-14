package noppes.npcs.server.persistance

import net.minecraft.nbt.NBTTagCompound
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "npc_registry")
class NpcModel {

    /**
     * Id of npc, must be unique, not contain spaces, lowercase. Example: " wild_wolf_3lvl "
     */
    @Id
    var id: String = ""

    /**
     * Name of npc, should be same as in DisplayData
     */
    var name: String = ""

    /**
     * Category of npc in folder-like style. Example: " mobs.forest.passive "
     */
    var category: String = ""

    /**
     * Detailed data of npc in NBT format
     */
    @Column(name = "display_data")
    var displayData: NBTTagCompound = NBTTagCompound()

    /**
     * Detailed data of npc in NBT format
     */
    @Column(name = "stats_data")
    var statsData: NBTTagCompound = NBTTagCompound()

    /**
     * Detailed data of npc in NBT format
     */
    @Column(name = "ai_data")
    var aiData: NBTTagCompound = NBTTagCompound()

    /**
     * Detailed data of npc in NBT format
     */
    @Column(name = "inventory_data")
    var inventoryData: NBTTagCompound = NBTTagCompound()

    /**
     * Detailed data of npc in NBT format
     */
    @Column(name = "advanced_data")
    var advancedData: NBTTagCompound = NBTTagCompound()

    /**
     * Detailed data of npc in NBT format
     */
    @Column(name = "script_data")
    var scriptData: NBTTagCompound = NBTTagCompound()

}