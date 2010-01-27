package com.gampire.pc.swing.lookandfeel;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * This class describes a theme using "primary" colors. You can change the
 * colors to anything else you want.
 */

public class PCIITheme extends DefaultMetalTheme {

	static public final Color GRAY = new Color(60, 60, 60);
	static public final Color LIGHT_GRAY = GRAY.brighter().brighter()
			.brighter();
	static public final Color DARK_GRAY = GRAY.darker().darker();

	static final public Font FONT;

	static final private FontUIResource FONT_UI_RESOURCE;

	static {
		Font font = null;
		try {
			String path = "/com/gampire/pc/resources/font/stencil.ttf";
			font = Font.createFont(Font.TRUETYPE_FONT, PCIITheme.class
					.getResourceAsStream(path));
		} catch (IOException e) {
			System.out.print(e.toString());
		} catch (FontFormatException e) {
			System.out.print(e.toString());
		}

		if (font != null) {
			FONT = font.deriveFont(Font.PLAIN, 18F);

			FONT_UI_RESOURCE = new FontUIResource(FONT);
		} else {
			FONT = null;
			FONT_UI_RESOURCE = null;
		}

		// set colors used by option pane
		Color veryLightGray = LIGHT_GRAY.brighter();
		UIManager.put("List.foreground", veryLightGray);
		UIManager.put("List.background", DARK_GRAY);
		UIManager.put("Label.foreground", veryLightGray);
		UIManager.put("OptionPane.background", GRAY);
		UIManager.put("Panel.background", GRAY);
	}

	@Override
	public String getName() {
		return "PCII";
	}

	private final ColorUIResource primary1 = new ColorUIResource(DARK_GRAY);
	private final ColorUIResource primary2 = new ColorUIResource(LIGHT_GRAY);
	private final ColorUIResource primary3 = new ColorUIResource(LIGHT_GRAY);

	private final ColorUIResource secondary1 = new ColorUIResource(DARK_GRAY);
	private final ColorUIResource secondary2 = new ColorUIResource(LIGHT_GRAY);
	private final ColorUIResource secondary3 = new ColorUIResource(LIGHT_GRAY);

	@Override
	protected ColorUIResource getPrimary1() {
		return primary1;
	}

	@Override
	protected ColorUIResource getPrimary2() {
		return primary2;
	}

	@Override
	protected ColorUIResource getPrimary3() {
		return primary3;
	}

	@Override
	protected ColorUIResource getSecondary1() {
		return secondary1;
	}

	@Override
	protected ColorUIResource getSecondary2() {
		return secondary2;
	}

	@Override
	protected ColorUIResource getSecondary3() {
		return secondary3;
	}

	@Override
	public FontUIResource getControlTextFont() {
		return FONT_UI_RESOURCE;
	}

	@Override
	public FontUIResource getSystemTextFont() {
		return FONT_UI_RESOURCE;
	}

	@Override
	public FontUIResource getUserTextFont() {
		return FONT_UI_RESOURCE;
	}

	@Override
	public FontUIResource getMenuTextFont() {
		return FONT_UI_RESOURCE;
	}

	@Override
	public FontUIResource getWindowTitleFont() {
		return FONT_UI_RESOURCE;
	}

	@Override
	public FontUIResource getSubTextFont() {
		return FONT_UI_RESOURCE;
	}

}
