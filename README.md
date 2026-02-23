# Salim Android

WhatsApp AI Agent Android app built with Kotlin + Jetpack Compose.

## Setup

1. Upload this folder to a GitHub repo named `salim-android`
2. GitHub Actions will automatically build the APK on every push
3. Go to **Actions** tab → click the latest workflow run → download `salim-debug-apk`
4. Install on your Android phone (enable "Install from unknown sources")

## Important: gradle-wrapper.jar

The `gradle/wrapper/gradle-wrapper.jar` file must be present for builds to work.
GitHub Actions handles this automatically. For local builds, run:
```
gradle wrapper --gradle-version 8.4
```

## Backend URL

Default: `https://salim-bot-mn7c.onrender.com`
You can change it in the app under Connection screen → Edit.

## Screens

- **Connection** — QR code scan or phone number pairing, status indicator
- **Chats** — All WhatsApp chats, tap to view full conversation, send messages
- **Status** — Post manual or AI-generated WhatsApp status
- **History** — AI conversation log with search and stats
- **Admin** — Full config panel: agent name, tone, humor, temperature, knowledge base

## Build locally

```bash
./gradlew assembleDebug
# APK at: app/build/outputs/apk/debug/app-debug.apk
```
