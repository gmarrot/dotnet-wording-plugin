# Gradle Wording Plugin for .Net Projects

## Summary

This plugin allow you to manage .Net application's wording with a simple Google Sheet file. Just create a sheet with columns for keys, wording and comments. The plugin will generate or update existing .resx files. Your product owner will be able to edit himself application's wording.

## Requirements

As this plugin has been written in Kotlin, it requires Gradle 4.9+ to work.

## Quick Start

First, you need to apply the plugin in your `build.gradle`.

```groovy
plugins {
    id "com.betomorrow.dotnet.wording" version "1.0.0"
}
```

Or you can use the legacy plugin declaration.
As it uses third-party libraries from Maven Central, you will also need to update your buildscript.

```groovy
buildscript {
    repositories {
        mavenCentral()
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "com.betomorrow.gradle:dotnet-wording-plugin:1.0.0"
    }
}

apply plugin: "com.betomorrow.dotnet.wording"
```

TODO

## Tasks

Plugin creates several tasks to manage wording :

* __downloadWording__ : Export sheet in local .xlsx file that you can commit for later edit. It prevent risks to have unwanted wording changes when you fix bugs.
* __updateWording__ : Update all wording files.
* __upgradeWording__ : Download Google Sheet and update all wording files.

It also creates tasks for each defined languages : updateWordingDefault, updateWordingFr, ...

## Complete DSL

TODO