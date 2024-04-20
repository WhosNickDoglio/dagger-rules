/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.testapp.hilt

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.whosnickdoglio.testapp.greeter.Greeter
import dev.whosnickdoglio.testapp.greeter.GreeterImpl

@Suppress("MissingContributesToAnnotation")
@InstallIn(SingletonComponent::class)
@Module
public interface BindModule {

    @Suppress("ContributesBindingOverBinds") @Binds
    public fun bindGreeter(impl: GreeterImpl): Greeter
}
