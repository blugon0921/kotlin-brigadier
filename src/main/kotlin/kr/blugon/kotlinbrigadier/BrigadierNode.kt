package kr.blugon.kotlinbrigadier

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.Commands.argument
import net.minecraft.commands.SharedSuggestionProvider
import kotlin.reflect.KProperty


interface BrigadierNode {
    fun then(literal: String, node: LiteralBrigadierNode.() -> Unit = {})
    fun <T> then(argument: Pair<String, ArgumentType<T>>, node: RequiredBrigadierNode<T>.() -> Unit = {})
    fun require(require: (CommandSourceStack) -> Boolean)
    fun requires(requires: (CommandSourceStack) -> List<Boolean>)
    fun executes(execute: CommandSourceStack.(CommandContext<CommandSourceStack>) -> Unit)
    operator fun String.invoke(node: LiteralBrigadierNode.() -> Unit) = then(this, node)
}

class LiteralBrigadierNode(val builder: LiteralArgumentBuilder<CommandSourceStack>): BrigadierNode {
    override fun then(literal: String, node: LiteralBrigadierNode.() -> Unit) {
        builder.then(Commands.literal(literal).apply {
            node(LiteralBrigadierNode(this))
        })
    }
    override fun <T> then(argument: Pair<String, ArgumentType<T>>, node: RequiredBrigadierNode<T>.() -> Unit) {
        builder.then(argument(argument.first, argument.second).apply {
            node(RequiredBrigadierNode(this))
        })
    }

    override fun require(require: CommandSourceStack.() -> Boolean) {
        builder.requires(require)
    }
    override fun requires(requires: CommandSourceStack.() -> List<Boolean>) {
        var isRequire = true
        builder.requires { source ->
            val conditions = requires(source)
            conditions.forEach {
                isRequire = isRequire && it
            }
            isRequire
        }
    }

    override fun executes(execute: CommandSourceStack.(CommandContext<CommandSourceStack>) -> Unit) {
        builder.executes { c->
            execute(c.source, c)
            return@executes 1
        }
    }
}
class RequiredBrigadierNode <T> (val builder: RequiredArgumentBuilder<CommandSourceStack, T>): BrigadierNode {
    override fun then(literal: String, node: LiteralBrigadierNode.() -> Unit) {
        builder.then(Commands.literal(literal).apply {
            node(LiteralBrigadierNode(this))
        })
    }
    override fun <T> then(argument: Pair<String, ArgumentType<T>>, node: RequiredBrigadierNode<T>.() -> Unit) {
        builder.then(argument(argument.first, argument.second).apply {
            node(RequiredBrigadierNode(this))
        })
    }
    override fun require(require: CommandSourceStack.() -> Boolean) {
        builder.requires(require)
    }
    override fun requires(requires: CommandSourceStack.() -> List<Boolean>) {
        var isRequire = true
        builder.requires { source ->
            val conditions = requires(source)
            conditions.forEach {
                isRequire = isRequire && it
            }
            isRequire
        }
    }
    fun suggests(isSharedSuggestion: Boolean = true, suggest: CommandSourceStack.(CommandContext<CommandSourceStack>) -> List<String>) {
        builder.suggests { context, suggestionsBuilder ->
            if(isSharedSuggestion) SharedSuggestionProvider.suggest(suggest(context.source, context), suggestionsBuilder)
            else {
                suggest(context.source, context).forEach { suggestion->
                    suggestionsBuilder.suggest(suggestion)
                }
                suggestionsBuilder.buildFuture()
            }
        }
    }
    fun suggests(suggestions: List<String>, isSharedSuggestion: Boolean = true) {
        suggests(isSharedSuggestion) {
            suggestions
        }
    }
    fun suggestsWithBuilder(suggest: CommandSourceStack.(CommandContext<CommandSourceStack>, SuggestionsBuilder) -> Unit) {
        builder.suggests { commandContext, suggestionsBuilder ->
            suggest(commandContext.source, commandContext, suggestionsBuilder)
            suggestionsBuilder.buildFuture()
        }
    }

    override fun executes(execute: CommandSourceStack.(CommandContext<CommandSourceStack>) -> Unit) {
        builder.executes { c->
            execute(c.source, c)
            return@executes 1
        }
    }
}

inline operator fun <reified T> CommandContext<CommandSourceStack>.get(name: String): T {
    return this.getArgument(name, T::class.java)
}

inline operator fun <reified T> CommandContext<CommandSourceStack>.getValue(thisRef: Any?, property: KProperty<*>): T {
    return this.getArgument(property.name, T::class.java)
}