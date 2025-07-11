plugins {
    java
}

group = "me.drownek"
version = "2.0.2"

repositories {
    mavenCentral()
    maven {
        name = "storehouse-releases"
        url = uri("https://storehouse.okaeri.eu/repository/maven-releases/")
    }
    maven {
        name = "panda-repo"
        url = uri("https://repo.panda-lang.org/releases")
    }
}

subprojects {
    apply(plugin = "java")
    group = rootProject.group
    version = rootProject.version

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(16))
        }
    }

    repositories {
        mavenCentral()
        maven {
            name = "storehouse-releases"
            url = uri("https://storehouse.okaeri.eu/repository/maven-releases/")
        }
        maven {
            name = "panda-repo"
            url = uri("https://repo.panda-lang.org/releases")
        }
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.36")
        annotationProcessor("org.projectlombok:lombok:1.18.36")
    }
}
