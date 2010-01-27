package com.gampire.pc.view.frame;

import java.awt.HeadlessException;

import javax.swing.JFrame;

import com.gampire.pc.util.image.ImageUtil;

public class PCFrame extends JFrame {

	public PCFrame() throws HeadlessException {
		super("Panzer Combat II");

		setIconImage(ImageUtil.getScaledImage("pcII.jpg"));

		setDefaultCloseOperation(EXIT_ON_CLOSE);		
	}
	
}
