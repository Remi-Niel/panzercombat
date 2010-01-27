package com.gampire.pc.view.table.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.gampire.pc.model.Army;
import com.gampire.pc.model.Unit;
import com.gampire.pc.util.image.ImageUtil;

public class UnitPictureTableCellRenderer extends JLabel implements
		TableCellRenderer {

	public UnitPictureTableCellRenderer() {
		setHorizontalAlignment(CENTER);
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object object,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Unit unit = (Unit) object;
		Army army = unit.getUnitType().getArmy();

		// set color
		setForeground(army.getBrightColor());
		if (!isSelected) {
			setBackground(army.getColor());
		} else {
			setBackground(army.getMiddleBrightColor());
		}

		if (unit.isSelectedForAction()) {
			if (unit.getAnimation().getBlinkOn()) {
				setBackground(Color.RED);
			}
		} else if (unit.isDestroyed()) {
			setBackground(Color.BLACK);
		}

		BufferedImage image = unit.getUnitType().getBigImage();
		
		BufferedImage tranformedImage = transformImage(unit, isSelected, image);
		
		setIcon(new ImageIcon(tranformedImage));

		return this;
	}
	
	private BufferedImage transformImage(Unit unit, boolean isSelected, BufferedImage image) {
		boolean isLeft = unit.getCamp().isLeft();
		
		if (unit.isMoving()) {
			// add movement
			image = ImageUtil.addMovement(image, isLeft, unit.getAnimation()
					.getMovementPercentage());
		}
		if (unit.hasFired()) {
			// add cloud
			image = ImageUtil.addCloud(image, isLeft, unit.getAnimation()
					.getCloudMovementPercentage());
		}
		if (unit.getAdditionalDefendPoints() > 0) {
			// add wall
			image = ImageUtil.addWall(image, unit.getAdditionalDefendPoints());
		}
		if (unit.isExploding()) {
			// add explosion
			image = ImageUtil.addExplosion(image, isLeft, unit.getAnimation()
					.getExplosionPercentage());
		}
		if (isSelected && unit.getCamp().isSelectedAsTarget()) {
			// add lens
			image = ImageUtil.addLens(image);
		}
		if (!unit.isSelectedForAction() && unit.getCamp().isSelectedForAction()) {
			// change contrast
			image = ImageUtil.fade(image,1);
		}
		return image;
	}
}
