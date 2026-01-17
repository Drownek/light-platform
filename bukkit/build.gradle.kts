plugins {
    `java-17-convention`
    id("maven-publish")
    alias(libs.plugins.shadow)
}

dependencies {
    api(project(":bukkit-core"))
    api(project(":bukkit-configs"))
    api(project(":bukkit-persistence"))
    api(project(":bukkit-litecommands"))
    api(libs.bukkit.utils)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.Drownek"
            artifactId = "platform-bukkit"
            from(components["java"])
        }
    }
}