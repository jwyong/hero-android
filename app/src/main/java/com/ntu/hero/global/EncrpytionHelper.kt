package com.ntu.hero.global

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import com.ntu.hero.global.encryption.Decryptor
import com.ntu.hero.global.encryption.Encrpytor
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class EncrpytionHelper {
    val encrpytor = Encrpytor()
    val decryptor = Decryptor()

    // encrypt and save to pref based on android ver
    fun encryptAndSaveStr(stringToEncrypt: String, prefName: String?) {
        val encryptedStr: String = stringToEncrypt
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // >= API 23
//            encryptedStr = encryptStr(stringToEncrypt)
//        } else { // < API 23
//            encryptedStr = encryptStrAPI21(stringToEncrypt)
//        }

        // save to pref if need
        if (prefName != null) {
            MPreferences.save(prefName, encryptedStr)
        }
    }

    // decrypt code based on pref
    fun decryptStrFromPref(prefName: String): String {
        val stringToDecrypt = MPreferences.getValue(prefName) ?: ""

        val decryptedStr: String = stringToDecrypt
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // >= API 23
//            decryptedStr = decryptStr(stringToDecrypt)
//        } else { // < API 23
//            decryptedStr = decryptStrAPI21(stringToDecrypt)
//        }

        return decryptedStr
    }

    // generate keystore (for >= API 23)
    @RequiresApi(Build.VERSION_CODES.M)
    fun getSecretKey(
        keyPropBlockMode: String,
        keyPropPadding: String,
        reqLockScreen: Boolean,
        authValiditySecs: Int
    ): SecretKey {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, GlobalVars.AND_KEY_STORE) // 1
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            GlobalVars.KEY_STORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(keyPropBlockMode)
            .setEncryptionPaddings(keyPropPadding)
            .setUserAuthenticationRequired(reqLockScreen) // 2 requires lock screen, invalidated if lock screen is disabled
            .setUserAuthenticationValidityDurationSeconds(authValiditySecs) // 3 only available x seconds from password authentication. -1 requires finger print - every time
            .setRandomizedEncryptionRequired(true) // 4 different ciphertext for same plaintext on each call
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()

        //Get the key
        val keyStore = KeyStore.getInstance(GlobalVars.AND_KEY_STORE)
        keyStore.load(null)
        val secretKeyEntry =
            keyStore.getEntry(GlobalVars.KEY_STORE_ALIAS, null) as KeyStore.SecretKeyEntry

        return secretKeyEntry.secretKey
    }

    // encrypt string
    private fun encryptStr(string: String): String {
        val encryptedBytes = encrpytor.encryptText(GlobalVars.KEY_STORE_ALIAS, string)
        val encStr = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)

        return encStr
    }

    // decrypt string
    private fun decryptStr(string: String): String {
        // get iv
        val ivBytesStr = MPreferences.getValue(GlobalVars.PREF_IV)
        val ivBytes = Base64.decode(ivBytesStr, Base64.NO_WRAP)

        val strBytes = Base64.decode(string, Base64.NO_WRAP)

        val decryptedStr = decryptor.decryptData(GlobalVars.KEY_STORE_ALIAS, strBytes, ivBytes)

        return decryptedStr
    }

    // encrpyt string - below API 23
//    private fun encryptStrAPI21(string: String): String {
//        // get random shio
//        val random = SecureRandom()
//        val shio = ByteArray(256)
//        random.nextBytes(shio)
//
//        // get secret key
//        val keySpec = getSecretKeyAPI21(shio)
//
//        // create iv
//        val ivRandom = SecureRandom() //not caching previous seeded instance of SecureRandom
//        val iv = ByteArray(16)
//        ivRandom.nextBytes(iv)
//        val ivSpec = IvParameterSpec(iv) // 2
//
//        // encrpyt
//        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding") // 1
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
//        val encryptedBytes = cipher.doFinal(string.toByteArray(charset("UTF-8"))) // 2
//
//        // save data into pref
//        MPreferences.save(GlobalVars.PREF_IV, Base64.encodeToString(iv, Base64.NO_WRAP))
//        MPreferences.save(GlobalVars.PREF_SHIO, Base64.encodeToString(shio, Base64.NO_WRAP))
//
//        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
//    }
//
//    // decrypt string - below API 23
//    private fun decryptStrAPI21(string: String): String {
//        // get data from pref
//        val shio = MPreferences.getValue(GlobalVars.PREF_SHIO)
//        val shioBytes = Base64.decode(shio, Base64.NO_WRAP)
//        val iv = MPreferences.getValue(GlobalVars.PREF_IV)
//        val ivBytes = Base64.decode(iv, Base64.NO_WRAP)
//
//        //regenerate key from password
//        val keySpec = getSecretKeyAPI21(shioBytes)
//
//        //Decrypt
//        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
//        val ivSpec = IvParameterSpec(ivBytes)
//        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
//        val decryptedBytes = cipher.doFinal(string.toByteArray(charset("UTF-8")))
//
//        return String(decryptedBytes, Charsets.UTF_8)
//    }
}