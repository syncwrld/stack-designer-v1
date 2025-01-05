package me.syncwrld.minecraft.stackdesigner.util.item

import me.syncwrld.minecraft.stackdesigner.StackDesigner
import me.syncwrld.minecraft.stackdesigner.ext.color
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta

class ItemBuilder {

    private val baseStack: ItemStack
    private val replacements = mutableMapOf<String, Any>()
    private var itemFlags: Array<ItemFlag>? = null

    constructor(input: String, plugin: StackDesigner) {
        baseStack = try {
            ItemAdapter(input).toStack(plugin)
        } catch (e: IllegalArgumentException) {
            ItemBuilder(Material.BARRIER)
                .name("§cItem inválido")
                .lore(listOf("§7O item que você tentou criar é inválido."))
                .build()
        }
    }

    constructor(playerName: String, plugin: StackDesigner, ignored: Boolean) {
        baseStack = plugin.headUtil.toItem(playerName)
    }

    constructor(material: Material, data: Byte = 0) {
        baseStack = ItemStack(material, 1, data.toShort())
    }

    constructor(baseStack: ItemStack) {
        this.baseStack = baseStack
    }

    fun addReplacement(key: String, value: Any): ItemBuilder {
        replacements[key] = value
        return this
    }

    fun addBracketReplacement(key: String, value: Any): ItemBuilder {
        val formattedKey = key.run {
            (if (!startsWith("\${")) "\${" else "") + this + (if (!endsWith("}")) "}" else "")
        }
        return addReplacement(formattedKey, value)
    }

    fun amount(amount: Int): ItemBuilder {
        baseStack.amount = amount
        return this
    }

    fun type(material: Material): ItemBuilder {
        baseStack.type = material
        return this
    }

    fun data(data: Byte): ItemBuilder {
        if (isDye()) {
            if (!this.baseStack.type.name.startsWith("INK_SAC")) {
                return this
            }
            return this
        }

        baseStack.durability = data.toShort()
        return this
    }

    fun data(data: Int): ItemBuilder {
        data(data.toByte())
        return this
    }

    fun durability(durability: Short): ItemBuilder {
        baseStack.durability = durability
        return this
    }

    fun name(name: String): ItemBuilder {
        editAndApply { it.displayName = name.color() }
        return this
    }

    fun flags(vararg flags: ItemFlag): ItemBuilder {
        editAndApply { it.addItemFlags(*flags) }
        return this
    }

    fun armorColor(red: Int, green: Int, blue: Int): ItemBuilder {
        if (!baseStack.type.name.contains("LEATHER_")) {
            throw IllegalArgumentException("The item must be a part of a leather armor to set the color.")
        }
        editAndApply { (it as LeatherArmorMeta).color = Color.fromRGB(red, green, blue) }
        return this
    }

    fun hideAllFlags(): ItemBuilder {
        itemFlags = itemFlags ?: ItemFlag.entries.toTypedArray()
        editAndApply { it.addItemFlags(*itemFlags!!) }
        return this
    }

    fun enchantment(enchantment: Enchantment, level: Int): ItemBuilder {
        editAndApply {
            val maxLevel = enchantment.maxLevel
            it.addEnchant(enchantment, level.coerceAtMost(maxLevel), true)
        }
        return this
    }

    fun lore(lore: List<String>): ItemBuilder {
        editAndApply { it.lore = lore.color() }
        return this
    }

    fun glow() {
        enchantment(Enchantment.LUCK, 1)
        hideAllFlags()
    }

    fun skullOwner(owner: String): ItemBuilder {
        if (baseStack.type != skullMaterial()) {
            throw IllegalArgumentException("The item must be a skull to set the skull owner.")
        }
        editAndApply { (it as SkullMeta).owner = owner }
        return this
    }

    fun skullMaterial(): Material {
        return try {
            Material.valueOf("PLAYER_HEAD")
        } catch (e: IllegalArgumentException) {
            Material.valueOf("SKULL_ITEM")
        }
    }

//    fun nbt(nbtConsumer: Consumer<ReadWriteItemNBT>): ItemBuilder {
//        NBT.modify(baseStack, nbtConsumer)
//        return this
//    }

    fun build(): ItemStack {
        if (replacements.isNotEmpty()) {
            val meta = baseStack.itemMeta ?: return baseStack

            meta.displayName = meta.displayName.color();
            meta.lore = meta.lore?.map { it.color() }

            replacements.forEach { (key, value) ->
                meta.displayName = (meta.displayName?.replace(key, value.toString()))?.color() ?: meta.displayName
                meta.lore = meta.lore?.map { it.replace(key, value.toString()).color() }
            }

            baseStack.itemMeta = meta
        }
        return baseStack
    }

    private fun isDye(): Boolean {
        return baseStack.type.name.contains("DYE") || baseStack.type.name.startsWith("INK_SAC")
    }

    private fun editAndApply(action: (ItemMeta) -> Unit) {
        val meta = baseStack.itemMeta ?: return
        action(meta)
        baseStack.itemMeta = meta
    }

    fun clone(): ItemStack = build().clone()
}
