package cz.trailsthroughshadows.api.images;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageScaler {

    public static Resource scaleImage(Resource resource, int width) throws IOException {
        return scaleImage(resource, width, width);
    }

    public static Resource scaleImage(Resource resource, int width, int height) throws IOException {

        BufferedImage img = ImageIO.read(resource.getInputStream());
        BufferedImage scaledImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        scaledImg.getGraphics().drawImage(img, 0, 0, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(scaledImg, "png", baos);
        return new ByteArrayResource(baos.toByteArray());
    }

}
