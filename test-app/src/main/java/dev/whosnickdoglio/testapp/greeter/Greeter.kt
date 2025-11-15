// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.testapp.greeter

import android.util.Log
import com.squareup.anvil.annotations.ContributesBinding
import dev.whosnickdoglio.testapp.anvil.AppScope
import javax.inject.Inject

public fun interface Greeter {
    public fun hello(name: String)
}

@ContributesBinding(AppScope::class)
public class GreeterImpl @Inject constructor() : Greeter {
    override fun hello(name: String) {
        Log.i("Greeter", "Hello $name")
    }
}
