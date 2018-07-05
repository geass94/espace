package ge.boxwood.espace.config.utils;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageUtils {
    public byte[] scale(byte[] fileData, int width, int height) {
        ByteArrayInputStream in = new ByteArrayInputStream(fileData);
        try {
            BufferedImage img = ImageIO.read(in);
            if(height == 0) {
                height = (width * img.getHeight())/ img.getWidth();
            }
            if(width == 0) {
                width = (height * img.getWidth())/ img.getHeight();
            }
            int newW = img.getWidth();
            int newH = img.getHeight();
            if (img.getWidth() > width) {
                //scale width to fit
                newW = width;
                //scale height to maintain aspect ratio
                newH = (newW * img.getHeight()) / img.getWidth();
            }

            if (newH > height) {
                //scale height to fit instead
                newH = height;
                //scale width to maintain aspect ratio
                newW = (newH * img.getWidth()) / img.getHeight();
            }
            Image scaledImage = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            BufferedImage imageBuff = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
            imageBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0,0,0, 0), null);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            ImageIO.write(imageBuff, "jpeg", buffer);

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("IOException in scale");
        }
    }
}