package com.kaaneneskpc.cointy.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

internal const val DATASTORE_FILE_NAME = "cointy_settings.preferences_pb"

expect fun createDataStore(): DataStore<Preferences>

