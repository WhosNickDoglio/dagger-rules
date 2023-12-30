/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

plugins { id("rules.shared") }

val catalog =
    extensions.findByType(VersionCatalogsExtension::class.java) ?: error("No Catalog found!")

val libs = catalog.named("libs")

dependencies {
    "compileOnly"(libs.findBundle("lintApi").get())
    "testImplementation"(libs.findBundle("test").get())
    "testImplementation"(libs.findBundle("lintTest").get())
}
