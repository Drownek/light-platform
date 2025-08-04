plugins {
    `java-library`
    id("maven-publish")
}

dependencies {
    compileOnly("com.mojang:authlib:1.5.25")
    api("com.github.Drownek:bukkit-utils:1.0.4")

    api("dev.rollczi:litecommands-bukkit:3.10.3")

    // core
    api(project(":core"))

    // persistence
    api("eu.okaeri:okaeri-persistence-flat:${Versions.OKAERI_PERSISTENCE_VERSION}")

    // commons + tasker
    api("eu.okaeri:okaeri-commons-bukkit:${Versions.OKAERI_COMMONS_VERSION}")
    api("eu.okaeri:okaeri-tasker-bukkit:${Versions.OKAERI_TASKER_VERSION}")

    // configs
    api("eu.okaeri:okaeri-configs-yaml-bukkit:${Versions.OKAERI_CONFIGS_VERSION}")
    api("eu.okaeri:okaeri-configs-serdes-bukkit:${Versions.OKAERI_CONFIGS_VERSION}")
    api("eu.okaeri:okaeri-configs-serdes-okaeri-bukkit:${Versions.OKAERI_CONFIGS_VERSION}")

    // Spigot API
    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")

    api("eu.okaeri:okaeri-configs-serdes-okaeri:${Versions.OKAERI_CONFIGS_VERSION}")
    api("eu.okaeri:okaeri-injector:${Versions.OKAERI_INJECTOR_VERSION}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            println("Publishing as ${listOf(groupId, artifactId, version).joinToString(":") { it ?: "NONE" }}")
            from(components["java"])
        }
    }
}