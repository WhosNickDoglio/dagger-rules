## Dagger Rules

[//]: # (TODO add sources or links for rules that have them)

### Types annotated with `@Component` must be abstract

Types annotated with the [`@Component` annotation](https://dagger.dev/api/latest/dagger/Component.html) need to be
abstract so Dagger can
generate a class that implements it.
If a concrete class is annotated with `@Component` Dagger will throw an error at
compile time.

`error: @Component may only be applied to an interface or abstract class`

```kotlin

// Safe
@Component
interface MyComponent

// Safe
@Component
abstract class MyComponent

// Not Safe!
@Component
class MyComponent
```

### Prefer constructor injection over field injection

Field injection should only ever be used for classes that you don't manage their creation (like Android `Activities`
or `Fragments`)
if the creation of the class you're trying to add to the Dagger graph isn't managed by something else like the Android
OS you should be using constructor injection.

```kotlin

class MyClass {
    // BAD! don't do this!
    @Inject
    lateinit var foo: Foo
}

// Good!
class MyClass @Inject constructor(private val foo: Foo)

```

There are a few reasons we want to favor constructor injection over field injection.
The first being is we should think of
a class constructor as a contract of sorts, "if I provide you these dependencies then in return you give me an instance
of this class", if we're doing field injection this resides **outside** the constructor and isn't intuitive that this
class requires additional setup which can lead to consumers misusing it or bugs.
The second being with constructor
injection, we can make our dependencies both private and immutable, so they cannot be altered by anything outside this
class nor could they be reassigned somewhere within the class, because Dagger requires field injection to be public and
mutable this gives things outside our class the ability to mutate or reassign our dependencies which could lead to
unpredictable and hard to debug issues.

More information
here: [Keeping the Daggers Sharp](https://developer.squareup.com/blog/keeping-the-daggers-sharp/#favor-constructor-injection-over-field-injection)

[//]: # (TODO mention `AppComponentFactory` and `FragmentFactory`)

## Prefer `@Inject` annotating classes in your project domain over providing them in a Dagger module

When you want to add something to the Dagger graph that is a **class we own** (A class that has been written by a Zillow
engineer and the source code can be modified), you should use constructor injection over writing a `@Provides` method in
a Dagger `@Module` to add this class to the Dagger graph.
Methods are only for things Dagger can't figure
out how to provide on its own.
These are the three most common scenarios for when you should use a `@Provides` method:

1. Adding a third party dependency to your Dagger Graph.
   When you're using something like Room, Retrofit, or
   `SharedPreferences` you cannot access their code to properly annotate their classes with an `@Inject` annotation for
   Dagger to pick them up.
2. Adding a class to the Dagger graph that doesn't have a straight forward constructor but instead uses a `Factory`
   or `Builder`.
3. Adding a duplicate class to the Dagger graph using a `@Named` or Qualifier annotation.
   Sometimes you need multiple
   instances of a single class (maybe one per feature or one for prod vs testing) so you can create a custom qualifier
   annotation or use `@Named` to differentiate them.

TODO mention dependency inversion

A common pattern that arises when using dependency injection is to have an interface/implementation pair to allow to
easily swap implementations for testing (maybe a `Repository` that hits a real API in prod and fake data for tests).
In
these scenarios, you want to make sure your class depends on the interface and not the concrete implementation to make
swapping implementations easy.
A common mistake is to do this via a `@Provides` method instead of `@Binds` method.

```kotlin
interface Greeter {
    fun hello(): String
}

class GreeterImpl @Inject constructor() : Greeter {
    override fun hello(): String = "Hello World"
}

// Bad!
@Module
abstract class GreeterModule {
    @Provides
    fun provideGreeter(): Greeter = GreeterImpl()
}

// Good!
@Module
abstract class GreeterModule {

    @Binds
    abstract fun provideGreeter(impl: GreeterImpl): Greeter
}
```

We want to do this for two major reasons.
The first being if we avoid using a `@Provides` method we can avoid
maintaining the constructor contract in two different places, at the class definition and also the `@Provides` method.
The `Greeter` example is simple with a no parameter constructor but imagine it having six dependencies, we'd need wire
that up correctly in both the constructor and `@Provides` method and then maintain it in two places if a new dependency
is added.
The second reason is by using `@Binds` Dagger will generate less code!
For `@Binds` functions no extra code is
actually generated.
Dagger can swap in the concrete implementation wherever the interface is used but if using
a `Provides` method Dagger would have to generate another class to supply this dependency to its consumers.

### Methods annotated with `@Binds` must be abstract

Methods annotated with the [`@Binds` annotation](https://dagger.dev/api/latest/dagger/Binds.html) need to be abstract.
The `@Binds` annotation is used to tell Dagger to delegate to a concrete implementation when injecting an interface.
Dagger requires these methods to be abstract and will throw an error at compile time if they are
not.

`error: @Binds methods must be abstract`

```kotlin

// Safe
@Module
interface MyModule {
    fun bindMyThing(impl: MyThingImpl): MyThing
}

// Not Safe!
@Module
object MyModule {

    @Binds
    fun bindMyThing(): MyThing = MyThingImpl()
}
```

### A `@Binds` method parameter should be a subclass of it's return type

The `@Binds` annotation is used to connect a concrete implementation of a class to it's interface in the Dagger graph so
consumers can easily swap out different implementations of an interface in different scenarios (prod vs test code). The
parameter of a `@Binds` method **needs** to be a subclass of the return type or else Dagger will throw an error at
compile time.

`error: @Binds methods' parameter type must be assignable to the return type`

```kotlin
@Module
interface BindModule {

    // Safe
    @Binds
    fun bindNetworkRepository(impl: NetworkRepository): Repository

    // Not Safe
    @Binds
    fun bindRepository(impl: NotARepository): Repository
}
```

### Classes with `@Provides`, `@Binds` or `@Multibinds` methods should be annotated with `@Module`

A class or interface that contains `@Provides`, `@Binds` or `@Multibinds` methods requires the `@Module` for Dagger to
pick up these methods and apply them to your Dagger graph, without this annotation Dagger will fail at compile time.

```kotlin

// Missing @Module annotation, nothing is added to DI graph
interface MyBrokenModule {
    @Binds
    fun bind(impl: ThingImpl): Thing

    companion object {
        @Provides
        fun provideMyFactory(): MyFactory = MyFactory.create()
    }
}

@Module // everything added to DI graph!
interface MyWorkingModule {
    @Binds
    fun bind(impl: ThingImpl): Thing

    companion object {
        @Provides
        fun provideMyFactory(): MyFactory = MyFactory.create()
    }
}
```

### `@Provides` methods should be static

`@Provides` methods that are static will generate will allow Dagger to generate more efficient code under the hood.

In Java this would just be adding the `static` keyword to your provides method like so:

```java

@Module
public final class StaticModule {

    @Provides
    public static MyFactory provideMyFactory() {
        return MyFactory.create();
    }
}
```

In Kotlin as of Dagger [2.26](https://github.com/google/dagger/releases/tag/dagger-2.26) you only need to make your
module a Kotlin `object` to get the same benefits as `static` in Java.

```kotlin
@Module
object StaticModule {
    @Provides
    fun provideMyFactory(): MyFactory = MyFactory.create()
}
```

More information
here: [Keeping the Daggers Sharp](https://developer.squareup.com/blog/keeping-the-daggers-sharp/#favor-static-provides-methods)

### Objects on the DI graph can only have one `@Scope` annotation

Dagger supports the concept of scoping classes to the lifecycle of `Components` by annotating them with the same scope
annotation. This means when you access a dependency that shares the same scope annotation as a `Component` you will get
the same instance each time. Scopes, however, are not repeatable, and you are unable to connect a class to multiple
scopes;
Dagger will fail at compile time when attempting to do this.

`error: A single binding may not declare more than one @Scope`

```kotlin
@Scope
annotation class AppScope

@Scope
annotation class FeatureScope

// Unsafe will error at compile time
@FeatureScope
@AppScope
class MyThing @Inject constructor()

// Safe
@AppScope
class MyOtherThing @Inject constructor()
```

### Classes annotated with scopes require their constructors to be annotated with `@Inject` to be added to the Dagger graph

Dagger supports the concept of scoping classes to the lifecycle of `Components` by annotating them with the same scope
annotation **and adding them to the DI graph**.
For example, if there is an `AppComponent` annotated with the `@Singleton` scope
and another class in the project is annotated with `@Singleton` the same instance of that class will be
retained as long as `AppComponent` is.
Adding the same scope annotation is only part of the process, we must all ensure the class is added to the Dagger graph
by annotating one of its constructors with the `@Inject` annotation.

```kotlin
@Singleton
@Component
interface AppComponent

// Is not part of the DI graph and won't be scoped as a singleton
@Singleton MyClass()

// is part of the DI graph and will be scoped as a singleton
@Singleton MyOtherClass @Inject constructor()
```

### Valid `@Component` methods

`@Components` and `@Subcomponents` only support two types of methods, provision methods and members-injection methods.
Trying to add any other kind of method to your component will lead to a crash at compile time.

#### Provision methods

Provision methods cover exposing specific dependencies from your graph via the `@Component`
and are defined as "Provision methods have no parameters and return an injected or provided type."

```kotlin

// All valid provision methods
@Component
interface AppComponent {

    fun myThing(): MyThing

    fun myMultipleOtherThings(): Set<MyOtherThing>

    @Qualified
    fun MyQualifiedThing(): MyQualifiedThing
}
```

#### Member-injection methods

Member injection methods are used to supply dependencies to the parameter supplied to the method.
Method injection methods are defined as "Members-injection methods have a single parameter and
inject dependencies into each of the Inject-annotated fields and methods of the passed instance. A
members-injection method may be void or return its single parameter as a convenience for chaining"

```kotlin
// All valid member injection methods
@Component
interface AppComponent {
    fun inject(target: MyFragment)
    fun inject(target: MyOtherFragment): MyOtherFragment
}
```

In the example above when you call `AppComponent.inject` in `MyFragment` any dependencies
annotated with `@Inject` will be supplied.

More information here: [
`@Component` Dagger documentation](https://dagger.dev/api/latest/dagger/Component.html)

## Anvil Rules

### Prefer using `@ContributesBinding` over `@Binds`

Anvil provides
the [`@ContributesBinding` annotation](https://github.com/square/anvil/blob/main/annotations/src/main/java/com/squareup/anvil/annotations/ContributesBinding.kt)
as a way to reduce the amount of code we have to write to bind an implementation to it's interface in the Dagger graph.
Instead of creating a `@Binds` function in a `@Module` (or creating a whole new module for this) we can annotate the
implementation directly to bind it!

Let's say we had a `Repository` interface that is implemented by `NetworkRepository` this is the code we would need
to bind `NetworkRepository` to `Repository` in the Dagger graph using Hilt.

```kotlin
interface Repository

class NetworkRepository @Inject constructor() : Repository

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryBindsModule {

    @Binds
    fun bindRepository(networkRepository: NetworkRepository): Repository
}
```

With Anvil, we can remove the `RepositoryBindsModule` completely!

```kotlin

interface Repository

@ContributesBinding(AppScope::class)
class NetworkRepository @Inject constructor() : Repository
```

### Classes annotated with `@ContributesBinding` or `@ContributesMultibinding` should have a supertype to be bound to

The [`@ContributesBinding`](https://github.com/square/anvil/blob/main/annotations/src/main/java/com/squareup/anvil/annotations/ContributesBinding.kt)
and [`@ContributesMultibinding`](https://github.com/square/anvil/blob/main/annotations/src/main/java/com/squareup/anvil/annotations/ContributesMultibinding.kt)
annotations are used to bind a concrete implementation of an interface or abstract class to it's super in the DI graph.
If you attempt to use one of these annotations with a class without a super, it will crash at compile time.

`dev.whosnickdoglio.dagger.MyThing contributes a binding, but does not specify the bound type. This is only allowed with exactly one direct super type.
If there are multiple or none, then the bound type must be explicitly defined in the @ContributesBinding annotation.`

There is one notable exception to this
where you can set the `boundType` to `Any` since it's a super for all Kotlin classes.

```kotlin
interface Thing

// Not safe and will crash at compile time
@ContributesBinding(AppScope::class)
class MyThing @Inject constructor()

// Safe!
@ContributesBinding(AppScope::class)
class MyOtherThing @Inject constructor() : Thing

// Also safe!
@ContributesBinding(AppScope::class, boundType = Any::class)
class SomethingElse @Inject constructor()
```

### A class annotated with `@Module` should also be annotated with `@ContributesTo`

The [`@ContributesTo` annotation from Anvil](https://github.com/square/anvil/blob/main/annotations/src/main/java/com/squareup/anvil/annotations/ContributesTo.kt)
is how Anvil connects a Dagger `@Module` in the dependency graph to the Dagger `@Component` for the provided scope.
Without this annotation, anything defined in the given module **won't** be added to the Dagger graph.

```kotlin

// Missing @ContributesTo annotation and will not be automatically added to the Dagger graph
@Module
object MyModule {

    @Provides
    fun provideMyFactory(): MyFactory = MyFactory.create()
}

// With the @ContributesTo annotation included here, this module will be added the Dagger graph scoped with AppScope
@ContributesTo(AppScope::class)
@Module
object MyOtherModule {

    @Provides
    fun provideMyFactory(): MyFactory = MyFactory.create()
}
```

### Anvil cannot be used from Java

[Anvil is a Kotlin compiler plugin, and therefore does not support being used within Java code.](https://github.com/square/anvil#no-java-support)

You can, however, use Anvil in Kotlin files in modules with a mixed Java/Kotlin source set.

## Hilt Rules

### The `@EntryPoint` annotation can only be applied to interfaces

The [`@EntryPoint` annotation](https://dagger.dev/api/latest/dagger/hilt/EntryPoint.html) can be used
to define an interface that exposes a dependency on the DI graph to make it easier to consume in places where you
currently can't use constructor injection or fully migrate a class to Hilt. This `interface` will be implemented by the
Hilt component it's scoped to, so it's important it's defined as an interface otherwise an error will be thrown at
compile time.

`error: [Hilt] Only interfaces can be annotated with @EntryPoint: dev.whosnickdoglio.hilterrors.MyEntryPoint`

```kotlin
// Unsafe and will crash at compile time
@InstallIn(SingletonComponent::class)
@EntryPoint
class MyEntryPoint {

    fun getMyClass(): MyClass = MyClass()
}

// Also unsafe and will crash at compile time
@InstallIn(SingletonComponent::class)
@EntryPoint
abstract class MyOtherEntryPoint {

    abstract fun getMyClass(): MyClass
}

// Safe
@InstallIn(SingletonComponent::class)
@EntryPoint
interface MySafeEntryPoint {

    fun getMyClass(): MyClass
}
```

[Read more about it in the Hilt documentation](https://dagger.dev/hilt/entry-points)

### Android components should be annotated with `@AndroidEntryPoint`

For member injection to work in classes such as `Activites`, `Fragments`, `Views`, `Services`
and `BroadcastRecievers` they need to be annotated with
the [`@AndroidEntryPoint` annotation](https://dagger.dev/api/latest/dagger/hilt/android/AndroidEntryPoint.html),
otherwise you'll hit an issue at runtime when trying to use the injected dependencies.

```kotlin

// Safe
@AndroidEntryPoint
class MyFragment : Fragment() {

    @Inject
    lateinit var something: Something

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        something.doSomething()
    }
}

// Unsafe will throw UninitializedPropertyAccessException
// when trying to use `something`
class MyOtherFragment : Fragment() {

    @Inject
    lateinit var something: Something

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        something.doSomething()
    }
}
```

### `Application` subclasses should be annotated with `@HiltAndroidApp`

Hilt requires the `Application` subclass in your be annotated with
the [`@HiltAndroidApp` annotation](https://dagger.dev/api/latest/dagger/hilt/android/HiltAndroidApp.html), this
annotation is necessary for generating all the Hilt components and wiring them up.
Without this annotation, you'll hit a crash at runtime when the app is first launched.

`java.lang.IllegalStateException: Hilt Activity must be attached to an @HiltAndroidApp Application`

```kotlin

// Unsafe, will crash at runtime
class HiltApp : Application()

// Safe
@HiltAndroidApp
class HiltApp : Application()
```

### `ViewModel` subclasses should be annotated with `@HiltViewModel`

For `ViewModels` to be correctly wired up by Hilt with their necessary dependencies they need to be annotated with
the [`@HiltViewModel` annotation](https://dagger.dev/api/latest/dagger/hilt/android/lifecycle/HiltViewModel.html).
Without this annotation you'll get a crash at runtime when you try to access the given `ViewModel`.

`Caused by: java.lang.RuntimeException: Cannot create an instance of class dev.whosnickdoglio.hilterrors.MyViewModel`

```kotlin
// Unsafe
class MyViewModel @Inject constructor(
    private val something: Something
) : ViewModel()

// Safe
@HiltViewModel
class MyViewModel @Inject constructor(
    private val something: Something
) : ViewModel()

// Also safe! If your ViewModel doesn't take any dependencies,
// it doesn't need any annotations
class MyOtherViewModel : ViewModel()
```

### A class annotated with `@Module` or `@EntryPoint` should also be annotated with `@InstallIn`

The [`@InstallIn` annotation](https://dagger.dev/api/latest/dagger/hilt/InstallIn.html) is how you contribute modules or
entry points to the Hilt DI graph, without the `@InstallIn` annotation these classes won't be connected to the Hilt DI
graph and their dependencies won't be available to other classes. Hilt will throw an error at compile time if it notices
an entry point or module missing the `@InstallIn annotation`

`error: [Hilt] @EntryPoint dev.whosnickdoglio.hilterrors.TestEntryPoint must also be annotated with @InstallIn`

`error: [Hilt] dev.whosnickdoglio.hilterrors.MyModule is missing an @InstallIn annotation. If this was intentional, see https://dagger.dev/hilt/flags#disable-install-in-check for how to disable this check.`

```kotlin
// Will crash at compile time
@EntryPoint
interface MyUnsafeEntryPoint {
    fun getMyClass(): MyClass
}

// Safe
@InstallIn(SingletonComponent::class)
@EntryPoint
interface MySafeEntryPoint {
    fun getMyClass(): MyClass
}

// Will crash at compile time
@Module
object MyUnsafeModule {
    @Provides
    fun provideMyFactory(): MyFactory = MyFactory.create()
}

// Safe
@InstallIn(SingletonComponent::class)
@Module
object MySafeModule {
    @Provides
    fun provideMyFactory(): MyFactory = MyFactory.create()
}
```

This rule is also configurable if you have custom Hilt components already defined!
In your `lint.xml` file you can add a
list of fully qualified class names for any custom Hilt components to be included in the quick fix suggestions.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<lint>
    <issue id="MissingInstallInAnnotation">
        <option name="customHiltComponents" value="dev.whosnickdoglio.testapp.hilt.MyCustomComponent"/>
    </issue>
</lint>
```

You can read more about this
in [the Lint API guide](https://googlesamples.github.io/android-custom-lint-rules/api-guide.html#options/usage).
