/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.anvil.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import dev.whosnickdoglio.stubs.daggerAnnotations
import dev.whosnickdoglio.stubs.daggerMultibindingAnnotations
import org.junit.Test

class FavorContributesBindingOverBindsDetectorTest {
  private val myThingStubs =
    TestFiles.kotlin(
      """
                interface MyThing
                class MyThingImpl: MyThing
                """,
    )
      .indented()

  private val pizzaMakerStubs =
    TestFiles.kotlin(
      """
                    interface PizzaMaker
                    class PizzaMakerImpl: PizzaMaker
                    """,
    )
      .indented()

  private val multibindingStubs =
    TestFiles.kotlin(
      """
            interface JsonAdapter

            class BasketballJsonAdapter: JsonAdapter
            class BaseballJsonAdapter: JsonAdapter
    """,
    )
      .indented()

  @Test
  fun `kotlin @Binds method should trigger warning`() {
    TestLintTask.lint()
      .files(
        daggerAnnotations,
        myThingStubs,
        pizzaMakerStubs,
        TestFiles.kotlin(
          """
                import dagger.Module
                import dagger.Binds

                @Module
                interface MyModule {
                    @Binds
                    fun provideMyThing(impl: MyThingImpl): MyThing

                    @Binds
                    fun providePizzaMaker(impl: PizzaMakerImpl): PizzaMaker
                }
            """,
        )
          .indented(),
      )
      .issues(FavorContributesBindingOverBindsDetector.ISSUE)
      .run()
      .expect(
        """
                    src/MyModule.kt:7: Warning: You can use @ContributesBinding over @Binds [ContributesBindingOverBinds]
                        fun provideMyThing(impl: MyThingImpl): MyThing
                            ~~~~~~~~~~~~~~
                    src/MyModule.kt:10: Warning: You can use @ContributesBinding over @Binds [ContributesBindingOverBinds]
                        fun providePizzaMaker(impl: PizzaMakerImpl): PizzaMaker
                            ~~~~~~~~~~~~~~~~~
                    0 errors, 2 warnings
                """
          .trimIndent(),
      )
      .expectWarningCount(2)
  }

  @Test
  fun `kotlin companion object @Binds method should trigger warning`() {
    TestLintTask.lint()
      .files(
        daggerAnnotations,
        myThingStubs,
        pizzaMakerStubs,
        TestFiles.kotlin(
          """
                import dagger.Module
                import dagger.Binds

                @Module
                interface MyModule {
                    @Binds
                    fun provideMyThing(impl: MyThingImpl): MyThing

                    @Binds
                    fun providePizzaMaker(impl: PizzaMakerImpl): PizzaMaker

                    companion object {
                        @Provides fun provideSomething(): String = "Hello world"

                        @Provides fun provideAnotherThing(): Int = 1
                    }
                }
            """,
        )
          .indented(),
      )
      .issues(FavorContributesBindingOverBindsDetector.ISSUE)
      .run()
      .expect(
        """
                    src/MyModule.kt:7: Warning: You can use @ContributesBinding over @Binds [ContributesBindingOverBinds]
                        fun provideMyThing(impl: MyThingImpl): MyThing
                            ~~~~~~~~~~~~~~
                    src/MyModule.kt:10: Warning: You can use @ContributesBinding over @Binds [ContributesBindingOverBinds]
                        fun providePizzaMaker(impl: PizzaMakerImpl): PizzaMaker
                            ~~~~~~~~~~~~~~~~~
                    0 errors, 2 warnings
                """
          .trimIndent(),
      )
      .expectWarningCount(2)
  }

  @Test
  fun `java @Binds method should not trigger warning`() {
    TestLintTask.lint()
      .files(
        daggerAnnotations,
        myThingStubs,
        pizzaMakerStubs,
        TestFiles.java(
          """
                import dagger.Module;
                import dagger.Binds;

                @Module
                interface MyModule {
                    @Binds
                    MyThing provideMyThing(MyThingImpl impl);

                    @Binds
                    PizzaMaker providePizzaMaker(PizzaMakerImpl impl);
                }
            """,
        )
          .indented(),
      )
      .issues(FavorContributesBindingOverBindsDetector.ISSUE)
      .run()
      .expectClean()
      .expectWarningCount(0)
  }

  @Test
  fun `kotlin @IntoMap on @Binds method should trigger warning`() {
    TestLintTask.lint()
      .files(
        daggerAnnotations,
        daggerMultibindingAnnotations,
        multibindingStubs,
        TestFiles.kotlin(
          """
                import dagger.Module
                import dagger.Binds
                import dagger.multibindings.IntoMap
                import dagger.multibindings.StringKey

                @Module
                interface MyModule {

                    @Binds @IntoMap @StringKey("baseball")
                    fun baseball(impl: BaseballJsonAdapter): JsonAdapter

                    @Binds @IntoMap @StringKey("basketball")
                    fun basketball(impl: BasketballJsonAdapter): JsonAdapter
                }
            """,
        )
          .indented(),
      )
      .issues(FavorContributesBindingOverBindsDetector.ISSUE)
      .run()
      .expect(
        """
                src/MyModule.kt:10: Warning: You can use @ContributesMultibinding over @Binds [ContributesBindingOverBinds]
                    fun baseball(impl: BaseballJsonAdapter): JsonAdapter
                        ~~~~~~~~
                src/MyModule.kt:13: Warning: You can use @ContributesMultibinding over @Binds [ContributesBindingOverBinds]
                    fun basketball(impl: BasketballJsonAdapter): JsonAdapter
                        ~~~~~~~~~~
                0 errors, 2 warnings
            """
          .trimIndent(),
      )
      .expectWarningCount(2)
  }

  @Test
  fun `kotlin @IntoSet on @Binds method should trigger warning`() {
    TestLintTask.lint()
      .files(
        daggerAnnotations,
        daggerMultibindingAnnotations,
        multibindingStubs,
        TestFiles.kotlin(
          """
                import dagger.Module
                import dagger.Binds
                import dagger.multibindings.IntoSet

                @Module
                interface MyModule {

                    @Binds @IntoSet
                    fun baseball(impl: BaseballJsonAdapter): JsonAdapter

                    @Binds @IntoSet
                    fun basketball(impl: BasketballJsonAdapter): JsonAdapter
                }
            """,
        )
          .indented(),
      )
      .issues(FavorContributesBindingOverBindsDetector.ISSUE)
      .run()
      .expect(
        """
                src/MyModule.kt:9: Warning: You can use @ContributesMultibinding over @Binds [ContributesBindingOverBinds]
                    fun baseball(impl: BaseballJsonAdapter): JsonAdapter
                        ~~~~~~~~
                src/MyModule.kt:12: Warning: You can use @ContributesMultibinding over @Binds [ContributesBindingOverBinds]
                    fun basketball(impl: BasketballJsonAdapter): JsonAdapter
                        ~~~~~~~~~~
                0 errors, 2 warnings
            """
          .trimIndent(),
      )
      .expectWarningCount(2)
  }

  @Test
  fun `kotlin @Binds method with return type that takes a generic shows no warning`() {
    TestLintTask.lint()
      .files(
        daggerAnnotations,
        TestFiles.kotlin(
          """
                import dagger.Module
                import dagger.Binds

                interface Adapter<T>
                class MyAdapter: Adapter<String>

                @Module
                interface MyModule {

                    @Binds @IntoSet
                    fun baseball(impl: MyAdapter): Adapter<String>
                }
            """,
        )
          .indented(),
      )
      .issues(FavorContributesBindingOverBindsDetector.ISSUE)
      .run()
      .expectClean()
      .expectWarningCount(0)
  }

  @Test
  fun `java @IntoMap on @Binds method should not trigger warning`() {
    TestLintTask.lint()
      .files(
        daggerAnnotations,
        daggerMultibindingAnnotations,
        multibindingStubs,
        TestFiles.java(
          """
                import dagger.Module;
                import dagger.Binds;
                import dagger.multibindings.IntoMap;
                import dagger.multibindings.StringKey;

                @Module
                interface MyModule {

                    @Binds @IntoMap @StringKey("baseball")
                    JsonAdapter baseball(BaseballJsonAdapter impl);

                    @Binds @IntoMap @StringKey("basketball")
                    JsonAdapter basketball(BasketballJsonAdapter impl);
                }
            """,
        )
          .indented(),
      )
      .issues(FavorContributesBindingOverBindsDetector.ISSUE)
      .run()
      .expectClean()
      .expectWarningCount(0)
  }

  @Test
  fun `java @IntoSet on @Binds method should not trigger warning`() {
    TestLintTask.lint()
      .files(
        daggerAnnotations,
        daggerMultibindingAnnotations,
        multibindingStubs,
        TestFiles.java(
          """
                import dagger.Module;
                import dagger.Binds;
                import dagger.multibindings.IntoSet;

                @Module
                interface MyModule {

                    @Binds @IntoSet
                    JsonAdapter baseball(BaseballJsonAdapter impl);

                    @Binds @IntoSet
                    JsonAdapter basketball(BasketballJsonAdapter impl);
                }
            """,
        )
          .indented(),
      )
      .issues(FavorContributesBindingOverBindsDetector.ISSUE)
      .run()
      .expectClean()
      .expectWarningCount(0)
  }
}
