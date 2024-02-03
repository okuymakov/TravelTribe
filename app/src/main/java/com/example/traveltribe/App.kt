package com.example.traveltribe

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("4fa31978-3b8d-4fc3-ab20-a22d63529cc9")
    }
}