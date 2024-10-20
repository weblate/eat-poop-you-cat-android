# Eat Poop You Cat for Android

## Status

[![build status](https://img.shields.io/github/actions/workflow/status/JamesOsborn-SE/eat-poop-you-cat-android/android.yml)](https://github.com/JamesOsborn-SE/eat-poop-you-cat-android/actions/workflows/android.yml)
[![deployment status](https://img.shields.io/github/actions/workflow/status/JamesOsborn-SE/eat-poop-you-cat-android/android-package.yml)](https://github.com/JamesOsborn-SE/eat-poop-you-cat-android/actions/workflows/android-package.yml)
[![current release](https://img.shields.io/github/v/release/JamesOsborn-SE/eat-poop-you-cat-android?include_prereleases)]()
[![fdroid release](https://img.shields.io/f-droid/v/dev.develsinthedetails.eatpoopyoucat.svg?logo=F-Droid)](https://f-droid.org/en/packages/dev.develsinthedetails.eatpoopyoucat/)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/819f04beefcf4d58b9e4248c4f6d643f)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=JamesOsborn-SE/eat-poop-you-cat-android&amp;utm_campaign=Badge_Grade)
[![Android Build](https://github.com/JamesOsborn-SE/eat-poop-you-cat-android/actions/workflows/android.yml/badge.svg)](https://github.com/JamesOsborn-SE/eat-poop-you-cat-android/actions/workflows/android.yml)
[![Android Package](https://github.com/JamesOsborn-SE/eat-poop-you-cat-android/actions/workflows/android-package.yml/badge.svg)](https://github.com/JamesOsborn-SE/eat-poop-you-cat-android/actions/workflows/android-package.yml)
[![open issues](https://img.shields.io/github/issues/JamesOsborn-SE/eat-poop-you-cat-android)](https://github.com/JamesOsborn-SE/eat-poop-you-cat-android/issues)
[![licence](https://img.shields.io/github/license/JamesOsborn-SE/eat-poop-you-cat-android)]()

## Premise

Eat Poop You Cat is a bit like telephone meets that one game where you try to get folks to guess the drawing.

You start with a sentence pass it to the next person and it they draw a picture. Then they pass it to the next person and they can only see the last entry (picture in this case) and they write a sentence. It goes on until everyone has had a turn or boredom takes hold.

## Screenshots

|  |  |  |  |
|--|--|--|--|
| ![Welcome](metadata/android/en-US/images/phoneScreenshots/1.png) | ![First turn](metadata/android/en-US/images/phoneScreenshots/2.png) | ![Second turn](metadata/android/en-US/images/phoneScreenshots/3.png) | ![Third turn](metadata/android/en-US/images/phoneScreenshots/4.png) |
|  |  |  |  |

### Info Needed from user

* Display Name

### Rules

* Each person can only play once per Unique game
* only Monochrome drawings

### Permissions

* **No** permissions needed at the moment

### Tech used

* [ORM - Room](https://developer.android.com/training/data-storage/room/)
* [Wireframes - Figma](https://www.figma.com/file/N5rf2UZaGy0LhD4S7r28OI/EPYC?node-id=0%3A1)

### Run locally

* Install the latest [Android Studio](https://developer.android.com/studio/)
* Enable developer tools on a physical device or make a new virtual device in Android Device Manager [Run your app](https://developer.android.com/studio/)

### Data

```mermaid
classDiagram
    Player --> Entry
    Game <-- "1..*" Entry
    
    class Game {
        +UUID Id
        +Entry[] Entries
    }
    
    class Player{
        +String UserName
        +UUID Id
    }
    
    class Entry{
        +int Sequence
        +String? Sentence
        +ByteArray? Drawing [gzip Json object]
        +Player Player
    }
```
