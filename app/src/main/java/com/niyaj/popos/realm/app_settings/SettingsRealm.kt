package com.niyaj.popos.realm.app_settings

import com.niyaj.popos.util.Constants.SETTINGS_ID
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class SettingsRealm(): RealmObject {
    @PrimaryKey
    var _id: String = SETTINGS_ID

    var expensesDataDeletionInterval: Int = 0

    var reportDataDeletionInterval: Int = 7

    var cartDataDeletionInterval: Int = 0

    var cartOrderDataDeletionInterval: Int = 0

    var createdAt: String = System.currentTimeMillis().toString()

    var updatedAt: String? = null

}