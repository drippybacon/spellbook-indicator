# Spellbook Indicator

Display your current spellbook using its in-game icon, its name, or both.

## Features

- Automatically follows the active Standard, Ancient, Lunar, or Arceuus spellbook.
- Independent icon and name overlays with separate visibility settings.
- Custom name colors for each spellbook.
- Move and resize both overlays with the mouse. The icon keeps its proportions, and the name's text scales to fit.
- RuneLite saves each overlay's position and size in the active configuration profile.

## Settings

Open **Spellbook Indicator** in RuneLite's plugin settings.

### Display

**Spellbook icon** and **Spellbook name** each offer these choices:

| Visibility | Behavior |
| --- | --- |
| Off | Hide the overlay. |
| Everywhere | Show while logged in, including at the bank. |
| Bank only | Show while the bank interface is open. |
| Outside bank | Show while the bank interface is closed. |

The icon defaults to **Everywhere** and the name defaults to **Off**. Bank visibility follows the open bank interface; deposit boxes and the Grand Exchange do not count as the bank.

The icon starts at **24 × 24 pixels** and can be resized down to **16 × 16 pixels**.

### Name colors

Each color applies when that spellbook is active:

| Spellbook | Default color |
| --- | --- |
| Standard | Tan (`#C4A984`) |
| Ancient | Purple (`#A984C4`) |
| Lunar | White (`#FFFFFF`) |
| Arceuus | Cyan (`#00FFFF`) |

## Mouse controls

Use RuneLite's overlay modifier, **Alt** by default:

- **Move:** hold the modifier and drag the middle of an overlay.
- **Resize:** hold the modifier and drag a corner of an overlay.
- **Reset position and size:** hold the modifier and right-click the overlay.
- **Open settings:** hold **Shift**, right-click an overlay, and choose **Configure**.

The icon and name can be positioned and resized separately. The name initially uses RuneLite's interface font; resizing it adjusts the text size.

Very small name boxes keep a readable minimum font size and shorten the name with `...` when needed. Text is clipped to the resized box, so switching spellbooks keeps its saved dimensions.

## Tests

Run `./gradlew test`, or `.\gradlew.bat test` on Windows.

The tests cover visibility settings, spellbook mappings, bank detection, overlay lifecycle, settings migration, and rendering bounds. They use off-screen graphics and mocked RuneLite services, so they run without a game login.

## License

[BSD 2-Clause](LICENSE).
