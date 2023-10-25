plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}



android {
    namespace = "com.example.manager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.manager"
        minSdk = 30
        targetSdk = 33
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

    buildFeatures{
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.2.0")
    implementation("com.google.firebase:firebase-messaging:23.3.0")
    implementation(fileTree(mapOf(
        "dir" to "D:\\zalopaytest",
        "include" to listOf("*.aar", "*.jar"))))




    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //glider
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    // RxJava 3
    implementation ("io.reactivex.rxjava3:rxjava:3.0.0")
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.0")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")

    //bradge
    implementation ("com.nex3z:notification-badge:1.0.4")

    //evenBus
    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("org.greenrobot:eventbus-java:3.3.1")

    //Gson
    implementation ("com.google.code.gson:gson:2.10.1")

    //Paper
    implementation ("io.github.pilgr:paperdb:2.7.2")

    //LottieFiles
    implementation ("com.airbnb.android:lottie:6.1.0")

    //neumorphism
    implementation ("com.github.fornewid:neumorphism:0.3.2")

    //Image picker
    implementation ("com.github.dhaval2404:imagepicker:2.1")

    //zalopay
    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    implementation("commons-codec:commons-codec:1.14")
}