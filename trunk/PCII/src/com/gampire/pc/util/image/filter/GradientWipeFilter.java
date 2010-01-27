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

import java.awt.image.BufferedImage;

/**
 * A filter which can be used to produce wipes by transferring the luma of a
 * mask image into the alpha channel of the source.
 */
public class GradientWipeFilter extends AbstractBufferedImageOp {

	private float density = 0;
	private float softness = 0;
	private boolean invert;
	private BufferedImage mask;

	public GradientWipeFilter(float density, float softness, boolean invert,
			BufferedImage mask) {
		this.density = density;
		this.softness = softness;
		this.invert = invert;
		this.mask = mask;
	}

	public BufferedImage filter(BufferedImage src, BufferedImage dst) {
		BufferedImage destination=dst;
		
		int width = src.getWidth();
		int height = src.getHeight();

		if (destination == null)
			destination = createCompatibleDestImage(src, null);
		if (mask == null)
			return destination;

		int maskWidth = mask.getWidth();
		int maskHeight = mask.getHeight();

		float d = density * (1 + softness);
		float lower = 255 * (d - softness);
		float upper = 255 * d;

		int[] inPixels = new int[width];
		int[] maskPixels = new int[maskWidth];

		for (int y = 0; y < height; y++) {
			getRGB(src, 0, y, width, 1, inPixels);
			getRGB(mask, 0, y % maskHeight, maskWidth, 1, maskPixels);

			for (int x = 0; x < width; x++) {
				int maskRGB = maskPixels[x % maskWidth];
				int inRGB = inPixels[x];
				int v = brightness(maskRGB);
				float f = smoothStep(lower, upper, v);
				int a = (int) (255 * f);

				if (invert)
					a = 255 - a;
				inPixels[x] = (a << 24) | (inRGB & 0x00ffffff);
			}

			setRGB(destination, 0, y, width, 1, inPixels);
		}

		return destination;
	}

	/**
	 * A smoothed step function. A cubic function is used to smooth the step
	 * between two thresholds.
	 * 
	 * @param a
	 *            the lower threshold position
	 * @param b
	 *            the upper threshold position
	 * @param y
	 *            the input parameter
	 * @return the output value
	 */
	private static float smoothStep(float a, float b, float x) {
		float y=x;
		if (y < a)
			return 0;
		if (y >= b)
			return 1;
		y = (y - a) / (b - a);
		return y * y * (3 - 2 * y);
	}

	private static int brightness(int rgb) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		return (r + g + b) / 3;
	}

	@Override
	public String toString() {
		return "Transitions/Gradient Wipe...";
	}
}
