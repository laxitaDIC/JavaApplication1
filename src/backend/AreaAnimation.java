package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/* For Animating calculating coordinate
 * Input 'disp_ag_q_spl' of  'AreaSpline.java'
 * Output of 'mat_px' and 'mat_py' used in 'VisualAction.java'
 */
/**
 *
 * @author Laxita
 */
public class AreaAnimation {

    public double[][] mat_px;
    public double[][] mat_py;
    public double[][] mat_k2trng;
    public double[][] mat_h2_trng;
    public ArrayList<Double> H2_POA_pos = new ArrayList<Double>();
    public ArrayList<Double> K2_POA_pos = new ArrayList<Double>();

    public AreaSpline areaSpline;

    /**
     * @param args the command line arguments
     */
    public void animation() throws Exception {
        areaSpline = new AreaSpline();
        areaSpline.Spline();
        double h1[] = {40, 53, 64, 76, 87, 100, 113, 131, 151, 170, 188, 200, 215, 223, 227, 229, 231, 230, 229};
        double k1[] = {271, 267, 262, 253, 246, 237, 228, 221, 220, 220, 224, 232, 244, 262, 271, 284, 299, 311, 326};
        double h2[] = {42, 55, 72, 88, 101, 112, 124, 136, 148, 161, 172, 182, 192, 200, 205, 206, 205, 203, 202};
        double k2[] = {278, 282, 276, 274, 270, 266, 260, 258, 255, 255, 256, 259, 266, 277, 284, 293, 306, 314, 325};
        int jaw_rest_h[] = {45, 38, 32, 31, 36, 42, 40, 39, 34, 35, 41, 51, 66, 147};
        int jaw_rest_k[] = {278, 277, 278, 287, 293, 300, 308, 321, 336, 346, 350, 354, 353, 350};
        int glottal_h[] = {161, 170, 174, 175};
        int glottal_k[] = {357, 375, 399, 406};
        int jaw_length = jaw_rest_h.length;

        double mslope[] = {1.6250, 1.7500, 1.7143, 2.4167, 2.9091, 7.4000, 11.6667, 3.8889, 2.0000, 1.5000, 0.9565, 0.6522, 0.5909, 0.3913, 0.2692, 0.1111, 0.0370, 0.0690, 0.1379, 0.2759};
        double min_val = 0;
        int min_ind = 0;
        for (int i = 0; i < k2.length; i++) {
            if (min_val > k2[i]) {
                min_val = k2[i];
                min_ind = i;
            }
        }
        double[][] upperLine2Spline = {{40, 271}, {52.9360004286472, 266.875809062708}, {65.7326839607545, 260.395195908744}, {77.7510094891844, 251.996672314888}, {88.9972788100407, 244.530499768412}, {101.560947818378, 235.919343818046}, {116.692152159854, 226.564163048946}, {133.708040851485, 221.156663555356}, {149.914887715874, 220.205821658707}, {167.892918504593, 220.514554160563}, {186.490022596768, 223.782507702690}, {203.001246645247, 234.400997316198}, {215.864734030133, 248.010750852725}, {223.677580897805, 263.524557020062}, {228.340120595860, 280.394533140559}, {230.657204479494, 296.905521961607}};
        double[][] lowerLine2Spline = {{42, 278}, {57.9927931600524, 280.254565293355}, {72.4275149051325, 276.406052700154}, {87.4654902081469, 273.964889725696}, {102.635925807367, 269.308349627955}, {115.665455111959, 264.016517163537}, {126.454097514820, 260.146358766186}, {137.424925350213, 257.463405611480}, {150.097978451390, 255.440051443791}, {161.336188825983, 255.251769623771}, {171.469861790117, 256.478571860473}, {180.639420766243, 259.366302226416}, {189.352839974387, 264.788981515045}, {197.529839155633, 273.620755945127}, {203.395452733142, 283.808710834683}, {205.513110321672, 296.098490176520}};
        ArrayList<Double> h2_tmp = new ArrayList<Double>();
        ArrayList<Double> k2_tmp = new ArrayList<Double>();

//  for(int i=0;i<lowerLine2Spline.length;i++){
        for (int j = 0; j < lowerLine2Spline.length; j++) {
            h2_tmp.add(lowerLine2Spline[j][0]);
        }
        h2_tmp.add(h2[h2.length - 1] * 1.0);
        for (int j = 0; j < lowerLine2Spline.length; j++) {
            k2_tmp.add(lowerLine2Spline[j][1]);
        }
        k2_tmp.add(k2[k2.length - 1] * 1.0);
        h2_tmp.add(0, jaw_rest_h[0] * 1.0);
        k2_tmp.add(0, jaw_rest_k[0] * 1.0);

        h2_tmp.remove(1);
        k2_tmp.remove(1);

        int str = 1;
        int seg_length = h2_tmp.size();
        ArrayList<Double> h2_f = new ArrayList<Double>();
        ArrayList<Double> k2_f = new ArrayList<Double>();

        for (int rev = h2_tmp.size() - 1; rev > 0; rev--) {
            h2_f.add(h2_tmp.get(rev));
            k2_f.add(k2_tmp.get(rev));
        }
        ArrayList<Double> rx = new ArrayList<Double>();
        ArrayList<Double> ry = new ArrayList<Double>();
        for (int i = 0; i < jaw_rest_h.length; i++) {
            rx.add(1.0 * jaw_rest_h[i]);
            ry.add(1.0 * jaw_rest_k[i]);
        }
        for (int i = 0; i < glottal_h.length; i++) {
            rx.add(1.0 * glottal_h[i]);
            ry.add(1.0 * glottal_k[i]);
        }
        for (int i = 0; i < h2_f.size(); i++) {
            rx.add(h2_f.get(i));
            ry.add(k2_f.get(i));
        }

        double[] px = new double[jaw_rest_h.length + glottal_h.length + 16];
        double py[] = new double[jaw_rest_h.length + glottal_h.length + 16];

        /*         BufferedReader br = new BufferedReader(new FileReader("disp_ag_q_spl.txt"));
        String line_jaw;
        ArrayList<Double> Temp_upper_x = new ArrayList<Double>();

        while ((line_jaw = br.readLine()) != null) {
            String[] result_mat = line_jaw.split("\\s");
            for (int x = 0; x < result_mat.length; x++) {
                if (!result_mat[x].equals("")) {
                    Temp_upper_x.add(Double.parseDouble(result_mat[x]));
                }
            }

        }*/
        int pp = 0;
//        for (int x = 0; x < areaSpline.disp_ag_q_spl.length; x++) {
//            for (int y = 0; y < areaSpline.disp_ag_q_spl[0].length; y++) {
//                System.out.print("  "+ areaSpline.disp_ag_q_spl[x][y]);
//            }
//            System.out.println("");
//        }
//        System.exit(0);
//        System.out.println("lennnnn= "+areaSpline.disp_ag_q_spl.length);
//        

        mat_px = new double[areaSpline.disp_ag_q_spl.length][jaw_rest_h.length + glottal_h.length + 16];
        mat_py = new double[areaSpline.disp_ag_q_spl.length][jaw_rest_h.length + glottal_h.length + 16];

        mat_h2_trng = new double[areaSpline.disp_ag_q_spl.length][3];
        mat_k2trng = new double[areaSpline.disp_ag_q_spl.length][3];

        double dmax = 7.8;
        int dmaxgraph = 50;
        for (int i = 0; i < upperLine2Spline.length; i++) {
            mslope[i] = (upperLine2Spline[i][1] - lowerLine2Spline[i][1]) / (upperLine2Spline[i][0] - lowerLine2Spline[i][0]);
        }
        ArrayList<Double> d_top_rest = new ArrayList<Double>();

        d_top_rest.add(Math.sqrt(Math.pow((upperLine2Spline[0][1] - lowerLine2Spline[0][1]), 2) + Math.pow((upperLine2Spline[0][0] - lowerLine2Spline[0][0]), 2)));
        d_top_rest.add(Math.sqrt(Math.pow((upperLine2Spline[1][1] - lowerLine2Spline[1][1]), 2) + Math.pow((upperLine2Spline[1][0] - lowerLine2Spline[1][0]), 2)));
        int sd = areaSpline.disp_ag_q_spl.length;
        double h1_t[] = new double[upperLine2Spline.length];
        double k1_t[] = new double[upperLine2Spline.length];

        for (int i = 0; i < upperLine2Spline.length; i++) {
            h1_t[i] = upperLine2Spline[i][0];
        }
        for (int i = 0; i < upperLine2Spline.length; i++) {
            k1_t[i] = upperLine2Spline[i][1];
        }
        for (int segmentnum = 0; segmentnum < sd; segmentnum++) {
            ArrayList<Double> d_top_a = new ArrayList<Double>();
            for (int yy = 0; yy < areaSpline.disp_ag_q_spl[0].length; yy++) {
                d_top_a.add((areaSpline.disp_ag_q_spl[segmentnum][yy]) * (dmaxgraph / dmax));
            }
            double temp = ((d_top_a.get(0) - d_top_rest.get(0)) / Math.sqrt(1 + (mslope[0] * mslope[0])));
            ArrayList<Double> jaw_new_hr = new ArrayList<Double>();
            ArrayList<Double> jaw_new_kr = new ArrayList<Double>();

            if (mslope[0] < 0) {
                for (int yy = 0; yy < jaw_length - 1; yy++) {
                    jaw_new_hr.add(jaw_rest_h[yy] - temp);
                    jaw_new_kr.add(jaw_rest_k[yy] - mslope[0] * temp);
                }
            } else {
                for (int yy = 0; yy < jaw_length - 1; yy++) {
                    jaw_new_hr.add(jaw_rest_h[yy] + temp);
                    jaw_new_kr.add(jaw_rest_k[yy] + mslope[0] * temp);

                }
            }
            jaw_new_hr.add(jaw_rest_h[jaw_length - 1] * 1.0);
            jaw_new_kr.add(jaw_rest_k[jaw_length - 1] * 1.0);

            h2[0] = jaw_new_hr.get(0);
            k2[0] = jaw_new_kr.get(0);
            int dmin = 100;
            for (int i = 1; i < h1_t.length; i++) {
                temp = ((d_top_a.get(i)) / Math.sqrt(1 + (mslope[i] * mslope[i])));
                if (mslope[i] < 0) {
                    h2[i] = h1_t[i] - temp;
                    k2[i] = k1_t[i] - (mslope[i]) * temp;
                } else {
                    if (upperLine2Spline[i][1] < lowerLine2Spline[1][1]) {
                        h2[i] = h1_t[i] + temp;
                        k2[i] = k1_t[i] + (mslope[i]) * temp;
                    } else {
                        h2[i] = h1_t[i] - temp;
                        k2[i] = k1_t[i] - (mslope[i]) * temp;
                    }
                }

            }
            double mink2 = areaSpline.disp_ag_q_spl[segmentnum][0];
            int mink2ind = 0;
            for (int yy = 0; yy < 12; yy++) {
         
                if (mink2 > areaSpline.disp_ag_q_spl[segmentnum][yy]) {
                    mink2 = areaSpline.disp_ag_q_spl[segmentnum][yy];
                    mink2ind = yy;

                }
            }
         
            ArrayList<Double> h2trng = new ArrayList<Double>();
            h2trng.add(h2[0]);
            h2trng.add(h2[mink2ind]);
            h2trng.add(202.0);

            ArrayList<Double> k2trng = new ArrayList<Double>();
            k2trng.add(k2[0]);
            k2trng.add(k2[mink2ind]);
            k2trng.add(321.0);

            ArrayList<Double> h2trng_f = new ArrayList<Double>();
            h2trng_f.add(202.0);
            h2trng_f.add(h2[mink2ind]);
            h2trng_f.add(h2[0]);

            ArrayList<Double> k2trng_f = new ArrayList<Double>();
            k2trng_f.add(321.0);
            k2trng_f.add(k2[mink2ind]);
            k2trng_f.add(k2[0]);
            ArrayList<Double> px_trng = new ArrayList<Double>();
            ArrayList<Double> py_trng = new ArrayList<Double>();
            for (int i = 0; i < jaw_new_hr.size(); i++) {
                px_trng.add(jaw_new_hr.get(i));
                py_trng.add(jaw_new_kr.get(i));
            }
            for (int i = 0; i < glottal_h.length; i++) {
                px_trng.add(glottal_h[i] * 1.0);
                py_trng.add(glottal_k[i] * 1.0);
            }
            for (int i = 0; i < h2trng_f.size(); i++) {
                px_trng.add(h2trng_f.get(i));
                py_trng.add(k2trng_f.get(i));
            }

            h2_f.clear();
            k2_f.clear();
            for (int yy = 0, rev = 15; rev >= 0; rev--, yy++) {
                h2_f.add(yy, h2[rev]);
                k2_f.add(yy, k2[rev]);
            }

            int k = 0;
            for (int i = 0; i < jaw_new_hr.size(); i++, k++) {
                px[k] = jaw_new_hr.get(i);
                py[k] = jaw_new_kr.get(i);
            }
            for (int i = 0; i < glottal_h.length; i++, k++) {
                px[k] = glottal_h[i];
                py[k] = glottal_k[i];
            }
            for (int i = 0; i < h2_f.size(); i++, k++) {
                px[k] = h2_f.get(i);
                py[k] = k2_f.get(i);
            }
            for (int i = 0; i < px.length; i++) {
                mat_px[segmentnum][i] = px[i];
                // System.out.print("  "+px[i]);
                mat_py[segmentnum][i] = py[i];
            }
            for (int i = 0; i < 3; i++) {
                mat_h2_trng[segmentnum][i] = h2trng.get(i);
                mat_k2trng[segmentnum][i] = k2trng.get(i);
            }
        }
        for (int i = 0; i < mat_h2_trng.length; i++) {
            H2_POA_pos.add(mat_h2_trng[i][1]);
            K2_POA_pos.add(mat_k2trng[i][1]);
        }
        // System.exit(0);
        /* for(int i=0;i<mat_px.length;i++){
            for(int j=0;j<mat_px[0].length;j++){
            System.out.print("  "+mat_px[i][j]);
            }
            System.out.println("i= "+i);
        }
        System.exit(0);*/
//        System.exit(0);
    }
}
