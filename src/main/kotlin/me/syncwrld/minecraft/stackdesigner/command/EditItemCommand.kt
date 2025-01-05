package me.syncwrld.minecraft.stackdesigner.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer
import me.syncwrld.minecraft.stackdesigner.StackDesigner
import me.syncwrld.minecraft.stackdesigner.ext.send
import org.bukkit.entity.Player

@CommandAlias("%editItemCommand")
class EditItemCommand(private val plugin: StackDesigner) : BaseCommand() {

    @Default
    @CommandPermission("stackdesigner.edititem")
    @Syntax("<category> <itemId>")
    @CommandCompletion("@categories @stacks")
    fun editItemCommand(player: Player, category: String, itemId: String) {
        val stackTemplate = plugin.stacks.loadTemplate(category, itemId)

        if (stackTemplate == null) {
            player.send("&cO item que você tentou editar não existe.")
            return
        }

        plugin.views.editItem.openInventory(player) { viewer: Viewer ->
            val propertyMap = viewer.propertyMap
            propertyMap["stack"] = stackTemplate
        }
    }


}