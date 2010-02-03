package com.gampire.pc.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.gampire.pc.json.JSONArray;
import com.gampire.pc.json.JSONBeanFactory;
import com.gampire.pc.json.JSONException;
import com.gampire.pc.json.JSONObject;
import com.gampire.pc.json.JSONTokener;
import com.gampire.pc.swing.renderer.HasImageIcon;
import com.gampire.pc.util.image.ImageUtil;
import com.gampire.pc.util.string.StringUtil;

public class UnitType implements HasImageIcon, Comparable<UnitType> {

	private static final Map<Alliance, Vector<UnitType>> allianceMap = new HashMap<Alliance, Vector<UnitType>>();

	private static final String UNIT_TYPES_FILE_PATH = "data/unitTypes.txt";

	private static final String UNIT_TYPE_IMAGES_DIRECTORY_PATH = "data/unitTypeImages/";

	private static final UnitType[] all;

	static {
		UnitTypeBean[] unitTypeBeans = (UnitTypeBean[]) JSONBeanFactory
				.createBeans(UnitTypeBean.class, UNIT_TYPES_FILE_PATH);

		File dir = new File(UNIT_TYPE_IMAGES_DIRECTORY_PATH);

		Map<String, Boolean> unitTypeImageUsedMap = new HashMap<String, Boolean>();

		String[] unitTypeImages = dir.list();
		if (unitTypeImages == null) {
			// Either dir does not exist or is not a directory
			throw new Error("The unitType image drectory '"
					+ UNIT_TYPE_IMAGES_DIRECTORY_PATH + "' does not exist.");
		}
		for (String unitTypeImage : unitTypeImages) {
			// skip ".svn" and "Thumbs.db" files / dirs
			if (unitTypeImage.equals(".svn")
					|| unitTypeImage.equals("Thumbs.db"))
				continue;
			unitTypeImageUsedMap.put(unitTypeImage, Boolean.FALSE);
		}

		FileReader reader;
		try {
			reader = new FileReader(UNIT_TYPES_FILE_PATH);
		} catch (FileNotFoundException e1) {
			throw new Error("Missing file '" + UNIT_TYPES_FILE_PATH + "'");
		}
		JSONTokener tokener = new JSONTokener(reader);
		try {
			JSONArray array = new JSONArray(tokener);
			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				String image = jsonObject.getString("image");
				if (image.equals("")) {
					String name = jsonObject.getString("name");
					Pattern pattern = Pattern.compile(" |\\/");
					Matcher matcher = pattern.matcher(name);
					String imageName = matcher.replaceAll("_") + ".jpg";
					jsonObject.put("image", imageName);
				}
			}
			System.out.println(array.toString(3));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		all = new UnitType[unitTypeBeans.length];
		Vector<String> inexistingUnitTypeImages = new Vector<String>();
		for (int i = 0; i < unitTypeBeans.length; i++) {
			UnitTypeBean unitTypeBean = unitTypeBeans[i];
			if (unitTypeBean.getImage().equals("")) {
				Pattern pattern = Pattern.compile(" |\\/");
				Matcher matcher = pattern.matcher(unitTypeBean.getName());
				unitTypeBean.setImage(matcher.replaceAll("_") + ".jpg");
			}
			all[i] = new UnitType(unitTypeBean);
			if (all[i].isImageReplacedByFlag()) {
				inexistingUnitTypeImages.add(unitTypeBean.getImage());
			}
			unitTypeImageUsedMap.put(unitTypeBean.getImage(), Boolean.TRUE);
		}

		Set<Map.Entry<String, Boolean>> unitTypeImageUsedMapEntries = unitTypeImageUsedMap
				.entrySet();
		Iterator<Map.Entry<String, Boolean>> unitTypeImageUsedMapEntriesIterator = unitTypeImageUsedMapEntries
				.iterator();
		String unusedUnitTypeImagesString = "";
		Vector<String> unusedUnitTypeImages = new Vector<String>();
		while (unitTypeImageUsedMapEntriesIterator.hasNext()) {
			Map.Entry<String, Boolean> unitTypeImageUsedMapEntry = unitTypeImageUsedMapEntriesIterator
					.next();
			if (unitTypeImageUsedMapEntry.getValue() == Boolean.TRUE)
				continue;
			String unusedUnitTypeImage = unitTypeImageUsedMapEntry.getKey();
			unusedUnitTypeImages.add(unusedUnitTypeImage);
		}
		Collections.sort(unusedUnitTypeImages);
		for (String unusedUnitTypeImage : unusedUnitTypeImages) {
			// find closest match in inexistingUnitTypeImages
			int minDistance = 5;
			String closestInexistingUnitTypeImage = "";
			for (String inexistingUnitTypeImage : inexistingUnitTypeImages) {
				int distance = StringUtil.getLevenshteinDistance(
						inexistingUnitTypeImage, unusedUnitTypeImage);
				if (distance < minDistance) {
					minDistance = distance;
					closestInexistingUnitTypeImage = inexistingUnitTypeImage;
				}
			}
			unusedUnitTypeImagesString += "'" + unusedUnitTypeImage + "'";
			if (closestInexistingUnitTypeImage.length() > 0) {
				unusedUnitTypeImagesString += " (maybe you meant '"
						+ closestInexistingUnitTypeImage + "')";
			}
			unusedUnitTypeImagesString += "\n";
		}
		if (unusedUnitTypeImagesString.length() > 0) {
			throw new Error(
					"The following files are not used in the directory '"
							+ UNIT_TYPE_IMAGES_DIRECTORY_PATH + "':\n"
							+ unusedUnitTypeImagesString);
		}
	}

	private final static int IMAGE_HEIGHT = 100;

	private final static int BIG_IMAGE_HEIGHT = 160;

	private static int nextIndex = 0;

	private final UnitTypeBean unitTypeBean;

	private final Army army;

	private final int index = nextIndex++;

	private final BufferedImage bigImage;

	private BufferedImage image;

	private final ImageIcon bigIcon;

	private final ImageIcon icon;

	private final int points;

	private boolean imageReplacedByFlag = false;

	private UnitType(UnitTypeBean unitTypeBean) {
		this.unitTypeBean = unitTypeBean;
		army = Army.get(unitTypeBean.getArmyName());

		// points
		points = (getMoveRange() * 2 + getAntiPersonnelRange()
				+ getAntiTankRange() + getDefendValue() * 2) / 2;

		// image and icon
		String path = "data/unitTypeImages/" + unitTypeBean.getImage();
		File imageFile = new File(path);
		BufferedImage loadedImage = null;
		try {
			loadedImage = ImageIO.read(new FileInputStream(imageFile));
		} catch (IOException e) {
			System.err.println("Missing unitType image '" + path
					+ "' for army " + army.toString());
			loadedImage = army.getFlag();
			imageReplacedByFlag = true;
		}

		if (loadedImage != null) {
			bigImage = ImageUtil.computeScaledImage(loadedImage,
					BIG_IMAGE_HEIGHT);
			image = ImageUtil.computeScaledImage(loadedImage, IMAGE_HEIGHT);
			if (!imageReplacedByFlag) {
				// superpose the flag on the left upper corner
				image = ImageUtil.superpose(image, army.getSmallFlag());
			}
			icon = new ImageIcon(image, getName());
			bigIcon = new ImageIcon(bigImage, getName());
		} else {
			bigImage = null;
			image = null;
			bigIcon = null;
			icon = null;
		}

		// construct allianceMap
		Alliance alliance = army.getAlliance();
		Vector<UnitType> unitTypes = allianceMap.get(alliance);
		if (unitTypes == null) {
			unitTypes = new Vector<UnitType>();
			allianceMap.put(alliance, unitTypes);
		}
		unitTypes.add(this);
	}

	public UnitTypeBean getUnitTypeBean() {
		return unitTypeBean;
	}

	public int getAntiPersonnelRange() {
		return unitTypeBean.getAntiPersonnelRange();
	}

	public int getAntiTankRange() {
		return unitTypeBean.getAntiTankRange();
	}

	public Army getArmy() {
		return army;
	}

	public int getDefendValue() {
		return unitTypeBean.getDefendValue();
	}

	public ImageIcon getImageIcon() {
		return icon;
	}

	public ImageIcon getBigImageIcon() {
		return bigIcon;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getBigImage() {
		return bigImage;
	}

	public int getIndex() {
		return index;
	}

	public int getMoveRange() {
		return unitTypeBean.getMoveRange();
	}

	public int getPoints() {
		return points;
	}

	public String getName() {
		return unitTypeBean.getName();
	}

	public String getShortName() {
		return unitTypeBean.getShortName();
	}

	public boolean isArmored() {
		return unitTypeBean.isArmored();
	}

	public int getMaximumEffectiveRange(UnitType target) {
		// the effective range is the range + a dice result - half the defend value (for rear shot)
		int rangeToTarget = target.isArmored() ? getAntiTankRange()
				: getAntiPersonnelRange();
		int rangeFromTarget = isArmored() ? target.getAntiTankRange() : target
				.getAntiPersonnelRange();

		// take minimal additional defend points for this (i.e 0) and half defend value (rear shot)
		int maximumEffectiveRangeToTarget = rangeToTarget + 6
				- (target.getDefendValue()) / 2;
		int maximumEffectiveRangeFromTarget = rangeFromTarget + 6
				- (getDefendValue()) / 2;

		int maximumEffectiveRange = maximumEffectiveRangeToTarget > maximumEffectiveRangeFromTarget ? maximumEffectiveRangeToTarget
				: maximumEffectiveRangeFromTarget;
		return maximumEffectiveRange;
	}

	public int getMeanEffectiveRange(UnitType target) {
		// the effective range is the range + a dice result - the defend value
		int rangeToTarget = target.isArmored() ? getAntiTankRange()
				: getAntiPersonnelRange();

		// take minimal additional defend points (i.e 0)
		// and 3 for the dice roll
		int meanEffectiveRange = rangeToTarget + 3 - target.getDefendValue();

		return meanEffectiveRange < 0 ? 0 : meanEffectiveRange;
	}

	@Override
	public String toString() {
		return getName();
	}

	public static UnitType selectRandomUnitType(Alliance alliance) {
		Vector<UnitType> unitTypes = UnitType.getAllForAliance(alliance);
		int numUnitTypes = unitTypes.size();
		int randomIndex = (int) Math.floor(Math.random() * numUnitTypes);
		return unitTypes.get(randomIndex);
	}

	public static Vector<UnitType> getAllForAliance(Alliance alliance) {
		// memory consumption to high?
		// return unitTypes.toArray(new UnitType[unitTypes.size()]);
		return allianceMap.get(alliance);
	}

	public static UnitType[] getAll() {
		// Public static method should not expose internal representation by
		// returning an array
		// shallow copy sufficient
		return all.clone();
	}

	public int compareTo(UnitType unitType) {
		// Note: this class has a natural ordering that is inconsistent with
		// equals
		return index - unitType.index;
	}

	public boolean isImageReplacedByFlag() {
		return imageReplacedByFlag;
	}

}
