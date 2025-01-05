package me.syncwrld.minecraft.stackdesigner.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.syncwrld.minecraft.stackdesigner.StackDesigner
import me.syncwrld.minecraft.stackdesigner.ext.send
import me.syncwrld.minecraft.stackdesigner.ext.sound
import org.bukkit.entity.Player
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.random.Random

@CommandAlias("%exportCommand")
class ExportCommand(private val plugin: StackDesigner) : BaseCommand() {

    @Default
    @CommandPermission("stackdesigner.export")
    @Syntax("<category>")
    @CommandCompletion("@categories")
    fun exportCommand(player: Player, category: String) {
        if (!plugin.stacks.getCategories().contains(category)) {
            player.send("&cA categoria que você tentou exportar não existe.")
            return
        }

        player.send("&eExportando categoria: &a${category}...")

        val exportFolder = File(plugin.dataFolder, "exports").apply { mkdirs() }
        val categoryFolder = File(plugin.dataFolder, "stacks/$category")
        val exportFile = File(exportFolder, "${category}-export-latest.zip")

        if (exportFile.exists()) {
            val randomId = Random.nextInt(10000000, 99999999)
            val backupFile = File(exportFolder, "${category}.old-$randomId.zip")
            if (exportFile.renameTo(backupFile)) {
                player.send("&eBackup do export anterior definido como: &f${backupFile.name}")
            } else {
                player.send("&cNão foi possível renomear o backup do export anterior.")
            }
        }

        try {
            ZipOutputStream(FileOutputStream(exportFile)).use { zipOut ->
                categoryFolder.listFiles { file -> file.isFile && file.extension == "yml" }?.forEach { file ->
                    FileInputStream(file).use { fileIn ->
                        zipOut.putNextEntry(ZipEntry(file.name))
                        fileIn.copyTo(zipOut)
                    }
                }
            }

            player.send("&aCategoria exportada com sucesso para: &a${exportFile.name}")
            player.sound("ENTITY_PLAYER_LEVELUP", "LEVEL_UP")
        } catch (e: Exception) {
            player.send("&cOcorreu um erro ao exportar a categoria: ${e.message}")
            e.printStackTrace()
        }
    }

}
