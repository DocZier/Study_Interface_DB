import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)


            implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")
            implementation("mysql:mysql-connector-java:8.0.33")
            implementation("org.jetbrains.exposed:exposed-core:0.41.1")
            implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
            implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
        }
        desktopMain.dependencies {


            implementation("mysql:mysql-connector-java:8.0.33")
            implementation("org.jetbrains.exposed:exposed-core:0.41.1")
            implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
            implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")

            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}


compose.desktop {
    application {
        mainClass = "org.example.project.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.project"
            packageVersion = "1.0.0"
        }
    }
}
