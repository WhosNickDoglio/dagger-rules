## Dagger Rules

[//]: # (TODO add sources or links for rules that them)

### Classes annotated with `@Component` must be abstract

The [`@Component` annotation](https://dagger.dev/api/latest/dagger/Component.html) needs to be abstract so Dagger can
generate a class that implements it. if a concrete class is annotated with `@Component` Dagger will throw an error at
compile time. `error: @Component may only be applied to an interface or abstract class`

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

Field injection should only ever be used for classes that you don't manage their creation (`Activities` or `Fragments`)
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

There's a few reasons we want to favor constructor injection over field injection. The first being is we should think of
a class constructor as a contract of sorts, "if I provide you these dependencies then in return you give me an instance
of this class", if we're doing field injection this resides **outside** the constructor and isn't intuitive that this
class requires additional setup which can lead to consumers misusing it or bugs. The second being with constructor
injection we can make our dependencies both private and immutable, so they cannot be altered by anything outside this
class nor could they be reassigned somewhere within the class, because Dagger requires field injection to be public and
mutable this gives things outside our class the ability to mutate or reassign our dependencies which could lead to
unpredictable and hard to debug issues.

[//]: # (TODO) mention `AppComponentFactory` and `FragmentFactory`

### Methods annotated with `@Binds` must be abstract

The [`@Binds` annotation](https://dagger.dev/api/latest/dagger/Binds.html)

`error: @Binds methods must be abstract`

### A `@Binds` method parameter should be a subclass of it's return type

The `@Binds` annotation is used to connect a concrete implementation of a class to it's interface in the Dagger graph so
consumers can easily swap out different implementations of an interface in different scenarios (prod vs test code). The
parameter of a `@Binds` method **needs** to be a subclass of the return type or else Dagger will throw an error at
compile time.

### Correct `@Component.Factory`

### Correct `@Component.Builder`

### Classes with `@Provides`, `@Binds` or `@Multibinds` methods should be annotated with `@Module`

A class or interface that contains `@Provides`, `@Binds` or `@Multibinds` methods requires the `@Module` for Dagger to
pick up these methods and apply them to your Dagger graph, without this annotation Dagger will fail at compile time.

### `@Provides` methods should be static

### Valid `@Component` methods

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

### Classes annotated with `@ContributesBinding` should have a supertype to be bound to

### A class annotated with `@Module` should also be annotated with `@ContributesTo`

### Anvil cannot be used from Java

[Anvil is a Kotlin compiler plugin and therefor does not support being used within Java code.](https://github.com/square/anvil#no-java-support) 

## Hilt Rules

### The `@EntryPoint` annotation can only be applied to interfaces

The [`@EntryPoint` annotation](https://dagger.dev/api/latest/dagger/hilt/EntryPoint.html)

[Read more about it in the Hilt documentation](https://dagger.dev/hilt/entry-points)

### Android components should be annotated with `AndroidEntryPoint`

### `Application` subclasses should be annotated with `@HiltAndroidApp`

### `ViewModel` subclasses should be annotated with `@HiltViewModel`

### A class annotated with `@Module` or `@EntryPoint` should also be annotated with `@InstallIn`
