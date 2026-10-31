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

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Spellbook Indicator",
	description = "Shows the active spellbook icon and name with custom colors and bank visibility settings"
)
public class SpellbookIndicatorPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private SpellbookIndicatorOverlay overlay;

	@Inject
	private SpellbookNameOverlay nameOverlay;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(nameOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(nameOverlay);
	}

	@Provides
	SpellbookIndicatorConfig provideConfig(ConfigManager configManager)
	{
		migrateVisibility(configManager, "showIcon", "iconVisibility");
		migrateVisibility(configManager, "showName", "nameVisibility");
		return configManager.getConfig(SpellbookIndicatorConfig.class);
	}

	private static void migrateVisibility(ConfigManager configManager, String oldKey, String newKey)
	{
		String legacyValue = configManager.getConfiguration(SpellbookIndicatorConfig.GROUP, oldKey);
		if (legacyValue == null)
		{
			return;
		}
		if (configManager.getConfiguration(SpellbookIndicatorConfig.GROUP, newKey) == null
			&& ("true".equals(legacyValue) || "false".equals(legacyValue)))
		{
			SpellbookVisibility visibility = Boolean.parseBoolean(legacyValue)
				? SpellbookVisibility.EVERYWHERE : SpellbookVisibility.OFF;
			configManager.setConfiguration(SpellbookIndicatorConfig.GROUP, newKey, visibility);
		}
		configManager.unsetConfiguration(SpellbookIndicatorConfig.GROUP, oldKey);
	}

	int getCurrentSpellbook()
	{
		return client.getVarbitValue(VarbitID.SPELLBOOK);
	}

	boolean isBankOpen()
	{
		Widget bank = client.getWidget(InterfaceID.Bankmain.UNIVERSE);
		return bank != null && !bank.isHidden();
	}
}
