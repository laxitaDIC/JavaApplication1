/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vspeech;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 *
 * @author abc
 */
public class ColorConstants {
    public static String menuBar;
    public static String border;
    public static String label = ColorCombination.WHITE;
    public static String background;
    public static String title;
    public static String bottom;
    public String component[] = {"", menuBar, border, background};
    
    // Reading default coloring schemes for different components of application from settings.txt file

    public ColorConstants() throws IOException {
        File file = new File("settings.txt");
        InputStream in = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        String content = "";   //Prints the string content read from input stream
        while ((line = reader.readLine()) != null) {
            content += line;
        }
        int i = 0;
        StringTokenizer section = new StringTokenizer(content, "=");
        while (section.hasMoreTokens()) {
            String temp = section.nextToken();
            String cont = temp.substring(0, 7);
            if (i != 0) {
                if (i == 1) {
                    menuBar = cont;
                }
                if (i == 2) {
                    border = cont;
                }
                if (i == 3) {
                    background = cont;
                }
                if (i == 4) {
                    title = cont;
                }
                if (i == 5) {
                    bottom = cont;
                }
            }
            i++;
        }
        reader.close();

    }
}
