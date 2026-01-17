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

    // LiteCommands
    api(libs.litecommands.core)
    api(libs.litecommands.adventure.platform)
    api(libs.litecommands.bukkit)

    // Adventure
    api(libs.adventure.api)
    api(libs.adventure.text.serializer.legacy)
    api(libs.adventure.text.minimessage)
    api(libs.adventure.platform.bukkit)

    // For LiteCommandsConfig
    compileOnly(libs.okaeri.configs.core)
}
