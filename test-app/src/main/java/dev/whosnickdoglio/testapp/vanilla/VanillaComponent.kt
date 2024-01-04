/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.testapp.vanilla

import android.content.Context
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface VanillaComponent

interface ComponentProvider {
    val component: VanillaComponent
}

val Context.injector get() = (applicationContext as ComponentProvider).component
