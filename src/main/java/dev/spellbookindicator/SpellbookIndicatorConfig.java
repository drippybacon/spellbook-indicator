/*
 * Copyright (c) 2026, Spellbook Indicator contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package dev.spellbookindicator;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(SpellbookIndicatorConfig.GROUP)
public interface SpellbookIndicatorConfig extends Config
{
	String GROUP = "spellbookindicator";

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
