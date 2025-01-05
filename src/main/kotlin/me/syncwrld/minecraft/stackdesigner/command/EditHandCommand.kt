package me.syncwrld.minecraft.stackdesigner.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer
import me.syncwrld.minecraft.stackdesigner.StackDesigner
import me.syncwrld.minecraft.stackdesigner.ext.send
import me.syncwrld.minecraft.stackdesigner.util.prompt.ResponseWaiter
import org.bukkit.Material
import org.bukkit.entity.Player

@CommandAlias("%editHandCommand")
class EditHandCommand(private val plugin: StackDesigner) : BaseCommand() {

    @Default
    @CommandPermission("stackdesigner.edithand")
    fun editHandCommand(player: Player) {
        val itemInHand = player.itemInHand

        if (itemInHand == null || itemInHand.type == Material.AIR) {
            player.send("&cVocê precisa estar segurando um item para editar.")
            return
        }

        player.send("", "&7Digite a categoria do item.", "&7para cancelar, digite 'cancelar'.", "")
        plugin.responseWaiter.ask(player, ResponseWaiter.RequiredType.STRING) outer@{ categoryResponse ->
            player.send("", "&7Digite o nome do item.", "&7para cancelar, digite 'cancelar'.", "")
            plugin.responseWaiter.ask(player, ResponseWaiter.RequiredType.STRING) inner@{ itemIdResponse ->
                val exists = plugin.stacks.existsStack(categoryResponse, itemIdResponse)

                if (exists) {
                    player.send("&cJá existe um item com esse nome nesta categoria. Operação cancelada.")
                    return@inner
                }

                val stackTemplate = plugin.stacks.createBased(categoryResponse, itemIdResponse, itemInHand)
                stackTemplate.saveToFile()

                player.send("&aItem baseado no atual criado com sucesso. Abrindo menu de edição...")
                plugin.views.editItem.openInventory(player) { viewer: Viewer ->
                    val propertyMap = viewer.propertyMap
                    propertyMap["stack"] = stackTemplate
                }
            }
        }

    }

}