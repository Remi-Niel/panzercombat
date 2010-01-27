package com.gampire.pc.view.table.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.gampire.pc.model.Army;
import com.gampire.pc.model.Unit;
import com.gampire.pc.model.UnitType;

public class UnitDetailsTableCellRenderer extends JPanel implements
		TableCellRenderer {

	private final JLabel armyFlagLabel = new JLabel();

	private final JLabel unitNameLabel = new JLabel();

	private final JLabel moveRangeLabel = new JLabel(" Move ");

	private final JLabel antiPersonnelRangeLabel = new JLabel(" AP ");

	private final JLabel antiTankRangeLabel = new JLabel(" AT ");

	private final JLabel defendValueLabel = new JLabel(" Defend ");

	private final JLabel pointsLabel = new JLabel(" Points ");

	private final JLabel moveRangeField = new JLabel();

	private final JLabel antiPersonnelRangeField = new JLabel();

	private final JLabel antiTankRangeField = new JLabel();

	private final JLabel defendValueField = new JLabel();

	private final JLabel pointsField = new JLabel();

	public UnitDetailsTableCellRenderer() {
		super(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		// add the components
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.anchor = GridBagConstraints.LINE_START;

		c.weightx = 1.0;
		c.weighty = 0.5;
		add(armyFlagLabel, c);
		c.gridy++;
		c.weighty = 1.0;
		c.gridwidth = 2;
		c.insets = new Insets(0, 0, 10, 0);
		c.anchor = GridBagConstraints.CENTER;
		add(unitNameLabel, c);
		c.gridwidth = 1;
		c.gridy++;
		c.insets = new Insets(0, 0, 0, 0);
		c.anchor = GridBagConstraints.LINE_START;
		add(moveRangeLabel, c);
		c.gridx++;
		c.anchor = GridBagConstraints.LINE_END;
		add(moveRangeField, c);
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_START;
		add(antiPersonnelRangeLabel, c);
		c.gridx++;
		c.anchor = GridBagConstraints.LINE_END;
		add(antiPersonnelRangeField, c);
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_START;
		add(antiTankRangeLabel, c);
		c.gridx++;
		c.anchor = GridBagConstraints.LINE_END;
		add(antiTankRangeField, c);
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_START;
		add(defendValueLabel, c);
		c.gridx++;
		c.anchor = GridBagConstraints.LINE_END;
		add(defendValueField, c);
		setOpaque(true);
	}

	@Override
	public String getToolTipText(MouseEvent me) {
		int y = me.getPoint().y;
		if (y > moveRangeLabel.getY()
				&& y < moveRangeLabel.getY() + moveRangeLabel.getHeight()) {
			return "move range";
		} else if (y > antiPersonnelRangeLabel.getY()
				&& y < antiPersonnelRangeLabel.getY()
						+ antiPersonnelRangeLabel.getHeight()) {
			return "anti personnel fire range (used for unarmored targets)";
		} else if (y > antiTankRangeLabel.getY()
				&& y < antiTankRangeLabel.getY()
						+ antiTankRangeLabel.getHeight()) {
			return "anti tank fire range (used for armored targets)";
		} else if (y > defendValueLabel.getY()
				&& y < defendValueLabel.getY() + defendValueLabel.getHeight()) {
			return "defend value (armour or defence)";
		}
		return getToolTipText();
	}

	public Component getTableCellRendererComponent(JTable table, Object object,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}

		// set font
		Font font = table.getFont();
		Font boldFont = font.deriveFont(Font.BOLD);
		unitNameLabel.setFont(boldFont);
		moveRangeLabel.setFont(font);
		antiPersonnelRangeLabel.setFont(font);
		antiTankRangeLabel.setFont(font);
		defendValueLabel.setFont(font);
		pointsLabel.setFont(font);
		moveRangeField.setFont(font);
		antiPersonnelRangeField.setFont(font);
		antiTankRangeField.setFont(font);
		defendValueField.setFont(font);
		pointsField.setFont(font);

		Unit unit = (Unit) object;
		Army army = unit.getUnitType().getArmy();

		// set foreground
		setForeground(army.getBrightColor());
		unitNameLabel.setForeground(army.getBrightColor());
		moveRangeLabel.setForeground(army.getBrightColor());
		antiPersonnelRangeLabel.setForeground(army.getBrightColor());
		antiTankRangeLabel.setForeground(army.getBrightColor());
		defendValueLabel.setForeground(army.getBrightColor());
		pointsLabel.setForeground(army.getBrightColor());
		moveRangeField.setForeground(army.getBrightColor());
		antiPersonnelRangeField.setForeground(army.getBrightColor());
		antiTankRangeField.setForeground(army.getBrightColor());
		defendValueField.setForeground(army.getBrightColor());
		pointsField.setForeground(army.getBrightColor());

		// set background
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
		
		ImageIcon icon = army.getFlagIcon();
		armyFlagLabel.setIcon(icon);

		unitNameLabel.setText(unit.getName());

		UnitType unitType = unit.getUnitType();
		moveRangeField.setText(Integer.toString(unitType.getMoveRange()) + " ");
		antiPersonnelRangeField.setText(Integer.toString(unitType
				.getAntiPersonnelRange())
				+ " ");
		antiTankRangeField.setText(Integer
				.toString(unitType.getAntiTankRange())
				+ " ");
		String defendValueText = Integer.toString(unitType.getDefendValue());
		if (unit.getAdditionalDefendPoints() != 0) {
			defendValueText += " + " + unit.getAdditionalDefendPoints();
		}
		defendValueField.setText(defendValueText + " ");
		pointsField.setText(Integer.toString(unitType.getPoints()) + " ");

		return this;
	}
}
