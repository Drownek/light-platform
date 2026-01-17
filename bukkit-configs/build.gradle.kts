plugins {
    `java-17-convention`
    id("maven-publish")
    alias(libs.plugins.shadow)
    `publishing-convention`
}

dependencies {
    // core
    api(project(":core"))

    // Spigot API
    compileOnly(libs.spigot.api)

    // configs
    api(libs.okaeri.configs)
    api(libs.okaeri.configs.core)
    api(libs.okaeri.configs.validator.okaeri) {
        exclude(group = "eu.okaeri", module = "okaeri-validator")
    }
    api(libs.okaeri.configs.yaml.bukkit)
    api(libs.okaeri.configs.serdes.okaeri)
    api(libs.okaeri.configs.serdes.bukkit)
    api(libs.okaeri.configs.serdes.commons)
    api(libs.okaeri.configs.serdes.okaeri.bukkit)

    // Mojang authlib for skulls
    api(libs.authlib)

    // Registering utils serdes if present
    compileOnly(libs.bukkit.utils)
}
