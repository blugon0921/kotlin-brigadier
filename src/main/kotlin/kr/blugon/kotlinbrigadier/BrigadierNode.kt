package kr.blugon.kotlinbrigadier

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.Commands.argument


interface BrigadierNode {

    fun then(literal: String, node: LiteralBrigadierNode.() -> Unit = {})
    fun <T> then(argument: Pair<String, ArgumentType<T>>, node: RequiredBrigadierNode<T>.() -> Unit = {})
    fun require(require: (CommandSourceStack) -> Boolean)
    fun requires(requires: (CommandSourceStack) -> List<Boolean>)
    fun executes(execute: CommandContext<CommandSourceStack>.() -> Boolean)
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

    override fun executes(execute: CommandContext<CommandSourceStack>.() -> Boolean) {
        builder.executes { c->
            val response = execute(c)
            if(response) 1
            else 0
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
    fun suggests(suggest: CommandContext<CommandSourceStack>.(SuggestionsBuilder) -> Unit) {
        builder.suggests { commandContext, suggestionsBuilder ->
            suggest(commandContext, suggestionsBuilder)
            suggestionsBuilder.buildFuture()
        }
    }

    override fun executes(execute: CommandContext<CommandSourceStack>.() -> Boolean) {
        builder.executes { c->
            val response = execute(c)
            if(response) 1
            else 0
        }
    }
}