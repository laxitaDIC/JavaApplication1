/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Laxita
 */
public class Spectrogram {

    public double[][] y2;
    public double[][] b1;
    private double[][] fft;

    public void specgram(AreaAnimation areaAnim) {
        try {
            double[] x = new double[areaAnim.areaSpline.areaVal.vt_area.value.calcArea.pitchOrder.pointDetect.pitch.Filelen];
            for (int i = areaAnim.areaSpline.areaVal.vt_area.value.calcArea.pitchOrder.pointDetect.N1 - 1; i < areaAnim.areaSpline.areaVal.vt_area.value.calcArea.pitchOrder.pointDetect.N2 - 1; i++) {
                x[i] = areaAnim.areaSpline.areaVal.vt_area.value.calcArea.pitchOrder.pointDetect.pitch.data_raw[i];
            }
            int Fs = 10000;
            int wsize = Math.round(29 * Fs / 1000);
            double noverlap = Math.round(0.9 * wsize);
            double maxstftm = 10;
            double int112 = 0.0001;
            int nfft = 512;
            double[] window = window(wsize);
            double eps = 2.22044604925031e-16;

            /// Spectrogram Display
            int nx = x.length;
            int nwind = window.length;
            int ncol = (int) Math.round((nx - noverlap) / (nwind - noverlap));
            ArrayList<Double> colindex = new ArrayList<Double>();
            ArrayList<Double> rowindex = new ArrayList<Double>();
            int p = 0;
            while (p < ncol) {
                colindex.add(1 + p * (nwind - noverlap));
                p++;
            }
            p = 1;
            while (p < nwind) {
                rowindex.add(p * 1.0);
                p++;
            }
            int use_chirp = 0;
            double y[][] = new double[nwind][ncol];
            p = 0;
            double row[][] = new double[nwind][ncol];
            for (int i = 0; i < nwind; i++) {
                for (int j = 0; j < ncol; j++) {
                    if (p == rowindex.size()) {
                        break;
                    }
                    row[i][j] = rowindex.get(p) - 1;
                }
                p++;
            }

            double column[][] = new double[nwind][ncol];
            for (int i = 0; i < nwind; i++) {
                p = 0;
                for (int j = 0; j < ncol; j++) {
                    if (p == colindex.size()) {
                        break;
                    }
                    column[i][j] = colindex.get(p++) - 1;
                }
            }
            p = 0;
            for (int i = 0; i < nwind; i++) {
                for (int j = 0; j < ncol; j++) {
                    int a = (int) row[i][j];
                    int b = (int) column[i][j];
                    if ((a + b) >= x.length) {
                        break;
                    }
                    //System.out.println("a= "+a+" b= "+b);
                    //double xx = x[a + b];
                    y[i][j] = x[(int) row[i][j] + (int) column[i][j]];
                }
            }

            p = 0;
            for (int i = 0; i < nwind; i++) {
                for (int j = 0; j < ncol; j++) {
                    y[i][j] = window[p] * y[i][j];
                }
                p++;
            }

            double[] tmp = new double[y.length];
            double y1[][] = new double[nwind][ncol];
           double y11[][] = new double[nwind][ncol];

            for (int i = 0; i < y[0].length; i++) {
                tmp = new double[y.length];
                for (int j = 0; j < y.length; j++) {
                    tmp[j] = y[j][i];
                }
                double[] tmp1 = fft(tmp, nfft);
                for (int k = 0; k < y.length; k++) {
                    y1[k][i] = fft[k][0];
                    y11[k][i]=fft[k][1];
                }

            }
            double[] select = new double[(nfft / 2) + 1];
            for (int k = 0; k < select.length; k++) {
                select[k] = k;
            }
            y2 = new double[select.length][ncol];
            double[][] y21 = new double[select.length][ncol];
            for (int i = 0; i < y2.length; i++) {
                for (int j = 0; j < ncol; j++) {
                    y2[i][j] = y1[i][j];
                    y21[i][j] = y11[i][j];
                }
            }

       /*     Scanner input = new Scanner(new File("dd.txt"));
            int rows = 0;
            while (input.hasNextLine()) {
                ++rows;
                Scanner colReader = new Scanner(input.nextLine());
            }
            int columns = 0;
            Scanner input_new = new Scanner(new File("dd.txt"));

            String[] length = input_new.nextLine().trim().split("\\s+");
            for (int i = 0; i < length.length; i++) {
                columns++;
            }
            String filename;
            Scanner input_new_temp = new Scanner(new File("dd.txt"));
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < columns; ++j) {
                    if (input_new_temp.hasNextDouble()) {
                        y2[i][j] = input_new_temp.nextDouble();
                    }
                }
            }*/
            b1 = new double[select.length][ncol];

            for (int i = 0; i < y2.length; i++) {
                for (int j = 0; j < ncol; j++) {
                    b1[i][j] =  20*Math.log10((Math.sqrt(Math.pow(y2[i][j],2)+Math.pow(y21[i][j],2))+eps)/int112);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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

    public double[] fft(double[] data, int length) {
        double[] F = new double[length];

         fft = new double[length][2];

        int k = 0;
        for (int i = 1; i < length; i++) {
            for (int j = 1; j < data.length; j++) {
                double temp_y = (-2 * Math.PI) * (i - 1) * (j - 1) * Math.cos(0) / length;
                fft[k][0] += Math.cos(temp_y) * data[j];
                fft[k][1] += Math.sin(temp_y) * data[j];
            }
            F[k] = (fft[k][0]*fft[k][0]) + (fft[k][1] * fft[k][1]);
            //     System.out.println("  "+F[k]);
            // F[k] = (fft[k][0] * fft[k][0]) + (fft[k][1] * fft[k][1]);
            k++;
        }
        //   System.exit(0);

        return F;

    }

}
