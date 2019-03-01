/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Laxita Filtering of data using low pass filter Input column vector of
 * type double as 'input' from PitchCalculation.java Output as ArrayList
 * 'outputvector' used in PitchCalculation.java
 */
public class demo_fir1 {

    public ArrayList<Double> fir1(Double[] input) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("fir.txt"));
        String line = br.readLine();
        String[] result_time = line.split("\\s");
        ArrayList<Double> b_temp = new ArrayList<Double>();
        int x = 0;
        while (x < result_time.length) {
            if (!result_time[x].equals("")) {
                b_temp.add(Double.parseDouble(result_time[x]));
            }
            x++;
        }
        Double[] b = {};

        b = b_temp.toArray(b);

        double[] a = new double[b.length];

        a[0] = 1;
        // for (int i = 0; i < eng_hamm.size(); i++) {
        double[] y = new double[input.length];
        ArrayList<Double> input_temp = new ArrayList<Double>();

        for (int ff = 0; ff < input.length; ff++) {
            input_temp.add(input[ff]);
        }
        filter(b, b.length - 1, a, input_temp, input_temp.size() - 1, y);
        ArrayList<Double> outputVector = new ArrayList<Double>();

        for (int i = 0; i < y.length; i++) {
            outputVector.add(y[i]);
        }
     
        /* BufferedReader br = new BufferedReader(new FileReader("fir.txt"));
        String line = br.readLine();
        String[] result_time = line.split("\\s");
        double a[] = new double[result_time.length / 2];
        double b[] = new double[result_time.length / 2];
        ArrayList<Double> aVector = new ArrayList<Double>();
        ArrayList<Double> bVector = new ArrayList<Double>();

        int k = 1;

        for (int x = 0; x < result_time.length; x++) {
            if (!result_time[x].equals("")) {
                bVector.add(Double.parseDouble(result_time[x]));
                aVector.add(0.0);

            }
        }
        for (int i = 0; i < bVector.size(); i++) {
            b[i] = bVector.get(i);
            a[i] = aVector.get(i);
        }
        a[0] = 1;
        ArrayList<Double> outputVector = new ArrayList<Double>();

        filter(b, a, bVector, outputVector);

        /*    ArrayList<Double> inputVector = new ArrayList<Double>();
        for (int i = 0; i < input.length; i++) {
            inputVector.add(input[i]);
            filter(b, a, inputVector, outputVector);
        }*/
     
        return outputVector;
    }

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
