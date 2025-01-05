package me.syncwrld.minecraft.stackdesigner.util.prompt

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import java.util.function.Consumer

/**
 * Lightweight class to ask for a response for a player
 *
 * @author syncwrld
 * @website https://github.com/syncwrld
 */
class ResponseWaiter(private val plugin: Plugin) : Listener {

    private val cachedAskers = mutableMapOf<Player, Consumer<String>>()
    private val requiredTypes = mutableMapOf<Player, RequiredType>()
    private val requiredEnums = mutableMapOf<Player, Enum<*>>()

    fun setup() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun ask(player: Player, requiredType: RequiredType, onComplete: Consumer<String>) {
        cachedAskers[player] = onComplete
        requiredTypes[player] = requiredType
    }

    fun askWithEnum(player: Player, requiredType: RequiredType, requiredEnum: Enum<*>, onComplete: Consumer<String>) {
        cachedAskers[player] = onComplete
        requiredTypes[player] = requiredType
        requiredEnums[player] = requiredEnum
    }

    @EventHandler
    fun onResponseDetected(event: AsyncPlayerChatEvent) {
        val player = event.player
        val onComplete = cachedAskers[player]
        val requiredType = requiredTypes[player]

        if (onComplete == null || requiredType == null) return

        val message = event.message
        event.isCancelled = true

        when {
            message.equals("cancelar", ignoreCase = true) || message.equals("cancel", ignoreCase = true) -> {
                cachedAskers.remove(player)
                requiredTypes.remove(player)
                player.sendMessage("§cAção cancelada.")
                return
            }

            requiredType == RequiredType.CONFIRMATION -> {
                if (!message.equals("sim", ignoreCase = true) && !message.equals("yes", ignoreCase = true)) {
                    cachedAskers.remove(player)
                    requiredTypes.remove(player)
                    player.sendMessage(requiredType.errorMessage)
                    return
                }
            }

            requiredType == RequiredType.PLAYER -> {
                val target = Bukkit.getPlayerExact(message)
                if (target == null) {
                    player.sendMessage(requiredType.errorMessage.replace("\${value}", message))
                    return
                }
            }

            requiredType == RequiredType.INTEGER -> {
                if (message.toIntOrNull() == null) {
                    player.sendMessage(requiredType.errorMessage.replace("\${value}", message))
                    return
                }
            }

            requiredType == RequiredType.DOUBLE -> {
                if (message.toDoubleOrNull() == null) {
                    player.sendMessage(requiredType.errorMessage.replace("\${value}", message))
                    return
                }
            }

            requiredType == RequiredType.ENUM -> {
                val requiredEnum = requiredEnums[player]
                if (requiredEnum == null) {
                    player.sendMessage("§cErro interno: Enum não encontrado.")
                    return
                }

                val enumValue = requiredEnum.javaClass.enumConstants.find { it.name.equals(message, ignoreCase = true) }
                if (enumValue == null) {
                    player.sendMessage(requiredType.errorMessage.replace("\${value}", message))
                    return
                }
            }
        }

        cachedAskers.remove(player)
        requiredTypes.remove(player)

        Bukkit.getScheduler().runTask(plugin) {
            onComplete.accept(message)
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val player = event.player
        cachedAskers.remove(player)
        requiredTypes.remove(player)
    }

    enum class RequiredType(val errorMessage: String) {
        PLAYER("§cO jogador '\${value}' não foi encontrado."),
        INTEGER("§cO valor '\${value}' não é um número inteiro."),
        DOUBLE("§cO valor '\${value}' não é um número válido."),
        CONFIRMATION("Você cancelou a operação."),
        ENUM("§cO valor '\${value}' não é um valor válido."),
        STRING("");
    }
}
