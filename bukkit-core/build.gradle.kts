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

    api(libs.okaeri.tasker.bukkit)
    api(libs.okaeri.commons.bukkit)
}
