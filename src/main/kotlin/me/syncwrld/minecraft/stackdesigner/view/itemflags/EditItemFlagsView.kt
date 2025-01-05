package me.syncwrld.minecraft.stackdesigner.view.itemflags

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer
import me.syncwrld.minecraft.stackdesigner.StackDesigner
import me.syncwrld.minecraft.stackdesigner.model.template.StackTemplate
import me.syncwrld.minecraft.stackdesigner.util.item.ItemBuilder
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemFlag

class EditItemFlagsView(val plugin: StackDesigner) :
    SimpleInventory("stackdesigner.itemflags", "Selecionar flags", 4 * 9) {

    private val stacks = EditItemFlagStacks()

    override fun configureInventory(viewer: Viewer, editor: InventoryEditor) {
        val propertyMap = viewer.propertyMap

        val stackTemplate = propertyMap.get<StackTemplate>("stack")

        val itemFlags = ItemFlag.entries.toTypedArray()
        val stackFlags = stackTemplate.flags
        val slots = arrayOf(11, 13, 15, 20, 22, 24)

        for ((index, flag) in itemFlags.withIndex()) {
            val flagDisplayName = stacks.flagsDisplayName[flag] ?: continue
            val flagStatus = if (stackFlags.contains(flag)) "&aAtivado" else "&cDesativado"
            val statusData = if (stackFlags.contains(flag)) 5 else 14

            val item = ItemBuilder(stacks.template.clone())
                .data(statusData)
                .name("&e$flagDisplayName")
                .lore(
                    listOf(
                        "&7Status: $flagStatus.",
                        "",
                        "&7Clique para alterar."
                    )
                )
                .build()

            editor.setItem(slots[index], InventoryItem.of(item)
                .defaultCallback { it.isCancelled = true }
                .callback(ClickType.LEFT) { event ->
                    if (event.clickType == ClickType.LEFT) {
                        if (stackFlags.contains(flag)) {
                            stackFlags.remove(flag)
                        } else {
                            stackFlags.add(flag)
                        }

                        stackTemplate.flags = stackFlags
                        stackTemplate.saveToFile()
                        update(viewer, editor)
                    }
                })
        }

    }

    override fun update(viewer: Viewer, editor: InventoryEditor) {
        super.update(viewer, editor)
        configureInventory(viewer, editor)
    }

}