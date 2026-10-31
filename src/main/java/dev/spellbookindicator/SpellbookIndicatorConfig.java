package dev.spellbookindicator;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("spellbookindicator")
public interface SpellbookIndicatorConfig extends Config
{
	@ConfigSection(
		name = "Display",
		description = "Choose when the spellbook icon and name appear.",
		position = 0
	)
	String displaySection = "display";

	@ConfigSection(
		name = "Name colors",
		description = "Choose a name color for each spellbook.",
		position = 1
	)
	String nameColorsSection = "nameColors";

	@ConfigItem(
		keyName = "iconVisibility",
		name = "Spellbook icon",
		description = "Show the spellbook icon everywhere, only in the bank, only outside the bank, or turn it off.",
		position = 0,
		section = displaySection
	)
	default SpellbookVisibility iconVisibility()
	{
		return SpellbookVisibility.EVERYWHERE;
	}

	@ConfigItem(
		keyName = "nameVisibility",
		name = "Spellbook name",
		description = "Show the spellbook name everywhere, only in the bank, only outside the bank, or turn it off.",
		position = 1,
		section = displaySection
	)
	default SpellbookVisibility nameVisibility()
	{
		return SpellbookVisibility.OFF;
	}

	@Range(min = 16, max = 40)
	@ConfigItem(
		keyName = "iconSize",
		name = "Legacy icon size",
		description = "Initial icon size saved by older versions, before mouse resizing.",
		position = 2,
		hidden = true
	)
	default int legacyIconSize()
	{
		return 24;
	}

	@ConfigItem(
		keyName = "standardNameColor",
		name = "Standard",
		description = "Color of the Standard spellbook name.",
		position = 0,
		section = nameColorsSection
	)
	default Color standardNameColor()
	{
		return new Color(196, 169, 132);
	}

	@ConfigItem(
		keyName = "ancientNameColor",
		name = "Ancient",
		description = "Color of the Ancient spellbook name.",
		position = 1,
		section = nameColorsSection
	)
	default Color ancientNameColor()
	{
		return new Color(169, 132, 196);
	}

	@ConfigItem(
		keyName = "lunarNameColor",
		name = "Lunar",
		description = "Color of the Lunar spellbook name.",
		position = 2,
		section = nameColorsSection
	)
	default Color lunarNameColor()
	{
		return Color.WHITE;
	}

	@ConfigItem(
		keyName = "arceuusNameColor",
		name = "Arceuus",
		description = "Color of the Arceuus spellbook name.",
		position = 3,
		section = nameColorsSection
	)
	default Color arceuusNameColor()
	{
		return Color.CYAN;
	}
}
