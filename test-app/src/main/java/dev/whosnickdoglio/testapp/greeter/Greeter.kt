// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.testapp.greeter

import android.util.Log
import javax.inject.Inject

public fun interface Greeter {
    public fun hello(name: String)
}

public class GreeterImpl @Inject constructor() : Greeter {
    override fun hello(name: String) {
        Log.i("Greeter", "Hello $name")
    }
}
