package kr.blugon.kotlinbrigadier

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player

val CommandSourceStack.player: Player get() = this.sender as Player