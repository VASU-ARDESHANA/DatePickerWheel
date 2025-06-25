plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "vasu.apps.datepickerwheel"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("DatePickerWheel") {
                from(components["release"])

                groupId = "com.github.VASU-ARDESHANA"
                artifactId = "DatePickerWheel"
                version = "1.1.4"

                pom {
                    name.set("DatePickerWheel")
                    description.set("A wheel-style date picker for Android in Kotlin.")
                    url.set("https://github.com/VASU-ARDESHANA/DatePickerWheel")
                    developers {
                        developer {
                            id.set("vasu")
                            name.set("VASU-ARDESHANA")
                        }
                    }
                    scm {
                        url.set("https://github.com/VASU-ARDESHANA/DatePickerWheel")
                    }
                }
            }
        }
    }
}