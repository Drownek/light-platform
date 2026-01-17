plugins {
    `java-17-convention`
    id("maven-publish")
}

dependencies {
    // core
    api(project(":core"))

    api(libs.litecommands.core)
    api(libs.litecommands.adventure.platform)
    api(libs.litecommands.velocity)

    // tasker
    api(libs.okaeri.tasker.velocity)

    // configs
    api(libs.okaeri.configs.core)
    api(libs.okaeri.configs.serdes.commons)
    api(libs.okaeri.configs.serdes.okaeri)
    api(libs.okaeri.configs)
    api(libs.okaeri.configs.validator.okaeri) {
        exclude(group = "eu.okaeri", module = "okaeri-validator")
    }
    api(libs.okaeri.configs.yaml.snakeyaml) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
    api(libs.okaeri.configs.serdes.adventure)

    // velocity
    compileOnly(libs.velocity.api)

    api(libs.okaeri.persistence.core)
    api(libs.okaeri.persistence.jdbc)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.Drownek"
            artifactId = "platform-velocity"
            from(components["java"])
        }
    }
}