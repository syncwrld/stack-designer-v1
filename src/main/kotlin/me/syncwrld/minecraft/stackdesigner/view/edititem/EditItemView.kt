package me.syncwrld.minecraft.stackdesigner.view.edititem

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor
import com.henryfabio.minecraft.inventoryapi.event.impl.CustomInventoryClickEvent
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer
import me.syncwrld.minecraft.stackdesigner.StackDesigner
import me.syncwrld.minecraft.stackdesigner.ext.color
import me.syncwrld.minecraft.stackdesigner.ext.removeColorCodes
import me.syncwrld.minecraft.stackdesigner.ext.send
import me.syncwrld.minecraft.stackdesigner.model.template.StackTemplate
import me.syncwrld.minecraft.stackdesigner.util.item.ItemBuilder
import me.syncwrld.minecraft.stackdesigner.util.prompt.ResponseWaiter
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class EditItemView(val plugin: StackDesigner) : SimpleInventory("stackdesigner.edit-item", "Editar item", 4 * 9) {

    private val stacks = EditItemStacks()

    override fun configureInventory(viewer: Viewer, editor: InventoryEditor) {
        val player = viewer.player
        val propertyMap = viewer.propertyMap
        val stackTemplate = propertyMap.get<StackTemplate>("stack")

        editor.setItem(10, itemOf(stacks.stackInput, {
            close(player)

            player.send("", "&7Digite o novo tipo/textura do item", "&7para cancelar, digite 'cancelar'.", "")
            plugin.responseWaiter.ask(player, ResponseWaiter.RequiredType.STRING) { response ->
                stackTemplate.stackInput = response
                stackTemplate.stackData = 0
                stackTemplate.saveToFile()
                reopen(player, stackTemplate)
            }
        }, {
            close(player)

            player.send("", "&7Digite o novo valor da data do item", "&7para cancelar, digite 'cancelar'.", "")
            plugin.responseWaiter.ask(player, ResponseWaiter.RequiredType.INTEGER) { response ->
                stackTemplate.stackData = response.toInt()
                stackTemplate.saveToFile()
                reopen(player, stackTemplate)
            }
        }))

        editor.setItem(11, itemOf(stacks.editFlags, {
            plugin.views.itemFlagsView.openInventory(player) { viewer: Viewer ->
                val ifViewPropertyMap = viewer.propertyMap
                ifViewPropertyMap["stack"] = stackTemplate
            }
        }, null))


        var changeColor = stacks.changeColor
        if (changeColor.type.name.contains("INK_SAC")) {
            changeColor = ItemBuilder(changeColor).data(9).build()
        }

        editor.setItem(12, itemOf(changeColor, {
            close(player)

            player.send("", "&7Digite o novo valor da cor do item (coloque sem o símbolo)", "&7para cancelar, digite 'cancelar'.", "")
            plugin.responseWaiter.ask(player, ResponseWaiter.RequiredType.STRING) { response ->
                val colorByChar = ChatColor.getByChar(response)
                if (colorByChar == null) {
                    player.send("&cA cor digitada não é válida. Operação cancelada.")
                    return@ask
                }

                val color = if (!response.startsWith("&") || response.startsWith("§")) "&$response" else response
                if (color.length != 2) {
                    player.send("&cA cor deve ter apenas 1 caractere. Ex.: a, 1, 3, c. Operação cancelada.")
                    return@ask
                }

                stackTemplate.displayName = color + stackTemplate.displayName.removeColorCodes()
                stackTemplate.description = stackTemplate.description.map { color + it.removeColorCodes() }.toMutableList()
                stackTemplate.saveToFile()

                reopen(player, stackTemplate)
            }
        }, null))

        editor.setItem(19, itemOf(stacks.changeAmount, {
            close(player)

            player.send("", "&7Digite o novo valor da quantidade do item", "&7para cancelar, digite 'cancelar'.", "")
            plugin.responseWaiter.ask(player, ResponseWaiter.RequiredType.INTEGER) { response ->
                stackTemplate.amount = response.toInt()
                stackTemplate.saveToFile()
                reopen(player, stackTemplate)
            }
        }, null))


        val glowingStatus = if (stackTemplate.glow) "&aAtivado" else "&cDesabilitado"
        editor.setItem(20   , itemOf(ItemBuilder(stacks.toggleGlow).addReplacement("#stats", glowingStatus).build(), {
            stackTemplate.glow = !stackTemplate.glow
            stackTemplate.saveToFile()
            reopen(player, stackTemplate)
        }, null))

        editor.setItem(14, itemOf(stacks.editDisplayName, {
            close(player)

            player.send("", "&7Digite o novo nome de exibição do item", "&7para cancelar, digite 'cancelar'.", "")
            plugin.responseWaiter.ask(player, ResponseWaiter.RequiredType.STRING) { response ->
                stackTemplate.displayName = response.color()
                stackTemplate.saveToFile()
                reopen(player, stackTemplate)
            }
        }, null))

        editor.setItem(23, itemOf(stacks.editLore, {
            close(player)

            val possibleValues = mutableListOf<String>()
            stackTemplate.description.size.let { size ->
                for (i in 0 until size) {
                    possibleValues.add(i.toString())
                }
            }

            player.send(
                "",
                "&7Digite o índice da linha que deseja editar",
                "&7Valores possíveis válidos: ${possibleValues.joinToString(", ")}",
                "&7Dica: você pode criar uma linha que não existe ainda. Basta digitar o índice.",
                "Ex.: ${stackTemplate.description.size}",
                "",
                "&7Caso queira cancelar, digite 'cancelar'.",
                ""
            )
            plugin.responseWaiter.ask(player, ResponseWaiter.RequiredType.INTEGER) { indexResponse ->
                val index = indexResponse.toInt()
                if (index < 0) {
                    player.send("&cO índice não pode ser menor que 0.")
                    return@ask
                }

                player.send("", "&7Digite o novo valor da linha $index", "&7para cancelar, digite 'cancelar'.", "")
                plugin.responseWaiter.ask(player, ResponseWaiter.RequiredType.STRING) { response ->

                    if (index >= stackTemplate.description.size) {
                        while (stackTemplate.description.size <= index) {
                            stackTemplate.description.add("")
                        }
                    }

                    stackTemplate.description[index] = response.color()
                    stackTemplate.saveToFile()
                    reopen(player, stackTemplate)
                }
            }
        }, null))

        editor.setItem(16, itemOf(stackTemplate.toStack(plugin), {
            close(player)
            player.inventory.addItem(stackTemplate.toStack(plugin))
            player.updateInventory()
        }, {
            close(player)

            player.send(
                "",
                "&7Digite a quantidade de itens deste",
                "&7tipo que você quer receber em seu inventário",
                "",
                "&7Digite 'cancelar' para cancelar a ação.",
                ""
            )
            plugin.responseWaiter.ask(player, ResponseWaiter.RequiredType.INTEGER) { response ->
                player.inventory.addItem(stackTemplate.toStack(plugin).apply { amount = response.toInt() })
                player.updateInventory()
            }
        }))
    }

    private fun itemOf(
        stack: ItemStack,
        onLeftClick: (CustomInventoryClickEvent) -> Unit,
        onRightClick: ((CustomInventoryClickEvent?) -> Unit?)?
    ): InventoryItem {
        return InventoryItem.of(stack)
            .defaultCallback { it.isCancelled = true }
            .callback(ClickType.LEFT) { clickEvent ->
                clickEvent.isCancelled = true
                onLeftClick(clickEvent)
            }
            .callback(ClickType.RIGHT, { clickEvent ->
                clickEvent.isCancelled = true
                if (onRightClick != null) {
                    onRightClick(clickEvent)
                }
            })
    }

    override fun update(viewer: Viewer, editor: InventoryEditor) {
        super.update(viewer, editor)
        configureInventory(viewer, editor)
    }

    private fun close(player: Player) {
        player.openInventory?.close()
    }

    private fun reopen(player: Player, stackTemplate: StackTemplate) {
        close(player)
        plugin.views.editItem.openInventory(player) { viewer: Viewer ->
            val propertyMap = viewer.propertyMap
            propertyMap["stack"] = stackTemplate
        }
    }

}