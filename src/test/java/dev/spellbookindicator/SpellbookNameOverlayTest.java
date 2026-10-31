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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpellbookNameOverlayTest
{
	private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 16);
	private static final String[] NAMES = {"Standard", "Ancient", "Lunar", "Arceuus"};
	private static final Color[] COLORS = {
		new Color(220, 60, 40), new Color(80, 210, 50),
		new Color(60, 90, 220), new Color(190, 40, 200)
	};

	private SpellbookIndicatorConfig config;
	private SpellbookIndicatorPlugin plugin;
	private SpellbookNameOverlay overlay;

	@Before
	public void setUp()
	{
		config = mock(SpellbookIndicatorConfig.class);
		plugin = mock(SpellbookIndicatorPlugin.class);
		when(config.nameVisibility()).thenReturn(SpellbookVisibility.EVERYWHERE);
		when(config.standardNameColor()).thenReturn(COLORS[0]);
		when(config.ancientNameColor()).thenReturn(COLORS[1]);
		when(config.lunarNameColor()).thenReturn(COLORS[2]);
		when(config.arceuusNameColor()).thenReturn(COLORS[3]);
		overlay = new SpellbookNameOverlay(config, plugin);
	}

	@Test
	public void rendersEachNameWithItsConfiguredColor()
	{
		for (int book = 0; book < NAMES.length; book++)
		{
			when(plugin.getCurrentSpellbook()).thenReturn(book);
			BufferedImage image = image();
			Dimension actual = render(image);
			Graphics2D graphics = image.createGraphics();
			try
			{
				graphics.setFont(FONT);
				FontMetrics metrics = graphics.getFontMetrics();
				assertEquals(new Dimension(metrics.stringWidth(NAMES[book]) + 1,
					metrics.getHeight() + 1), actual);
			}
			finally
			{
				graphics.dispose();
			}
			assertTrue(NAMES[book], containsColor(image, COLORS[book]));
		}
	}

	@Test
	public void keepsRequestedBoundsAndClipsAcrossBooksAndFonts()
	{
		Font[] fonts = {
			FONT,
			new Font(Font.MONOSPACED, Font.PLAIN, 24),
			new Font(Font.SERIF, Font.BOLD, 32)
		};
		Dimension[] sizes = {
			new Dimension(16, 16), new Dimension(24, 20),
			new Dimension(80, 24), new Dimension(160, 48),
			new Dimension(1, 1)
		};

		for (int book = 0; book < NAMES.length; book++)
		{
			when(plugin.getCurrentSpellbook()).thenReturn(book);
			for (Font font : fonts)
			{
				for (Dimension size : sizes)
				{
					BufferedImage image = image();
					Graphics2D graphics = image.createGraphics();
					try
					{
						graphics.setFont(font);
						graphics.setColor(Color.ORANGE);
						graphics.setClip(0, 0, image.getWidth(), image.getHeight());
						Rectangle clip = graphics.getClipBounds();
						AffineTransform transform = graphics.getTransform();
						overlay.setPreferredSize(new Dimension(size));
						Dimension expected = new Dimension(Math.max(16, size.width), Math.max(16, size.height));
						String context = NAMES[book] + ", " + font + ", " + size;

						assertEquals(context, expected, overlay.render(graphics));
						assertEquals(context, size, overlay.getPreferredSize());
						assertEquals(font, graphics.getFont());
						assertEquals(Color.ORANGE, graphics.getColor());
						assertEquals(clip, graphics.getClipBounds());
						assertEquals(transform, graphics.getTransform());
						assertPixelsInside(image, expected, context);
					}
					finally
					{
						graphics.dispose();
					}
				}
			}
		}
	}

	@Test
	public void followsItsOwnVisibilitySetting()
	{
		when(config.iconVisibility()).thenReturn(SpellbookVisibility.OFF);
		for (SpellbookVisibility visibility : SpellbookVisibility.values())
		{
			when(config.nameVisibility()).thenReturn(visibility);
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
	public void hidesUnknownSpellbooks()
	{
		for (int book : new int[]{-1, 4, 99})
		{
			when(plugin.getCurrentSpellbook()).thenReturn(book);
			assertNull(render(image()));
		}
	}

	@Test
	public void returnsToNaturalSizeWhenPreferredSizeIsReset()
	{
		Dimension natural = render(image());
		overlay.setPreferredSize(new Dimension(160, 48));
		assertEquals(new Dimension(160, 48), render(image()));

		overlay.setPreferredSize(null);

		assertEquals(natural, render(image()));
	}

	private Dimension render(BufferedImage image)
	{
		Graphics2D graphics = image.createGraphics();
		try
		{
			graphics.setFont(FONT);
			return overlay.render(graphics);
		}
		finally
		{
			graphics.dispose();
		}
	}

	private static BufferedImage image()
	{
		return new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
	}

	private static boolean containsColor(BufferedImage image, Color color)
	{
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				if (image.getRGB(x, y) == color.getRGB())
				{
					return true;
				}
			}
		}
		return false;
	}

	private static void assertPixelsInside(BufferedImage image, Dimension bounds, String context)
	{
		int painted = 0;
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				if ((image.getRGB(x, y) >>> 24) != 0)
				{
					painted++;
					assertTrue(context + ": pixel outside bounds at " + x + "," + y,
						x < bounds.width && y < bounds.height);
				}
			}
		}
		assertTrue(context + ": no text painted", painted > 0);
	}
}
