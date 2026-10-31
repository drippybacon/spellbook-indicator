package dev.spellbookindicator;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public final class SpellbookIndicatorDev
{
	private SpellbookIndicatorDev()
	{
	}

	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SpellbookIndicatorPlugin.class);
		RuneLite.main(args);
	}
}
