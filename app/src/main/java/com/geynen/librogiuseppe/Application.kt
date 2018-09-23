package com.geynen.librogiuseppe

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

/**
 * Created by Geynen on 22/09/2018.
 */
class Application : Application() {

    companion object {
        lateinit var instance: com.geynen.librogiuseppe.Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Fresco.initialize(this)
    }
}