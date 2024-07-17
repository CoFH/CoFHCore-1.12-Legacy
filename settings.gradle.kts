pluginManagement {
    repositories {
        maven {
            url = uri("https://mvn.falsepattern.com/fpgradle/")
            name = "fpgradle"
            content {
                includeModule("com.gtnewhorizons", "retrofuturagradle")
                includeModule("com.falsepattern", "fpgradle-plugin")
                includeModule("fpgradle-minecraft", "fpgradle-minecraft.gradle.plugin")
            }
        }
        maven {
            url = uri("https://mvn.falsepattern.com/releases/")
            name = "mavenpattern"
            content {
                includeGroup("com.falsepattern")
            }
        }
        maven {
            url = uri("https://mvn.falsepattern.com/jitpack/")
            name = "jitpack"
            content {
                includeModule("io.github.LegacyModdingMC.MappingGenerator", "MappingGenerator")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "CoFHCore"
