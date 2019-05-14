/*
 * Copyright (C) 2010-2019, Danilo Pianini and contributors listed in the main(project"s alchemist/build.gradle file.
 *
 * This file is part of Alchemist, and is distributed under the terms of the
 * GNU General Public License, with a linking exception,
 * as described in the file LICENSE in the Alchemist distribution"s top directory.
 */

dependencies {
    api(project(":alchemist-interfaces"))
    api("org.protelis:protelis-interpreter:${extra["protelisVersion"]}")
    api("org.protelis:protelis-lang:${extra["protelisVersion"]}")
    implementation(project(":alchemist-time"))
    implementation(project(":alchemist-implementationbase"))
    implementation(project(":alchemist-maps"))
    implementation("com.google.guava:guava:${extra["guavaVersion"]}")
    testImplementation(project(":alchemist-engine"))
    testImplementation(project(":alchemist-loading"))
}
