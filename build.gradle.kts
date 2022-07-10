/* Copyright (c) 2022 - Kaylen Dart and contributors. All rights reserved.
 *
 * This file is part of Hellfire Hellspawn.
 *
 * Hellfire Hellspawn is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Hellfire Hellspawn is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Hellfire Hellspawn. If not, see
 * <https://www.gnu.org/licenses/>.
 */
plugins {
    java
    `java-gradle-plugin`
    kotlin("jvm") version "1.7.10"
    id("com.diffplug.spotless") version "6.8.0"
}
// Set group and verison
version = "0.1.0"
group = "app.hellfire.hellspawn"
// Configure repositories
repositories {
    mavenCentral()
}
// Configure spotless formatter
spotless {
    java {
        googleJavaFormat().aosp().reflowLongStrings()
        licenseHeaderFile(rootProject.file("LICENSE_HEADER"))
    }
    kotlin {
        ktlint()
        licenseHeaderFile(rootProject.file("LICENSE_HEADER"))
    }
    kotlinGradle {
        ktlint()
        licenseHeaderFile(rootProject.file("LICENSE_HEADER"), "plugins")
    }
    format("styling") {
        target("**/*.json", "**/*.md")
        prettier().configFile(rootProject.file("./.prettierrc"))
    }
}
// Target Java 17, with Java 19 language features.
tasks.withType<JavaCompile> {
    targetCompatibility = JavaVersion.VERSION_17.toString()
    sourceCompatibility = JavaVersion.VERSION_17.toString()
}
// Add spotlessApply to task dependencies - this fixes formatting violations on build.
tasks.build.configure {
    dependsOn.add("spotlessApply")
}
// Set build to depend on git hook installation task.
tasks.build.configure {
    dependsOn("installGitHooks")
}
// Installs git hooks into the .git directory.
tasks.register<Copy>("installGitHooks") {
    description = "Installs git hooks from ./scripts/ into the git directory."
    from("${rootProject.rootDir}/scripts")
    into("${rootProject.rootDir}/.git/hooks") {
        fileMode = 775
    }
    // TODO: I am unsure why this also needs to be specified on top of into.
    destinationDir = File(rootProject.rootDir, ".git/hooks")
}

gradlePlugin {
    plugins {
        create("Hellspawn") {
            id = "app.hellfire.hellspawn"
            implementationClass = "app.hellfire.hellspawn.HellspawnPlugin"
        }
    }
}
