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

    // persistence
    api(libs.okaeri.persistence.core)
    api(libs.okaeri.persistence.jdbc)
    api(libs.okaeri.persistence.flat)

    // Configs
    api(libs.okaeri.configs.yaml.bukkit)
}
