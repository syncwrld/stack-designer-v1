package me.syncwrld.minecraft.stackdesigner.util.item

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.util.*

/**
 * Classe simples para criar uma cabeça com textura personalizada.
 * Alto desempenho comparada a outras soluções, pois utiliza cache.
 *
 * @author syncwrld
 * @version 1.2
 * @since 06/06/2024
 */
class HeadUtil {

    private val defaultHead: ItemStack = ItemBuilder(findHeadMaterial(), 3)
        .name("§cCabeça inválida")
        .lore(listOf("§7A cabeça que você tentou usar é inválida."))
        .build()

    private val cachedHeads: MutableMap<String, ItemStack> = mutableMapOf()

    private fun findHeadMaterial(): Material = try {
        Material.valueOf("PLAYER_HEAD")
    } catch (e: IllegalArgumentException) {
        Material.valueOf("SKULL_ITEM")
    }

    fun toItem(textureOrUsername: String): ItemStack {
        return cachedHeads.getOrPut(textureOrUsername) { createHead(textureOrUsername) }
    }

    private fun createHead(textureOrUsername: String): ItemStack {
        var head = defaultHead.clone()

        if (textureOrUsername.length <= 16) {
            try {
                val meta = head.itemMeta as? SkullMeta
                meta?.owner = textureOrUsername
                head.itemMeta = meta
            } catch (e: Exception) {
                head = defaultHead.clone()
                e.printStackTrace()
            }
            return head
        }

        return createWithUrl(textureOrUsername)
    }

    private fun createWithUrl(url: String?): ItemStack {
        var formattedUrl = url ?: return defaultHead.clone()
        if (!formattedUrl.startsWith("http://textures.minecraft.net/texture/")) {
            formattedUrl = "http://textures.minecraft.net/texture/$formattedUrl"
        }

        val skull = defaultHead.clone()
        val skullMeta = skull.itemMeta as? SkullMeta ?: return skull

        val profile = GameProfile(UUID.randomUUID(), "synclindao")
        val encodedData = Base64.getEncoder().encodeToString(
            """{"textures":{"SKIN":{"url":"$formattedUrl"}}}""".toByteArray()
        )
        profile.properties.put("textures", Property("textures", encodedData))

        val profileField: Field? = try {
            skullMeta::class.java.getDeclaredField("profile").apply { isAccessible = true }
        } catch (e: NoSuchFieldException) {
            null
        }

        profileField?.let {
            try {
                it.set(skullMeta, profile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        skull.itemMeta = skullMeta
        return skull
    }
}
