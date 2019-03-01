package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javafx.scene.control.ProgressIndicator;

/**
 * Calculate the area values only at these selected frames Input from 'calcArea'
 * an object of CalculateArea.java Output matrix 'a_eng' is used in VTArea.java
 *
 * @author Laxita
 */
public class ValueSelectedFrame {

    public double a_eng[][];
    public CalculateArea calcArea;
    public FFT fftCalc;
    public Levinson levi;
    private double ii;

    /**
     * @param args the command line arguments
     */
    public void valueSelect() throws IOException, InterruptedException, Exception {
        calcArea = new CalculateArea();
        calcArea.Area();
        fftCalc = new FFT();
        levi = new Levinson();

        int lpc_ord = 12;
        a_eng = new double[calcArea.sel_frames.size()][12];
        /*for (int j = 0; j < calcArea.sel_frames.size(); j++) {
            for (int i = 0; i < 12; i++) {

                a_eng[j][i] = 0.0;
            }
        }*/
        //System.out.println(""+calcArea.sel_frames);
        double[] ww = new double[calcArea.sel_frames.size()];
        double r[] = new double[lpc_ord + 1];
        double s[] = new double[lpc_ord + 1];
        double kk[] = new double[lpc_ord];
        double acr[] = new double[2 * lpc_ord + 1];
        // System.out.println("sel= "+calcArea.sel_frames+"   \n size=  "+calcArea.sel_frames.size());
        //System.exit(0);
      //  SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
        /*BufferedReader br = new BufferedReader(new FileReader("data_prep.txt"));
        StringBuilder sb = new StringBuilder();
        ArrayList<Double> b_temp = new ArrayList<Double>();
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
            b_temp.add(Double.parseDouble(line));
        }
        for (int i = 0; i < calcArea.pitchOrder.pointDetect.pitch.data_prep.length; i++) {
            calcArea.pitchOrder.pointDetect.pitch.data_prep[i] = b_temp.get(i);
        }*/
        DecimalFormat df = new DecimalFormat("#.####");

        for (int j = 0; j < calcArea.sel_frames.size(); j++) {
           // String current_time_str = time_formatter.format(System.currentTimeMillis());
          //  System.out.println("Begin= " + current_time_str);

            int k = 0;
            ArrayList<Double> x = new ArrayList<Double>();

            for (double i = calcArea.sel_frames.get(j); i < (calcArea.sel_frames.get(j) + calcArea.pitchOrder.pointDetect.pitch.win_size); i++, k++) {
                ww[k] = Double.parseDouble(df.format(calcArea.pitchOrder.pointDetect.pitch.data_prep[(int) i]));
                if (k < calcArea.pitchOrder.pointDetect.pitch.w.length) {
                    double tt = (Double.parseDouble(df.format(calcArea.pitchOrder.pointDetect.pitch.w[k])) * ww[k]);
                    x.add(Double.parseDouble(df.format(tt)));
                }

            }

            int power = 2 * nextPowerOf2(x.size());
          //  String current_time_str11 = time_formatter.format(System.currentTimeMillis());
          //  System.out.println("Next= " + current_time_str11);
            double c[] = fftCalc.compute(x, power);
            int f = 0;
           // String current_time_str14 = time_formatter.format(System.currentTimeMillis());
          //  System.out.println("Next= " + current_time_str14);

            for (int e = c.length - lpc_ord; e < c.length; e++, f++) {
                acr[f] = c[e];
            }
            for (int e = 0; e < (lpc_ord + 1); e++, f++) {
                acr[f] = c[e];
            }
            for (int m = 0, p = lpc_ord; p < (2 * lpc_ord + 1); m++, p++) {
                r[m] = acr[p];
                //  r[m] = Double.parseDouble(df.format(r[m]));

            }

            // String current_time_str111 = time_formatter.format(System.currentTimeMillis());
            // System.out.println("Next= " + current_time_str111);
            if (r[0] < 0.0001) {
                for (int m = 0; m < lpc_ord; m++) {
                    kk[m] = 0;
                }
            } else {
                double[] matrc = levi.levinson(r);

                for (int m = 0; m < lpc_ord - 1; m++) {
                    kk[m] = Double.parseDouble(df.format(matrc[m + 1]));
                  
                }
                kk[kk.length - 1] = Double.parseDouble(df.format(matrc[matrc.length - 1]));
            }

         //   String current_time_str1111 = time_formatter.format(System.currentTimeMillis());
          //  System.out.println("Next= " + current_time_str1111);

            int kk_count = 0;
            for (int m = 0; m < kk.length; m++) {
                if (kk[m] != 0) {
                    kk_count = 1;
                    break;
                }
            }
            if (kk_count == 0) {
                s[lpc_ord] = 0;
            } else {
                s[lpc_ord] = 1;
            }

            for (int m = lpc_ord - 1; m >= 0; m--) {
                double a = 1 + kk[m];
                double b = 1 - kk[m];
                double d = (a / b);
                s[m] = d * s[m + 1];
                // System.out.println("d= "+s[m]);
                /*    if (s[m] < 1) {
                    s[m] = 0;
                }*/
            }

            for (int m = 0; m < lpc_ord; m++) {
                if (s[m] < 0) {
                    a_eng[j][m] = 0;
                } else {
                    a_eng[j][m] = s[m];
                }
            }
         //   String current_time_str1 = time_formatter.format(System.currentTimeMillis());
         //   System.out.println("End= " + current_time_str1);

        }

        /*  br = new BufferedReader(new FileReader("a_en.txt"));
        String line_jaw;
        ArrayList<Double> Temp_upper_x = new ArrayList<Double>();

        while ((line_jaw = br.readLine()) != null) {
            String[] result_mat = line_jaw.split("\\s");
            for (int x = 0; x < result_mat.length; x++) {
                if (!result_mat[x].equals("")) {
                    Temp_upper_x.add(Double.parseDouble(result_mat[x]));
                }
            }

        }
        int pp = 0;
        for (int x = 0; x < a_eng.length; x++) {
            for (int y = 0; y < a_eng[0].length; y++) {
                a_eng[x][y] = Temp_upper_x.get(pp++);
            }
        }*/
     //   String current_time_str = time_formatter.format(System.currentTimeMillis());
     //   System.out.println("Fourth Last= " + current_time_str);

    }

    public int nextPowerOf2(final int a) {
        int b = 1;
        while (b < a) {
            b = b << 1;
        }
        return b;
    }

}
