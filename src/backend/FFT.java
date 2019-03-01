package backend;

/** FFT and IFFT calculation
 * Input 'input' variable of ValueselectedFrame.java
 * Output as 'F1' as column vector of type double call in ValueSelectedFrame.java
*/

import java.util.ArrayList;

public class FFT {

    private double[] F;

    private double[] F1;

    public void fft(double[] data, int length) {

        double[][] fft = new double[length][2];
        F = new double[length];

        int k = 0;
        for (int i = 1; i < length; i++) {

            // fft[k][0] = 0.0;
            //fft[k][1] = 0.0;
            //F[k] = 0.0;
            //int j = 0;
            for (int j = 1; j < data.length; j++) {

                // REAL
                //double temp_x = (-2 * Math.PI) * (i - 1) * (j - 1) * Math.sin(0) / length;
                // IMAGINARY
                double temp_y = (-2 * Math.PI) * (i - 1) * (j - 1) * Math.cos(0) / length;
                fft[k][0] += Math.cos(temp_y) * data[j];
                fft[k][1] += Math.sin(temp_y) * data[j];

                //           fft[k][0] += Math.exp(temp_x) * Math.cos(temp_y) * data[j];
                //         fft[k][1] += Math.sin(temp_y) * Math.exp(temp_x) * data[j];
            }

            F[k] = (fft[k][0] * fft[k][0]) + (fft[k][1] * fft[k][1]);
            k++;
        }

    }

    public void ifft(double[] data) {

        double[][] fft = new double[data.length][2];
        F1 = new double[data.length];

        int k = 0;
        for (int i = 1; i < data.length; i++) {

            //fft[k][0] = 0.0;
            // fft[k][1] = 0.0;
            //F1[k] = 0.0;
            //int j = 0;
            for (int j = 1; j < data.length; j++) {

                // REAL
                // double temp_x = (2 * Math.PI) * (i - 1) * (j - 1) * Math.sin(0) / data.length;
                // IMAGINARY
                double temp_y = (2 * Math.PI) * (i - 1) * (j - 1) * Math.cos(0) / data.length;
                fft[k][0] += (Math.cos(temp_y) * data[j]) / data.length;
                //fft[k][1] += (Math.sin(temp_y) * Math.exp(temp_x) * data[j]) / data.length;
            }

            // real^2 + img^2
//            F1[k] = (fft[k][0] * fft[k][0]) + (fft[k][1] * fft[k][1]);
            F1[k] = fft[k][0];

            k++;
        }

    }

    public double[] compute(ArrayList<Double> input, int b) {

        double[] cinput = new double[input.size()];
        for (int i = 0; i < input.size(); i++) {
            cinput[i] = input.get(i);
        }

        fft(cinput, b);
        ifft(F);
        return F1;

    }
}
