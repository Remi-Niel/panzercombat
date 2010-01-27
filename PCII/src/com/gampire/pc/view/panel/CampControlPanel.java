package com.gampire.pc.view.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicBorders;

import com.gampire.pc.license.LicenseManager;
import com.gampire.pc.model.Camp;
import com.gampire.pc.model.Unit;
import com.gampire.pc.swing.lookandfeel.PCIITheme;
import com.gampire.pc.util.image.ImageUtil;
import com.gampire.pc.view.table.UnitTable;
import com.gampire.pc.view.table.UnitTableModel;

public class CampControlPanel extends JPanel implements TableModelListener,
		ActionListener {

	public final static String ADD_UNIT = "add unit";

	public final static String REMOVE_UNIT = "remove unit";

	private final Camp camp;

	private final UnitTableModel unitTableModel;

	private final UnitTable unitTable;

	private final JButton addButton = new JButton();

	private final JButton removeButton = new JButton();

	private final JLabel campPointsLabel = new JLabel();

	private final JLabel allianceFlagLabelLeft = new JLabel();

	private final JLabel allianceFlagLabelRight = new JLabel();

	private static final int ICON_SIZE = 18;

	public CampControlPanel(UnitTable unitTable) {
		super(new GridBagLayout());

		this.unitTable = unitTable;
		unitTableModel = (UnitTableModel) unitTable.getModel();
		camp = unitTableModel.getCamp();

		// this class listens to the unitTableModel in order to adapt the
		// campPointsLabel
		unitTableModel.addTableModelListener(this);

		// this class listens to its buttons to add or remove units
		addButton.addActionListener(this);
		removeButton.addActionListener(this);

		// not opaque
		setOpaque(false);

		// removed because a camp can have several countries, so Allied and Axis
		// symbols required instead
		// set icons of labels
		allianceFlagLabelLeft.setIcon(camp.getAlliance().getFlagIcon());
		allianceFlagLabelRight.setIcon(camp.getAlliance().getFlagIcon());

		// format buttons
		formatButton(addButton, "add.gif", ADD_UNIT);
		formatButton(removeButton, "remove.gif", REMOVE_UNIT);

		// update label
		updateCampPointsLabel();

		// format label
		campPointsLabel.setForeground(unitTable.getBackground());

		campPointsLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		campPointsLabel.setFont(PCIITheme.FONT);
		campPointsLabel.setToolTipText("number of points "
				+ camp.getAlliance().getName() + " camp");

		// add the components
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 20);
		add(addButton, c);
		c.gridx++;
		 c.insets = new Insets(0, 0, 1, 20);
		add(allianceFlagLabelLeft, c);
		c.gridx++;
		c.insets = new Insets(0, 0, 1, 0);
		add(campPointsLabel, c);
		c.gridx++;
		c.insets = new Insets(0, 20, 1, 0);
		add(allianceFlagLabelRight, c);
	    c.gridx++;
		c.insets = new Insets(0, 20, 0, 0);
		add(removeButton, c);
	}

	private void formatButton(JButton button, String fileName, String actionName) {
		button.setIcon(new ImageIcon(ImageUtil.getScaledImage(fileName,
				ICON_SIZE)));
		button.setToolTipText(actionName);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setBorder(BasicBorders.getButtonBorder());
	}

	private void updateCampPointsLabel() {
		campPointsLabel.setText(Integer.toString(camp.getNumPoints()));
	}

	public void tableChanged(TableModelEvent e) {
		// update the label showing the number of points of the camp
		updateCampPointsLabel();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addButton) {
			int numUnits = unitTableModel.getRowCount();
			// check license file
			if (!LicenseManager.LICENSE_VALID && numUnits > 2) {
				LicenseManager.showLimitedFunctionalityShortMessage(this);
			} else {
				unitTableModel.addUnit();
			}
		} else {
			Unit selectedUnit = unitTable.getSelectedUnit();
			unitTableModel.removeUnit(selectedUnit);
		}
	}

	public void setActivated(boolean activated) {
		addButton.setVisible(activated);
		removeButton.setVisible(activated);
	}
}
