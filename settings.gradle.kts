plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "light-platform"
include("light-platform-core", "light-platform-bukkit", "light-platform-bukkit-example", "light-platform-velocity", "light-platform-velocity-example")