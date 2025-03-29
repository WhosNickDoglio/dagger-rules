/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.testapp.vanilla

import android.content.Context
import dagger.Component
import javax.inject.Singleton

@Singleton @Component public interface VanillaComponent

internal interface ComponentProvider {
    val component: VanillaComponent
}

internal val Context.injector
    get() = (applicationContext as ComponentProvider).component
