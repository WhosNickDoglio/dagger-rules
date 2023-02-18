# Dagger Rules

## Prefer constructor injection over field injection

## Prefer constructor injection over @Provides methods

## Prefer using @Binds over @Provides

## Classes with @Provides or @Binds methods should be annotated with @Module

## A @Binds method parameter should be a subclass of it's return type

# Anvil Rules

## A class annotated with @Module should also be annotated with @ContributesTo

## Prefer using @ContributesBinding over @Binds

## Classes annotated with @ContributesBinding should have a supertype to be bound to

# Hilt Rules

## A class annotated with @Module should also be annotated with @InstallIn

## Android components should be annotated with the correct Hilt annotations
