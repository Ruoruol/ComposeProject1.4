package com.example.composeproject1.model

object Constant {
    const val TYPE_MEDICATION = 1
    const val TYPE_HISTORY = 2

    object DbKey {
        const val DB_NAME = "HDB"
        const val DB_USER_TABLE_NAME = "users"
        const val KEY_USER_NAME = "usname"
        const val KEY_USER_PASSWORD = "uspassword"
        const val KEY_USER_ID = "id"
    }

    object FileDbKey {
        const val KEY_SP_USER_ID = "user_id"
        const val KEY_SP_USER_NAME = "Loginname"
    }

    object BundleKey {
        const val KEY_BUNDLE_USER_ID = "user_id"

        // 血压信息id
        const val KEY_BUNDLE_BLOOD_PRESSURE_ID = "blood_pressure_id"
    }
}