import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.syncwrld"
version = "1.0"

repositories {
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://nexus.iridiumdevelopment.net/repository/maven-releases/")
    maven("https://papermc.io/repo/repository/maven-releases/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.25")

    implementation("com.github.HenryFabio:inventory-api:2.0.3")
    implementation("com.iridium:IridiumColorAPI:1.0.9")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    // implementation("de.tr7zw:item-nbt-api:2.14.0")
}

val targetJavaVersion = 8
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.shadowJar {
    // relocate("de.tr7zw.changeme.nbtapi", "me.syncwrld.minecraft.stackdesigner.libs.nbtapi")
    relocate("co.aikar.commands", "me.syncwrld.minecraft.stackdesigner.libs.aikar.commands")
    relocate("co.aikar.locales", "me.syncwrld.minecraft.stackdesigner.libs.aikar.locales")
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.compileJava {
    options.compilerArgs.plusAssign(mutableListOf("-parameters"))
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
