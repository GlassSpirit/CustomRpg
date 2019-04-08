package ru.glassspirit.cnpcs.event

import net.minecraft.entity.player.EntityPlayer
import noppes.npcs.api.NpcAPI
import noppes.npcs.api.entity.IPlayer
import noppes.npcs.api.event.CustomNPCsEvent

class AvailabilityEvent(player: EntityPlayer) : CustomNPCsEvent() {
    val player: IPlayer<*> = NpcAPI.Instance().getIEntity(player) as IPlayer<*>
}
