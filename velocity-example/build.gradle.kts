plugins {
    `java-21-convention`
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.velocity)
}

group = "me.drownek"
version = "1.0-SNAPSHOT"

tasks {
    runVelocity {
        velocityVersion("3.3.0-SNAPSHOT")
    }
}

dependencies {
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)

    implementation(project(":velocity"))

    implementation(libs.okaeri.configs.json.simple)
    implementation(libs.okaeri.persistence.jdbc)

    /* lombok */
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks.shadowJar {
    archiveFileName.set("light-platform-velocity-example-${project.version}.jar")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "META-INF/**",
        "javax/**"
    )

    val prefix = "me.drownek.example.libs"
    listOf(
        "eu.okaeri",
        "dev.rollczi.litecommands",
        "com.cryptomorin",
        "dev.triumphteam",
        "panda",
        "net.jodah",
        "me.drownek.util",
    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }

    /* Fail as it wont work on server versions with plugin remapping */
    duplicatesStrategy = DuplicatesStrategy.FAIL
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
    options.encoding = "UTF-8"
}