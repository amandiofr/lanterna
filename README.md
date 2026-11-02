# Lanterna

Minimal Android flashlight with no screen and no ads.

- Tap the icon: the torch turns on, with a "Lanterna acesa" notification.
- Tap the icon again, or the notification: the torch turns off.
- The torch switches itself off after 30 minutes.

`icon.ps1` draws the launcher icon (white screw-in bulb on yellow):

```
powershell -File icon.ps1 -out app\src\main\res\mipmap-xxxhdpi\ic_launcher.png -S 192
```

Build and install:

```
gradlew assembleRelease
adb install -r app\build\outputs\apk\release\app-release.apk
```
