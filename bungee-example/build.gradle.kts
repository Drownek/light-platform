plugins {
    `java-17-convention`
    alias(libs.plugins.plugin.yml.bungee)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.waterfall)
}

group = "me.drownek"
version = "1.0-SNAPSHOT"

tasks {
    runWaterfall {
        waterfallVersion("1.21")
    }
}

bungee {
    main = "me.drownek.example.ExamplePlugin"
    name = "ExamplePlugin"
    author = "Drownek"
    version = "${project.version}"
}

dependencies {
    compileOnly(libs.bungee.api)

    implementation(project(":bungee"))

    implementation(libs.okaeri.configs.json.simple)

    /* lombok */
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.postgresql)
}

tasks.shadowJar {
    archiveFileName.set("light-platform-bungee-example-${project.version}.jar")

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
        "net.kyori",
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