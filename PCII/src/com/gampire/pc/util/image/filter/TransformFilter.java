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
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * An abstract superclass for filters which distort images in some way. The
 * subclass only needs to override two methods to provide the mapping between
 * source and destination pixels.
 */
public abstract class TransformFilter extends AbstractBufferedImageOp {

	/**
	 * Treat pixels off the edge as zero.
	 */
	public final static int ZERO = 0;

	/**
	 * Clamp pixels to the image edges.
	 */
	public final static int CLAMP = 1;

	/**
	 * Wrap pixels off the edge onto the oppsoite edge.
	 */
	public final static int WRAP = 2;

	/**
	 * Use nearest-neighbout interpolation.
	 */
	public final static int NEAREST_NEIGHBOUR = 0;

	/**
	 * Use bilinear interpolation.
	 */
	public final static int BILINEAR = 1;

	/**
	 * The action to take for pixels off the image edge.
	 */
	protected int edgeAction = ZERO;

	/**
	 * The type of interpolation to use.
	 */
	protected int interpolation = BILINEAR;

	/**
	 * The output image rectangle.
	 */
	protected Rectangle transformedSpace;

	/**
	 * The input image rectangle.
	 */
	protected Rectangle originalSpace;

	/**
	 * Set the action to perform for pixels off the edge of the image.
	 * 
	 * @param edgeAction
	 *            one of ZERO, CLAMP or WRAP
	 * @see #getEdgeAction
	 */
	public void setEdgeAction(int edgeAction) {
		this.edgeAction = edgeAction;
	}

	/**
	 * Get the action to perform for pixels off the edge of the image.
	 * 
	 * @return one of ZERO, CLAMP or WRAP
	 * @see #setEdgeAction
	 */
	public int getEdgeAction() {
		return edgeAction;
	}

	/**
	 * Set the type of interpolation to perform.
	 * 
	 * @param interpolation
	 *            one of NEAREST_NEIGHBOUR or BILINEAR
	 * @see #getInterpolation
	 */
	public void setInterpolation(int interpolation) {
		this.interpolation = interpolation;
	}

	/**
	 * Get the type of interpolation to perform.
	 * 
	 * @return one of NEAREST_NEIGHBOUR or BILINEAR
	 * @see #setInterpolation
	 */
	public int getInterpolation() {
		return interpolation;
	}

	/**
	 * Inverse transform a point. This method needs to be overriden by all
	 * subclasses.
	 * 
	 * @param x
	 *            the X position of the pixel in the output image
	 * @param y
	 *            the Y position of the pixel in the output image
	 * @param out
	 *            the position of the pixel in the input image
	 */
	protected abstract void transformInverse(int x, int y, float[] out);

	/**
	 * Forward transform a rectangle. Used to determine the size of the output
	 * image.
	 * 
	 * @param rect
	 *            the rectangle to transform
	 */
	protected void transformSpace(Rectangle rect) {
		// nothing to do
	}

	public BufferedImage filter(BufferedImage src, BufferedImage dst) {
		BufferedImage destination = dst;

		int width = src.getWidth();
		int height = src.getHeight();
		originalSpace = new Rectangle(0, 0, width, height);
		transformedSpace = new Rectangle(0, 0, width, height);
		transformSpace(transformedSpace);

		if (destination == null) {
			ColorModel dstCM = src.getColorModel();
			destination = new BufferedImage(dstCM, dstCM
					.createCompatibleWritableRaster(transformedSpace.width,
							transformedSpace.height), dstCM
					.isAlphaPremultiplied(), null);
		}
		int[] inPixels = getRGB(src, 0, 0, width, height, null);

		if (interpolation == NEAREST_NEIGHBOUR)
			return filterPixelsNN(destination, width, height, inPixels,
					transformedSpace);

		int srcWidth = width;
		int srcHeight = height;
		int srcWidth1 = width - 1;
		int srcHeight1 = height - 1;
		int outWidth = transformedSpace.width;
		int outHeight = transformedSpace.height;
		int outX, outY;
		int[] outPixels = new int[outWidth];

		outX = transformedSpace.x;
		outY = transformedSpace.y;
		float[] out = new float[2];

		for (int y = 0; y < outHeight; y++) {
			for (int x = 0; x < outWidth; x++) {
				transformInverse(outX + x, outY + y, out);
				int srcX = (int) Math.floor(out[0]);
				int srcY = (int) Math.floor(out[1]);
				float xWeight = out[0] - srcX;
				float yWeight = out[1] - srcY;
				int nw, ne, sw, se;

				if (srcX >= 0 && srcX < srcWidth1 && srcY >= 0
						&& srcY < srcHeight1) {
					// Easy case, all corners are in the image
					int i = srcWidth * srcY + srcX;
					nw = inPixels[i];
					ne = inPixels[i + 1];
					sw = inPixels[i + srcWidth];
					se = inPixels[i + srcWidth + 1];
				} else {
					// Some of the corners are off the image
					nw = getPixel(inPixels, srcX, srcY, srcWidth, srcHeight);
					ne = getPixel(inPixels, srcX + 1, srcY, srcWidth, srcHeight);
					sw = getPixel(inPixels, srcX, srcY + 1, srcWidth, srcHeight);
					se = getPixel(inPixels, srcX + 1, srcY + 1, srcWidth,
							srcHeight);
				}
				outPixels[x] = bilinearInterpolate(xWeight, yWeight, nw, ne,
						sw, se);
			}
			setRGB(destination, 0, y, transformedSpace.width, 1, outPixels);
		}
		return destination;
	}

	final private int getPixel(int[] pixels, int x, int y, int width, int height) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			switch (edgeAction) {
			case ZERO:
			default:
				return 0;
			case WRAP:
				return pixels[(mod(y, height) * width) + mod(x, width)];
			case CLAMP:
				return pixels[(clamp(y, 0, height - 1) * width)
						+ clamp(x, 0, width - 1)];
			}
		}
		return pixels[y * width + x];
	}

	protected BufferedImage filterPixelsNN(BufferedImage dst, int width,
			int height, int[] inPixels, Rectangle transformed) {
		int srcWidth = width;
		int srcHeight = height;
		int outWidth = transformed.width;
		int outHeight = transformed.height;
		int outX, outY, srcX, srcY;
		int[] outPixels = new int[outWidth];

		outX = transformed.x;
		outY = transformed.y;
		int[] rgb = new int[4];
		float[] out = new float[2];

		for (int y = 0; y < outHeight; y++) {
			for (int x = 0; x < outWidth; x++) {
				transformInverse(outX + x, outY + y, out);
				srcX = (int) out[0];
				srcY = (int) out[1];
				// int casting rounds towards zero, so we check out[0] < 0, not
				// srcX < 0
				if (out[0] < 0 || srcX >= srcWidth || out[1] < 0
						|| srcY >= srcHeight) {
					int p;
					switch (edgeAction) {
					case ZERO:
					default:
						p = 0;
						break;
					case WRAP:
						p = inPixels[(mod(srcY, srcHeight) * srcWidth)
								+ mod(srcX, srcWidth)];
						break;
					case CLAMP:
						p = inPixels[(clamp(srcY, 0, srcHeight - 1) * srcWidth)
								+ clamp(srcX, 0, srcWidth - 1)];
						break;
					}
					outPixels[x] = p;
				} else {
					int i = srcWidth * srcY + srcX;
					rgb[0] = inPixels[i];
					outPixels[x] = inPixels[i];
				}
			}
			setRGB(dst, 0, y, transformed.width, 1, outPixels);
		}
		return dst;
	}

	/**
	 * Bilinear interpolation of ARGB values.
	 * 
	 * @param x
	 *            the X interpolation parameter 0..1
	 * @param y
	 *            the y interpolation parameter 0..1
	 * @param rgb
	 *            array of four ARGB values in the order NW, NE, SW, SE
	 * @return the interpolated value
	 */
	private static int bilinearInterpolate(float x, float y, int nw, int ne,
			int sw, int se) {
		float m0, m1;
		int a0 = (nw >> 24) & 0xff;
		int r0 = (nw >> 16) & 0xff;
		int g0 = (nw >> 8) & 0xff;
		int b0 = nw & 0xff;
		int a1 = (ne >> 24) & 0xff;
		int r1 = (ne >> 16) & 0xff;
		int g1 = (ne >> 8) & 0xff;
		int b1 = ne & 0xff;
		int a2 = (sw >> 24) & 0xff;
		int r2 = (sw >> 16) & 0xff;
		int g2 = (sw >> 8) & 0xff;
		int b2 = sw & 0xff;
		int a3 = (se >> 24) & 0xff;
		int r3 = (se >> 16) & 0xff;
		int g3 = (se >> 8) & 0xff;
		int b3 = se & 0xff;

		float cx = 1.0f - x;
		float cy = 1.0f - y;

		m0 = cx * a0 + x * a1;
		m1 = cx * a2 + x * a3;
		int a = (int) (cy * m0 + y * m1);

		m0 = cx * r0 + x * r1;
		m1 = cx * r2 + x * r3;
		int r = (int) (cy * m0 + y * m1);

		m0 = cx * g0 + x * g1;
		m1 = cx * g2 + x * g3;
		int g = (int) (cy * m0 + y * m1);

		m0 = cx * b0 + x * b1;
		m1 = cx * b2 + x * b3;
		int b = (int) (cy * m0 + y * m1);

		return (a << 24) | (r << 16) | (g << 8) | b;
	}

}
