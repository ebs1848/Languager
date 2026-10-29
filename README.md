# Languager

An Android translation app written in Java with Android Studio. It uses Google ML Kit on-device translation to translate between 50+ languages: language models are downloaded once (Wi-Fi only) and translation then works offline, without an API key.

## Download

Get the latest APK from the [Releases page](https://github.com/ebs1848/Languager/releases/latest).

## Features

- Translate text between any two of the 50+ languages offered by ML Kit on-device translation (default pair: English to Turkish).
- Pick the source and target language from a popup menu.
- Language models are downloaded once, over Wi-Fi only; after that translation works offline.
- No API key or account needed.
- Empty input is caught with a short prompt, and a progress dialog shows model download and translation status.

## Tech

- Java, Android Studio, Gradle (Kotlin DSL)
- Google ML Kit Translate (com.google.mlkit:translate:17.0.2)
- AndroidX AppCompat, Material Components, ConstraintLayout
- minSdk 24, compileSdk 34

## Build and run

1. Open the Languager/ folder (the one containing settings.gradle.kts) in Android Studio.
2. Let Gradle sync.
3. Run it on an emulator or device with Android 7.0 (API 24) or newer. The first translation for a language needs a Wi-Fi connection to download its model.

## Code layout

- Languager/app/src/main/java/com/example/languager/MainActivity.java: screen, language selection and translation flow.
- Languager/app/src/main/java/com/example/languager/ModelLanguage.java: language code and display title.
