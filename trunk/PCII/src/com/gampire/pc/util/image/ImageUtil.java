package com.gampire.pc.util.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.gampire.pc.util.image.filter.ContrastFilter;
import com.gampire.pc.util.image.filter.FlipFilter;
import com.gampire.pc.util.image.filter.GradientWipeFilter;
import com.gampire.pc.util.image.filter.MotionBlurFilter;
import com.gampire.pc.util.image.filter.SphereFilter;

public class ImageUtil {

	static private final String IMAGE_DIRECTORY = "/com/gampire/pc/resources/images/";

	// make sure to cover all icons
	private static final int IMAGE_SIZE = 190;

	private static final BufferedImage explodeImage = getScaledImage(
			"explode.jpg", IMAGE_SIZE);
	private static final BufferedImage cloudLeftImage = getScaledImage(
			"cloudLeft.jpg", IMAGE_SIZE);
	private static final BufferedImage cloudRightImage = getScaledImage(
			"cloudRight.jpg", IMAGE_SIZE);
	private static final BufferedImage[] brickWallImages = {
			getScaledImage("brickWall1.gif", IMAGE_SIZE),
			getScaledImage("brickWall2.gif", IMAGE_SIZE),
			getScaledImage("brickWall3.gif", IMAGE_SIZE) };

	public static BufferedImage getImage(String fileName) {
		String path = IMAGE_DIRECTORY + fileName;
		URL url = ImageUtil.class.getResource(path);
		BufferedImage image = null;
		if (url != null) {

			try {
				image = ImageIO.read(url);
			} catch (IOException e) {
				throw new Error("Missing file " + path);
			}
		}
		return image;
	}

	public static BufferedImage getScaledImage(String fileName) {
		BufferedImage image = getImage(fileName);
		if (image != null) {
			return computeScaledImage(image, image.getHeight(null));
		} else {
			return null;
		}
	}

	public static BufferedImage getScaledImage(String fileName, int height) {
		BufferedImage image = getImage(fileName);
		if (image != null) {
			return ImageUtil.computeScaledImage(image, height);
		} else {
			return null;
		}
	}

	static public BufferedImage computeScaledImage(Image source, int width,
			int height) {
		BufferedImage scaledImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) scaledImage.getGraphics();
		g2d.drawImage(source, 0, 0, width, height, null);
		return scaledImage;
	}

	static public BufferedImage computeScaledImage(Image source, int height) {
		double factor = (double) height / (double) source.getHeight(null);
		int width = (int) (factor * source.getWidth(null));
		BufferedImage scaledImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) scaledImage.getGraphics();
		g2d.drawImage(source, 0, 0, width, height, null);
		return scaledImage;
	}

	static private BufferedImage computeOverlayedImage(Image under,
			Image above, int width, int height) {
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.drawImage(under, 0, 0, width, height, null);
		g2d.drawImage(above, 0, 0, width, height, null);
		return image;
	}

	static public BufferedImage addExplosion(BufferedImage source,
			boolean isLeft, float explosionPercentage) {
		BufferedImage modifiedSource = source;
		// add backwards movement when hit
		if (explosionPercentage < 0.3f) {
			modifiedSource = addMovement(modifiedSource, !isLeft, 0.6f);
		}
		float density = explosionPercentage * 0.45f;
		GradientWipeFilter filter = new GradientWipeFilter(density, 0.50f,
				false, explodeImage);
		BufferedImage filtered = filter.filter(modifiedSource, null);
		return computeOverlayedImage(explodeImage, filtered, filtered
				.getWidth(), filtered.getHeight());
	}

	static public BufferedImage addCloud(BufferedImage source, boolean isLeft,
			float cloudMovementPercentage) {
		BufferedImage modifiedSource = source;
		BufferedImage cloudImage = isLeft ? cloudRightImage : cloudLeftImage;
		// stop computing when cloud is gone
		if (cloudMovementPercentage == 1.0f) {
			return source;
		}
		// add backwards movement when just fired
		if (cloudMovementPercentage < 0.2f) {
			modifiedSource = addMovement(modifiedSource, !isLeft, 0.4f);
		}
		// first scale to have better performance
		BufferedImage scaledCloudImage = computeScaledImage(cloudImage, source
				.getWidth(), source.getHeight());
		BufferedImage movingScaledCloudImage = addCloudMovement(
				scaledCloudImage, isLeft, cloudMovementPercentage);
		GradientWipeFilter filter = new GradientWipeFilter(0.60f, 0.30f, false,
				movingScaledCloudImage);
		BufferedImage filtered = filter.filter(source, null);
		return computeOverlayedImage(movingScaledCloudImage, filtered, filtered
				.getWidth(), filtered.getHeight());
	}

	static private BufferedImage addCloudMovement(BufferedImage source,
			boolean toTheRightSide, float percentage) {
		// do not go higher than 30 blur cycles, or performance will not be
		// sufficient
		float distance = (toTheRightSide ? -30.0f : 30.0f) * percentage;
		float angle = (toTheRightSide ? 45.0f : -45.0f);
		MotionBlurFilter filter = new MotionBlurFilter(distance, angle, 0.0f,
				0.0f);
		return filter.filter(source, null);
	}

	static public BufferedImage addMovement(BufferedImage source,
			boolean toTheRightSide, float percentage) {
		float distance = (toTheRightSide ? -10.0f : 10.0f) * percentage;
		MotionBlurFilter filter = new MotionBlurFilter(distance, 0.0f, 0.0f,
				0.0f);
		return filter.filter(source, null);
	}

	static private BufferedImage addCrossHair(BufferedImage source) {
		Graphics2D g2d = (Graphics2D) source.getGraphics();
		int radius = 50;
		int diameter = 2 * radius;
		int centerX = source.getWidth() / 2;
		int centerY = source.getHeight() / 2;
		int leftX = centerX - radius;
		int topY = centerY - radius;

		Ellipse2D circle = new Ellipse2D.Double(leftX, topY, diameter, diameter);
		g2d.draw(circle);
		g2d.drawLine(leftX, centerY, leftX + diameter, centerY);
		g2d.drawLine(centerX, topY, centerX, topY + diameter);
		return source;
	}

	static public BufferedImage addLens(BufferedImage source) {
		SphereFilter filter = new SphereFilter(50.0f, 2.0f);
		return addCrossHair(filter.filter(source, null));
	}

	static public BufferedImage addWall(BufferedImage source, int height) {
		int modifiedHeight = height;
		if (height < 1) {
			return source;
		} else if (height > 3) {
			modifiedHeight = 3;
		}
		return computeOverlayedImage(source,
				brickWallImages[modifiedHeight - 1], source.getWidth(), source
						.getHeight());
	}

	static public BufferedImage fade(BufferedImage source, int times) {
		ContrastFilter contrastFilter = new ContrastFilter(1f - times * 0.2f,
				0.5f);
		return contrastFilter.filter(source, null);
	}

	static public BufferedImage superpose(BufferedImage under, BufferedImage above) {
		BufferedImage image = new BufferedImage(under.getWidth(), under.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.drawImage(under, 0, 0, under.getWidth(), under.getHeight(), null);
		g2d.drawImage(above, 0, 0, above.getWidth(), above.getHeight(), null);
		return image;
	}

	static public BufferedImage flipHorizontally(BufferedImage source) {
		FlipFilter filter = new FlipFilter(FlipFilter.FLIP_H);
		return filter.filter(source, null);
	}
}
