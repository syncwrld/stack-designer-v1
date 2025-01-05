package me.syncwrld.minecraft.stackdesigner.view.edititem

import me.syncwrld.minecraft.stackdesigner.util.item.ItemBuilder
import org.bukkit.Material
import tryIfNot

class EditItemStacks {

    val toggleGlow = ItemBuilder(Material.BEACON)
        .name("&dGlow/Brilho")
        .lore(
            listOf(
                "&7Ativa/desativa o brilho do item.",
                "&7Status: &c#stats.",
                "",
                "&7Clique para alterar."
            )
        )
        .build()

    val editFlags = ItemBuilder(tryIfNot("NAME_TAG", "NAMETAG"))
        .name("&dFlags do Item")
        .lore(
            listOf(
                "&7As flags te deixam escolher o que",
                "&7mostrar ou esconder dos atributos",
                "&7do item que está editando.",
                "",
                "&7Clique para visualizar e editar."
            )
        )
        .build()

    val changeColor = ItemBuilder(tryIfNot("PINK_DYE", "INK_SACK", "INK_SACK"))
        .name("&dCores do Item")
        .lore(
            listOf(
                "&7Altera a cor primária do",
                "&7item e substitui pela escolhida.",
                "",
                "&7Clique para alterar."
            )
        )
        .build()

    val changeAmount = ItemBuilder(tryIfNot("STORAGE_MINECART", "CHEST_MINECART"))
        .name("&dQuantidade")
        .lore(
            listOf(
                "&7Altere a quantidade do item.",
                "",
                "&7Clique para alterar."
            )
        )
        .build()

    val stackInput = ItemBuilder(tryIfNot("BOOK_AND_QUILL", "WRITABLE_BOOK"))
        .name("&dTextura/Material")
        .lore(
            listOf(
                "&7Altera o tipo ou textura do item.",
                "",
                " &eFormas de utilização &7(tipo)&e:",
                " &8▸ &fMaterial: &7GRASS_BLOCK",
                " &8▸ &fCabeça: &7HEAD:fa1e66d6c...",
                "",
                "&7B. Esquerdo: &dAlterar o tipo",
                "&7B. Direito: &dAlterar a data"
            )
        )
        .build()

    val editDisplayName = ItemBuilder(Material.PAPER)
        .name("&dNome de Exibição")
        .lore(
            listOf(
                "&7Troca o nome de exibição do item.",
                "",
                "&7Clique para alterar."
            )
        )
        .build()

    val editLore = ItemBuilder(Material.BOOK)
        .name("&dDescrição")
        .lore(
            listOf(
                "&7Troca a descrição do item.",
                "",
                "&6&lDICA!",
                "&fPara editar a lore de forma precisa e fácil,",
                "&fedite o arquivo na pasta do &6StackDesigner &f;)",
                "",
                "&7Clique para alterar."
            )
        )
        .build()

}