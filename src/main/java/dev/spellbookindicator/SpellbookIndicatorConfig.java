package dev.spellbookindicator;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("spellbookindicator")
public interface SpellbookIndicatorConfig extends Config
{
	@ConfigItem(
		keyName = "showIcon",
		name = "Show spellbook icon",
		description = "Display the active spellbook's in-game icon.",
		position = 0
	)
	default boolean showIcon()
	{
		return true;
	}

	@Range(min = 16, max = 40)
	@ConfigItem(
		keyName = "iconSize",
		name = "Icon size",
		description = "Choose the spellbook icon size in pixels.",
		position = 1
	)
	default int iconSize()
	{
		return 24;
	}

	@ConfigItem(
		keyName = "showName",
		name = "Show spellbook name",
		description = "Display the active spellbook's name in its own movable box.",
		position = 2
	)
	default boolean showName()
	{
		return false;
	}
}
