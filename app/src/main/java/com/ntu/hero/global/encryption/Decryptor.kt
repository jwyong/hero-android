package com.ntu.hero.global.encryption

import android.os.Build
import android.util.Log
import com.ntu.hero.global.GlobalVars
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class Decryptor @Throws(
    CertificateException::class,
    NoSuchAlgorithmException::class,
    KeyStoreException::class,
    IOException::class
)
constructor() {

    private var keyStore: KeyStore? = null

    init {
        initKeyStore()
    }

    @Throws(
        KeyStoreException::class,
        CertificateException::class,
        NoSuchAlgorithmException::class,
        IOException::class
    )
    private fun initKeyStore() {
        keyStore = KeyStore.getInstance(GlobalVars.AND_KEY_STORE)
        keyStore!!.load(null)
    }

    @Throws(
        UnrecoverableEntryException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IOException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidAlgorithmParameterException::class
    )
    fun decryptData(alias: String, encryptedData: ByteArray, encryptionIv: ByteArray): String {

        val cipher = Cipher.getInstance(GlobalVars.ENCRYPT_TRANS)
        val spec = GCMParameterSpec(128, encryptionIv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec)

        return String(cipher.doFinal(encryptedData), Charsets.UTF_8)
    }

    @Throws(
        NoSuchAlgorithmException::class,
        UnrecoverableEntryException::class,
        KeyStoreException::class
    )
    private fun getSecretKey(alias: String): SecretKey {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // >= API 23
            Log.d(GlobalVars.TAG1, "Decryptor: getSecretKey API23")

            return (keyStore!!.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey

        } else {
            Log.d(GlobalVars.TAG1, "Decryptor: getSecretKey API21")

            val pbKeySpec = PBEKeySpec(GlobalVars.PF.toCharArray(), GlobalVars.SHIO.toByteArray(), 1324, 256)
            val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded

            return SecretKeySpec(keyBytes, "AES")
        }
    }
}
