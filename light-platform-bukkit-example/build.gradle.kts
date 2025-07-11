plugins {
    id("java")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.gradleup.shadow") version "9.0.0-beta12"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "me.drownek"
version = "1.0-SNAPSHOT"

tasks {
    runServer {
        minecraftVersion("1.19.4")
        val toolchains = project.extensions.getByType<JavaToolchainService>()
        javaLauncher.set(toolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(21)) // Run server with Java 21
        })
    }
}

bukkit {
    main = "me.drownek.example.ExamplePlugin"
    apiVersion = "1.13"
    name = "ExamplePlugin"
    author = "Drownek"
    version = "${project.version}"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
    maven("https://repo.panda-lang.org/releases")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")

    implementation(project(":light-platform-bukkit"))

    implementation("eu.okaeri:okaeri-configs-json-simple:5.0.6")
    implementation("eu.okaeri:okaeri-persistence-jdbc:3.0.1-beta.2")

    /* lombok */
    val lombok = "1.18.32"
    compileOnly("org.projectlombok:lombok:$lombok")
    annotationProcessor("org.projectlombok:lombok:$lombok")
}

tasks.shadowJar {
    archiveFileName.set("light-platform-bukkit-example-${project.version}.jar")

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

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
    options.encoding = "UTF-8"
}