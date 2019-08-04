# Gradle Wording Plugin for .Net Projects

## Summary

This plugin allow you to manage .Net application's wording with a simple Google Sheet file. Just create a sheet with columns for keys, wording and comments. The plugin will generate or update existing .resx files. Your product owner will be able to edit himself application's wording.

## Requirements

As this plugin has been written in Kotlin and use particular features of the language, it requires Gradle 5.0+ to work.

## Quick Start

### Wording Update

You can create a Google Sheet for your application's wording with one column for keys, one optionally for comments and columns for languages like this :

| Keys          | Comments              | English    | French |
|---------------|-----------------------|------------|--------|
| UserFirstName | The user's first name | First Name | Prénom |
| UserLastName  | The user's last name  | Last Name  | Nom    |

You can find a sample [here](https://docs.google.com/spreadsheets/d/1t_1gM90TfD2A2UbXTghwZ7AklFQMUikyeqlC9lXmLtM/edit#gid=0).

In your project, you need to apply the plugin in your `build.gradle`.

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

Then you can declare your wording configuration :

```groovy
wording {
    sheetId = "1xIZpaIsHMr1U6uuI6ox8kOPoH6O9A91aiVw21OYVyDQ"

    keysColumn = "A"

    languages {
        "default" {
            output = "WordingSampleApp/Resources/StringResources.resx"
            column = "C"
        }
        "fr" {
            output = "WordingSampleApp/Resources/StringResources.fr.resx"
            column = "D"
        }
    }
}
```

At this step, you can run the wording's upgrade : `./gradlew upgradeWording`.

The first time you launch this command, the plugin will ask you to grant access on Google Sheet.

```bash
> Task :downloadWording
Please open the following address in your browser:
  https://accounts.google.com/o/oauth2/auth?access_type=offline&client_id=470805092329-2ecth74ds608pet9b711flfk43s43478.apps.googleusercontent.com&redirect_uri=http://localhost:8888/Callback&response_type=code&scope=https://www.googleapis.com/auth/drive
Attempting to open that address in the default browser now...
```

You will see an [authorization request](https://github.com/gmarrot/dotnet-wording-plugin/blob/master/images/authorization_request.png) in your browser. You have to accept it to allow the plugin to download the wording file.

The plugin will finally update wording files. In this sample, `WordingSampleApp/Resources/StringResources.resx` and `WordingSampleApp/Resources/StringResources.fr.resx`.

### Wording Check

You can specify for each language a states column to check its validity. If you do it, you will also need to set a `validWordingStates` to the wording DSL for share it with all languages or in each one.

Here is an example with three languages (two with shared valid states and one with his own) :

```groovy
wording {
    sheetId = "1xIZpaIsHMr1U6uuI6ox8kOPoH6O9A91aiVw21OYVyDQ"

    keysColumn = "A"
    validWordingStates = ["Validated"]

    languages {
        "default" {
            output = "WordingSampleApp/Resources/StringResources.resx"
            column = "C"
            statesColumn = "F"
        }
        "fr" {
            output = "WordingSampleApp/Resources/StringResources.fr.resx"
            column = "E"
            statesColumn = "F"
            validWordingStates = ["Validé"]
        }
        "es" {
            output = "WordingSampleApp/Resources/StringResources.es.resx"
            column = "G"
            statesColumn = "H"
        }
    }
}
```

When you specify these two parameters, you can run the wording's check : `./gradlew checkWording`.

## Tasks

Plugin creates several tasks to manage wording :

* __downloadWording__ : Export sheet in local .xlsx file that you can commit for later edit. It prevent risks to have unwanted wording changes when you fix bugs.
* __updateWording__ : Update all wording files.
* __upgradeWording__ : Download Google Sheet and update all wording files.
* __checkWording__ : Check the validity of all wording keys for each defined language.

It also creates tasks for each defined languages : updateWordingDefault, checkWordingDefault, updateWordingFr, checkWordingFr, ...

## Complete DSL

```groovy
wording {
    credentials = "credentials.json"    // Optional, default : use provided credentials  
    clientId = ""                       // Optional, default : use provided credentials  
    clientSecret = ""                   // Optional, default : use provided credentials  

    sheetId = "THE SHEET ID"            // *Required*
    sheetNames = ["commons", "app"]     // Optional, default: use all sheets of the file
    filename = "wording.xlsx"           // Optional, default: "wording.xlsx"
    keysColumn = "A"                    // Optional, default: "A"
    commentsColumn = ""                 // Optional, default: null

    validWordingStates = []             // List of valid states for wording. Optional, default: empty list

    skipHeaders = true                  // Skip headers. Optional, default: true
    addMissingKeys = false              // Add missing key from sheet in wording files. If false, it will throw errors on default wording file when missing keys. Optional, default: false
    removeNonExistingKeys = false       // Remove wording from resx files that not exist in Google Sheet. Optional, default: false
    sortWording = false                 // Sort wording with Google Sheet file keys order. Optional, default: false

    languages {
        'default' {
            output "src/main/res/values/strings.xml"        // Path and name of the wording file for the language. *Required*
            column = "B"                                    // Column of the language's wording. *Required*
            statesColumn = ""                               // Column of the wording's state for the language. Optional, default: null
            validWordingStates = []                         // List of valid states for wording. If not set, it uses the validWordingStates for parent DSL. Optional, default: empty list
        }
        'fr' {
            output = "src/main/res/values-es/strings.xml"   // Path and name of the wording file for the language. *Required*
            column = "C"                                    // Column of the language's wording. *Required*
            statesColumn = ""                               // Column of the wording's state for the language. Optional, default: null
            validWordingStates = []                         // List of valid states for wording. If not set, it uses the validWordingStates for parent DSL. Optional, default: empty list
        }
        // [...] Add more languages here
    }
}

```

## Note

The plugin includes Google Projet credentials for convenience use but you can setup your own projet. Create new project in [GCP Console](https://console.cloud.google.com) then enable **Drive API** in *API library* and create credentials. You can use `credentials.json` file or `clientId` / `clientSecret`.
