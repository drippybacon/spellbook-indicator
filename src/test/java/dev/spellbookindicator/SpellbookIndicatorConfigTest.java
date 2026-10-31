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
import net.runelite.client.config.ConfigGroup;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SpellbookIndicatorConfigTest
{
	private final SpellbookIndicatorConfig config = new SpellbookIndicatorConfig()
	{
	};

	@Test
	public void defaultsToIconEverywhereAndNameOff()
	{
		assertEquals(SpellbookVisibility.EVERYWHERE, config.iconVisibility());
		assertEquals(SpellbookVisibility.OFF, config.nameVisibility());
	}

	@Test
	public void suppliesEachSpellbooksDefaultColor()
	{
		assertEquals(new Color(196, 169, 132), config.standardNameColor());
		assertEquals(new Color(169, 132, 196), config.ancientNameColor());
		assertEquals(Color.WHITE, config.lunarNameColor());
		assertEquals(Color.CYAN, config.arceuusNameColor());
	}

	@Test
	public void keepsTheOriginalIconSizeDefault()
	{
		assertEquals(24, config.legacyIconSize());
	}

	@Test
	public void keepsThePersistedConfigurationGroup()
	{
		assertEquals("spellbookindicator", SpellbookIndicatorConfig.GROUP);
		assertEquals(SpellbookIndicatorConfig.GROUP,
			SpellbookIndicatorConfig.class.getAnnotation(ConfigGroup.class).value());
	}

	@Test
	public void givesVisibilityChoicesReadableLabels()
	{
		assertEquals("Off", SpellbookVisibility.OFF.toString());
		assertEquals("Everywhere", SpellbookVisibility.EVERYWHERE.toString());
		assertEquals("Bank only", SpellbookVisibility.BANK_ONLY.toString());
		assertEquals("Outside bank", SpellbookVisibility.OUTSIDE_BANK.toString());
	}
}
