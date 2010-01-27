package com.gampire.pc.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.gampire.pc.json.JSONBeanFactory;
import com.gampire.pc.util.image.ImageUtil;

public class Army {

	private static final String ARMIES_FILE_PATH = "data/armies.txt";

	private static final String ARMY_FLAGS_DIRECTORY = "data/armyFlags/";

	private static final Army[] all;

	static {
		ArmyBean[] armyBeans = (ArmyBean[]) JSONBeanFactory.createBeans(
				ArmyBean.class, ARMIES_FILE_PATH);
		all = new Army[armyBeans.length];
		for (int i = 0; i < armyBeans.length; i++) {
			all[i] = new Army(armyBeans[i]);
		}
	}

	private final static int ICON_HEIGHT = 18;

	private final ArmyBean armyBean;

	private final Alliance alliance;

	private final BufferedImage flag;

	private final BufferedImage smallFlag;

	private final ImageIcon flagIcon;

	private final Color color;

	private final Color middleBrightColor;

	private final Color brightColor;

	private Army(ArmyBean armyBean) {
		this.armyBean = armyBean;
		alliance = Alliance.get(armyBean.getAllianceName());

		// imageIcon
		String path = ARMY_FLAGS_DIRECTORY + armyBean.getFlag();
		File imageFile = new File(path);
		try {
			flag = ImageIO.read(new FileInputStream(imageFile));
		} catch (IOException e) {
			throw new Error("Missing file " + path);
		}

		if (flag != null) {
			smallFlag = ImageUtil.computeScaledImage(flag, ICON_HEIGHT);
			flagIcon = new ImageIcon(smallFlag);
			// imageIconLeft = new ImageIcon(ImageUtil
			// .flipHorizontally(scaledImage));
		} else {
			smallFlag = null;
			flagIcon = null;
		}
		color = new Color(armyBean.getColorRedValue(), armyBean
				.getColorGreenValue(), armyBean.getColorBlueValue());
		middleBrightColor = color.brighter().brighter();
		brightColor = middleBrightColor.brighter().brighter();
	}

	public static Army[] getAll() {
		// Public static method should not expose internal representation by
		// returning an array
		// shallow copy sufficient
		return all.clone();
	}

	public static Army get(String name) {
		for (Army army : all) {
			if (name.compareTo(army.getName()) == 0) {
				return army;
			}
		}
		throw new Error("The army " + name + " has not been defined.");
	}

	public String getName() {
		return armyBean.getName();
	}

	@Override
	public String toString() {
		return getName();
	}

	public Alliance getAlliance() {
		return alliance;
	}

	public BufferedImage getFlag() {
		return flag;
	}

	public BufferedImage getSmallFlag() {
		return smallFlag;
	}
	
	public ImageIcon getFlagIcon() {
		return flagIcon;
	}

	public Color getColor() {
		return color;
	}

	public Color getMiddleBrightColor() {
		return middleBrightColor;
	}

	public Color getBrightColor() {
		return brightColor;
	}


}
