package me.syncwrld.minecraft.stackdesigner.management

import me.syncwrld.minecraft.stackdesigner.StackDesigner
import me.syncwrld.minecraft.stackdesigner.model.template.StackTemplate
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.io.File

class Stacks(private val plugin: StackDesigner) {

    private val baseFolder = File(plugin.dataFolder, "stacks")

    fun getCategories(): List<String> {
        return baseFolder.listFiles()?.filter { it.isDirectory }?.mapNotNull { it.name } ?: emptyList()
    }

    fun existsStack(category: String, stack: String): Boolean {
        return findFileOf(category, stack)?.exists() ?: false
    }

    fun getStacks(category: String): List<String> {
        if ("every".equals(category, ignoreCase = true)) {
            return baseFolder.listFiles()?.filter { it.isDirectory }?.flatMap { it.listFiles()?.filter { it.isFile }?.mapNotNull { it.nameWithoutExtension } ?: emptyList() }
                ?: emptyList()
        }

        return File(File(baseFolder, category).path).listFiles()?.filter { it.isFile }?.mapNotNull { it.nameWithoutExtension }
            ?: emptyList()
    }

    fun findSimilarStacks(category: String, stack: String): List<String> {
        return getStacks(category).filter { it.contains(stack, ignoreCase = true) }
    }

    fun loadTemplate(category: String, stack: String): StackTemplate? {
        val file = findFileOf(category, stack) ?: return null
        return StackTemplate.load(file)
    }

    fun createBased(category: String, stackId: String, baseStack: ItemStack): StackTemplate {
        val categoryFolder = File(baseFolder, category)
        if (!categoryFolder.exists()) {
            categoryFolder.mkdirs()
        }

        val file = findFileOf(category, stackId) ?: File(categoryFolder, "$stackId.yml")
        if (!file.exists()) {
            file.createNewFile()
        }

        val stackTemplate = StackTemplate(
            file,
            YamlConfiguration.loadConfiguration(file),
            baseStack.itemMeta.displayName ?: "&cSem nome.",
            if (baseStack.itemMeta.hasLore()) baseStack.itemMeta.lore else emptyList<String>().toMutableList(),
            baseStack.type.name,
            baseStack.durability.toInt(),
            isUnbreakable(baseStack.itemMeta),
            baseStack.itemMeta.itemFlags.toMutableList(),
            baseStack.amount
        )
        stackTemplate.saveToFile()

        return stackTemplate
    }

    fun createEmptyTemplate(category: String, stack: String): StackTemplate {
        val categoryFolder = File(baseFolder, category)
        if (!categoryFolder.exists()) {
            categoryFolder.mkdirs()
        }

        val file = findFileOf(category, stack) ?: File(categoryFolder, "$stack.yml")
        if (!file.exists()) {
            file.createNewFile()
        }

        val stackTemplate = StackTemplate(
            file,
            YamlConfiguration.loadConfiguration(file),
            "&bFornalha de Aquamarine",
            listOf(
                "&7A fornalha mais potente do jogo.",
            ).toMutableList(),
            "FURNACE",
            0,
            false,
            mutableListOf(
                ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_PLACED_ON
            ),
            1
        )
        stackTemplate.saveToFile()
        return stackTemplate

    }

    private fun findFileOf(category: String, stack: String): File? {
        val stackFilename = if (stack.endsWith(".yml")) stack else "$stack.yml"
        return File(File(baseFolder, category), stackFilename)
    }

    private fun isUnbreakable(meta: ItemMeta): Boolean {
        return try {
            meta.spigot().isUnbreakable
        } catch (e: NoSuchMethodError) {
            try {
                meta.javaClass.getDeclaredMethod("isUnbreakable").invoke(meta) as Boolean
            } catch (e: Exception) {
                false
            }
        }
    }

}