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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.runelite.api.gameval.SpriteID;
import net.runelite.client.game.SpriteManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class SpellbookIndicatorOverlayTest
{
	private SpellbookIndicatorConfig config;
	private SpellbookIndicatorPlugin plugin;
	private SpriteManager sprites;
	private SpellbookIndicatorOverlay overlay;

	@Before
	public void setUp()
	{
		config = mock(SpellbookIndicatorConfig.class);
		plugin = mock(SpellbookIndicatorPlugin.class);
		sprites = mock(SpriteManager.class);
		when(config.iconVisibility()).thenReturn(SpellbookVisibility.EVERYWHERE);
		when(config.legacyIconSize()).thenReturn(24);
		BufferedImage icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = icon.createGraphics();
		try
		{
			graphics.setColor(Color.BLUE);
			graphics.fillRect(0, 0, 16, 16);
		}
		finally
		{
			graphics.dispose();
		}
		when(sprites.getSprite(anyInt(), eq(0))).thenReturn(icon);
		overlay = new SpellbookIndicatorOverlay(config, plugin, sprites);
	}

	@Test
	public void usesTheCorrectSpriteForEverySpellbook()
	{
		int[] expectedSprites = {
			SpriteID.SideiconsInterface.MAGIC,
			SpriteID.SideiconsInterface.SPELLBOOK_ANCIENT_MAGICKS,
			SpriteID.SideiconsInterface.SPELLBOOK_LUNAR,
			SpriteID.SideiconsInterface.SPELLBOOK_ARCEUUS
		};
		for (int book = 0; book < expectedSprites.length; book++)
		{
			clearInvocations(sprites);
			when(plugin.getCurrentSpellbook()).thenReturn(book);

			assertEquals(new Dimension(24, 24), render(image()));
			verify(sprites).getSprite(expectedSprites[book], 0);
		}
	}

	@Test
	public void hidesUnknownSpellbooksWithoutRequestingASprite()
	{
		for (int book : new int[]{-1, 4, 99})
		{
			when(plugin.getCurrentSpellbook()).thenReturn(book);
			assertNull(render(image()));
		}
		verifyNoInteractions(sprites);
	}

	@Test
	public void hidesUntilTheSpriteIsAvailable()
	{
		when(sprites.getSprite(anyInt(), eq(0))).thenReturn(null);

		assertNull(render(image()));
	}

	@Test
	public void followsItsOwnVisibilitySetting()
	{
		when(config.nameVisibility()).thenReturn(SpellbookVisibility.OFF);
		for (SpellbookVisibility visibility : SpellbookVisibility.values())
		{
			when(config.iconVisibility()).thenReturn(visibility);
			for (boolean bankOpen : new boolean[]{false, true})
			{
				when(plugin.isBankOpen()).thenReturn(bankOpen);
				Dimension size = render(image());
				if (visibility.isVisible(bankOpen))
				{
					assertNotNull(size);
				}
				else
				{
					assertNull(size);
				}
			}
		}
	}

	@Test
	public void boundsTheLegacySize()
	{
		int[][] cases = {{-100, 16}, {16, 16}, {24, 24}, {40, 40}, {100, 40}};
		for (int[] testCase : cases)
		{
			when(config.legacyIconSize()).thenReturn(testCase[0]);
			assertEquals(new Dimension(testCase[1], testCase[1]), render(image()));
		}
	}

	@Test
	public void centersASquareIconWithinTheResizedBounds()
	{
		overlay.setPreferredSize(new Dimension(80, 40));
		BufferedImage image = image();

		assertEquals(new Dimension(80, 40), render(image));

		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				int expected = x >= 20 && x < 60 && y < 40 ? Color.BLUE.getRGB() : 0;
				assertEquals("Pixel " + x + "," + y, expected, image.getRGB(x, y));
			}
		}
	}

	@Test
	public void boundsOldSavedDimensionsWithoutChangingThePreference()
	{
		overlay.setPreferredSize(new Dimension(1, 1));

		assertEquals(new Dimension(16, 16), render(image()));
		assertEquals(new Dimension(1, 1), overlay.getPreferredSize());
	}

	@Test
	public void resetsToTheSavedLegacySize()
	{
		when(config.legacyIconSize()).thenReturn(32);
		overlay.setPreferredSize(new Dimension(64, 64));
		assertEquals(new Dimension(64, 64), render(image()));

		overlay.setPreferredSize(null);

		assertEquals(new Dimension(32, 32), render(image()));
	}

	@Test
	public void keepsIconAndNameSizesIndependent()
	{
		SpellbookNameOverlay name = new SpellbookNameOverlay(config, plugin);
		name.setPreferredSize(new Dimension(100, 40));
		overlay.setPreferredSize(new Dimension(32, 32));

		name.setPreferredSize(new Dimension(80, 20));

		assertEquals(new Dimension(32, 32), overlay.getPreferredSize());
		assertEquals(new Dimension(80, 20), name.getPreferredSize());
		assertEquals(new Dimension(32, 32), render(image()));
	}

	private Dimension render(BufferedImage image)
	{
		Graphics2D graphics = image.createGraphics();
		try
		{
			return overlay.render(graphics);
		}
		finally
		{
			graphics.dispose();
		}
	}

	private static BufferedImage image()
	{
		return new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
	}
}
