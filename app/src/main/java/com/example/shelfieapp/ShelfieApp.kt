package com.example.shelfieapp

import android.app.Application
import com.example.shelfieapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ShelfieApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ShelfieApp)
            modules(appModule)
        }
    }
}