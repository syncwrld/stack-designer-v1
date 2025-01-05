package me.syncwrld.minecraft.stackdesigner.util.item

import me.syncwrld.minecraft.stackdesigner.StackDesigner
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import tryIfNot

class ItemAdapter(
    private val input: String,
    private val data: Byte = 0
) {
    constructor(input: String) : this(input, 0)

    private fun getMaterial(): Material {
        require(input.isNotBlank()) { "Material não pode ser nulo ou vazio" }

        if (input.startsWith("HEAD")) {
            return tryIfNot("PLAYER_HEAD", "SKULL_ITEM")
        }

        val dotsSplit = input.split(":")
        if (input.startsWith("COLORED_")) {
            return when (val armorPart = dotsSplit[0].replace("COLORED_", "")) {
                "HELMET" -> Material.LEATHER_HELMET
                "CHESTPLATE" -> Material.LEATHER_CHESTPLATE
                "LEGGINGS" -> Material.LEATHER_LEGGINGS
                "BOOTS" -> Material.LEATHER_BOOTS
                else -> throw IllegalArgumentException("Parte de armadura inválida -> $armorPart")
            }
        }

        if (input.startsWith("INK_SAC") || input.contains("DYE")) {
            return tryIfNot(input, "DYE", "INK_SAC", "INK_SACK")
        }

        return Material.getMaterial(dotsSplit[0].uppercase())
            ?: throw IllegalArgumentException("Material inválido -> $input")
    }

    fun toStack(plugin: StackDesigner): ItemStack {
        require(input.isNotBlank()) { "Stack input não pode ser nulo ou vazio" }

        val dotsSplit = input.split(":")
        val material = getMaterial()

        if (input.startsWith("HEAD")) {
            if (dotsSplit.size < 2) {
                plugin.log("&cCabeça customizada inválida: $input")
                return ItemBuilder(material, 3).name("&cCabeça inválida")
                    .lore(listOf("&7A cabeça que você tentou usar é inválida.")).build()
            }
            val texture = dotsSplit[1]
            return plugin.headUtil.toItem(texture)
        }

        if (input.startsWith("COLORED_")) {
            val split = input.split("_")
            if (split.size < 2) {
                plugin.log("&cParte de armadura inválida: $input")
                return ItemStack(material, 1, data.toShort())
            }

            val armorColor = findArmorColor()
            if (armorColor.isEmpty()) {
                plugin.log("&cCor de armadura inválida: $input")
                return ItemStack(material, 1, data.toShort())
            }

            val color = Color.fromRGB(armorColor[0], armorColor[1], armorColor[2])
            return wrapArmor(material, color)
        }

        if (dotsSplit.size >= 2) {
            val possibleData = dotsSplit[1].toByteOrNull()
            return ItemStack(material, 1, (possibleData ?: data).toShort())
        }

        return ItemStack(material, 1, data.toShort())
    }

    private fun wrapArmor(material: Material, color: Color): ItemStack {
        require(material.name.startsWith("LEATHER_")) { "Material não é de armadura de couro -> ${material.name}" }

        val itemStack = ItemStack(material)
        val itemMeta = itemStack.itemMeta as? LeatherArmorMeta
            ?: throw IllegalArgumentException("Meta não é LeatherArmorMeta -> ${material.name}")
        itemMeta.color = color
        itemStack.itemMeta = itemMeta

        return itemStack
    }

    private fun findArmorColor(): IntArray {
        require(input.startsWith("COLORED_")) { "Material não é de armadura colorida -> $input" }

        val split = input.split(":")
        if (split.size < 2) {
            throw IllegalArgumentException("Cor de armadura inválida -> Use COLORED_ARMOR:R-G-B | Atual: $input")
        }

        val colorSplit = split[1].split("-")
        if (colorSplit.size != 3) {
            throw IllegalArgumentException("Formato de cor inválido -> $input")
        }

        val red = colorSplit[0].toIntOrNull()
            ?: throw IllegalArgumentException("Valor inválido para vermelho -> ${colorSplit[0]}")
        val green = colorSplit[1].toIntOrNull()
            ?: throw IllegalArgumentException("Valor inválido para verde -> ${colorSplit[1]}")
        val blue = colorSplit[2].toIntOrNull()
            ?: throw IllegalArgumentException("Valor inválido para azul -> ${colorSplit[2]}")

        return intArrayOf(red, green, blue)
    }
}
