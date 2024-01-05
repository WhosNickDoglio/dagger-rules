/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.testapp.hilt

import android.content.Context
import dagger.BindsInstance
import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Scope

@MyCustomScope
@DefineComponent(parent = SingletonComponent::class)
interface MyCustomComponent {

    @DefineComponent.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder

        fun build(): MyCustomComponent
    }
}

@Scope @Retention annotation class MyCustomScope
