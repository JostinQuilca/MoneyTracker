plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.moneytracker"
    compileSdk = 35 // Ajustado a una versión estable estándar

    defaultConfig {
        applicationId = "com.example.moneytracker"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Librerías base (Mantenemos las que ya tenías usando el catálogo libs)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // --- LIBRERÍAS AGREGADAS PARA EL PROYECTO ---

    // 1. Retrofit & GSON (Para la API de Monedas y JSON)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 2. MPAndroidChart (Para los gráficos estadísticos)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}