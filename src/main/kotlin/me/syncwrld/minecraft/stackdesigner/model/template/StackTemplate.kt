package me.syncwrld.minecraft.stackdesigner.model.template

import me.syncwrld.minecraft.stackdesigner.StackDesigner
import me.syncwrld.minecraft.stackdesigner.util.item.ItemBuilder
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.io.File

class StackTemplate(
    private val file: File,
    private val configuration: FileConfiguration,
    var displayName: String,
    var description: MutableList<String>,
    var stackInput: String,
    var stackData: Int,
    var glow: Boolean,
    var flags: MutableList<ItemFlag>,
    var amount: Int,
) {

    fun toStack(plugin: StackDesigner): ItemStack {
        val itemBuilder = ItemBuilder(stackInput, plugin)
            .name(displayName)
            .lore(description)
            .data(stackData)
            .amount(amount)

        if (glow) {
            itemBuilder.glow()
        }

        if (flags.isNotEmpty()) {
            itemBuilder.flags(*flags.toTypedArray())
        }

        return itemBuilder.build()
    }

    fun saveToFile() {
        if (!file.exists()) {
            file.createNewFile()
            configuration.save(file)
        }

        configuration.set("display-name", displayName)
        configuration.set("description", description)
        configuration.set("input", stackInput)
        configuration.set("data", stackData)
        configuration.set("glow", glow)
        configuration.set("flags", flags.map { it.name })
        configuration.set("amount", amount)

        configuration.save(file)
        configuration.load(file)
    }

    fun delete(): Boolean {
        return file.delete()
    }

    companion object {
        fun load(file: File): StackTemplate? {
            if (!file.exists())
                return null

            val configuration = YamlConfiguration.loadConfiguration(file)

            val displayName = configuration.getString("display-name") ?: "&cNome inválido"
            val description = configuration.getStringList("description") ?: listOf("&cDescrição inválida")
            val stackInput = configuration.getString("input") ?: "STONE"
            val stackData = configuration.getInt("data")

            val glow = configuration.getBoolean("glow")
            val flags =
                configuration.getStringList("flags")?.mapNotNull { ItemFlag.entries.find { flag -> flag.name == it } }
                    ?: emptyList()

            val amount = configuration.getInt("amount") ?: 1

            return StackTemplate(
                file,
                configuration,
                displayName,
                description.toMutableList(),
                stackInput,
                stackData,
                glow,
                flags.toMutableList(),
                amount
            )
        }
    }

}