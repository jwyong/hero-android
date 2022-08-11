package com.ntu.hero.global

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.hardware.biometrics.BiometricPrompt
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.provider.Settings
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ntu.hero.R
import java.io.IOException
import java.security.InvalidKeyException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableKeyException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.security.cert.CertificateException


class BioMetricsHelper {
    // helpers
    val uiHelper = UIHelper()
    val miscHelper = MiscHelper()

    // check if phone supports touch id
    fun phoneSupportsTouchID(context: Context?): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            var fingerprintManager =
                context?.getSystemService(Context.FINGERPRINT_SERVICE)

            if (fingerprintManager != null) {
                fingerprintManager = fingerprintManager as FingerprintManager

                if (!fingerprintManager.isHardwareDetected) {
                    // Device doesn't support fingerprint authentication
                    return 0

                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    // User hasn't enrolled any fingerprints to authenticate with
                    return 1

                } else {
                    // Everything is ready for fingerprint authentication
                    return 2
                }
            } else {
                // Device doesn't support fingerprint authentication
                return 0
            }
        } else {
            return 0
        }
    }

    // add touch id btns
    fun enableDisableTouchID(
        context: Context?,
        obsIsTouchIDEnabled: ObservableBoolean
    ) {
        // check if user enabled touch id or not
        if (MPreferences.getIntValue(GlobalVars.PREF_TOUCH_ID_ENABLED) == 1) { // already enabled, do disable actions
            // show dialog before disabling
            val disableAction = Runnable {
                // set pref to not enabled
                MPreferences.saveint(GlobalVars.PREF_TOUCH_ID_ENABLED, 0)

                obsIsTouchIDEnabled.set(false)
            }
            uiHelper.dialog2btn(
                context!!,
                R.string.disable_touch_id_confirm,
                R.string.ok,
                null,
                disableAction,
                null,
                true
            )

        } else { // not enabled yet, do enable actions
            // show fingerprint scanner for verification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val authSuccessFunc = Runnable {
                    // if auth success, save to pref and toast user
                    MPreferences.saveint(GlobalVars.PREF_TOUCH_ID_ENABLED, 1)

                    miscHelper.toastMsgInt(
                        context,
                        R.string.touch_id_enabled,
                        Toast.LENGTH_SHORT
                    )

                    obsIsTouchIDEnabled.set(true)
                }
                verifyTouchID(context, authSuccessFunc)
            }
        }
    }

    // function for verifying touchID (will show popup)
    fun verifyTouchID(
        context: Context?,
        authSuccessFunc: Runnable?
    ) {
        // check if touchID added or not first
        when (phoneSupportsTouchID(context)) {
            0 -> { // no touchID hardware
                uiHelper.dialog1btn(
                    context!!,
                    com.ntu.hero.R.string.no_touch_hardware,
                    null,
                    null,
                    true
                )
            }

            1 -> { // got touch hardware, no id yet - ask user to add in android settings
                val addTouchIDFunc = Runnable {
                    context?.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                }

                uiHelper.dialog2btn(
                    context!!,
                    com.ntu.hero.R.string.no_touch_id,
                    com.ntu.hero.R.string.add_fingerprint,
                    null,
                    addTouchIDFunc,
                    null,
                    true
                )
            }

            2 -> { // got hardware and id
                // show touch prompt (based on android ver)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    // biometric prompt
                    bioMetricsDialogAPI28(context!!, authSuccessFunc)

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // fingerprint manager
                    bioMetricsDialogAPI23(context, authSuccessFunc)
                }
            }
        }
    }

    // create cipher for fingerprint
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initCipher(secretKey: SecretKey): Cipher? {
        try {
            val cipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )

            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return cipher

        } catch (e: KeyPermanentlyInvalidatedException) {
            return null

        } catch (e: KeyStoreException) {
            throw RuntimeException("KeyStoreException", e)

        } catch (e: CertificateException) {
            throw RuntimeException("CertificateException", e)

        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("UnrecoverableKeyException", e)

        } catch (e: IOException) {
            throw RuntimeException("IOException", e)

        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("NoSuchAlgorithmException", e)

        } catch (e: InvalidKeyException) {
            throw RuntimeException("InvalidKeyException", e)

        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("NoSuchPaddingException", e)

        }

    }

    // universal cancellation signal
    private val cancellationSignal = CancellationSignal()

    // prompt for biometrics dialog (api28)
    @RequiresApi(Build.VERSION_CODES.P)
    private fun bioMetricsDialogAPI28(
        context: Context,
        authSuccessFunc: Runnable?
    ) {
        val biometricCallback = BioMetricsAuthCallbackAPI28(
            context,
            authSuccessFunc
        )

        val mainEx = context.mainExecutor
        val biometricPrompt = BiometricPrompt.Builder(context)
            .setTitle(context.getString(R.string.touch_scan_title))
            .setDescription(context.getString(R.string.touch_scan_msg))
            .setNegativeButton("Cancel", mainEx,
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        Log.d(GlobalVars.TAG1, "BioMetricsHelper, onClick: cancelled")
                    }

                })
            .build()

        biometricPrompt.authenticate(cancellationSignal, mainEx, biometricCallback)
    }

    // prompt for biometrics dialog (api23)
    @RequiresApi(Build.VERSION_CODES.M)
    private fun bioMetricsDialogAPI23(
        context: Context?,
        authSuccessFunc: Runnable?
    ) {
        // show fingerprint UI
        val btmSheetDialog = uiHelper.btmDialogTouchID(context!!, null, true)

        // generate key first
        val secretKey = EncrpytionHelper().getSecretKey(
            KeyProperties.BLOCK_MODE_CBC, KeyProperties.ENCRYPTION_PADDING_PKCS7,
            false, 120
        )

        // init cipher then auth fingerprint
        val cipher = initCipher(secretKey)
        if (cipher != null) {
            val fingerprintManager =
                context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager

            fingerprintManager.authenticate(
                FingerprintManager.CryptoObject(cipher),
                cancellationSignal,
                0, // flags
                FingerPrintAuthCallbackAPI23(
                    context,
                    btmSheetDialog,
                    authSuccessFunc
                ), // authentication callback
                null
            ) // handler

            btmSheetDialog.setOnDismissListener {
                cancellationSignal.cancel()
            }
        }
    }

    // biometrics callback for android 28
    @RequiresApi(Build.VERSION_CODES.P)
    class BioMetricsAuthCallbackAPI28(
        val context: Context?,
        val authSuccessFunc: Runnable?
    ) : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)

            Log.e(
                GlobalVars.TAG1,
                "BioMetricsAuthCallbackAPI28, onAuthenticationError: errorCode = $errorCode, errorStr = $errString"
            )
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)

            authSuccessFunc?.run()

            Log.d(
                GlobalVars.TAG1,
                "BioMetricsAuthCallbackAPI28, onAuthenticationSucceeded: results = $result"
            )
        }

        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
            super.onAuthenticationHelp(helpCode, helpString)

            Log.d(
                GlobalVars.TAG1,
                "BioMetricsAuthCallbackAPI28, onAuthenticationHelp: helpCode = $helpCode, helpStr = $helpString"
            )
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()

            Log.e(GlobalVars.TAG1, "BioMetricsAuthCallbackAPI28, onAuthenticationFailed: ")
        }

    }

    // fingerprint callback for android 23
    @RequiresApi(Build.VERSION_CODES.M)
    class FingerPrintAuthCallbackAPI23(
        val context: Context?,
        val btmSheetDialog: BottomSheetDialog,
        val authSuccessFunc: Runnable?
    ) : FingerprintManager.AuthenticationCallback() {
        val btmSheetTV = btmSheetDialog.findViewById<TextView>(R.id.dialog_msg)

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)

            btmSheetTV?.text = errString

            Log.e(
                GlobalVars.TAG1,
                "fingerAuthCallbackAPI23, onAuthenticationError: errorCode = $errorCode, errorStr = $errString"
            )
        }

        override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)

            btmSheetTV?.text = context?.getString(R.string.touch_id_enabled)
            authSuccessFunc?.run()
            btmSheetDialog.dismiss()

            Log.d(
                GlobalVars.TAG1,
                "fingerAuthCallbackAPI23, onAuthenticationSucceeded: result = $result"
            )
        }

        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
            super.onAuthenticationHelp(helpCode, helpString)

            btmSheetTV?.text = helpString

            Log.d(
                GlobalVars.TAG1,
                "fingerAuthCallbackAPI23, onAuthenticationHelp: helpCode = $helpCode, helpStr = $helpString"
            )
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()

            btmSheetTV?.text = context?.getString(R.string.try_again)

            Log.e(GlobalVars.TAG1, "fingerAuthCallbackAPI23, onAuthenticationFailed: ")
        }
    }
}