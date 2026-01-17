plugins {
    `java-17-convention`
    id("maven-publish")
    `publishing-convention`
}

dependencies {
    // litecommands
    compileOnly(libs.litecommands.core)
    compileOnly(libs.litecommands.adventure.platform)

    // persistence
    compileOnly(libs.okaeri.persistence.core)
    compileOnly(libs.okaeri.persistence.jdbc)

    // commons
    api(libs.okaeri.commons.core)

    // configs
    compileOnly(libs.okaeri.configs.core)
    compileOnly(libs.okaeri.configs.serdes.commons)
    compileOnly(libs.okaeri.configs.serdes.okaeri)
    compileOnly(libs.okaeri.configs)
    compileOnly(libs.okaeri.configs.validator.okaeri) {
        exclude(group = "eu.okaeri", module = "okaeri-validator")
    }

    // injector
    api(libs.okaeri.injector)

    // validator
    api(libs.okaeri.validator)

    // tasker
    api(libs.okaeri.tasker.core)

    api(libs.jetbrains.annotations)
}
