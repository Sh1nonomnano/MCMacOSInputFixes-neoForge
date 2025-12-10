# MacOS Input Fixes (NeoForge)

Original author: [Hamarb123](https://github.com/Hamarb123)
Migrated to NeoForge by Jules.

## What it does

Fixes [MC-122296](https://bugs.mojang.com/browse/MC-122296)

Fixes [MC-121772](https://bugs.mojang.com/browse/MC-121772)

Fixes [MC-59810](https://bugs.mojang.com/browse/MC-59810)

Fixes [MC-22882](https://bugs.mojang.com/browse/MC-22882)

Fixes control + tab and control + escape not being detected

Specific fixes:
- Correctly detects left click while control is pressed
- Make trackpad scrolling not scroll a ridiculous number of items at once
- It also fixes momentum scrolling (which changes the number of scroll events based on how quickly you did it, even by like x5-10, meaning you couldn't easily scroll to the correct item)
- On the trackpad it also only considers scrolling while fingers are on the trackpad (and the same for any fancy mice that support the relevant api e.g. probably apple's fancy mice/trackpad thing)
- It also fixes (almost perfectly) scrolling being broken when shift is down, this issue only affects mice that use older input APIs and doesn't change anything on the trackpad. It converts scrolling with shift down which shows as horizontal scrolling to the correct vertical scroll, the only issue when you actually scroll horizontally and hold shift, this will show as vertical scrolling (which is imo acceptable since very few people would be scrolling Minecraft items with horizantal scrolling on a non-apple input device compared to people scrolling vertical on any mice; and they could, if they need, use vertical scrolling instead which would be completely consistent - and this also isn't an issue if the Minecraft item scroll direction for + vertical scrolling is treated the same as + horizontal scrolling). TL;DR - this project will work properly for both vertical and horizontal scrolling including when pressing shift.
- When dropping an item, Minecraft checks for command + the key, since the default key is Q, this doesn't make sense, so this mod allows both control + key and command + key to work
- Fixes control + tab and control + escape not being detected

Menu Options (under Mouse Settings Screen):
- Option for trackpad scrolling sensitivity (macOS only)
- Option to enable momentum scrolling on hotbar (macOS only)
- Option to disable workaround for smooth scrolling in interfaces (macOS only)
- Option to reverse scrolling of the whole game
- Option to reverse scrolling of the hotbar
- Option to disable the fix for the ctrl + left click becomes right click bug (macOS only)

On platforms other than macOS, the mod does nothing (except the aformentioned menu options), so it can be safely included in any modpack.

## Running

This mod requires **NeoForge** and Minecraft 1.20+.
Note: While the codebase aims to support modern Minecraft versions, binary compatibility across major versions (e.g. 1.20.1 vs 1.21.4) requires separate builds due to Java version differences (Java 17 vs Java 21) and NeoForge API changes.

## Building

To build this, you also need to build the native file before building the mod itself any time you modify it. This can only be done on macOS and requires Apple's XCode (or command line-tools) to be installed on the machine. To build the native library, simply run `make clean && make` in the `src/main/native` directory. This should work on both intel and arm machines. The resulting binary supports both x64 and arm64.

To build the mod jar, run:
```bash
./gradlew build
```

## License

This project is available under the BSD-3-Clause license.

Some files and/or folders may be under different license(s), please check any relevant files and folders to see if they are under a different license.
