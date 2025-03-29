/*
 * Copyright (C) 2025 Nicholas Doglio
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
                    .indented(),
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
                    .indented(),
            )
            .issues(CorrectBindsUsageDetector.ISSUE_CORRECT_RETURN_TYPE)
            .run()
            .expect(
                """
                    src/MyModule.java:7: Error: @Binds method parameters need to be a subclass of the return type. Make sure you're passing the correct parameter or the intended subclass is implementing the return type interface.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#a-binds-method-parameter-should-be-a-subclass-of-its-return-type for more information. [BindsWithCorrectReturnType]
                        @Binds PizzaMaker bindsPizzaMaker(NotAPizzaMaker impl);
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    src/MyModule.java:9: Error: @Binds method parameters need to be a subclass of the return type. Make sure you're passing the correct parameter or the intended subclass is implementing the return type interface.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#a-binds-method-parameter-should-be-a-subclass-of-its-return-type for more information. [BindsWithCorrectReturnType]
                        @Binds Repository bindsRepository(NotARepository impl);
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    2 errors, 0 warnings
                """
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
                    .indented(),
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
                    .indented(),
            )
            .issues(CorrectBindsUsageDetector.ISSUE_CORRECT_RETURN_TYPE)
            .run()
            .expect(
                """
                    src/MyModule.kt:7: Error: @Binds method parameters need to be a subclass of the return type. Make sure you're passing the correct parameter or the intended subclass is implementing the return type interface.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#a-binds-method-parameter-should-be-a-subclass-of-its-return-type for more information. [BindsWithCorrectReturnType]
                        @Binds fun bindsPizzaMaker(impl: NotAPizzaMaker): PizzaMaker
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
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
                    .indented(),
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
                    .indented(),
            )
            .issues(CorrectBindsUsageDetector.ISSUE_CORRECT_RETURN_TYPE)
            .run()
            .expect(
                """
                    src/MyModule.kt:7: Error: @Binds method parameters need to be a subclass of the return type. Make sure you're passing the correct parameter or the intended subclass is implementing the return type interface.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#a-binds-method-parameter-should-be-a-subclass-of-its-return-type for more information. [BindsWithCorrectReturnType]
                        @Binds fun NotPizzaMaker.bindPizzaMaker(): PizzaMaker
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
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
                    .indented(),
            )
            .issues(CorrectBindsUsageDetector.ISSUE_BINDS_ABSTRACT)
            .run()
            .expect(
                """
                    src/MyModule.kt:7: Error: A @Binds method needs to be abstract or Dagger will throw an error at compile time. See https://whosnickdoglio.dev/dagger-rules/rules/#methods-annotated-with-binds-must-be-abstract for more information. [BindsMustBeAbstract]
                        @Binds fun bindPizzaMaker(): PizzaMaker = PizzaMakerImpl()
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
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
                    .indented(),
            )
            .issues(CorrectBindsUsageDetector.ISSUE_BINDS_ABSTRACT)
            .run()
            .expect(
                """
                    src/MyModule.java:7: Error: A @Binds method needs to be abstract or Dagger will throw an error at compile time. See https://whosnickdoglio.dev/dagger-rules/rules/#methods-annotated-with-binds-must-be-abstract for more information. [BindsMustBeAbstract]
                        @Binds PizzaMaker bindPizzaMaker() {
                        ^
                    1 errors, 0 warnings
                """
            )
            .expectErrorCount(1)
    }

    @Test
    fun `kotlin @Binds method that returns Unit triggers error`() {
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

                        @Binds fun bindsPizzaMaker(impl: PizzaMakerImpl): Unit

                        @Binds fun bindsRepository(impl: InMemoryRepository)
                    }
                """
                    )
                    .indented(),
            )
            .issues(CorrectBindsUsageDetector.ISSUE_CORRECT_RETURN_TYPE)
            .run()
            .expect(
                """
                src/MyModule.kt:7: Error: @Binds method parameters need to be a subclass of the return type. Make sure you're passing the correct parameter or the intended subclass is implementing the return type interface.

                See https://whosnickdoglio.dev/dagger-rules/rules/#a-binds-method-parameter-should-be-a-subclass-of-its-return-type for more information. [BindsWithCorrectReturnType]
                    @Binds fun bindsPizzaMaker(impl: PizzaMakerImpl): Unit
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                src/MyModule.kt:9: Error: @Binds method parameters need to be a subclass of the return type. Make sure you're passing the correct parameter or the intended subclass is implementing the return type interface.

                See https://whosnickdoglio.dev/dagger-rules/rules/#a-binds-method-parameter-should-be-a-subclass-of-its-return-type for more information. [BindsWithCorrectReturnType]
                    @Binds fun bindsRepository(impl: InMemoryRepository)
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                2 errors, 0 warnings
            """
            )
            .expectErrorCount(2)
    }

    @Test
    fun `java @Binds method that returns Void triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                repositoryStubs,
                TestFiles.java(
                        """
                    import dagger.Module;
                    import dagger.Binds;

                    @Module
                    interface MyModule {
                        @Binds Void bindsRepository(InMemoryRepository impl);
                    }
                """
                    )
                    .indented(),
            )
            .issues(CorrectBindsUsageDetector.ISSUE_CORRECT_RETURN_TYPE)
            .run()
            .expect(
                """
                src/MyModule.java:6: Error: @Binds method parameters need to be a subclass of the return type. Make sure you're passing the correct parameter or the intended subclass is implementing the return type interface.

                See https://whosnickdoglio.dev/dagger-rules/rules/#a-binds-method-parameter-should-be-a-subclass-of-its-return-type for more information. [BindsWithCorrectReturnType]
                    @Binds Void bindsRepository(InMemoryRepository impl);
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
            """
            )
            .expectErrorCount(1)
    }
}
