plugins {
    `java-17-convention`
    alias(libs.plugins.plugin.yml.bukkit)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

group = "me.drownek"
version = "1.0-SNAPSHOT"

tasks {
    runServer {
        minecraftVersion("1.19.4")
    }
}

bukkit {
    main = "me.drownek.example.ExamplePlugin"
    apiVersion = "1.13"
    name = "ExamplePlugin"
    author = "Drownek"
    version = "${project.version}"
    softDepend = listOf("Vault")
}

dependencies {
    compileOnly(libs.spigot.api.old)

    implementation(project(":bukkit"))

    implementation(libs.okaeri.configs.json.simple)

    /* hooks */
    compileOnly(libs.vault.api)

    /* lombok */
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.postgresql)
}

tasks.shadowJar {
    archiveFileName.set("light-platform-bukkit-example-${project.version}.jar")

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
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
    options.encoding = "UTF-8"
}