plugins {
    id(Plugin.LIBRARY)
    id(Plugin.KOTLIN_ANDROID)
    id(Plugin.KOTLIN_ANDROID_EXTENSIONS)
    id(Plugin.KOTLIN_KAPT)
}
//apply from: 'gradle-mvn-push.gradle'

android {
    compileSdkVersion(Config.COMPILE_SDK)
    buildToolsVersion(Config.BUILD_SDK)

    defaultConfig {
        minSdkVersion(Config.MIN_SDK)
        targetSdkVersion(Config.TARGET_SDK)
        versionCode = Config.VERSION_CODE
        versionName = Config.VERSION_NAME
    }

    buildTypes {
        getByName(Config.BuildType.DEBUG) {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName(Config.BuildType.RELEASE) {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Dependency.KOTLIN)
    implementation(Dependency.KTX)
    implementation(Dependency.APPCOMPAT)
    implementation(Dependency.ANNOTATION)
    implementation(Dependency.MATERIAL)
    implementation(Dependency.RECYCLERVIEW)
    implementation(Dependency.CONSTRAINT)
}