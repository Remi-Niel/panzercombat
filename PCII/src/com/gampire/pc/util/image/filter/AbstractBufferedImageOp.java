/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.gampire.pc.util.image.filter;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

/**
 * A convenience class which implements those methods of BufferedImageOp which
 * are rarely changed.
 */
public abstract class AbstractBufferedImageOp implements BufferedImageOp {

	public BufferedImage createCompatibleDestImage(BufferedImage src,
			ColorModel dstCM) {
		ColorModel destinationCM = dstCM;
		if (destinationCM == null)
			destinationCM = src.getColorModel();
		return new BufferedImage(destinationCM,
				destinationCM.createCompatibleWritableRaster(src.getWidth(),
						src.getHeight()), destinationCM.isAlphaPremultiplied(),
				null);
	}

	public Rectangle2D getBounds2D(BufferedImage src) {
		return new Rectangle(0, 0, src.getWidth(), src.getHeight());
	}

	public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
		Point2D pt = dstPt;
		if (pt == null)
			pt = new Point2D.Double();
		pt.setLocation(srcPt.getX(), srcPt.getY());
		return pt;
	}

	public RenderingHints getRenderingHints() {
		return null;
	}

	/**
	 * A convenience method for getting ARGB pixels from an image. This tries to
	 * avoid the performance penalty of BufferedImage.getRGB unmanaging the
	 * image.
	 * 
	 * @param image
	 *            a BufferedImage object
	 * @param x
	 *            the left edge of the pixel block
	 * @param y
	 *            the right edge of the pixel block
	 * @param width
	 *            the width of the pixel arry
	 * @param height
	 *            the height of the pixel arry
	 * @param pixels
	 *            the array to hold the returned pixels. May be null.
	 * @return the pixels
	 * @see #setRGB
	 */
	static protected int[] getRGB(BufferedImage image, int x, int y, int width,
			int height, int[] pixels) {
		int type = image.getType();
		if (type == BufferedImage.TYPE_INT_ARGB
				|| type == BufferedImage.TYPE_INT_RGB)
			return (int[]) image.getRaster().getDataElements(x, y, width,
					height, pixels);
		return image.getRGB(x, y, width, height, pixels, 0, width);
	}

	/**
	 * A convenience method for setting ARGB pixels in an image. This tries to
	 * avoid the performance penalty of BufferedImage.setRGB unmanaging the
	 * image.
	 * 
	 * @param image
	 *            a BufferedImage object
	 * @param x
	 *            the left edge of the pixel block
	 * @param y
	 *            the right edge of the pixel block
	 * @param width
	 *            the width of the pixel arry
	 * @param height
	 *            the height of the pixel arry
	 * @param pixels
	 *            the array of pixels to set
	 * @see #getRGB
	 */
	static protected void setRGB(BufferedImage image, int x, int y, int width,
			int height, int[] pixels) {
		int type = image.getType();
		if (type == BufferedImage.TYPE_INT_ARGB
				|| type == BufferedImage.TYPE_INT_RGB)
			image.getRaster().setDataElements(x, y, width, height, pixels);
		else
			image.setRGB(x, y, width, height, pixels, 0, width);
	}

	/**
	 * Return a mod b. This differs from the % operator with respect to negative
	 * numbers.
	 * 
	 * @param a
	 *            the dividend
	 * @param b
	 *            the divisor
	 * @return a mod b
	 */
	static protected int mod(int a, int b) {
		int n = a / b;
		int result = a - n * b;
		if (result < 0)
			return result + b;
		return result;
	}

	/**
	 * Clamp a value to an interval.
	 * 
	 * @param a
	 *            the lower clamp threshold
	 * @param b
	 *            the upper clamp threshold
	 * @param x
	 *            the input parameter
	 * @return the clamped value
	 */
	static protected int clamp(int x, int a, int b) {
		return (x < a) ? a : (x > b) ? b : x;
	}

	/**
	 * Clamp a value to the range 0..255
	 */
	static protected int clamp(int c) {
		if (c < 0)
			return 0;
		if (c > 255)
			return 255;
		return c;
	}

}
