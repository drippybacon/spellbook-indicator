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

import java.util.HashMap;
import java.util.Map;
import net.runelite.client.config.ConfigManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpellbookConfigMigrationTest
{
	private static final String GROUP = SpellbookIndicatorConfig.GROUP;

	private final SpellbookIndicatorPlugin plugin = new SpellbookIndicatorPlugin();
	private final SpellbookIndicatorConfig config = new SpellbookIndicatorConfig()
	{
	};
	private ConfigManager manager;
	private Map<String, String> values;

	@Before
	public void setUp()
	{
		values = new HashMap<>();
		manager = mock(ConfigManager.class);
		when(manager.getConfiguration(eq(GROUP), anyString()))
			.thenAnswer(invocation -> values.get(invocation.getArgument(1)));
		when(manager.getConfig(SpellbookIndicatorConfig.class)).thenReturn(config);
		doAnswer(invocation ->
		{
			String key = invocation.getArgument(1);
			SpellbookVisibility visibility = invocation.getArgument(2);
			values.put(key, visibility.name());
			return null;
		}).when(manager).setConfiguration(eq(GROUP), anyString(), any(SpellbookVisibility.class));
		doAnswer(invocation ->
		{
			values.remove(invocation.getArgument(1));
			return null;
		}).when(manager).unsetConfiguration(eq(GROUP), anyString());
	}

	@Test
	public void leavesFreshConfigurationAlone()
	{
		assertSame(config, plugin.provideConfig(manager));
		assertTrue(values.isEmpty());
		verify(manager, never()).setConfiguration(anyString(), anyString(), any(SpellbookVisibility.class));
		verify(manager, never()).unsetConfiguration(anyString(), anyString());
	}

	@Test
	public void migratesEnabledIcon()
	{
		assertMigrates("showIcon", "iconVisibility", true, SpellbookVisibility.EVERYWHERE);
	}

	@Test
	public void migratesDisabledIcon()
	{
		assertMigrates("showIcon", "iconVisibility", false, SpellbookVisibility.OFF);
	}

	@Test
	public void migratesEnabledName()
	{
		assertMigrates("showName", "nameVisibility", true, SpellbookVisibility.EVERYWHERE);
	}

	@Test
	public void migratesDisabledName()
	{
		assertMigrates("showName", "nameVisibility", false, SpellbookVisibility.OFF);
	}

	@Test
	public void migratesIconAndNameIndependently()
	{
		values.put("showIcon", "false");
		values.put("showName", "true");

		plugin.provideConfig(manager);

		assertEquals("OFF", values.get("iconVisibility"));
		assertEquals("EVERYWHERE", values.get("nameVisibility"));
		assertFalse(values.containsKey("showIcon"));
		assertFalse(values.containsKey("showName"));
	}

	@Test
	public void preservesEveryExistingVisibilityChoice()
	{
		for (SpellbookVisibility visibility : SpellbookVisibility.values())
		{
			for (boolean legacy : new boolean[]{false, true})
			{
				values.clear();
				values.put("showIcon", Boolean.toString(legacy));
				values.put("showName", Boolean.toString(legacy));
				values.put("iconVisibility", visibility.name());
				values.put("nameVisibility", visibility.name());

				plugin.provideConfig(manager);

				assertEquals(visibility.name(), values.get("iconVisibility"));
				assertEquals(visibility.name(), values.get("nameVisibility"));
				assertFalse(values.containsKey("showIcon"));
				assertFalse(values.containsKey("showName"));
			}
		}
		verify(manager, never()).setConfiguration(anyString(), anyString(), any(SpellbookVisibility.class));
	}

	@Test
	public void dropsMalformedLegacyValuesWithoutCreatingChoices()
	{
		values.put("showIcon", "invalid");
		values.put("showName", "");

		plugin.provideConfig(manager);

		assertTrue(values.isEmpty());
		verify(manager, never()).setConfiguration(anyString(), anyString(), any(SpellbookVisibility.class));
	}

	@Test
	public void isIdempotent()
	{
		values.put("showIcon", "true");
		values.put("showName", "false");
		plugin.provideConfig(manager);
		Map<String, String> migrated = new HashMap<>(values);

		plugin.provideConfig(manager);

		assertEquals(migrated, values);
		verify(manager, times(1)).setConfiguration(GROUP, "iconVisibility", SpellbookVisibility.EVERYWHERE);
		verify(manager, times(1)).setConfiguration(GROUP, "nameVisibility", SpellbookVisibility.OFF);
		verify(manager, times(1)).unsetConfiguration(GROUP, "showIcon");
		verify(manager, times(1)).unsetConfiguration(GROUP, "showName");
	}

	@Test
	public void migratesSeparateProfilesWithoutOverwritingTheirChoices()
	{
		Map<String, String> firstProfile = values;
		firstProfile.put("showIcon", "false");
		firstProfile.put("showName", "true");
		plugin.provideConfig(manager);

		values = new HashMap<>();
		values.put("iconVisibility", "BANK_ONLY");
		values.put("showIcon", "true");
		values.put("showName", "false");
		plugin.provideConfig(manager);
		Map<String, String> secondProfile = values;

		values = firstProfile;
		plugin.provideConfig(manager);

		assertEquals("OFF", firstProfile.get("iconVisibility"));
		assertEquals("EVERYWHERE", firstProfile.get("nameVisibility"));
		assertEquals("BANK_ONLY", secondProfile.get("iconVisibility"));
		assertEquals("OFF", secondProfile.get("nameVisibility"));
		assertFalse(secondProfile.containsKey("showIcon"));
		assertFalse(secondProfile.containsKey("showName"));
	}

	@Test
	public void preservesOtherSettings()
	{
		values.put("standardNameColor", "123456");
		values.put("iconSize", "32");
		values.put("showIcon", "true");

		plugin.provideConfig(manager);

		assertEquals("123456", values.get("standardNameColor"));
		assertEquals("32", values.get("iconSize"));
	}

	private void assertMigrates(String oldKey, String newKey, boolean legacy, SpellbookVisibility expected)
	{
		values.put(oldKey, Boolean.toString(legacy));

		assertSame(config, plugin.provideConfig(manager));

		assertEquals(expected.name(), values.get(newKey));
		assertFalse(values.containsKey(oldKey));
	}
}
