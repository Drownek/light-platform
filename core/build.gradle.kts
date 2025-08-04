plugins {
    `java-library`
    id("maven-publish")
}

dependencies {
    // litecommands
    api("dev.rollczi:litecommands-core:3.10.3")

    api("dev.rollczi:litecommands-adventure-platform:3.10.3")

    // persistence
    api("eu.okaeri:okaeri-persistence-core:${Versions.OKAERI_PERSISTENCE_VERSION}")

    // commons
    api("eu.okaeri:okaeri-commons-core:${Versions.OKAERI_COMMONS_VERSION}")

    // configs
    api("eu.okaeri:okaeri-configs-core:${Versions.OKAERI_CONFIGS_VERSION}")
    api("eu.okaeri:okaeri-configs-serdes-commons:${Versions.OKAERI_CONFIGS_VERSION}")
    api("eu.okaeri:okaeri-configs-serdes-okaeri:${Versions.OKAERI_CONFIGS_VERSION}")

    // configs-validator
    api("eu.okaeri:okaeri-configs:${Versions.OKAERI_CONFIGS_VERSION}")
    api("eu.okaeri:okaeri-configs-validator-okaeri:${Versions.OKAERI_CONFIGS_VERSION}") {
        exclude(group = "eu.okaeri", module = "okaeri-validator")
    }

    // injector
    api("eu.okaeri:okaeri-injector:${Versions.OKAERI_INJECTOR_VERSION}")

    // validator
    api("eu.okaeri:okaeri-validator:${Versions.OKAERI_VALIDATOR_VERSION}")

    // tasker
    api("eu.okaeri:okaeri-tasker-core:${Versions.OKAERI_TASKER_VERSION}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            println("Publishing as ${listOf(groupId, artifactId, version).joinToString(":") { it ?: "NONE"}}")
            from(components["java"])
        }
    }
}