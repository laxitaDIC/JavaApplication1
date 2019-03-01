package backend;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * End Point Detection Input 'pitch' object of PitchCalculation.java Output as
 * 'EndPointdetection' object call in PitchCorrectOrder.java
 *
 * @author Laxita
 */
public class EndPointDetection {

    public double N1_t;
    public double N2_t;
    public double N3_t;
    public double N4_t;
    public double Nt_1;
    public PitchCalculation pitch;
    public int N1;
    public int N3;
    public int N2;
    public double[] timesave;
    public ArrayList<Double> wavedata;

    public void endPoint() throws IOException, InterruptedException, URISyntaxException {

        pitch = new PitchCalculation();
        pitch.pitchCalc();
        N1 = 1;
        N2 = pitch.data_prep.length;
        int wrapRatio = 1;
        N1 = Math.round(N1 * wrapRatio);            // convert frame no. to corresponding sample no.
        N2 = Math.round(N2 * wrapRatio);
        N3 = N2;                                    // storing right endpoint
        int len_data_row = pitch.data_raw.length;
        int Fs = 10000;
        // makes values after right end point zero
        for (int i = N3; i < len_data_row; i++) {
            pitch.data_raw[i] = 0;
        }
        timesave = new double[pitch.Filelen - N1 + 1];
        for (int i = 0; i < timesave.length; i++) {
            timesave[i] = i;
            timesave[i] = timesave[i] * (1.0 / Fs);
        }

        N1_t = N1 * (1.0 / Fs);
        N2_t = N2 * (1.0 / Fs);
        N3_t = N3 * (1.0 / Fs);
        N4_t = N3_t;
        Nt_1 = N3_t - N1_t;
        wavedata = new ArrayList<Double>();
        for (int i = N1-1; i < N2; i++) {
            wavedata.add(pitch.data_raw[i]);
        }
        int val=N2;
        while(val<pitch.Filelen) {
            wavedata.add(0.0);
            val++;
        }
    }

}
