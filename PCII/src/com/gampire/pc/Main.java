package com.gampire.pc;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.gampire.pc.control.BattleFieldController;
import com.gampire.pc.license.LicenseManager;
import com.gampire.pc.model.BattleField;
import com.gampire.pc.speech.Speaker;
import com.gampire.pc.swing.lookandfeel.PCIITheme;
import com.gampire.pc.util.thread.ThreadUtil;
import com.gampire.pc.view.frame.BattleFieldFrame;
import com.gampire.pc.view.frame.ErrorMessageFrame;
import com.gampire.pc.view.window.SplashScreen;

public class Main {

	private final static String WELCOME = "Welcome to Panzer Combat 2 computer assisted wargame.";

	private static int MAX_POINTS = 30;

	public static void main(String[] args) {		

		try {
			// in order to check if repaints are done on the AWT thread
			// CheckingRepaintManager.installCheckingRepaintManager();

			// look and feel
			try {
				MetalLookAndFeel.setCurrentTheme(new PCIITheme());
				UIManager.setLookAndFeel(new MetalLookAndFeel());
			} catch (UnsupportedLookAndFeelException e) {
				// nothing to do
			}

			// splash screen
			SplashScreen splash = new SplashScreen("splashScreen.jpg");
			splash.open(7000);
			//splash.open(100);

			// welcome
			Speaker.getInstance().speakAndWait(WELCOME);

			// check license file
			if (!LicenseManager.LICENSE_VALID) {
				MAX_POINTS = 10;
			}

			// model
			BattleField battleField = new BattleField(MAX_POINTS);

			// view
			BattleFieldFrame battleFieldFrame = new BattleFieldFrame(
					battleField);

			// controller
			new BattleFieldController(battleField, battleFieldFrame);

		} catch (Error e) {
			String message = e.getMessage();
			if (message == null) {
				message = e.toString();
			}

			System.err.println(message);

			new ErrorMessageFrame("Error: "+message);
			
			ThreadUtil.waitAWhile(15000);

			System.exit(-1);
		}
	}
}
