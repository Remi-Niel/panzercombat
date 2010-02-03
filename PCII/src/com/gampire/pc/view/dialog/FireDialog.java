package com.gampire.pc.view.dialog;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.BoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicBorders;

import com.gampire.pc.model.FireInfo;
import com.gampire.pc.model.UnitAction;
import com.gampire.pc.swing.cursor.CursorUtil;
import com.gampire.pc.swing.lookandfeel.PCIITheme;
import com.gampire.pc.swing.util.TranslucentComponent;
import com.gampire.pc.util.image.ImageUtil;

public class FireDialog extends JDialog {

	// private JPanel dialogPanel;
	private final JLabel messageLabel;
	protected final JLabel distanceLabel;
	protected final JSlider slider;
	private final JButton fireButton = new JButton();
	private final JButton cancelButton = new JButton();
	private final JCheckBox rearShotCheckBox = new JCheckBox();	
	
	private final static int ICON_SIZE = 80;

	private final int maxSelectionIndex;

	protected final FontMetrics fontMetrics = getFontMetrics(PCIITheme.FONT);

	private final static double FONT_CORRECTION = 1.15;

	private FireInfo fireInfo;

	public FireDialog(Frame frame, String message,
			int numPossibleSelections, int initialSelectionIndex, boolean isRear) {
		super(frame, null, true);

		// remove title bar
		setUndecorated(true);

		// avoid ending with a major tick with label for out of loss
		int modifiedNumPossibleSelections = numPossibleSelections;
		if (modifiedNumPossibleSelections % 2 != 0) {
			modifiedNumPossibleSelections++;
		}

		maxSelectionIndex = modifiedNumPossibleSelections - 1;

		messageLabel = new JLabel(message);
		messageLabel.setFont(PCIITheme.FONT);
		// workaround for dimension problem with the font (do not center and
		// correct)
		// messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		messageLabel.setPreferredSize(new Dimension((int) (fontMetrics
				.stringWidth(message) * FONT_CORRECTION), fontMetrics
				.getAscent()));

		String distanceDescription = getDistanceDescription(initialSelectionIndex);
		distanceLabel = new JLabel(distanceDescription);
		distanceLabel.setFont(PCIITheme.FONT);
		// workaround for dimension problem with the font (do not center and
		// correct)
		// distanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		distanceLabel.setPreferredSize(new Dimension((int) (fontMetrics
				.stringWidth(distanceDescription) * FONT_CORRECTION),
				fontMetrics.getAscent()));

		// format checkBox
		rearShotCheckBox.setText("rear shot");
		rearShotCheckBox.setToolTipText("check for shot at the rear of the target");
		rearShotCheckBox.setFont(PCIITheme.FONT);
		rearShotCheckBox.setOpaque(false);
		rearShotCheckBox.setSelected(isRear);
		
		// format buttons
		formatButton(fireButton, "fire.jpg", UnitAction.FIRE.toString());
		formatButton(cancelButton, "cancelFire.jpg", "cancel fire");

		slider = new JSlider(0, maxSelectionIndex, initialSelectionIndex);
		slider.setMajorTickSpacing(2);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setOpaque(false);

		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int selectionIndex = slider.getValue();
				String newDistanceDescription = getDistanceDescription(selectionIndex);
				distanceLabel.setText(newDistanceDescription);
				// workaround for dimension problem with the font
				distanceLabel
						.setPreferredSize(new Dimension(
								(int) (fontMetrics
										.stringWidth(newDistanceDescription) * FONT_CORRECTION),
								fontMetrics.getAscent()));
			}
		});

		// add action listener to buttons
		fireButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				fire();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				cancelFire();
			}
		});

		// translucent component instead of panel
		// JPanel dialogPanel = new JPanel(new GridBagLayout());
		TranslucentComponent dialogPanel = new TranslucentComponent(this);
		dialogPanel.setLayout(new GridBagLayout());

		// double clicks
		dialogPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					fire();
				}
			}
		});

		// mouse rolls
		dialogPanel.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				int intValue = slider.getValue() - e.getWheelRotation();
				BoundedRangeModel model = slider.getModel();
				if (model.getMaximum() >= intValue
						&& model.getMinimum() <= intValue) {
					slider.setValue(intValue);
				}
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(10, 10, 10, 10);
		c.gridx = 0;
		c.gridy = 0;
		dialogPanel.add(messageLabel, c);
		c.gridy = 1;
		c.gridwidth = 1;
		dialogPanel.add(distanceLabel, c);
		c.gridx = 1;
		dialogPanel.add(rearShotCheckBox, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		dialogPanel.add(slider, c);
		c.gridy = 3;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		dialogPanel.add(fireButton, c);
		c.gridx = 1;
		dialogPanel.add(cancelButton, c);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().add(dialogPanel);
		pack();
		setLocationRelativeTo(frame);

		// set mouse on the fire button
		CursorUtil.positionCursor(this, fireButton);

		// activate dialog
		setVisible(true);

	}

	protected String getDistanceDescription(int iDistance) {
		String distanceDescription;
		if (iDistance == 0) {
			distanceDescription = "the target is adjacent";
		} else if (iDistance == 1) {
			distanceDescription = "1 distance unit";
		} else if (iDistance < maxSelectionIndex - 1) {
			distanceDescription = Integer.toString(iDistance)
					+ " distance units";
		} else if (iDistance == maxSelectionIndex - 1) {
			distanceDescription = Integer.toString(iDistance)
					+ " distance units or more";
		} else {
			distanceDescription = "the target is out of line of sight";
		}
		return distanceDescription;

	}

	public FireInfo getFireInfo() {
		return fireInfo;
	}

	private void formatButton(JButton button, String fileName, String actionName) {
		button.setIcon(new ImageIcon(ImageUtil.getScaledImage(fileName,
				ICON_SIZE)));
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setBorder(BasicBorders.getButtonBorder());
		button.setToolTipText(actionName);
	}

	protected void fire() {
		int selectedDistance = slider.getValue();
		if (selectedDistance == maxSelectionIndex) {
			// out of los
			selectedDistance = Integer.MAX_VALUE;
		}
		fireInfo=new FireInfo(selectedDistance, rearShotCheckBox.isSelected());
		FireDialog.this.setVisible(false);
	}

	protected void cancelFire() {
		fireInfo=new FireInfo(-1, rearShotCheckBox.isSelected());
		FireDialog.this.setVisible(false);
	}
}
