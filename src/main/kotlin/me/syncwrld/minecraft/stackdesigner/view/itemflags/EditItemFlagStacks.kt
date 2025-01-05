package me.syncwrld.minecraft.stackdesigner.view.itemflags

import me.syncwrld.minecraft.stackdesigner.util.item.ItemBuilder
import org.bukkit.inventory.ItemFlag
import tryIfNot

class EditItemFlagStacks {

    val flagsDisplayName = mapOf(
        Pair(ItemFlag.HIDE_ATTRIBUTES, "Esconder Atributos"),
        Pair(ItemFlag.HIDE_DESTROYS, "Esconder Destruição"),
        Pair(ItemFlag.HIDE_ENCHANTS, "Esconder Encantamentos"),
        Pair(ItemFlag.HIDE_PLACED_ON, "Esconder 'Colocado Em'"),
        Pair(ItemFlag.HIDE_POTION_EFFECTS, "Esconder Efeitos de Poção"),
        Pair(ItemFlag.HIDE_UNBREAKABLE, "Esconder Inquebrável")
    )

    val template = ItemBuilder(
        tryIfNot(
            "STAINED_GLASS_PANE",
            "LEGACY_STAINED_GLASS_PANE",
            "GLASS_PANE",
            "GRAY_STAINED_GLASS_PANE"
        )
    )
        .name("&e#displayName")
        .lore(
            listOf(
                "&7Status: #status.",
                "",
                "&7Clique para alterar."
            )
        )
        .build()

}