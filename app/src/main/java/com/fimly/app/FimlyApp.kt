package com.fimly.app

import android.app.Application
import com.fimly.app.di.domainModule
import com.fimly.app.di.networkModule
import com.fimly.app.di.repositoryModule
import com.fimly.app.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FimlyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@FimlyApp)
            modules(networkModule, repositoryModule, domainModule,viewModelModule)
        }
    }
}