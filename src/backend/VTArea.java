package backend;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Calculating the area values with 5ms shift obtained using the energy window
 * selected area values and weighted average Input 'value' object of Input
 * ValueSelectedFrame.java object Output column vector 'a' used in
 * 'AreaValue.java'
 *
 * @author Laxita
 */
public class VTArea {

    private static int Fs = 10000;
    public ArrayList<Double> a;
    public ValueSelectedFrame value;
    public ArrayList<Double> t_en;
    public double[] av_enful;

    /**
     * @param args the command line arguments
     */
    public void vt_value() throws InterruptedException, Exception {

        value = new ValueSelectedFrame();
        value.valueSelect();
        ArrayList<Integer> sl_frame;
        a = new ArrayList<Double>();

        int lpc_ord = 12;
        int win_scale = 2;
        value.calcArea.pitchOrder.pointDetect.pitch.win_size = Math.round(win_scale * Fs / value.calcArea.pitchOrder.pointDetect.pitch.pitch_f) + 1;
        double[] w = value.calcArea.pitchOrder.pointDetect.pitch.window((int) value.calcArea.pitchOrder.pointDetect.pitch.win_size);
        double acr[] = new double[2 * lpc_ord + 1];
        double r[] = new double[lpc_ord + 1];
        double s[] = new double[lpc_ord + 1];
        double kk[] = new double[lpc_ord];
        for (int i = value.calcArea.pitchOrder.pointDetect.N1 - 1; i < value.calcArea.pitchOrder.pointDetect.N3 + 1; i = (int) (i + value.calcArea.pitchOrder.pointDetect.pitch.wshift_samples)) {

            if ((i + value.calcArea.pitchOrder.pointDetect.pitch.win_size) > value.calcArea.pitchOrder.pointDetect.N3) {
                break;
            }
            sl_frame = new ArrayList<>();

            int yy = 0;
            for (int j = 0; j < value.calcArea.sel_frames.size(); j++) {
                if (value.calcArea.sel_frames.get(j) > i) {
                    sl_frame.add(j);
                }
            }
            if (sl_frame.isEmpty()) {
                double[] ww = new double[(int) value.calcArea.pitchOrder.pointDetect.pitch.win_size - 1];
                int k = 0;
                ArrayList<Double> x = new ArrayList<Double>();

                for (int i1 = i; i1 < i + value.calcArea.pitchOrder.pointDetect.pitch.win_size - 1; i1++, k++) {
                    ww[k] = value.calcArea.pitchOrder.pointDetect.pitch.data_prep[i1];
                    x.add(w[k] * ww[k]);
                }
                int power = 2 * value.nextPowerOf2(x.size());

                double temp_acr[] = value.fftCalc.compute(x, power);
                int f = 0;
                for (int e = temp_acr.length - lpc_ord; e < temp_acr.length; e++, f++) {
                    acr[f] = temp_acr[e];
                }
                for (int e = 0; e < (lpc_ord + 1); e++, f++) {
                    acr[f] = temp_acr[e];
                }
                for (int m = 0, p = lpc_ord; p < (2 * lpc_ord) + 1; m++, p++) {
                    r[m] = acr[p];

                }
                if (r[0] < 0.0001) {
                    for (int m = 0; m < lpc_ord; m++) {
                        kk[m] = 0;
                    }
                } else {
                    double[] matrc = value.levi.levinson(r);
                    for (int n = 1, m = 0; m < lpc_ord; m++, n++) {
                        kk[m] = matrc[n];
                    }
                }
                int kk_count = 0;
                for (int m = 0; m < kk.length; m++) {
                    if (kk[m] != 0) {
                        kk_count = 1;
                    }
                }
                s[lpc_ord] = 1;
                if (kk_count == 0) {
                    s[lpc_ord] = 0;
                }
                for (int m = lpc_ord - 1; m >= 0; m--) {
                    s[m] = ((1 + kk[m]) / (1 - kk[m])) * s[m + 1];

                }

                if (a.size() > 0) {
                    int tt = a.size() + lpc_ord;
                    int temp = a.size() - lpc_ord + 1;
                    for (int q = 0, p = a.size(); p < tt; p++, q++) {
                        //double aa = s[q];
                        a.add(0.5 * s[q] + 0.5 * a.get(temp));
                        temp++;
                    }
                } else {
                    for (int p = 0; p < lpc_ord; p++) {
                        a.add(s[p]);
                    }

                }
                /* if(length(a)>1)
            a((length(a)+1):(length(a)+lpc_ord)) = 0.5*s(1:lpc_ord)+0.5*a((end-lpc_ord+1):(end)); %wastage of memory
        else
            a(1:lpc_ord)=s(1:lpc_ord);
        end;*/

            } else {
                if (sl_frame.get(0) > 1) {
                    double tmp_total = value.calcArea.sel_frames.get(sl_frame.get(0)) - value.calcArea.sel_frames.get(sl_frame.get(0) - 1) + 1;
                    double A1 = (value.calcArea.sel_frames.get(sl_frame.get(0)) - i) / tmp_total;
                    double B1 = (i - value.calcArea.sel_frames.get(sl_frame.get(0) - 1)) / tmp_total;
                    DecimalFormat df = new DecimalFormat("#.#");
                    // df.setRoundingMode(RoundingMode.CEILING);

                    int tt = a.size() + lpc_ord;
                    for (int bb = 0, p = a.size(); p < tt; p++, bb++) {

                        double q = value.a_eng[sl_frame.get(0)][bb] * B1;
                        double r1 = value.a_eng[sl_frame.get(0) - 1][bb] * A1;
                        // double q1 = Double.parseDouble(df.format(B1)) * q + Double.parseDouble(df.format(A1)) * r1;
                        double q1 = q + r1;

                        a.add(q1);

                    }
                } else {
                    if (a.size() > 0) {
                        int tt = a.size() + lpc_ord;
                        for (int p = a.size(); p < tt; p++) {
                            //double aa = s[q];
                            a.add(0.0);
                        }
                    } else {
                        for (int p = 0; p < lpc_ord; p++) {
                            a.add(0.0);
                        }
                    }
                }
            }
        }
        // double[] av_en = new double[value.calcArea.pitchOrder.pointDetect.N3 / (int) value.calcArea.pitchOrder.pointDetect.pitch.wshift_samples];
        ArrayList<Double> av_en = new ArrayList<Double>();

        for (int j = 0, i = value.calcArea.pitchOrder.pointDetect.N1; i < value.calcArea.pitchOrder.pointDetect.N3; i = i + (int) value.calcArea.pitchOrder.pointDetect.pitch.wshift_samples) {

            av_en.add(value.calcArea.WindSigEn.get(i));
            j++;
        }

        double[] No_samples = new double[value.calcArea.pitchOrder.pointDetect.N3 / (int) value.calcArea.pitchOrder.pointDetect.pitch.wshift_samples];
        for (int j = 0, i = value.calcArea.pitchOrder.pointDetect.N1; i < value.calcArea.pitchOrder.pointDetect.N3; i = i + (int) value.calcArea.pitchOrder.pointDetect.pitch.wshift_samples) {
            No_samples[j] = i;
            j++;
        }

        //  double[] t_en = new double[No_samples.length * (int) value.calcArea.pitchOrder.pointDetect.pitch.wshift_samples];
        t_en = new ArrayList<Double>();
        int wshift_time_ms=5;

        for (int j = 0; j < No_samples.length; j++) {
            t_en.add(j *5 *0.001);
        }
        
        //av_enful = new ArrayList<Double>();
        av_enful = new double[av_en.size()];
        for (int j = 0; j < av_en.size();) {

            av_enful[j] = (av_en.get(j));
            j++;
        }
        // Energy to dB xonversion taking 100 dB as maximum energy
        for (int intr = 0; intr < av_enful.length; intr++) {
            if (av_enful[intr] <= 0.001) {
                av_enful[intr] = 0.0;
            }

        }

        int endpt11 = 0;
        for (int intr = av_enful.length - 1; intr > 0; intr--) {
            if (av_enful[intr] >= 1e-3) {
                endpt11 = intr;
                break;
            }

        }

        int max12m = 16;
        double int01 = max12m * 1.0000e-05;
        double eps = 2.2204e-16;
        int tt = (int) (value.calcArea.pitchOrder.pointDetect.pitch.Filelen / (Fs * 0.005));

        double[] new_avg_en = new double[tt];

        for (int i = 0; i < av_enful.length; i++) {
            new_avg_en[i] = (20 * Math.log10((av_enful[i] / int01) + eps));
        }
        ArrayList<Integer> more80 = new ArrayList<>();

        for (int j = 0; j < new_avg_en.length; j++) {
            if (new_avg_en[j] < 0) {
                more80.add(j);
            }
        }
        for (int j = 0; j < more80.size(); j++) {
            new_avg_en[more80.get(j)] = 0.0;
        }

        for (int j = new_avg_en.length + 1; j < tt; j++) {
            new_avg_en[j] = 0.0;
        }

        tt = (int) (value.calcArea.pitchOrder.pointDetect.pitch.Filelen / (Fs * 0.005));
        float k = (float) (t_en.size() + 0.005);
        for (int j = t_en.size() + 1; j < tt; j++, k = (float) (k + 0.005)) {
            t_en.add((double) Math.round(k));
        }
        av_enful = new_avg_en;
       
    }

}
