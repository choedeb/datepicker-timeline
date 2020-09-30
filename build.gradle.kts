buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(Dependency.GRADLE)
        classpath(Dependency.KOTLIN_GRADLE)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url = uri(Repository.JITPACK) }
        maven { url = uri(Repository.GOOGLE) }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}