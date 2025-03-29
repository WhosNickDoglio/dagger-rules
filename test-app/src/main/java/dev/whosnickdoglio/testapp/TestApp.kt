/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.testapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.whosnickdoglio.testapp.anvil.DaggerAnvilComponent
import dev.whosnickdoglio.testapp.vanilla.ComponentProvider
import dev.whosnickdoglio.testapp.vanilla.DaggerVanillaComponent
import dev.whosnickdoglio.testapp.vanilla.VanillaComponent

@HiltAndroidApp
public class TestApp : Application(), ComponentProvider {
    override val component: VanillaComponent by lazy { DaggerVanillaComponent.create() }

    override fun onCreate() {
        super.onCreate()
        DaggerAnvilComponent.create()
    }
}
