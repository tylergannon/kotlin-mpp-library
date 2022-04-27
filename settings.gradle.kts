pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tylergannon/polestar-ephemeris")
            credentials {
                val props = java.util.Properties()
                props.load(File("${rootProject.projectDir.path}/local.properties").bufferedReader())

                fun envGet(name: String) = System.getenv(name)!!
                username = (props["gpr.user"] ?: envGet("USERNAME")).toString()
                password = (props["gpr.key"] ?: envGet("TOKEN")).toString()
            }
        }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android" || requested.id.name == "kotlin-android-extensions") {
                useModule("com.android.tools.build:gradle:4.1.2")
            }
        }
    }
}
rootProject.name = "kotlin-mpp-library"

