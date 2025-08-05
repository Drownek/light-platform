plugins {
    java
}

group = "me.drownek"
version = "2.1.1"

subprojects {
    apply(plugin = "java")
    group = rootProject.group
    version = rootProject.version

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(16))
        }
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.36")
        annotationProcessor("org.projectlombok:lombok:1.18.36")
    }
}
