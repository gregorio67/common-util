package cmn.util.image;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.mortennobel.imagescaling.ResampleOp;

import cmn.util.exception.UtilException;

public class ImageUtil {

    public static void write(BufferedImage image, float quality, OutputStream out) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpeg");
        if (!writers.hasNext())
            throw new IllegalStateException("No writers found");

        ImageWriter writer = writers.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(out);
        writer.setOutput(ios);
        ImageWriteParam param = writer.getDefaultWriteParam();

        if (quality >= 0) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }
        writer.write(null, new IIOImage(image, null, null), param);
    }

    public static BufferedImage read(byte[] bytes) {
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch(IOException e) {
            throw new UtilException(e.getMessage(), e);
        }
    }

    public static BufferedImage read(File file) {
        try {
            return ImageIO.read(new FileInputStream(file));
        } catch(IOException e) {
            throw new UtilException(e.getMessage(), e);
        }
    }

    public static byte[] getBytes(BufferedImage image, float quality) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(50000);
            write(image, quality, out);
            return out.toByteArray();
        } catch(IOException e) {
            throw new UtilException(e.getMessage(), e);
        }
    }

    public static BufferedImage compress(BufferedImage image, float quality) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(50000);
            write(image, quality, out);
            return ImageIO.read(new ByteArrayInputStream(out.toByteArray()));
        } catch(IOException e) {
            throw new UtilException(e.getMessage(), e);
        }
    }

	public static BufferedImage scale(BufferedImage src, int destWidth, int destHeight, int colorMode)
	{
        BufferedImage dest = new BufferedImage(destWidth, destHeight, colorMode);
		Graphics2D g = dest.createGraphics();
		AffineTransform at = AffineTransform.getScaleInstance(
		(double)destWidth/src.getWidth(),
		(double)destHeight/src.getHeight());
		g.drawRenderedImage(src,at);
		return dest;
	}

	public static byte[] resize(byte[] imageBytes, int targetWidth, int targetHeight, String imgType) throws Exception {

		InputStream in = new ByteArrayInputStream(imageBytes);
		BufferedImage imageConverter = ImageIO.read(in);

		if (targetHeight == 0) {
			targetHeight += imageConverter.getHeight() / imageConverter.getWidth();

		}
		ResampleOp resampleOp = new ResampleOp(targetWidth, targetHeight);
		BufferedImage rescaleImage = resampleOp.filter(imageConverter, null);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(rescaleImage, imgType, baos);
		baos.flush();

		byte[] rescaleImageByte = baos.toByteArray();

		baos.close();
		return rescaleImageByte;
	}

}
