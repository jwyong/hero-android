package com.ntu.hero.global.encryption

import android.util.Base64
import androidx.annotation.NonNull
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MPreferences
import java.io.IOException
import java.security.*
import javax.crypto.*
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class Encrpytor {

    var encryption: ByteArray? = null
        private set
    var iv: ByteArray? = null
        private set

    @Throws(
        UnrecoverableEntryException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IOException::class,
        InvalidAlgorithmParameterException::class,
        SignatureException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun encryptText(alias: String, textToEncrypt: String): ByteArray {

        val cipher = Cipher.getInstance(GlobalVars.ENCRYPT_TRANS)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias))

        iv = cipher.iv

        // save iv into pref
        val ivBytesStr = Base64.encodeToString(iv, Base64.NO_WRAP)
        MPreferences.save(GlobalVars.PREF_IV, ivBytesStr)

        return cipher.doFinal(textToEncrypt.toByteArray(charset("UTF-8")))
    }

    @NonNull
    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        InvalidAlgorithmParameterException::class
    )
    private fun getSecretKey(alias: String): SecretKey {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // >= API 23
//            val keyGenerator = KeyGenerator
//                .getInstance(KeyProperties.KEY_ALGORITHM_AES, GlobalVars.AND_KEY_STORE)
//
//            keyGenerator.init(
//                KeyGenParameterSpec.Builder(
//                    alias,
//                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
//                )
//                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
//                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
//                    .setRandomizedEncryptionRequired(true)
//                    .build()
//            )
//
//            return keyGenerator.generateKey()
//        } else { // < API 23

            val pbKeySpec = PBEKeySpec(GlobalVars.PF.toCharArray(), GlobalVars.SHIO.toByteArray(), 1324, 256)
            val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded

            return SecretKeySpec(keyBytes, "AES")
//        }
    }
}