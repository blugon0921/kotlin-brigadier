package kr.blugon.kotlinbrigadier

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.Plugin
import java.util.*


fun LifecycleEventManager<Plugin>.registerEventHandler(command: BrigadierCommand.() -> Unit) {
    this.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
        val registrar = event.registrar()
        command(BrigadierCommand(registrar))
    }
}

class BrigadierCommand(val registrar: Commands) {
    fun register(name: String, description: String = "", vararg aliases: String, node: LiteralBrigadierNode.() -> Unit) {
        val aliasesCollection = mutableListOf<String>()
        aliases.forEach { aliasesCollection.add(it) }
        registrar.register(Commands.literal(name).apply {
            node(LiteralBrigadierNode(this))
        }.build(), description, aliasesCollection)
    }
}