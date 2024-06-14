package kr.blugon.kotlinbrigadier

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

val CommandSourceStack.player: Player get() = this.sender as Player
val CommandContext<CommandSourceStack>.sender: CommandSender get() = this.source.sender
val CommandContext<CommandSourceStack>.player: Player get() = this.source.player
val CommandContext<CommandSourceStack>.location: Location get() = this.source.location
val CommandContext<CommandSourceStack>.executor: Entity? get() = this.source.executor