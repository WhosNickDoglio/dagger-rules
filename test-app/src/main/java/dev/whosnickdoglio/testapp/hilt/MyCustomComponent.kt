/*
 * Copyright (C) 2025 Nicholas Doglio
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
public interface MyCustomComponent {

    @DefineComponent.Builder
    public interface Builder {
        public fun context(@BindsInstance context: Context): Builder

        public fun build(): MyCustomComponent
    }
}

@Scope @Retention public annotation class MyCustomScope
