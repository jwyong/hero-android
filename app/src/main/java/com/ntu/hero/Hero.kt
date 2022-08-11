package com.ntu.hero

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.ntu.hero.global.ForegrndListener
import com.ntu.hero.sql.HeroDatabase


class Hero : Application() {
    override fun onCreate() {
        super.onCreate()

        debugGhost()
        setForegroundListener()

        instance = this
    }

    // set foreground listener
    private fun setForegroundListener() {
        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(ForegrndListener())
    }

    // for synchronisation
    companion object {
        @get:Synchronized
        var instance: Hero? = null
            private set

        val database: HeroDatabase?
            @Synchronized get() = if (instance != null) {
                HeroDatabase.getInstance(instance!!)
            } else null
    }

    // for debug ghost sqlite
    internal fun debugGhost() {
        try {
//            DebugGhostBridge(this, GlobalVars.DB_NAME, 5)

        } catch (e: Exception) {
            Log.e("DebugGhost", "DebugGhost not loaded: " + e.message)
        }
    }
}