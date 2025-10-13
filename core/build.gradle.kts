plugins {
    `java-17-convention`
    id("maven-publish")
}

dependencies {
    // litecommands
    api(libs.litecommands.core)
    api(libs.litecommands.adventure.platform)

    // persistence
    api(libs.okaeri.persistence.core)
    api(libs.okaeri.persistence.jdbc)

    // commons
    api(libs.okaeri.commons.core)

    // configs
    api(libs.okaeri.configs.core)
    api(libs.okaeri.configs.serdes.commons)
    api(libs.okaeri.configs.serdes.okaeri)
    api(libs.okaeri.configs)
    api(libs.okaeri.configs.validator.okaeri) {
        exclude(group = "eu.okaeri", module = "okaeri-validator")
    }

    // configs-validator
    api(libs.okaeri.configs.validator.okaeri) {
        exclude(group = "eu.okaeri", module = "okaeri-validator")
    }

    // injector
    api(libs.okaeri.injector)

    // validator
    api(libs.okaeri.validator)

    // tasker
    api(libs.okaeri.tasker.core)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "light-platform-core"
            println("Publishing as ${listOf(groupId, artifactId, version).joinToString(":") { it ?: "NONE"}}")
            from(components["java"])
        }
    }
}