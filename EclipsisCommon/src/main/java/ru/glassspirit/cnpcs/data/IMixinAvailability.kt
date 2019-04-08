package ru.glassspirit.cnpcs.data

import net.minecraft.nbt.NBTTagCompound
import noppes.npcs.controllers.IScriptHandler

interface IMixinAvailability : IScriptHandler {

    fun readScriptFromNbt(compound: NBTTagCompound)

    fun writeScriptToNbt(compound: NBTTagCompound): NBTTagCompound

}
