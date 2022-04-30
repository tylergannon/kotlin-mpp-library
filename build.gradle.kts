@file:Suppress("UnstableApiUsage")

import java.util.Properties

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("maven-publish")
}

group = "{{ project.group }}"
version = "{{ project.version }}"
val mvnArtifactId = name

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    android()
        publishLibraryVariants = listOf("release", "debug")
    }

    sourceSets {
        val commonMain by getting {
            implementation("com.github.kittinunf.result:result:5.2.1")
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions:1.0.1-pre.332-kotlin-1.6.21")
                implementation(npm("axios", "0.26.1"))
            }
        }
        val jsTest by getting
        val androidMain by getting {
            dependencies {
                implementation("com.google.android.material:material:1.5.0")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation("androidx.test:core:1.4.0")

                implementation("androidx.test:runner:1.4.0")
                implementation("androidx.test:rules:1.4.0")

                implementation("org.robolectric:robolectric:4.7.3")
            }
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 26
        targetSdk = 31
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    lint {
        this.isAbortOnError = false
        this.isCheckTestSources = false
        this.isCheckReleaseBuilds = false
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}



/**
 * Publishing-related stuff
 */


val privateMvnRepo: RepositoryHandler.(String) -> Unit = { repoName ->
    maven {
        this.name = "GitHubPackages"
        this.url = uri("https://maven.pkg.github.com/tylergannon/$repoName")
        credentials {
            Properties().also { props ->
                props.load(rootProject.file("local.properties").bufferedReader())
                username = props["gpr.user"].toString()
                password = props["gpr.key"].toString()
            }
        }
    }
}

publishing {
    repositories {
        privateMvnRepo("{{ project.name }}")
    }
}


afterEvaluate {
    configure<PublishingExtension> {
        publications.all {
            val mavenPublication = this as? MavenPublication
            mavenPublication?.artifactId = makeArtifactId(name)
        }
    }
}

configure<PublishingExtension> {
    publications {
        withType<MavenPublication> {
            groupId = "{{ project.package }}"
            artifactId = makeArtifactId(name)
            version
        }
    }
}

fun String.dasherize() = fold("") {acc, value ->
    if (value.isUpperCase()) {
        "$acc-${value.toLowerCase()}"
    } else {
        "$acc$value"
    }
}

fun makeArtifactId(name: String) =
    if ("kotlinMultiplatform" in name) {
        mvnArtifactId
    } else {
        "$mvnArtifactId-${name.dasherize()}"
    }

