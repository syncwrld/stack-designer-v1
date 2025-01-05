package me.syncwrld.minecraft.stackdesigner.ext

import org.bukkit.Sound
import org.bukkit.entity.Player

fun Player.send(vararg messages: String) {
    messages.forEach { sendMessage(it.color()) }
}

fun Player.sound(vararg soundTries: String) {
    soundTries.forEach {
        try {
            var sound = Sound.valueOf(it)
            playSound(location, sound, 1f, 1f)
        } catch (e: IllegalArgumentException) {
            // ignore
        }
    }
}