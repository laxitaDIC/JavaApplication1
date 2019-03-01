
package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * 
/**
 * Input from PitchCorrectOrder.java Output 'sel_frames' as column vector used
 * in ValueSelectedFrame.java
 *
 * @author Laxita
 */
public class CalculateArea {

    public ArrayList<Double> sel_frames;
    public PitchCorrectOrder pitchOrder;
    public ArrayList<Double> WindSigEn;

    /**
     * @param args the command line arguments
     */
    public void Area() throws IOException, InterruptedException, URISyntaxException {

        pitchOrder = new PitchCorrectOrder();
        pitchOrder.pitchCorrect();

        double[] eng_hamm = new double[pitchOrder.pointDetect.pitch.data_raw.length];
        WindSigEn = new ArrayList<Double>();
        for (int i = 0; i < pitchOrder.pointDetect.pitch.data_raw.length; i++) {
            //   eng_hamm.add(0.0);
            WindSigEn.add(0.0);

        }
        double sum_TmpWindSigEn = 0;
        double sum_ww = 0;

        ArrayList<Double> EngSig = new ArrayList<Double>();

        for (int i = 0; i < pitchOrder.pointDetect.pitch.data_prep.length; i++) {
            EngSig.add(Math.pow(pitchOrder.pointDetect.pitch.data_prep[i], 2));
        }
        ArrayList<Double> wEn = new ArrayList<Double>();
        ArrayList<Double> ww = new ArrayList<Double>();

        for (int i = 0; i < pitchOrder.pointDetect.pitch.w.length; i++) {
            wEn.add(pitchOrder.pointDetect.pitch.w[i] * pitchOrder.pointDetect.pitch.w[i]);
        }
        int j = pitchOrder.pointDetect.N1 - 1;

        for (int i = pitchOrder.pointDetect.N1 - 1; i < pitchOrder.pointDetect.N3; i++) {

            if ((i + pitchOrder.pointDetect.pitch.win_size - 1) > pitchOrder.pointDetect.N3) {
                break;
            }
            ww = new ArrayList<Double>();
            for (int i1 = i; i1 < i + pitchOrder.pointDetect.pitch.win_size - 1; i1++) {
                ww.add(EngSig.get(i1));
            }
            ArrayList<Double> TmpWindSigEn = new ArrayList<Double>();
            sum_TmpWindSigEn = 0;
            sum_ww = 0;

            for (int i1 = 0; i1 < ww.size(); i1++) {

                TmpWindSigEn.add(ww.get(i1) * wEn.get(i1));
                sum_TmpWindSigEn = sum_TmpWindSigEn + TmpWindSigEn.get(i1);
                sum_ww = sum_ww + ww.get(i1);
            }

            WindSigEn.add(j, sum_TmpWindSigEn);

            double RectEn = sum_ww;
            if (RectEn > 0) {
                eng_hamm[j] = WindSigEn.get(j) / RectEn;

            } else {
                eng_hamm[j] = 0.0;

            }
            j = j + 1;

        }

        BufferedReader br = new BufferedReader(new FileReader("fir1.txt"));
        StringBuilder sb = new StringBuilder();
        ArrayList<Double> b_temp = new ArrayList<Double>();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            b_temp.add(Double.parseDouble(line));
        }
        Double[] b = {};

        b = b_temp.toArray(b);

        double[] a = new double[b.length];

        a[0] = 1;
        // for (int i = 0; i < eng_hamm.size(); i++) {
        double[] y = new double[eng_hamm.length];
        ArrayList<Double> eng_hamm_temp = new ArrayList<Double>();

        for (int ff = 0; ff < eng_hamm.length; ff++) {
            eng_hamm_temp.add(eng_hamm[ff]);
        }
        filter(b, b.length - 1, a, eng_hamm_temp, eng_hamm.length - 1, y);

        /*for(int i=0;i<y.length;i++){
            System.out.println(" "+y[i]);
        }*/
        // System.exit(0);
        ArrayList<Double> eng_hamm_filt = new ArrayList<Double>();

        /*for (int i = 0; i < eng_hamm.size(); i++) {
            inputVector.add(eng_hamm.get(i));
            filter(b, a, inputVector, outputVector);
            //          if(i<=11){
            //        eng_hamm_filt.add(outputVector.get(i));
            //      }
        }*/
 /*br = new BufferedReader(new FileReader("eng_hamm_filt.txt"));
        sb = new StringBuilder();
        b_temp = new ArrayList<Double>();

        while ((line = br.readLine()) != null) {
            sb.append(line);
            eng_hamm_filt.add(Double.parseDouble(line));
        }*/
        for (int i = 10; i < y.length; i++) {
            eng_hamm_filt.add(y[i]);

        }

        for (int i = eng_hamm_filt.size(); i < eng_hamm.length; i++) {
            eng_hamm_filt.add(0.0);
        }

        /*for (int i = eng_hamm_filt.size(); i < eng_hamm.length; i++) {
            eng_hamm_filt.add(0.0);
        }*/
        //  Double[] b = {};
        //eng_hamm_filt = b_temp.toArray(eng_hamm_filt);
        int Fs = 10000;
        double min_window_size = (1 / 100.0) * Fs;
        double tmp_index = (pitchOrder.pointDetect.N1 - 1) - 20;
        ArrayList<Double> sel_frames_temp = new ArrayList<Double>();
        /* while ((tmp_index + min_window_size) < pitchOrder.pointDetect.N3) {
            ArrayList<Double> min_temp = new ArrayList<Double>();
            int i1 = (int) (tmp_index + 20);
            for (; i1 <= (tmp_index + min_window_size); i1++) {
                min_temp.add(eng_hamm_filt.get(i1));
                
            }
           if(tmp_index==202){
                System.out.println("min=  "+min_temp+"size= "+min_temp.size());
            }

            double min = 0;
            for (int i = 0; i < min_temp.size(); i++) {
                if (min > min_temp.get(i)) {
                    min = min_temp.get(i);
                }
            }
            Double minInd = eng_hamm_filt.get((int)tmp_index+20);
            for (int i = (int) (tmp_index+20); i <= (tmp_index + min_window_size); i++) {
                if (eng_hamm_filt.get(i) == min) {
                    minInd = (double)i;
                    break;
                }
            }
            if(tmp_index==202){
                System.out.println("min=  "+minInd);
                System.exit(0);
            }

            double tmpSelFrame = minInd;
            // tmpSelFrame = tmpSelFrame + tmp_index + 18;
            double tt;
            if (((tmpSelFrame + pitchOrder.pointDetect.pitch.win_size)) < pitchOrder.pointDetect.N3) {
                sel_frames_temp.add(tmpSelFrame);
                tt = tmpSelFrame;
            } else {
                break;
            }
            tmp_index = tt;
            

        }*/

        while ((tmp_index + min_window_size) < pitchOrder.pointDetect.N3) {
            /* double min = eng_hamm_filt.get(tmp_index + 20);
            int min_val = tmp_index + 20;
            for (int i = tmp_index + 20; i < (tmp_index + min_window_size); i++) {
                if (min > eng_hamm_filt.get(i)) {
                    min = eng_hamm_filt.get(i);
                    min_val = i;
                }
            }
            System.out.println(" " + min_val);

            int k = 0;
            for (int i = tmp_index + 20; i < (tmp_index + min_window_size); i++) {
                if (i == min_val) {
                    break;
                } else {
                    k++;
                }
            }*/

            ArrayList<Double> min_temp = new ArrayList<Double>();
            for (int i = (int) (tmp_index + 20); i < (tmp_index + min_window_size); i++) {
                min_temp.add(eng_hamm_filt.get(i));
            }
            int minIndex = min_temp.indexOf(Collections.min(min_temp));
            int tmpSelFrame = minIndex;
            tmpSelFrame = (int) (tmpSelFrame + tmp_index + 20);

            double tt;
            if (((tmpSelFrame + pitchOrder.pointDetect.pitch.win_size - 1)) < pitchOrder.pointDetect.N3) {
                sel_frames_temp.add(tmpSelFrame * 1.0);
                tt = tmpSelFrame;
            } else {
                break;
            }

            tmp_index = (int) tt;
            //  System.out.println("  "+tmp_index);
        }
        sel_frames = new ArrayList<Double>();
        for (int i = 3; i < sel_frames_temp.size() - 3; i++) {
            sel_frames.add(sel_frames_temp.get(i));
        }

        //  SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
        // String current_time_str34 = time_formatter.format(System.currentTimeMillis());
        // System.out.println("Second= " + current_time_str34);
        //   System.out.println("eng= " + sel_frames.size() + " \n " + sel_frames);
        //   System.exit(0);
    }

    /* public void filter(Double[] b, double[] a, ArrayList<Double> inputVector, ArrayList<Double> outputVector) {
        double rOutputY = 0.0;
        int j = 0;
        for (int i = 0; i < inputVector.size(); i++) {
            if (j < b.length) {
                rOutputY += b[j] * inputVector.get(inputVector.size() - i - 1);
            }
            j++;
        }
        j = 1;
        for (int i = 0; i < outputVector.size(); i++) {
            if (j < a.length) {
                rOutputY -= a[j] * outputVector.get(outputVector.size() - i - 1);
            }
            j++;
        }
        outputVector.add(rOutputY);
    }*/
    public static void filter(Double[] b, int ord, double a[], ArrayList<Double> x, int np, double[] y) {
        int i, j;
        y[0] = b[0] * x.get(0);
        for (i = 1; i < ord + 1; i++) {
            y[i] = 0.0;
            for (j = 0; j < i + 1; j++) {
                y[i] = y[i] + b[j] * x.get(i - j);
            }
            for (j = 0; j < i; j++) {
                y[i] = y[i] - a[j + 1] * y[i - j - 1];
            }
        }
        /* end of initial part */
        for (i = ord + 1; i < np + 1; i++) {
            y[i] = 0.0;
            for (j = 0; j < ord + 1; j++) {
                y[i] = y[i] + b[j] * x.get(i - j);
            }
            for (j = 0; j < ord; j++) {
                y[i] = y[i] - a[j + 1] * y[i - j - 1];
            }
        }
        return;
    }
    /* end of filter */
}
