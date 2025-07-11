plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "light-platform"
include(
    "core",
    "bukkit",
    "bukkit-example",
    "velocity",
    "velocity-example"
)