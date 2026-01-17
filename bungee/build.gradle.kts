plugins {
    `java-17-convention`
    id("maven-publish")
    alias(libs.plugins.shadow)
    `publishing-convention`
}

dependencies {
    api(libs.litecommands.core)
    api(libs.litecommands.adventure.platform)
    api(libs.litecommands.bungeecord)

    // core
    api(project(":core"))

    // persistence
    api(libs.okaeri.persistence.core)
    api(libs.okaeri.persistence.jdbc)
    api(libs.okaeri.persistence.flat)

    // commons + tasker
    api(libs.okaeri.tasker.bungee)

    // configs
    api(libs.okaeri.configs.core)
    api(libs.okaeri.configs.serdes.commons)
    api(libs.okaeri.configs.serdes.okaeri)
    api(libs.okaeri.configs)
    api(libs.okaeri.configs.validator.okaeri) {
        exclude(group = "eu.okaeri", module = "okaeri-validator")
    }
    api(libs.okaeri.configs.yaml.bungee)
    api(libs.okaeri.configs.serdes.okaeri)

    // Spigot API
    compileOnly(libs.bungee.api)

    api(libs.okaeri.injector)
}
