// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.testapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.whosnickdoglio.testapp.vanilla.ComponentProvider
import dev.whosnickdoglio.testapp.vanilla.DaggerVanillaComponent
import dev.whosnickdoglio.testapp.vanilla.VanillaComponent

@HiltAndroidApp
public class TestApp : Application(), ComponentProvider {
    override val component: VanillaComponent by lazy { DaggerVanillaComponent.create() }
}
