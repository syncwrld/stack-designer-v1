package me.syncwrld.minecraft.stackdesigner.ext

import com.iridium.iridiumcolorapi.IridiumColorAPI
import org.bukkit.ChatColor

fun String.color(): String {
    return IridiumColorAPI.process(this)
}

fun List<String>.color(): List<String> {
    return this.map { it.color() }
}

fun String.removeColorCodes(): String {
    return ChatColor.stripColor(this)
}

fun List<String>.removeColorCodes(): List<String> {
    return this.map { it.removeColorCodes() }
}