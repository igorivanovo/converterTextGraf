package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class GraphicsConverter implements TextGraphicsConverter {
    double maxRatio;
    int width;
    int height;
    int newWidth;
    int newHeight;
    TextColorSchema schema;

    public GraphicsConverter(double maxRatio, int width, int height, int newWidth, int newHeight, TextColorSchema schema) {
        this.maxRatio = maxRatio;
        this.width = width;
        this.height = height;
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        this.schema = schema;
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));
        int width0 = img.getWidth();
        int height0 = img.getHeight();
        double ratio = width0 / height0;
        if (ratio > maxRatio) {
            throw new BadImageSizeException(ratio, maxRatio);
        }
        if (width == 0 | height == 0) {
            if (width == 0 & height == 0) {
                newWidth = width0;
                newHeight = height0;
            } else if (width == 0) {
                if (height0 - height > 0) {
                    newHeight = height0;
                    newWidth = (int) Math.floor(width0 * height / height0);
                } else {
                    newWidth = width0;
                    newHeight = height0;
                }

            } else {
                if (height == 0) {
                    if (width0 - width > 0) {
                        newWidth = width0;
                        newHeight = (int) Math.floor(height0 * width / width0);
                    }
                } else {
                    newWidth = width0;
                    newHeight = height0;
                }

            }

        } else if ((width < width0) & (height < height0)) {
            if (width0 - width <= height0 - height) {
                if ((int) Math.floor(height0 * width / width0) <= height) {
                    newHeight = (int) Math.floor(height0 * width / width0);
                    newWidth = width;
                } else if ((int) Math.floor(width0 * height / height0) <= width) {
                    newWidth = (int) Math.floor(width0 * height / height0);
                    newHeight = height;
                } else {
                    newWidth = width;
                    newHeight = height;
                }
            }

        } else if (height < height0) {
            newWidth = (int) Math.floor(width0 * height0 / height);
            newHeight = height;
        } else if (width < width0) {
            newHeight = (int) Math.floor(height0 * width0 / width);
            newWidth = width;

        } else {
            newWidth = width0;
            newHeight = height0;
        }


        if (schema == null) {
            schema = new ColorSchema();
        }
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        ImageIO.write(bwImg, "png", new File("out.png"));
        WritableRaster bwRaster = bwImg.getRaster();

        int arr[][] = new int[3][];
        StringBuilder text0 = new StringBuilder();
        for (int h = 0; h < newHeight - 1; h++) {
            for (int w = 0; w < newWidth - 1; w++) {
                int color = bwRaster.getPixel(w, h, arr[0])[0];
                char c = schema.convert(color);
                text0.append(c);
                text0.append(c);
            }
            text0.append("\n");
        }
        String text = text0.toString();
        return text;
    }

    @Override
    public void setMaxWidth(int width) {
        this.width = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.height = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}