# JARVIS Assistant Android Project

This is a starter Android project for a simple tap-to-talk assistant.

## Included
- Dark JARVIS-inspired interface
- Tap-to-talk speech recognition using the Android speech-recognition service
- Spoken replies using Android Text-to-Speech
- Voice commands to open YouTube, WhatsApp, and Chrome
- Google web search by voice
- No OpenAI API key or OpenAI Python package

## Important limitations
- This project is source code, not a precompiled APK.
- Speech recognition availability/language support depends on the Android device and installed speech services; online recognition may use internet.
- It does not continuously listen for a wake word in the background.
- The debug APK is unsigned for Play Store release, but can generally be installed after allowing installation from the source used to download it.

## Build an APK from a phone
One route is GitHub Actions:
1. Create a GitHub repository and upload all files in this ZIP.
2. Open the repository's Actions tab.
3. Select "Build JARVIS APK" and run the workflow (or push to the `main` branch).
4. Open the completed workflow run and download the `JARVIS-debug-APK` artifact.
5. Extract the artifact ZIP to get `app-debug.apk`.

The project uses Android Gradle Plugin 8.6.1, Gradle 8.7, and Java 17.
