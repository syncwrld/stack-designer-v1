package me.syncwrld.minecraft.stackdesigner.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Syntax
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer
import me.syncwrld.minecraft.stackdesigner.StackDesigner
import me.syncwrld.minecraft.stackdesigner.ext.send
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@CommandAlias("%createItemCommand")
class CreateItemCommand(private val plugin: StackDesigner) : BaseCommand() {

    @Default
    @CommandPermission("stackdesigner.createitem")
    @Syntax("<category> <itemId>")
    @CommandCompletion("@categories")
    fun createCommand(player: Player, category: String, itemId: String) {
        val alreadyExists = plugin.stacks.existsStack(category, itemId)

        if (alreadyExists) {
            player.send("&cO item que você tentou criar já existe.")
            return
        }

        val templateStack = plugin.stacks.createEmptyTemplate(category, itemId)
        player.send("&aItem criado com sucesso. Abrindo menu de edição...")

        Bukkit.getScheduler().runTaskLater(plugin, {
            plugin.views.editItem.openInventory(player) { viewer: Viewer ->
                val propertyMap = viewer.propertyMap
                propertyMap["stack"] = templateStack
            }
        }, 10L)
    }


}