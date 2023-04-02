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
package dev.whosnickdoglio.hilt.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import dev.whosnickdoglio.stubs.daggerAnnotations
import org.junit.Test

class MissingInstallInDetectorTest {

    @Test
    fun `kotlin provides @Module but is missing @InstallIn annotation shows an error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                import dagger.Module
                import dagger.Provides

                @Module
                class MyModule {
                    @Provides fun provideMyThing(): String = "Hello World"
                    @Provides fun provideMyOtherThing(): Int = 1
                }
            """
                    )
                    .indented()
            )
            .issues(MissingInstallInDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyModule.kt:5: Error: Hilt modules and entry points require the @InstallIn annotation to be properly connected to a Component. Annotate this class with @InstallIn and the Hilt component you want to connect it to, the most commonly used Component is the SingletonComponent. [MissingInstallInAnnotation]
                    class MyModule {
                          ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `kotlin provides has both @Module and @InstallIn annotation does not show an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                import dagger.Module
                import dagger.Provides
                import dagger.hilt.InstallIn

                @Module
                @InstallIn
                class MyModule {
                    @Provides fun provideMyThing(): String = "Hello World"
                    @Provides fun provideMyOtherThing(): Int = 1
                }
            """
                    )
                    .indented()
            )
            .issues(MissingInstallInDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin @Binds @Module but is missing @InstallIn annotation shows an error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                import dagger.Module
                import dagger.Binds

                interface PizzaMaker
                class PizzaMakerImpl: PizzaMaker

                interface Repository
                class SqlRepository : Repository


                @Module
                interface MyModule {
                    @Binds fun bindsPizza(impl: PizzaMakerImpl): PizzaMaker
                    @Binds fun bindsRepository(impl: SqlRepository): Repository
                }
            """
                    )
                    .indented()
            )
            .issues(MissingInstallInDetector.ISSUE)
            .run()
            .expect(
                """
                    src/PizzaMaker.kt:12: Error: Hilt modules and entry points require the @InstallIn annotation to be properly connected to a Component. Annotate this class with @InstallIn and the Hilt component you want to connect it to, the most commonly used Component is the SingletonComponent. [MissingInstallInAnnotation]
                    interface MyModule {
                              ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `kotlin @Binds has both @Module and @InstallIn annotation does not show an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                import dagger.Module
                import dagger.Binds
                import dagger.hilt.InstallIn

                interface PizzaMaker
                class PizzaMakerImpl: PizzaMaker

                @Module
                @InstallIn
                interface MyModule {
                    @Binds fun bindsPizza(impl: PizzaMakerImpl): PizzaMaker
                }
            """
                    )
                    .indented()
            )
            .issues(MissingInstallInDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin companion object @Binds has both @Module and @InstallIn annotation does not show an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                import dagger.Module
                import dagger.Binds
                import dagger.Provides
                import dagger.hilt.InstallIn

                interface PizzaMaker
                class PizzaMakerImpl: PizzaMaker

                @Module
                @InstallIn
                interface MyModule {

                    @Binds fun bindsPizza(impl: PizzaMakerImpl): PizzaMaker

                    companion object {
                        @Provides fun provideSomething(): String = "Hello World"
                    }

                }
            """
                    )
                    .indented()
            )
            .issues(MissingInstallInDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin companion object @Binds has @Module but no  @InstallIn annotation show an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                import dagger.Module
                import dagger.Binds
                import dagger.Provides

                interface PizzaMaker
                class PizzaMakerImpl: PizzaMaker

                @Module
                interface MyModule {

                    @Binds fun bindsPizza(impl: PizzaMakerImpl): PizzaMaker

                    companion object {
                        @Provides fun provideSomething(): String = "Hello World"
                    }

                }
            """
                    )
                    .indented()
            )
            .issues(MissingInstallInDetector.ISSUE)
            .run()
            .expect(
                """
                    src/PizzaMaker.kt:9: Error: Hilt modules and entry points require the @InstallIn annotation to be properly connected to a Component. Annotate this class with @InstallIn and the Hilt component you want to connect it to, the most commonly used Component is the SingletonComponent. [MissingInstallInAnnotation]
                    interface MyModule {
                              ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `java provides @Module but is missing @InstallIn annotation shows an error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.java(
                        """
                import dagger.Module;
                import dagger.Provides;

                @Module
                class MyModule {
                    @Provides String provideMyThing() {
                        return "Hello World";
                    }
                }
            """
                    )
                    .indented()
            )
            .issues(MissingInstallInDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyModule.java:5: Error: Hilt modules and entry points require the @InstallIn annotation to be properly connected to a Component. Annotate this class with @InstallIn and the Hilt component you want to connect it to, the most commonly used Component is the SingletonComponent. [MissingInstallInAnnotation]
                    class MyModule {
                          ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `java provides has both @Module and @InstallIn annotation does not show an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                daggerAnnotations,
                TestFiles.java(
                        """
                import dagger.Module;
                import dagger.Provides;
                import dagger.hilt.InstallIn;

                @Module
                @InstallIn
                class MyModule {
                    @Provides String provideMyThing() {
                        return "Hello World";
                    }
                }
            """
                    )
                    .indented()
            )
            .issues(MissingInstallInDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java @Binds @Module but is missing @InstallIn annotation shows an error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.java(
                        """
                import dagger.Module;
                import dagger.Binds;

                interface PizzaMaker {}
                class PizzaMakerImpl extends PizzaMaker {}


                @Module
                interface MyModule {
                    @Binds PizzaMaker binds(PizzaMakerImpl impl);
                }
            """
                    )
                    .indented()
            )
            .issues(MissingInstallInDetector.ISSUE)
            .run()
            .expect(
                """
                    src/PizzaMaker.java:9: Error: Hilt modules and entry points require the @InstallIn annotation to be properly connected to a Component. Annotate this class with @InstallIn and the Hilt component you want to connect it to, the most commonly used Component is the SingletonComponent. [MissingInstallInAnnotation]
                    interface MyModule {
                              ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `java @Binds has both @Module and @InstallIn annotation does not show an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                daggerAnnotations,
                TestFiles.java(
                    """
                import dagger.Module;
                import dagger.Binds;
                import dagger.hilt.InstallIn;

                interface PizzaMaker {}
                class PizzaMakerImpl extends PizzaMaker {}

                @Module
                @InstallIn
                interface MyModule {
                    @Binds PizzaMaker binds(PizzaMakerImpl impl);
                }
            """
                        .trimIndent()
                )
            )
            .issues(MissingInstallInDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin entry point is missing @InstallIn annotation shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import dagger.hilt.EntryPoint

                    @EntryPoint
                    interface MyEntryPoint {
                        fun myString(): String
                    }
            """
                        .trimIndent()
                )
            )
            .issues(MissingInstallInDetector.ISSUE)
            .run()
            .expect(
                """
                src/MyEntryPoint.kt:4: Error: Hilt modules and entry points require the @InstallIn annotation to be properly connected to a Component. Annotate this class with @InstallIn and the Hilt component you want to connect it to, the most commonly used Component is the SingletonComponent. [MissingInstallInAnnotation]
                interface MyEntryPoint {
                          ~~~~~~~~~~~~
                1 errors, 0 warnings
            """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `java entry point is missing @InstallIn annotation shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.java(
                    """
                    import dagger.hilt.EntryPoint;

                    @EntryPoint
                    interface MyEntryPoint {
                        String myString();
                    }
            """
                        .trimIndent()
                )
            )
            .issues(MissingInstallInDetector.ISSUE)
            .run()
            .expect(
                """
                src/MyEntryPoint.java:4: Error: Hilt modules and entry points require the @InstallIn annotation to be properly connected to a Component. Annotate this class with @InstallIn and the Hilt component you want to connect it to, the most commonly used Component is the SingletonComponent. [MissingInstallInAnnotation]
                interface MyEntryPoint {
                          ~~~~~~~~~~~~
                1 errors, 0 warnings
            """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `kotlin entry point has @InstallIn annotation does not shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import dagger.hilt.EntryPoint
                    import dagger.hilt.InstallIn

                    @EntryPoint
                    @InstallIn
                    interface MyEntryPoint {
                        fun myString(): String
                    }
            """
                        .trimIndent()
                )
            )
            .issues(MissingInstallInDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java entry point has @InstallIn annotation does not shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.java(
                    """
                    import dagger.hilt.EntryPoint;
                    import dagger.hilt.InstallIn;

                    @EntryPoint
                    @InstallIn
                    interface MyEntryPoint {
                        String myString();
                    }
            """
                        .trimIndent()
                )
            )
            .issues(MissingInstallInDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }
}
