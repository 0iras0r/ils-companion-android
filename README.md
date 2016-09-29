# LD16 Companion
Just a simple fork of the advanced native Android schedule browser application for the [FOSDEM](http://fosdem.org/) conference in Brussels, Belgium. The original project was made by Christophe Beyls.

The challenge is to make an agnostic version for any kind of conference.
We hope we could make this.

##Dat Team (Alfabetic Order)
Rosario Antoci

Valerio Bozzolan

Ludovico Pavesi

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
