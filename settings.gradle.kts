plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://jitpack.io")
        maven("https://storehouse.okaeri.eu/repository/maven-releases/")
        maven("https://storehouse.okaeri.eu/repository/maven-public/")
        maven("https://repo.panda-lang.org/releases")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.minebench.de/")
        maven("https://libraries.minecraft.net/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}
rootProject.name = "light-platform"
include(
    "core",
    "bukkit",
    "bukkit-example",
    "velocity",
    "velocity-example"
)