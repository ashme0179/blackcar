# Black Car Chauffeur — Android App

A native Android WebView wrapper for **https://www.blackcarchauffeurservices.com/**, branded with your logo and colors.

## What's included
- **Splash screen** — your logo on a black background, using the modern Android 12+ SplashScreen API (falls back gracefully on older versions) with a short fade/scale-in animation.
- **Branded UI** — toolbar, progress bar, and app icon all use the orange (`#EC4E00`) and black (`#111111`) from your logo.
- **WebView wrapper** — loads your site, keeps `blackcarchauffeurservices.com` links inside the app, and sends `tel:`, `mailto:`, `sms:`, `geo:` links (e.g. a "Call Us" or "Get Directions" button) to the appropriate native app.
- **Pull-to-refresh**, a top loading progress bar, in-app back navigation (back button navigates WebView history first), and a friendly offline screen with a Retry button.
- **App icon** generated from the car silhouette in your logo, as an adaptive icon for Android 8+.

## How to open and run it
1. Install **Android Studio** (Giraffe or newer) if you don't have it: https://developer.android.com/studio
2. Choose **File → Open**, and select the `BlackCarChauffeur` folder (this project).
3. Android Studio will offer to "Sync" and generate the Gradle wrapper automatically — accept it. (The wrapper jar itself isn't bundled here since it's a binary; Studio fetches it on first sync.)
4. Once sync finishes, click **Run ▶** with an emulator or a plugged-in device selected.

## Publishing to the Play Store (when you're ready)
- Change `applicationId` in `app/build.gradle.kts` if you want a different package name.
- Build → Generate Signed Bundle/APK, create a signing key, and follow Android Studio's prompts.
- Update `versionCode`/`versionName` in `app/build.gradle.kts` for each release.

## Customizing further
- **Site URL**: `app/src/main/res/values/strings.xml` → `site_url`
- **Colors**: `app/src/main/res/values/colors.xml`
- **Splash duration**: `SplashActivity.kt` → `splashDurationMs`
- **App name**: `strings.xml` → `app_name`
- **Fonts**: this build uses the system font in bold/letter-spaced styling to match your logo's clean sans-serif look. To use your exact brand typeface, drop a `.ttf`/`.otf` file into a new `app/src/main/res/font/` folder and reference it from a `fontFamily` in the layouts/styles.
