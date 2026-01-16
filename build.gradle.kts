plugins {
    java
}

group = "com.github.Drownek"
version = "2.3.0"

subprojects {
    apply(plugin = "java")
    group = rootProject.group
    version = rootProject.version

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.36")
        annotationProcessor("org.projectlombok:lombok:1.18.36")
    }
}
