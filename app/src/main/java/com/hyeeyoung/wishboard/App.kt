package com.hyeeyoung.wishboard

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.hyeeyoung.wishboard.config.WishBoardDebugTree
import com.hyeeyoung.wishboard.data.remote.sse.EventSseClient
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var eventSseClient: EventSseClient

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setUpTimber()
        observeAppForeground()
    }

    private fun setUpTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(WishBoardDebugTree())
        }
    }

    // SSE는 포그라운드에서만 연결하고, 백그라운드로 가면 앱이 먼저 끊는다. (백그라운드 알림은 푸시가 맡는다)
    private fun observeAppForeground() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) = eventSseClient.start()

                override fun onStop(owner: LifecycleOwner) = eventSseClient.stop()
            },
        )
    }
}
