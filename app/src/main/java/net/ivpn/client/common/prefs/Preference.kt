package net.ivpn.client.common.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.ivpn.client.IVPNApplication
import net.ivpn.client.common.dagger.ApplicationScope
import javax.inject.Inject

/*
 IVPN Android app
 https://github.com/ivpn/android-app

 Created by Oleksandr Mykhailenko.
 Copyright (c) 2020 Privatus Limited.

 This file is part of the IVPN Android app.

 The IVPN Android app is free software: you can redistribute it and/or
 modify it under the terms of the GNU General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option) any later version.

 The IVPN Android app is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details.

 You should have received a copy of the GNU General Public License
 along with the IVPN Android app. If not, see <https://www.gnu.org/licenses/>.

*/@ApplicationScope
class Preference @Inject constructor() {

    companion object {
        const val LAST_LOGIC_VERSION = 1
        private const val CURRENT_LOGIC_VERSION = "CURRENT_LOGIC_VERSION"
        private const val COMMON_PREF = "COMMON_PREF"
        private const val TRUSTED_WIFI_PREF = "TRUSTED_WIFI_PREF"

        private const val SERVERS_PREF = "SERVERS_PREF"
        private const val WIREGUARD_SERVERS_PREF = "WIREGUARD_SERVERS_PREF"
        private const val FAVOURITES_SERVERS_PREF = "FAVOURITES_SERVERS_PREF"
        private const val DISALLOWED_APPS_PREF = "DISALLOWED_APPS_PREF"

        //Don't clear this shared preference after logout
        private const val STICKY_PREF = "STICKY_PREF"

        private const val SETTINGS_PREF = "ENCRYPTED_SETTINGS_PREF"
        private const val OLD_SETTINGS_PREF = "SETTINGS_PREF"

        private const val ACCOUNT_PREF = "ENC_ACCOUNT_PREF"
        private const val OLD_ACCOUNT_PREF = "ACCOUNT_PREF"
    }

    val isLogicVersionExist: Boolean
        get() {
            val sharedPreferences = commonSharedPreferences
            return sharedPreferences.contains(CURRENT_LOGIC_VERSION)
        }
    var logicVersion: Int
        get() {
            val sharedPreferences = commonSharedPreferences
            return sharedPreferences.getInt(CURRENT_LOGIC_VERSION, LAST_LOGIC_VERSION)
        }
        set(logicVersion) {
            val sharedPreferences = commonSharedPreferences
            sharedPreferences.edit()
                    .putInt(CURRENT_LOGIC_VERSION, logicVersion)
                    .apply()
        }

    private val mainKey: MasterKey = MasterKey.Builder(IVPNApplication.getApplication())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    fun removeAll() {
        clear(commonSharedPreferences)
        clear(settingsPreference)
        clear(oldSettingsPreference)
        clear(serversSharedPreferences)
        clear(disallowedAppsSharedPreferences)
        clear(networkRulesSharedPreferences)
        clear(accountPreference)
        clear(oldAccountSharedPreferences)
        clear(wireguardServersSharedPreferences)
    }

    private fun clear(sharedPreferences: SharedPreferences?) {
        sharedPreferences?.edit()?.clear()?.apply()
    }

    private val commonSharedPreferences: SharedPreferences
        get() = IVPNApplication.getApplication().getSharedPreferences(COMMON_PREF, Context.MODE_PRIVATE)
    val networkRulesSharedPreferences: SharedPreferences
        get() = IVPNApplication.getApplication().getSharedPreferences(TRUSTED_WIFI_PREF, Context.MODE_PRIVATE)
    val serversSharedPreferences: SharedPreferences
        get() = IVPNApplication.getApplication().getSharedPreferences(SERVERS_PREF, Context.MODE_PRIVATE)
    val wireguardServersSharedPreferences: SharedPreferences
        get() = IVPNApplication.getApplication().getSharedPreferences(WIREGUARD_SERVERS_PREF, Context.MODE_PRIVATE)
    val disallowedAppsSharedPreferences: SharedPreferences
        get() = IVPNApplication.getApplication().getSharedPreferences(DISALLOWED_APPS_PREF, Context.MODE_PRIVATE)
    val stickySharedPreferences: SharedPreferences
        get() = IVPNApplication.getApplication().getSharedPreferences(STICKY_PREF, Context.MODE_PRIVATE)
    val settingsPreference: SharedPreferences
        get() = EncryptedSharedPreferences.create(
                IVPNApplication.getApplication(),
                SETTINGS_PREF,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    val oldSettingsPreference: SharedPreferences
        get() = IVPNApplication.getApplication().getSharedPreferences(OLD_SETTINGS_PREF, Context.MODE_PRIVATE)
    val accountPreference: SharedPreferences
        get() = EncryptedSharedPreferences.create(
                IVPNApplication.getApplication(),
                ACCOUNT_PREF,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    val oldAccountSharedPreferences: SharedPreferences
        get() = IVPNApplication.getApplication().getSharedPreferences(OLD_ACCOUNT_PREF, Context.MODE_PRIVATE)
}