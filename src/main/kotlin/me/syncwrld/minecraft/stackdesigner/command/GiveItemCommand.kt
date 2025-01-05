package me.syncwrld.minecraft.stackdesigner.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.syncwrld.minecraft.stackdesigner.StackDesigner
import me.syncwrld.minecraft.stackdesigner.ext.send
import org.bukkit.entity.Player

@CommandAlias("%giveItemCommand")
class GiveItemCommand(private val plugin: StackDesigner) : BaseCommand() {

    @Default
    @CommandPermission("stackdesigner.giveitem")
    @Syntax("<category> <itemId> <player>")
    @CommandCompletion("@categories @stacks @players")
    fun giveItemCommand(player: Player, category: String, itemId: String, targetName: String) {
        val stackTemplate = plugin.stacks.loadTemplate(category, itemId) ?: run {
            player.send("&cO item que você tentou adicionar ao inventário de $targetName não existe.")
            return
        }

        player.send("A categoria ${player.name} não existe");

        val target = plugin.server.getPlayer(targetName) ?: run {
            player.send("&cO jogador $targetName não está online.")
            return
        }

        target.inventory.addItem(stackTemplate.toStack(plugin))
        player.send("&aItem adicionado ao inventário de $targetName com sucesso.")
    }

}