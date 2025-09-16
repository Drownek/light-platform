plugins {
    `java-21-convention`
    id("maven-publish")
}

dependencies {
    // core
    api(project(":core"))

    api(libs.litecommands.velocity)

    // tasker
    api(libs.okaeri.tasker.velocity)

    // configs
    api(libs.okaeri.configs.yaml.snakeyaml) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
    api(libs.okaeri.configs.serdes.adventure)

    // velocity
    compileOnly(libs.velocity.api)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "light-platform-velocity"
            println("Publishing as ${listOf(groupId, artifactId, version).joinToString(":") { it ?: "NONE" }}")
            from(components["java"])
        }
    }
}