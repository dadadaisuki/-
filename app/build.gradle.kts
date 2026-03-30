plugins {
    id("com.android.application") version "8.5.2"
    id("org.jetbrains.kotlin.android") version "2.0.21"
    // Kotlin 2.0+：Compose 必须用独立编译器插件（不要再写 composeOptions.kotlinCompilerExtensionVersion）
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
}

android {
    namespace = "com.travel.superapp"
    compileSdk = 36
    // 避免 AGP 默认去用 34.0.0（若网络/镜像下不到会一直报错）
    // 与 API 36 对齐；若本机没有该目录，请到 SDK Manager 勾选 Android SDK Build-Tools 36
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "com.travel.superapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")

    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3")

    implementation("androidx.navigation:navigation-compose:2.8.4")

    // 地图：osmdroid（免费离线可用，无需 API key）
    implementation("org.osmdroid:osmdroid-android:6.1.20")

    // GPS 定位：Google FusedLocationProvider
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // 图片加载 Coil
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")

    // Later: images/networking, etc.
}

