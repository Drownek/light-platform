plugins {
    java
}

group = "com.github.Drownek.light-platform"
version = "2.2.1-beta1"

subprojects {
    apply(plugin = "java")
    group = rootProject.group
    version = rootProject.version

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.36")
        annotationProcessor("org.projectlombok:lombok:1.18.36")
    }
}
