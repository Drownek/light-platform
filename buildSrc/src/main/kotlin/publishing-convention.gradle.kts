plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    coordinates("io.github.drownek", "platform-$name", version.toString())

    publishToMavenCentral()
    signAllPublications()

    pom {
        name.set("Light Platform")
        description.set("A platform library for creating Minecraft plugins with ease.")
        url.set("https://github.com/Drownek/light-platform")
        licenses {
            license {
                name.set("MIT License")
                url.set("http://www.opensource.org/licenses/mit-license.php")
            }
        }
        developers {
            developer {
                id.set("Drownek")
                url.set("https://github.com/Drownek/")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/Drownek/light-platform.git")
            developerConnection.set("scm:git:ssh://git@github.com/Drownek/light-platform.git")
            url.set("https://github.com/Drownek/light-platform")
        }
    }
}