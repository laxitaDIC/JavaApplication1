package backend;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.xml.stream.events.EndDocument;

/**
 * Arranging the pitch in correct order, First 0.1s of pitch to zero
 *
 * Input 'pointDetect' object of EndPoitDetection.java Output 'pf_12' vector of
 * type double used in CalculateArea.java
 *
 * @author Laxita
 */
public class PitchCorrectOrder {

    public EndPointDetection pointDetect;
    public ArrayList<Double> pf_12;
    public ArrayList<Double> pf_final;
    public ArrayList<Double> tpp_12;

    /**
     * @param args the command line arguments
     */
    public void pitchCorrect() throws IOException, InterruptedException, URISyntaxException {

        pointDetect = new EndPointDetection();
        pointDetect.endPoint();

        int p_start = 10, p_end = 10;

        for (int i = 0; i < pointDetect.pitch.TempRefdata_1.size(); i++) {
            if (pointDetect.pitch.TempRefdata_1.get(i) > pointDetect.N1_t) {
                p_start = 0;
            }
        }
        for (int i = 0; i < pointDetect.pitch.TempRefdata_1.size(); i++) {
            if (pointDetect.pitch.TempRefdata_1.get(i) > pointDetect.N4_t) {
                p_end = 0;
            }
        }
        if (p_start == 10) {
            p_start = 0;
        }
        if (p_end == 10) {
            p_end = pointDetect.pitch.TempRefdata_1.size();
        }
        double[] t_p_plot = new double[pointDetect.pitch.TempRefdata_1.size()];

        for (int i = 0; i < pointDetect.pitch.TempRefdata_1.size(); i++) {
            t_p_plot[i] = pointDetect.pitch.TempRefdata_1.get(i) - pointDetect.pitch.TempRefdata_1.get(p_start);
        }
        int tp_plindex = 0;
        for (int i = 0; i < pointDetect.pitch.TempRefdata_1.size(); i++) {
            if (t_p_plot[i] == 0) {
                tp_plindex = 1;
            }
        }
        double val_1=pointDetect.pitch.TempRefdata_1.get(tp_plindex);
        double val_2=pointDetect.pitch.TempRefdata_1.get(tp_plindex-1);
        double t_pwindow = Math.round((val_1-val_2) * 100);

        ArrayList<Double> tpp_l = new ArrayList<Double>();
        for (double i = 0; i < pointDetect.pitch.TempRefdata_1.get(p_start);) {
            tpp_l.add(i);
            i = i + (t_pwindow / 100.0);

        }
        ArrayList<Double> tpp_l_copy = new ArrayList<Double>();
        for (int i = 0; i < tpp_l.size(); i++) {
            tpp_l_copy.add(tpp_l.get(i));
        }

        for (int i = 0; i < pointDetect.pitch.TempRefdata_1.size(); i++) {
            tpp_l.add(pointDetect.pitch.TempRefdata_1.get(i));
        }

        /*double[] pf_l = new double[pointDetect.pitch.TempRefdata_2.size()];
        for (int i = 0; i < pointDetect.pitch.TempRefdata_2.size(); i++) {
            pf_l[i] =(pointDetect.pitch.TempRefdata_2.get(i));
        }*/
        double[] pf_l = new double[tpp_l_copy.size() + p_end];

        for (int i = tpp_l_copy.size() , k = tp_plindex - 1; i < pf_l.length; i++, k++) {
            pf_l[i] = pointDetect.pitch.TempRefdata_2.get(k);

        }
         int incr = 0;
        ArrayList<Double> difftpplot = new ArrayList<Double>();
        int No_ofterm;
        ArrayList<Double> pf_11 = new ArrayList<Double>();
        ArrayList<Double> tpp_11 = new ArrayList<Double>();
        //This loop works if there is a discontinuity in the pitch coming from
        //praat.. then it inserts some time points which are differed by window length.. i.e 0.01
        for (int len = 0; len < tpp_l.size() - 1; len++) {
            difftpplot.add(tpp_l.get(len + 1) - tpp_l.get(len));
            if (Math.round(100 * difftpplot.get(len)) > t_pwindow) {
                No_ofterm = (int) Math.round((100 * difftpplot.get(len)) / t_pwindow);
                for (int y = incr; y < incr + No_ofterm - 1; y++) {
                    pf_11.add(0.0);
                }
                incr = incr + No_ofterm - 1;

            } else {
                tpp_11.add(tpp_l.get(len));
                pf_11.add(pf_l[len]);
                incr = incr + 1;
            }
        }
        pf_12 = new ArrayList<Double>();
        tpp_12 = new ArrayList<Double>();
        tpp_12 = tpp_11;
        pf_12 = pf_11;
        int Fs = 10000;
        ArrayList<Double> ExtraTimeData = new ArrayList<Double>();
        for (double i = (tpp_12.get(tpp_12.size() - 1) + t_pwindow / 100); i < pointDetect.pitch.Filelen / Fs; i = (i + t_pwindow / 100)) {
            ExtraTimeData.add(i);
        }
        for (int i = tpp_12.size(), k = 0; i < (ExtraTimeData.size()) + (tpp_12.size()) && k < ExtraTimeData.size(); i++, k++) {
            double s = ExtraTimeData.get(k);
            tpp_12.add(s);
        }
        for (int i = pf_12.size(); i < tpp_12.size(); i++) {
            pf_12.add(0.0);
        }
        pf_final = medfilt1(pf_12, 3);
    }

    // Median Filter
    public ArrayList<Double> medfilt1(ArrayList<Double> x, int s) {
        int n = x.size();
        ArrayList<Integer> indr = new ArrayList<Integer>();
        ArrayList<Integer> indc = new ArrayList<Integer>();
        ArrayList<Double> indc_temp = new ArrayList<Double>();

        ArrayList<Double> indr_temp = new ArrayList<Double>();
        ArrayList<Double> ind = new ArrayList<Double>();

        for (int i = 0; i < s; i++) {
            indr.add(i);
        }
        for (int i = 0; i < n; i++) {
            indc.add(i);
        }
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < n - 1; j++) {
                indc_temp.add(indc.get(j) * 1.0);
            }

        }
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < n - 1; j++) {
                indr_temp.add(indr.get(i) * 1.0);
            }

        }
        for (int i = 0; i < indr_temp.size(); i++) {
            ind.add(indc_temp.get(i) + indr_temp.get(i));
            // ind.add(indc())
        }
        double X[] = new double[x.size() + 1];
        // X[0] = 0.0;

        for (int i = 0, j = 0; j < x.size(); j++, i++) {
            X[i] = x.get(j);
        }
        return x;
    }

}
