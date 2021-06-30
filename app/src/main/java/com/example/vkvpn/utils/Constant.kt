package com.example.vkvpn.utils

object Constant {
    const val SPLASH_TIME_OUT: Long = 4000
    const val OPEN_APPLICATION_FIRST_TIME = "OPEN_APPLICATION_FIRST_TIME"
    const val CAMERA: Int = 2
    const val DIALOG: Int = 3
    const val FILES: Int = 1
    val CAMERA_FILES_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}