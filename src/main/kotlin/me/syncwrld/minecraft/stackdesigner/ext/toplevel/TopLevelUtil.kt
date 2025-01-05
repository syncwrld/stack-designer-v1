@file:JvmName("MaterialUtil") // Opcional: renomeia a classe gerada para uso no Java

import org.bukkit.Material

fun tryIfNot(vararg tries: String): Material {
    for (tryIt in tries) {
        try {
            return Material.valueOf(tryIt)
        } catch (ignored: IllegalArgumentException) {
            continue
        }
    }
    throw IllegalArgumentException("Nenhum material válido encontrado")
}
