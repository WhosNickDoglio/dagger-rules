// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.testapp

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import dev.whosnickdoglio.testapp.greeter.Greeter
import javax.inject.Inject

@AndroidEntryPoint
public class MainActivity : AppCompatActivity() {

    @Inject public lateinit var greeter: Greeter

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        greeter.hello("world")
    }
}
