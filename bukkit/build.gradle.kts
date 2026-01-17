plugins {
    `java-17-convention`
    id("maven-publish")
    alias(libs.plugins.shadow)
    `publishing-convention`
}

dependencies {
    api(project(":bukkit-core"))
    api(project(":bukkit-configs"))
    api(project(":bukkit-persistence"))
    api(project(":bukkit-litecommands"))
    api(libs.bukkit.utils)
}
