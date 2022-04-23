package app.com.choptransit

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class ChopApplication : Application() {

    companion object {
        lateinit var instance: ChopApplication
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        instance = this
    }
}