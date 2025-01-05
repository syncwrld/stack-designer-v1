package me.syncwrld.minecraft.stackdesigner.view

import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager
import me.syncwrld.minecraft.stackdesigner.StackDesigner
import me.syncwrld.minecraft.stackdesigner.view.edititem.EditItemStacks
import me.syncwrld.minecraft.stackdesigner.view.edititem.EditItemView
import me.syncwrld.minecraft.stackdesigner.view.itemflags.EditItemFlagsView

class Views(val plugin: StackDesigner) {

    lateinit var editItem: EditItemView
    lateinit var itemFlagsView: EditItemFlagsView

    fun setup() {
        InventoryManager.enable(plugin)
        editItem = EditItemView(plugin).init()
        itemFlagsView = EditItemFlagsView(plugin).init()
    }

}