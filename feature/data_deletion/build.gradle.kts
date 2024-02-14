plugins {
    alias(libs.plugins.popos.android.feature)
    alias(libs.plugins.popos.android.library.compose)
    alias(libs.plugins.popos.android.library.jacoco)
}

android {
    namespace = "com.niyaj.feature.data_deletion"


    ksp {
        arg("compose-destinations.moduleName", "data_deletion")
        arg("compose-destinations.mode", "navgraphs")
        arg("compose-destinations.useComposableVisibility", "true")
    }
}

dependencies {

    implementation(libs.accompanist.permissions)
    implementation(libs.dialog.core)
    implementation(libs.dialog.datetime)
}