package com.akijoey.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ImageUtil {

    public static HashMap<Integer, BufferedImage> from = new HashMap<>();
    public static HashMap<Integer, BufferedImage> to = new HashMap<>();

    public static BufferedImage content = readBackground("content");
    public static BufferedImage battle = readBackground("battle");
    public static BufferedImage blank = readBackground("blank");

    static {
        BufferedImage image;
        for (int i = 1000;i < 2000;i++) {
            if ((image = readImage("keyframe/from/" + i)) != null) {
                from.put(i, image);
            }
            if ((image = readImage("keyframe/to/" + i)) != null) {
                to.put(i, image);
            }
        }
    }

    public static BufferedImage readImage(String relative) {
        String path = "image/" + relative + ".png";
        InputStream image = ImageUtil.class.getClassLoader().getResourceAsStream(path);
        if (image != null) {
            try {
                return ImageIO.read(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static BufferedImage readBackground(String name) {
        return readImage("background/" + name);
    }

    public static BufferedImage readPlayer(String name) {
        return readImage("player/" + name);
    }

}
