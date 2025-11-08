plugins {
    `java-17-convention`
    id("maven-publish")
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.authlib)

    api(libs.bukkit.utils)

    api(libs.litecommands.bukkit)

    // core
    api(project(":core"))

    // persistence
    api(libs.okaeri.persistence.flat)

    // commons + tasker
    api(libs.okaeri.commons.bukkit)
    api(libs.okaeri.tasker.bukkit)

    // configs
    api(libs.okaeri.configs.yaml.bukkit)
    api(libs.okaeri.configs.serdes.bukkit)
    api(libs.okaeri.configs.serdes.okaeri.bukkit)
    api(libs.okaeri.configs.serdes.okaeri)

    // Spigot API
    compileOnly(libs.spigot.api)

    api(libs.okaeri.injector)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "light-platform-bukkit"
            from(components["java"])
        }
    }
}