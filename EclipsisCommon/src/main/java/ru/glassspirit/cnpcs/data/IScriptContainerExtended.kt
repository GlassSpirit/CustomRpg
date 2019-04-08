package ru.glassspirit.cnpcs.data

import net.minecraftforge.fml.common.eventhandler.Event

interface IScriptContainerExtended {

    operator fun invoke(function: String, event: Event): Any

}
