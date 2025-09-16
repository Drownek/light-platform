plugins {
    java
}

group = "com.github.Drownek.light-platform"
version = "2.1.1-beta2"

subprojects {
    apply(plugin = "java")
    group = rootProject.group
    version = rootProject.version

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.36")
        annotationProcessor("org.projectlombok:lombok:1.18.36")
    }
}
