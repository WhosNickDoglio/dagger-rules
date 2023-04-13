/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import dev.whosnickdoglio.stubs.daggerAnnotations
import org.junit.Test

class CorrectBindsUsageDetectorTest {

    private val pizzaMakerStubs =
        TestFiles.kotlin(
                """
                    interface PizzaMaker
                    class PizzaMakerImpl: PizzaMaker
                    class NotAPizzaMaker
                    """
            )
            .indented()

    private val repositoryStubs =
        TestFiles.kotlin(
            """
                interface Repository
                class InMemoryRepository : Repository
                class NotARepository
                """
                .trimIndent()
        )

    @Test
    fun `java @Binds method with a parameter that is a subclass of the return type does not trigger error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                pizzaMakerStubs,
                repositoryStubs,
                TestFiles.java(
                        """
                    import dagger.Module;
                    import dagger.Binds;

                    @Module
                    interface MyModule {

                        @Binds PizzaMaker bindsPizzaMaker(PizzaMakerImpl impl);

                        @Binds Repository bindsRepository(InMemoryRepository impl);
                    }
                """
                    )
                    .indented()
            )
            .issues(CorrectBindsUsageDetector.ISSUE_CORRECT_RETURN_TYPE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java @Binds method with a parameter that is not a subclass of the return type triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                pizzaMakerStubs,
                repositoryStubs,
                TestFiles.java(
                        """
                    import dagger.Module;
                    import dagger.Binds;

                    @Module
                    interface MyModule {

                        @Binds PizzaMaker bindsPizzaMaker(NotAPizzaMaker impl);

                        @Binds Repository bindsRepository(NotARepository impl);
                    }
                """
                    )
                    .indented()
            )
            .issues(CorrectBindsUsageDetector.ISSUE_CORRECT_RETURN_TYPE)
            .run()
            .expect(
                """
                    src/MyModule.java:7: Error: @Binds method parameters need to be a subclass of the return type. Make sure you're passing the correct parameter or the intended subclass is implementing the return type interface. [BindsWithCorrectReturnType]
                        @Binds PizzaMaker bindsPizzaMaker(NotAPizzaMaker impl);
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    src/MyModule.java:9: Error: @Binds method parameters need to be a subclass of the return type. Make sure you're passing the correct parameter or the intended subclass is implementing the return type interface. [BindsWithCorrectReturnType]
                        @Binds Repository bindsRepository(NotARepository impl);
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    2 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(2)
    }

    @Test
    fun `kotlin @Binds method with a parameter that is a subclass of the return type does not trigger error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                pizzaMakerStubs,
                repositoryStubs,
                TestFiles.kotlin(
                        """

                    @Module
                    interface MyModule {

                        @Binds fun bindsPizzaMaker(impl: PizzaMakerImpl): PizzaMaker

                        @Binds fun bindsRepository(impl: InMemoryRepository): Repository
                    }
                """
                    )
                    .indented()
            )
            .issues(CorrectBindsUsageDetector.ISSUE_CORRECT_RETURN_TYPE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin @Binds method with a parameter that is not a subclass of the return type triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                pizzaMakerStubs,
                repositoryStubs,
                TestFiles.kotlin(
                        """
                    import dagger.Module
                    import dagger.Binds

                    @Module
                    interface MyModule {

                        @Binds fun bindsPizzaMaker(impl: NotAPizzaMaker): PizzaMaker
                    }
                """
                    )
                    .indented()
            )
            .issues(CorrectBindsUsageDetector.ISSUE_CORRECT_RETURN_TYPE)
            .run()
            .expect(
                """
                    src/MyModule.kt:7: Error: @Binds method parameters need to be a subclass of the return type. Make sure you're passing the correct parameter or the intended subclass is implementing the return type interface. [BindsWithCorrectReturnType]
                        @Binds fun bindsPizzaMaker(impl: NotAPizzaMaker): PizzaMaker
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `kotlin @Binds extension method with a parameter that is a subclass of the return type no error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                pizzaMakerStubs,
                TestFiles.kotlin(
                        """
                    import dagger.Module
                    import dagger.Binds

                    @Module
                    interface MyModule {

                        @Binds fun PizzaMakerImpl.bindPizzaMaker(): PizzaMaker
                    }
                """
                    )
                    .indented()
            )
            .issues(CorrectBindsUsageDetector.ISSUE_CORRECT_RETURN_TYPE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin @Binds extension method with a parameter that is not a subclass of the return type triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                pizzaMakerStubs,
                TestFiles.kotlin(
                        """
                    import dagger.Module
                    import dagger.Binds

                    @Module
                    interface MyModule {

                        @Binds fun NotPizzaMaker.bindPizzaMaker(): PizzaMaker
                    }
                """
                    )
                    .indented()
            )
            .issues(CorrectBindsUsageDetector.ISSUE_CORRECT_RETURN_TYPE)
            .run()
            .expect(
                """
                    src/MyModule.kt:7: Error: @Binds method parameters need to be a subclass of the return type. Make sure you're passing the correct parameter or the intended subclass is implementing the return type interface. [BindsWithCorrectReturnType]
                        @Binds fun NotPizzaMaker.bindPizzaMaker(): PizzaMaker
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `kotlin non-abstract @Binds method triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                pizzaMakerStubs,
                TestFiles.kotlin(
                        """
                    import dagger.Module
                    import dagger.Binds

                    @Module
                    class MyModule {

                        @Binds fun bindPizzaMaker(): PizzaMaker = PizzaMakerImpl()
                    }
                """
                    )
                    .indented()
            )
            .issues(CorrectBindsUsageDetector.ISSUE_BINDS_ABSTRACT)
            .run()
            .expect(
                """
                src/MyModule.kt:7: Error: Must be abstract [BindsMustBeAbstract]
                    @Binds fun bindPizzaMaker(): PizzaMaker = PizzaMakerImpl()
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
            """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `java non-abstract @Binds method triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                pizzaMakerStubs,
                TestFiles.java(
                        """
                    import dagger.Module;
                    import dagger.Binds;

                    @Module
                    class MyModule {

                        @Binds PizzaMaker bindPizzaMaker() {
                            return PizzaMakerImpl();
                        }
                    }
                """
                    )
                    .indented()
            )
            .issues(CorrectBindsUsageDetector.ISSUE_BINDS_ABSTRACT)
            .run()
            .expect(
                """
                src/MyModule.java:7: Error: Must be abstract [BindsMustBeAbstract]
                    @Binds PizzaMaker bindPizzaMaker() {
                    ^
                1 errors, 0 warnings
            """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }
}
