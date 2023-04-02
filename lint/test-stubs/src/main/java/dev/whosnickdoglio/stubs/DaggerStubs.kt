/*
 * MIT License
 *
 * Copyright (c) 2023 Nicholas Doglio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.whosnickdoglio.stubs

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles

val daggerAnnotations: TestFile =
    TestFiles.kotlin(
        """
        package  dagger

        annotation class Provides
        annotation class Binds
        annotation class Module
        annotation class Multibinds
    """
            .trimIndent()
    )

val daggerMultibindingAnnotations: TestFile =
    TestFiles.kotlin(
        """
    package dagger.multibinding

    annotation class IntoMap
    annotation class IntoSet
    annotation class StringKey(val key: String)
    annotation class IntKey(val key: Int)
"""
            .trimIndent()
    )

val injectAnnotation: TestFile =
    TestFiles.kotlin(
        """
    package javax.inject

    annotation class Inject
"""
            .trimIndent()
    )
