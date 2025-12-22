package com.kaaneneskpc.cointy.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

private lateinit var applicationContext: Context

fun initializeDataStore(context: Context) {
    applicationContext = context.applicationContext
}

actual fun createDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            applicationContext.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath.toPath()
        }
    )
}

