package me.syncwrld.minecraft.stackdesigner

import co.aikar.commands.Locales
import co.aikar.commands.PaperCommandManager
import me.syncwrld.minecraft.stackdesigner.command.CreateItemCommand
import me.syncwrld.minecraft.stackdesigner.command.EditHandCommand
import me.syncwrld.minecraft.stackdesigner.command.EditItemCommand
import me.syncwrld.minecraft.stackdesigner.command.GiveItemCommand
import me.syncwrld.minecraft.stackdesigner.command.ExportCommand
import me.syncwrld.minecraft.stackdesigner.ext.color
import me.syncwrld.minecraft.stackdesigner.management.Stacks
import me.syncwrld.minecraft.stackdesigner.model.option.ExecutionOptions
import me.syncwrld.minecraft.stackdesigner.util.item.HeadUtil
import me.syncwrld.minecraft.stackdesigner.util.prompt.ResponseWaiter
import me.syncwrld.minecraft.stackdesigner.view.Views
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class StackDesigner : JavaPlugin() {

    internal val headUtil = HeadUtil()
    internal val responseWaiter = ResponseWaiter(this)
    internal val stacks = Stacks(this)
    internal val views = Views(this)

    private lateinit var commandManager: PaperCommandManager
    private lateinit var executionOptions: ExecutionOptions

    override fun onLoad() {
        log("StackDesigner está carregando...")

        saveDefaultConfig()
        executionOptions = ExecutionOptions(
            config.getString("executions.edit-item") ?: "edititem|sdei",
            config.getString("executions.edit-hand") ?: "edithand|sdeh",
            config.getString("executions.create-item") ?: "createitem|sdci",
            config.getString("executions.give-item") ?: "giveitem|sdgi",
            config.getString("executions.export-category") ?: "export|sdex"
        )
    }

    override fun onEnable() {
        views.setup()
        responseWaiter.setup()
        registerCommands()

        log("StackDesigner está pronto e ativo.")
    }

    override fun onDisable() {
        log("&cStackDesigner foi desabilitado.")
    }

    private fun registerCommands() {
        commandManager = PaperCommandManager(this)
        commandManager.locales.defaultLocale = Locales.PORTUGUESE
        commandManager.enableUnstableAPI("help")

        commandManager.commandReplacements.addReplacement("editItemCommand", executionOptions.editItemCommand)
        commandManager.commandReplacements.addReplacement("editHandCommand", executionOptions.editHandCommand)
        commandManager.commandReplacements.addReplacement("createItemCommand", executionOptions.createItemCommand)
        commandManager.commandReplacements.addReplacement("giveItemCommand", executionOptions.giveItemCommand)
        commandManager.commandReplacements.addReplacement("exportCommand", executionOptions.exportCommand)

        commandManager.commandCompletions.registerCompletion("categories") {
            val categoryList = stacks.getCategories()
            categoryList
        }

        commandManager.commandCompletions.registerCompletion("stacks") { context ->
            val stackList = stacks.findSimilarStacks("every", context.input)
            stackList
        }

        commandManager.registerCommand(EditItemCommand(this))
        commandManager.registerCommand(EditHandCommand(this))
        commandManager.registerCommand(CreateItemCommand(this))
        commandManager.registerCommand(GiveItemCommand(this))
        commandManager.registerCommand(ExportCommand(this))
    }

    fun log(vararg messages: String) {
        messages.forEach { Bukkit.getConsoleSender().sendMessage("§6[StackDesigner] §f" + it.color()) }
    }

}
