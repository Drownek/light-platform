plugins {
    `java-17-convention`
    id("maven-publish")
    alias(libs.plugins.shadow)
}

dependencies {
    api(libs.litecommands.bungeecord)

    // core
    api(project(":core"))

    // persistence
    api(libs.okaeri.persistence.flat)

    // commons + tasker
    api(libs.okaeri.tasker.bungee)

    // configs
    api(libs.okaeri.configs.yaml.bungee)
    api(libs.okaeri.configs.serdes.okaeri)

    // Spigot API
    compileOnly(libs.bungee.api)

    api(libs.okaeri.injector)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "light-platform-bungee"
            println("Publishing as ${listOf(groupId, artifactId, version).joinToString(":") { it ?: "NONE" }}")
            from(components["java"])
        }
    }
}