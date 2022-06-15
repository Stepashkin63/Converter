package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {

    private int maxWidth, maxHeight;
    private double maxRatio;
    private TextColorSchema schema;

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {

        BufferedImage img = ImageIO.read(new URL(url));

        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();

        if (this.maxRatio != 0) {
            double currentRatio = this.getCurrentRatio(imgHeight, imgWidth);
            this.isBadRatio(currentRatio);
        }

        int newWidth = imgWidth;
        int newHeight = imgHeight;

        if (this.isSetMaxHeight() || this.isSetMaxWidth()) {
            if (imgHeight > this.maxHeight || imgWidth > this.maxWidth) {
                double coefficient = this.getCoefficient(imgHeight, imgWidth);
                newHeight = this.getNewHeight(imgHeight, coefficient);
                newWidth = this.getNewWidth(imgWidth, coefficient);
            }
        }

        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);

        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D graphics = bwImg.createGraphics();

        graphics.drawImage(scaledImage, 0, 0, null);

        WritableRaster bwRaster = bwImg.getRaster();

        imgWidth = bwImg.getWidth();
        imgHeight = bwImg.getHeight();

        int[] colorIntensity = new int[3];

        if (this.schema == null) {
            this.schema = new Schema();
        }

        StringBuilder sb = new StringBuilder();

        for (int h = 0; h < imgHeight; h++) {
            for (int w = 0; w < imgWidth; w++) {
                int color = bwRaster.getPixel(w, h, colorIntensity)[0];
                char c = this.schema.convert(color);
                sb.append(c);
                sb.append(c);
            }
            sb.append("\n");
        }
        String res = sb.toString();
        return res;
    }

    @Override
    public void setMaxWidth(int width) {
        this.maxWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }

    public boolean isSetMaxHeight() {
        if (this.maxHeight != 0) {
            return true;
        }
        return false;
    }

    public boolean isSetMaxWidth() {
        if (this.maxWidth != 0) {
            return true;
        }
        return false;
    }

    public double getCoefficient(int imgHeight, int imgWidth) {

        double coefficient = 1;

        if (imgHeight > imgWidth) {
            coefficient = (double) imgHeight / this.maxHeight;
        } else if (imgWidth > imgHeight || imgWidth == imgHeight) {
            coefficient = (double) imgWidth / this.maxWidth;
        }
        return coefficient;
    }

    public int getNewHeight(int imgHeight, double coefficient) {
        if (imgHeight != 0 && coefficient != 0) {
            int newHeight = (int) (imgHeight / coefficient);
            return newHeight;
        } else {
            return 0;
        }

    }

    public int getNewWidth(int imgWidth, double coefficient) {
        if (imgWidth != 0 && coefficient != 0) {
            int newWidth = (int) (imgWidth / coefficient);
            return newWidth;
        } else {
            return 0;
        }
    }

    public double getCurrentRatio(int imgHeight, int imgWidth) {
        if (imgHeight != 0 && imgWidth != 0) {
            double currentRatio = (double) imgWidth / imgHeight;
            return currentRatio;
        } else {
            return 0;
        }
    }

    private void isBadRatio(double currentRatio) throws BadImageSizeException {
        if (currentRatio > this.maxRatio) {
            throw new BadImageSizeException(currentRatio, this.maxRatio);
        }
    }
}