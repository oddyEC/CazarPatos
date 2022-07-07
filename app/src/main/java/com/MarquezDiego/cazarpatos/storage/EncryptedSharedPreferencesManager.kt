package com.MarquezDiego.cazarpatos.storage

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.MarquezDiego.cazarpatos.FileHandler
import com.MarquezDiego.cazarpatos.LOGIN_KEY
import com.MarquezDiego.cazarpatos.PASSWORD_KEY

class EncryptedSharedPreferencesManager (val actividad: Activity): FileHandler {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun SaveInformation(datosAGrabar:Pair<String,String>){
            val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            val sharedPref = EncryptedSharedPreferences.create(
                "secret_shared_prefs",
                masterKeyAlias,
                actividad,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            val editor = sharedPref.edit()
            editor.putString(LOGIN_KEY , datosAGrabar.first)
            editor.putString(PASSWORD_KEY, datosAGrabar.second)
            editor.apply()
        }
        @RequiresApi(Build.VERSION_CODES.M)
        override fun ReadInformation():Pair<String,String>{
            val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            val sharedPref = EncryptedSharedPreferences.create(
                "secret_shared_prefs",
                masterKeyAlias,
                actividad,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            val email = sharedPref.getString(LOGIN_KEY,"").toString()
            val clave = sharedPref.getString(PASSWORD_KEY,"").toString()
            return (email to clave)
        }
}