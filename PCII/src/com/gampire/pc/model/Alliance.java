package com.gampire.pc.model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.gampire.pc.json.JSONBeanFactory;
import com.gampire.pc.util.image.ImageUtil;

public class Alliance {

	private static final String ALLIANCE_FILE_PATH = "data/alliances.txt";
	
	private static final String ALLIANCE_FLAGS_DIRECTORY = "data/allianceFlags/";

	private final static int ICON_HEIGHT = 18;

	private final AllianceBean allianceBean;

	private final Image flag;
	
	private final ImageIcon flagIcon;
	
	private static final Alliance[] all=new Alliance[2];

	static {
		AllianceBean[] allianceBeans = (AllianceBean[]) JSONBeanFactory
				.createBeans(AllianceBean.class, ALLIANCE_FILE_PATH);
		if (allianceBeans.length != 2) {
			throw new Error("There should be 2 alliances in file \""
					+ ALLIANCE_FILE_PATH + "\"");
		}
		all[0] = new Alliance(allianceBeans[0]);
		all[1] = new Alliance(allianceBeans[1]);
	}

	private Alliance(AllianceBean allianceBean) {
		this.allianceBean = allianceBean;
		
		// imageIcon
		String path = ALLIANCE_FLAGS_DIRECTORY + allianceBean.getFlag();
		File imageFile = new File(path);
		try {
			flag = ImageIO.read(new FileInputStream(imageFile));
		} catch (IOException e) {
			throw new Error("Missing file " + path);
		}

		if (flag != null) {
			BufferedImage scaledImage = ImageUtil.computeScaledImage(
					flag, ICON_HEIGHT);
			flagIcon = new ImageIcon(scaledImage);
			// imageIconLeft = new ImageIcon(ImageUtil
			// .flipHorizontally(scaledImage));
		} else {
			flagIcon = null;
		}
	}

	public static Alliance get(String name) {
		for (Alliance alliance : all) {
			if (alliance.getName().compareTo(name) == 0) {
				return alliance;
			}
		}
		return null;
	}

	public static Alliance[] getAll() {
		// Public static method should not expose internal representation by
		// returning an array
		// shallow copy sufficient
		return all.clone();
	}
	
	public String getName() {
		return allianceBean.getName();
	}
	
	public Image getFlag() {
		return flag;
	}
	
	public ImageIcon getFlagIcon() {
		return flagIcon;
	}
}
