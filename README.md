# LDTO Companion (Linux Day Torino)
Just a simple fork of the [FOSDEM Companion](https://github.com/cbeyls/fosdem-companion-android) application. The original project was made by Christophe Beyls.

The goal was to make an agnostic version for any kind of conference.

## Dat Team (Alphabetic Order)
* Rosario Antoci
* Valerio Bozzolan
* Ludovico Pavesi

## How to adapt to other conferences\events
1. Fork this repository
2. Rename the it/linuxday/torino directories to something suitable, possibly with `git mv`
3. Search & replace every instance of `it.linuxday.torino` in app/src/main with something suitable
4. Add a modification notice to every file you modify, if you intend to release the app. This is required by the Apache 2.0 license.
5. Change colors, change the URL in app/src/main/java/.../api/ConferenceCompanionUrls.java and coordinates in app/src/main/java/.../fragments/MapFragment.java, edit the `about_text` string in app/src/main/res/values/strings.xml, change the map image, etc...

## XML format
The schedule format is the one called `xml` by the [frab](https://frab.github.io/frab/) software
which, as far as we can tell, is different from the Pentabarf\xCal format.

The app expects a few tags that aren't part of the standard; they're necessary to correctly "build"
URLs to events and people profiles. In case they aren't available, these links should simply be
hidden, but no thorough testing has been done. The [Tagliatella](https://github.com/lvps/tagliatella)
script was made exactly to provide an xml file compatible with this app.

The times used by FOSEDM in their 2016 schedule are in HH:MM format, while Frab emits them as
ISO 8601 in its default configuration: the app should support both, but we mostly tested with HH:MM.
The Date class provided by Java doesn't really support time zones, which may cause some problems
when a user downloads the schedule then moves to another time zone (events may move backward\forward
in time); keep in mind this hasn't been tested at all so we don't know for sure. Any pull
request for better date and time handling is very welcome.

## How to build

All dependencies are defined in ```app/build.gradle```. Import the project in Android Studio or use Gradle in command line:

```
./gradlew assembleRelease
```

The result apk file will be placed in ```app/build/outputs/apk/```.

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

## Used libraries

* [Android Support Library](http://developer.android.com/tools/support-library/) by The Android Open Source Project
* [ViewPagerIndicator](http://viewpagerindicator.com/) by Jake Wharton
* [PhotoView](https://github.com/chrisbanes/PhotoView) by Chris Banes
