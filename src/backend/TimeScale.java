/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import com.sun.org.apache.xerces.internal.xs.XSImplementation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import sounds.Waveform;
import vspeech.WaveLoader;

/**
 *
 * @author Laxita
 */
public class TimeScale {

    private File file;
    public ArrayList<Double>[] Y = new ArrayList[5];

    public void audioTime() throws IOException {
        int F[] = {1,2, 5, 10, 20};
        if (WaveLoader.soundFile != null) {
            file = WaveLoader.soundFile;
        } else {
            file = new File("aai.wav");
        }
        //System.out.println("file= "+file.getAbsolutePath());
        Waveform getY = new Waveform();
        Double[] data_temp = getY.Loadmatrix(file);

        for (int i = 0; i < F.length; i++) {
            Y[i] = audioScale(data_temp, 1.0 / F[i]);
            System.out.println("i= " + i);
        }
    }

    public ArrayList<Double> audioScale(Double X[], double F) throws FileNotFoundException, IOException {
        double W = 200;
        double Wov = W / 2;
        double Kmax = 2 * W;
        double Wsim = Wov;
        double xdecim = 8;
        double kdecim = 2;
        double Ss = W - Wov;
        int xpts = X.length;
        double ypts = Math.round(xpts / F);
        double Y[] = new double[(int) ypts];
        ArrayList<Double> xfwin = new ArrayList<Double>();
        for (int i = 0; i < Wov; i++) {
            xfwin.add(i / (Wov + 1.0));
        }
        ArrayList<Double> ovix = new ArrayList<Double>();
        for (int i = (int) (1 - Wov); i != 0; i++) {
            ovix.add(i * 1.0);
        }

        ArrayList<Double> newix = new ArrayList<Double>();
        for (int i = 0; i < W - Wov; i++) {
            newix.add(i * 1.0);
        }

        ArrayList<Double> simix = new ArrayList<Double>();
        for (int i = 1; i < Wsim; i = (int) (i + xdecim)) {
            simix.add((i - Wsim) * 1.0);
        }

        double padX[] = new double[(int) Wsim + X.length + (int) (Kmax + W - Wov)];
        int ind = 0;
        for (; ind < Wsim; ind++) {
            padX[ind] = 0;
        }
        for (int i = 0; ind < X.length; ind++, i++) {
            padX[ind] = X[i];
        }
        /*  BufferedReader br = new BufferedReader(new FileReader("padX.txt"));
        ArrayList<Double> TempRefdata = new ArrayList<Double>();
        String line = br.readLine();
        String[] result_time = line.split("\\s");
        for (int x = 0; x < result_time.length; x++) {
            if (!result_time[x].equals("")) {
                TempRefdata.add(Double.parseDouble(result_time[x]));
            }
        }

        for (int i = 0; i < TempRefdata.size(); i++) {
            padX[i] = TempRefdata.get(i);
        }*/

        for (int i = 0; i < Wsim; i++) {
            Y[i] = X[i];
        }
        int xabs = 0;
        int lastxpos = 0;
        int lastypos = 0;
        int km = 0;
        for (int ypos = (int) Wsim; ypos < (ypts - W); ypos = (int) (ypos + Ss)) {
            double xpos = F * ypos;
            double kmpred = km + ((xpos - lastxpos) - (ypos - lastypos));
            lastxpos = (int) xpos;
            lastypos = (int) xpos;
            ArrayList<Double> ysim = new ArrayList<Double>();

            if (kmpred <= Kmax && kmpred >= 0) {
                km = (int) kmpred;
            } else {
                for (int i = 0; i < simix.size(); i++) {
                    ysim.add(Y[simix.get(i).intValue() + ypos]);
                }

                double rxy[] = new double[(int) Kmax + 1];
                double rxx[] = new double[(int) Kmax + 1];
                int Kmin = 0;
                double[] xsim = new double[simix.size()];

                for (int p = -1, k = Kmin; k < Kmax; k = (int) (k + kdecim), p++) {

                    for (int i = 0; i < simix.size(); i++) {
                        int vv = (int) Wsim + (int) xpos + k + simix.get(i).intValue();
                        xsim[i] = (padX[vv - 1]);
                    }

                    double sum = 0;
                    for (int u = 0; u < xsim.length; u++) {
                        sum = sum + Math.pow(xsim[u], 2);
                    }

                    double aa = Math.sqrt(sum);
                    rxx[k] = aa;
                    //System.out.println("aa= "+aa);
                    double vsum = 0;
                    for (int e = 0; e < ysim.size(); e++) {
                        vsum = vsum + xsim[e] * ysim.get(e);
                    }

                    rxy[k] = vsum;
                }
                // System.exit(0);
                ArrayList<Double> Rxy = new ArrayList<Double>();
                for (int i = 0; i < rxx.length; i++) {
                    double temp = rxy[i] / (double) rxx[i];
                    if (Double.isNaN(temp)) {
                        temp = 0.0;
                    }
                    Rxy.add(temp);
                }

                Double max = Rxy.get(0);
                for (int p = 1; p < Rxy.size(); p++) {
                    if (max < Rxy.get(p)) {
                        max = Rxy.get(p);
                        km = p;
                    }
                }
                /*         km = 0;
                for (int p = 0; p < Rxy.size(); p++) {
                    if (max == Rxy.get(p)) {
                        km = p;
                        break;
                    }
                }*/
                
            }
            xabs = (int) (xpos + km - 2);

            for (int i = 0; i < ovix.size(); i++) {

                Y[(int) ypos + ovix.get(i).intValue()] = ((1 - xfwin.get(i).intValue()) * Y[(int) ypos + ovix.get(i).intValue()]) + (xfwin.get(i).intValue() * padX[((int) Wsim + (int) xabs + ovix.get(i).intValue())]);
            }

            for (int i = 0; i < newix.size(); i++) {
                int val = newix.get(i).intValue();
                Y[(int) ypos + val] = padX[((int) Wsim + (int) xabs + val)];
            }

        }
        ArrayList<Double> YTemp = new ArrayList<Double>();
        for (int i = 0; i < Y.length; i++) {
            YTemp.add(Y[i]);
        }

        return YTemp;

    }
    

}
