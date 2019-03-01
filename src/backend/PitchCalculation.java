/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import sounds.Waveform;
import vspeech.Analyzer;
import vspeech.NewView;
import vspeech.WaveLoader;

/**
 * Pitch Calculation from Praatcon Input 'audio' object of AudioRead.java Output
 * is 'data_prep' used in EndPointDetection.java and'TempRefdata_1',
 * 'TempRefdata_2' ,'w' and 'win_size' used in PitchCorrectOrder.java
 *
 * @author Laxita
 */
public class PitchCalculation {

    public double[] data_raw;
    public double[] data_prep;
    public int Filelen;
    public ArrayList<Double> TempRefdata_1;
    public ArrayList<Double> TempRefdata_2;
    public double[] w;
    public double win_size;
    public long wshift_samples;
    public double pitch_f;
    public int lpc_ord = 12;
    File file;

    public void pitchCalc() {

        try {
            double Fs = 10000;

            if (WaveLoader.soundFile != null) {
                file = WaveLoader.soundFile;
            } else {
                file = new File("aai.wav");
            }
            //System.out.println("file= "+file.getAbsolutePath());
            Waveform getY = new Waveform();
            Double[] data_temp = getY.Loadmatrix(file);

            Filelen = data_temp.length;
            demo_fir1 filter = new demo_fir1();

            ArrayList<Double> data_raw_temp = filter.fir1(data_temp);

            double[] data_raw_1 = new double[data_raw_temp.size()];
            data_raw = new double[data_raw_1.length - 100];
            int k = 0;
            for (int i = 0; i < data_raw_1.length; i++) {
                data_raw_1[i] = data_raw_temp.get(i);
                if (i >= 100) {
                    data_raw[k++] = data_raw_1[i];
                }
            }
            //Invoking Praatcon.exe

//        final Class<?> referenceClass = Spectrogram.class;
//        final URL url = referenceClass.getProtectionDomain().getCodeSource().getLocation();
//        final File jarPath = new File(url.toURI()).getParentFile();
//
//      //  Files.deleteIfExists(Paths.get("pitch.txt"));
//        String commands[] = {"praatcon.exe", "extract_pitch.praat", file.getName(), "pitch.txt", jarPath.getAbsolutePath() + "\\"};
//
////        String commands[] = {"praatcon.exe", "extract_pitch.praat", file.getName(), "pitch.txt", ".\\"};
//        Process pb = new ProcessBuilder(commands).start();
//        //pb.waitFor();
            //final Class<?> referenceClass = NewView.class;
           // final URL url = referenceClass.getProtectionDomain().getCodeSource().getLocation();
           // final File jarPath = new File(url.toURI()).getParentFile();

            BufferedReader br = new BufferedReader(new FileReader(Analyzer.pitchFile));
//  BufferedReader br = new BufferedReader(new FileReader(Spectrogram.pitchFile));

            String line = "";
            TempRefdata_1 = new ArrayList<Double>();
            TempRefdata_2 = new ArrayList<Double>();

            StringBuilder sb = new StringBuilder();
            int yy = 0;
            pitch_f = 0;
            ArrayList<Double> t_p_sample = new ArrayList<Double>();

//Reading values of pitch.txt
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                TempRefdata_1.add(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                TempRefdata_2.add(Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length())));
                pitch_f += TempRefdata_2.get(yy);
                t_p_sample.add(Fs * TempRefdata_1.get(yy++));

            }
            int data_length = data_raw.length;
            int win_scale = 2;
            pitch_f = pitch_f / TempRefdata_2.size();

            int wshift_time_ms = 5;

            data_prep = new double[data_raw.length];

            for (int i = 0; i < data_length - 1; i++) {
                data_prep[i] = data_raw[i + 1];
                data_prep[i] = data_prep[i] - data_raw[i];

            }

            win_size = Math.round(win_scale * Fs / pitch_f);
            if (win_size != 2 * Math.ceil(win_size / 2)) {
                win_size = (int) (2 * Math.ceil(win_size / 2));
            }

            w = window((int) win_size);
            wshift_samples = Math.round(wshift_time_ms * Fs * 0.001);
        } catch (IOException ex) {
            Logger.getLogger(PitchCalculation.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    // Returns 'w' as N-points hamming window in a column vector
    public double[] window(int nSamples) {
        // generate nSamples window function values 
        // for index values 0 .. nSamples - 1 
        int m = nSamples / 2;
        double r;
        double[] w = new double[nSamples];
        // Hamming window 
        r = 3.14 / m;
        for (int n = -m; n < m; n++) {
            w[m + n] = 0.54f + 0.46f * Math.cos(n * r);
        }
        return w;
    }
}
