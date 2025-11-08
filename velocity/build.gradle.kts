plugins {
    `java-17-convention`
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
            from(components["java"])
        }
    }
}