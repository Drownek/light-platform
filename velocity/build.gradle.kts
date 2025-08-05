plugins {
    `java-library`
    id("maven-publish")
}

dependencies {
    // core
    api(project(":core"))

    api("dev.rollczi:litecommands-velocity:3.10.3")

//    // adventure
//    api("net.kyori:adventure-api:4.23.0")
//    api("net.kyori:adventure-text-serializer-legacy:4.23.0")
//    api("net.kyori:adventure-text-minimessage:4.23.0")

    // tasker
    api("eu.okaeri:okaeri-tasker-velocity:${Versions.OKAERI_TASKER_VERSION}")

    // configs
    api("eu.okaeri:okaeri-configs-yaml-snakeyaml:${Versions.OKAERI_CONFIGS_VERSION}") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
    api("eu.okaeri:okaeri-configs-serdes-adventure:${Versions.OKAERI_CONFIGS_VERSION}")

    // velocity
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.drownek"
            artifactId = "light-platform-velocity"
            println("Publishing as ${listOf(groupId, artifactId, version).joinToString(":") { it ?: "NONE" }}")
            from(components["java"])
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}