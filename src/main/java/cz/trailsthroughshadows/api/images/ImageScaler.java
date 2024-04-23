package cz.trailsthroughshadows.api.images;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageScaler {

    public static Resource scaleImage(Resource resource, int width, int height, int radius) throws IOException {

        BufferedImage img = ImageIO.read(resource.getInputStream());
        BufferedImage scaledImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        scaledImg.getGraphics().drawImage(img, 0, 0, width, height, null);

        if (radius > 0) {
            scaledImg = ImageUtils.makeRoundedCorner(scaledImg, radius);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(scaledImg, "png", baos);
        return new ByteArrayResource(baos.toByteArray());
    }

    private static class ImageUtils {

        public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
            int w = image.getWidth();
            int h = image.getHeight();

            if (cornerRadius > Math.min(w, h) / 2) {
                cornerRadius = Math.min(w, h) / 2;
            }

            BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            // Draw the image
            output.getGraphics().drawImage(image, 0, 0, null);

            // Draw the corners
            for (int i = 0; i < cornerRadius; i++) {
                for (int j = 0; j < cornerRadius; j++) {
                    if (Math.pow(i - cornerRadius, 2) + Math.pow(j - cornerRadius, 2) >= Math.pow(cornerRadius, 2)) {
                        output.setRGB(i, j, 0);
                        output.setRGB(w - i - 1, j, 0);
                        output.setRGB(i, h - j - 1, 0);
                        output.setRGB(w - i - 1, h - j - 1, 0);
                    }
                }
            }

            return output;
        }
    }

}
