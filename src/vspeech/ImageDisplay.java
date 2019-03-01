/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vspeech;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageDisplay {

    // Creating Spectrogam and areagram by reading matrix value 
    public BufferedImage img(double[][] data_img, int scale, int numberYTicks, double yRange, String yTitle) {
        int rows = data_img.length;
        int columns = data_img[0].length;
      

        BufferedImage measurementImage = new BufferedImage(columns, rows, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) measurementImage.getGraphics();
        Font f = new Font("Monospaced", Font.PLAIN, 12);
        g2d.setFont(f);

        BufferedImage image3 = new BufferedImage(columns, rows, BufferedImage.TYPE_BYTE_GRAY);
        g2d = (Graphics2D) image3.getGraphics();
        g2d.setFont(f);

        for (int i = 0; i < image3.getWidth(); i++) {
            for (int j = 0; j < image3.getHeight(); j++) {
                if (scale == 80) {
                    int value = 255;
                    value = ((value << 16) | (value << 8) | value);
                    image3.setRGB(i, j, value);
                }

            }
        }

        for (int i = 0, x = 0; i < rows; i++, x++) {
            for (int j = 0; j < columns; j++) {
                if (scale == 80) {                              // for spectrogram range is between 0-80
                    if (data_img[i][j] >= 80) {
                        data_img[i][j] = 80;
                    } else if (data_img[i][j] <= 0) {
                        data_img[i][j] = 0;
                    }
                    int value = (int) (255 * (data_img[i][j] / 80));
                    value = (255 - value);
                    value = ((value << 16) | (value << 8) | value);
                    image3.setRGB(j, rows - i - 1, value);
                } else {
                    if (data_img[i][j] >= 7) {          // for areagram range is between 0-7
                        data_img[i][j] = 7;
                    } else if (data_img[i][j] <= 0) {
                        data_img[i][j] = 0;
                    }
                    int value = (int) (255 * (data_img[i][j] / 7));
                    value = ((value << 16) | (value << 8) | value);
                    image3.setRGB(j, i, value);

                }

            }
        }
        return image3;
    }

}
