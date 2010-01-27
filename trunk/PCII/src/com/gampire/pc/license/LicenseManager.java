package com.gampire.pc.license;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.gampire.pc.util.image.ImageUtil;

public class LicenseManager {

	static public final boolean LICENSE_VALID;

	static {
		File file = new File("license.txt");
		boolean licenseValid = false;
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));
			String licenseString = reader.readLine();
			long license = Long.valueOf(licenseString).longValue();
			if (license == 987563723891234347L) {
				licenseValid = true;
			}

		} catch (FileNotFoundException e) {
			// nothing to do
		} catch (IOException e) {
			// nothing to do
		} catch (NumberFormatException e) {
			// nothing to do
		}

		if (licenseValid) {
			LICENSE_VALID = true;
		} else {
			LICENSE_VALID = false;
		}
	}

	public static void showLimitedFunctionalityMessage(Component parentComponent) {
		// new JLabel required to have the right foreground color
		JOptionPane
				.showMessageDialog(
						parentComponent,
						new JLabel(
								"This free trial version limits the number of units to 3 per camp!"),
						"Panzer Combat II : free trial version",
						JOptionPane.WARNING_MESSAGE, new ImageIcon(ImageUtil
								.getScaledImage("pcII.jpg")));
	}

	public static void showLimitedFunctionalityShortMessage(
			Component parentComponent) {
		// new JLabel required to have the right foreground color
		JOptionPane.showMessageDialog(parentComponent, new JLabel(
				"Number of units limited to 3 per camp!"),
				"Panzer Combat II : free trial version",
				JOptionPane.WARNING_MESSAGE);
	}

}
