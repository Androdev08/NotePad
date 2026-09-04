# NotePad

A simple note-taking application for Android built with Jetpack Compose.

## Features
- Rich text formatting powered by [compose-rich-editor](https://github.com/MohamedRejeb/compose-rich-editor)
  - Bold, italic, underlined, strikethrough
  - Heading 1 through 6
  - Unordered and ordered lists
  - Hyperlinks
  - Subscript and superscript (HTML mode only)
  - Text alignment (HTML mode only)
- Organization into folders
- Export notes to `.md` or `.html`

## Screenshots
<img width="300" alt="Screenshot_2026-09-04-14-04-30-309_com nostadroid notes" src="https://github.com/user-attachments/assets/0129491c-6c8c-4342-98ed-dfe625910902" />
<img width="300" alt="Screenshot_2026-09-04-14-04-49-269_com nostadroid notes" src="https://github.com/user-attachments/assets/9c284471-d2e2-427a-9239-2d3cdd708217" />
<img width="300" alt="Screenshot_2026-09-04-14-04-33-050_com nostadroid notes" src="https://github.com/user-attachments/assets/b3e1f5d9-5fd7-4320-a8d5-3f9f127cc9c1" />

## Installation
- Download the latest APK from the [https://github.com/Androdev08/NotePad/releases/latest](releases page)

## Building from source
1. Clone this repository with `git clone`.
2. Open the folder with [Android Studio](https://developer.android.com/studio).
3. Click the "Run" button to build and run the app on your device. A run configuration is already supplied. **This approach generates a debug build.** To generate a release build follow these steps:
4. Go to `Build > Generate Signed App Bundle or APK...`
5. Make sure "APK" is selected on the dialog box that appears and click "Next."
6. You need to create a keystore. If there aren't any, click "Create new..." If there are, skip to step 9.
7. Supply the path to the keystore and its password. Under "Key" supply an alias and a password for it.
8. Under "Certificate" fill in at least one of the textboxes (First and Last Name, Country Code etc.) They don't have to be your actual details.
9. Select an alias and type your keystore's password and your key's password.
10. On the next screen, under "Build Variants" make sure only "Release" is selected and click "Create."
11. The app will take its time to build. When it's done, a popup message will appear at the bottom right of Android Studio. Click on the "locate" link to open your file explorer into the final APK's location.

## Features to be implemented
These features are a part of other note taking apps, but unfortunately have not been implemented by [compose-rich-editor](https://github.com/MohamedRejeb/compose-rich-editor) yet:
- [ ] Task lists
- [ ] Images in the editing area
- [ ] Tables
- [ ] Blockquotes

## Third-Party Libraries & Licenses
This project incorporates open-source software:
- **[compose-rich-editor](https://github.com/MohamedRejeb/compose-rich-editor)** — Copyright (c) Mohamed Rejeb  
  Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## License
This project is licensed under the **GNU General Public License v3.0** - see the [LICENSE](LICENSE) file for details.
