/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sounds;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author abc
 */
public class Waveform {

    /**
     * For converting Audio file into vector
     */
        private static final double MAX_16_BIT = Short.MAX_VALUE;     // 32,767

    public  Double[] Recordmatrix(File file) {
         byte[] data = readByte(file.getAbsolutePath());
        Double[] d = new Double[34000];
        int x=0;
        for (int i = 0; i < 34000; i++) {
            d[x] = ((short) (((data[2*i+1] & 0xFF) << 8) + (data[2*i] & 0xFF))) / ((double) MAX_16_BIT);
            x++;
        }
        return d;
    }
      public  Double[] Loadmatrix(File file) {
               byte[] data = readByte(file.getAbsolutePath());
        Double[] d = new Double[data.length/2];
        int x=0;
        for (int i = 0; i < d.length; i++) {

            d[x] = ((short) (((data[2*i+1] & 0xFF) << 8) + (data[2*i] & 0xFF))) / ((double) MAX_16_BIT);

            x++;
        }
        return d;
    }
     private static byte[] readByte(String filename) {
        byte[] data = null;
        AudioInputStream ais = null;
        try {

            // try to read from file
            File file = new File(filename);
            if (file.exists()) {
                ais = AudioSystem.getAudioInputStream(file);
                int bytesToRead = ais.available();
                data = new byte[bytesToRead];
                int bytesRead = ais.read(data);
                if (bytesToRead != bytesRead) throw new RuntimeException("read only " + bytesRead + " of " + bytesToRead + " bytes"); 
            }

        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Could not read " + filename);
        }

        catch (UnsupportedAudioFileException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(filename + " in unsupported audio format");
        }

        return data;
    }
}
