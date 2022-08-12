package com.jamessc94.soundtrack.permission

import android.Manifest.permission.*

sealed class Permission(vararg val permissions: String) {

    object Camera : Permission(CAMERA)
    object Storage : Permission(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)

    companion object {
        fun from(permission: String) = when (permission) {
            WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE -> Storage
            CAMERA -> Camera
            else -> throw IllegalArgumentException("Unknown permission: $permission")
        }
    }
}
