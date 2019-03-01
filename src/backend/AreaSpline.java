package backend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/* Fit-B Spline
 * Input AreaValue.java object 
 * Output as 'disp_ag_q_spl' of used in 'AreaAnimation.java'
 *
 *
 * @author Laxita
 */
public class AreaSpline {

    private static double[][] a;
    private static int addl;
    private static ArrayList<Double> calloc;
    private static double[][] b;
    private static int nb;
    private static ArrayList<Double> last;
    private static ArrayList<Double> rows;
    private static int k_t;
    private static double[][] bb;
    public AreaValue areaVal;
    public static double[] Tempx;
    public static double[] Tempy;
    public static Date d11;
    private static double[] knots_aug;
    public double[][] disp_ag_q_spl;
    public double[][] new_ag_q_spl;

    /**
     * @param args the command line arguments
     */
    public void Spline() throws Exception {
        /*  SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
        String dateStart = time_formatter.format(System.currentTimeMillis());
        d11 = time_formatter.parse(dateStart);*/

        //  System.out.println("First= " + dateStart);
        areaVal = new AreaValue();
        areaVal.area();
        new_ag_q_spl=areaVal.ag_q_spl;
        
        ArrayList<Double> temp_a = new ArrayList<Double>();
        for (int t = 0; t < areaVal.vt_area.a.size(); t++) {
            temp_a.add(Math.sqrt(areaVal.vt_area.a.get(t)));
        }
        ArrayList<Double> temp_aa = new ArrayList<Double>();

        for (int t = 0; t < areaVal.vt_area.a.size(); t++) {
            if (temp_a.get(t) > 7.8) {
                temp_aa.add(7.8);
            } else {
                temp_aa.add(temp_a.get(t));
            }
        }

        for (int i = temp_aa.size(); i < Math.round(areaVal.vt_area.value.calcArea.pitchOrder.pointDetect.pitch.Filelen / 50); i = i + 1) {
            temp_aa.add(0.0);
        }
        //         System.out.println("temp= "+temp_aa.size());

        a = new double[areaVal.vt_area.value.calcArea.pitchOrder.pointDetect.pitch.lpc_ord][temp_aa.size() / areaVal.vt_area.value.calcArea.pitchOrder.pointDetect.pitch.lpc_ord];
        int y = 0;
        for (int i = 0; i < a[0].length; i++) {
            for (int j = 0; j < a.length; j++) {
                a[j][i] = temp_aa.get(y++);
            }
        }

        double transpose[][] = new double[a[0].length][a.length];
        for (int c = 0; c < a.length; c++) {
            for (int d = 0; d < a[0].length; d++) {
                transpose[d][c] = a[c][d];
            }
        }

        double disp_ag_x[] = new double[12];
        ArrayList<Double> disp_ag_x_spl = new ArrayList<Double>();
        for (int i = 1; i < 13; i++) {
            disp_ag_x[i - 1] = i;
        }
        int k = 0;
        double p = 1;
        for (; p < areaVal.vt_area.value.calcArea.pitchOrder.pointDetect.pitch.lpc_ord;) {
            disp_ag_x_spl.add(p);
            p = p + ((areaVal.vt_area.value.calcArea.pitchOrder.pointDetect.pitch.lpc_ord - 1) / 19.0);
        }

        int knots = 4;
        int nSplinePts = 20;
        double a_t[] = new double[transpose[0].length];
        disp_ag_q_spl = new double[transpose.length][nSplinePts];
        k_t = 4;

        for (int i = 0; i < transpose.length; i++) {

            for (int j = 0; j < a_t.length; j++) {
                a_t[j] = transpose[i][j];
                //a_t[j]-=a_t[j]*0.06;
            }
            fitBSpline(disp_ag_x, a_t, knots, nSplinePts);
            for (int jj = 0; jj < Tempy.length; jj++) {
                disp_ag_q_spl[i][jj] = Tempy[jj];
            }
            // System.exit(0);

        }

        //  System.exit(0);
        /*   SimpleDateFormat time_formatter1 = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
        String dateStop = time_formatter1.format(System.currentTimeMillis());
        Date d21 = time_formatter1.parse(dateStop);

        long diff = d21.getTime() - d11.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        System.out.println("Last= " + dateStop);
        System.out.println(diffDays + " days, ");
        System.out.println(diffHours + " hours, ");
        System.out.println(diffMinutes + " minutes, ");
        System.out.println(diffSeconds + " seconds.");*/
    }

    public void fitBSpline(double[] xval, double[] yval, int knots, int nSplinePts) throws IOException {

        ArrayList<Double> xval_t = new ArrayList<Double>();
        ArrayList<Double> yval_t = new ArrayList<Double>();
        ArrayList<Double> xval_tt = new ArrayList<Double>();
        for (int i = 0; i < xval.length - 1; i++) {
            double tt = xval[i + 1] - xval[i];
            xval_t.add(Math.pow(tt, 2));
            xval_tt.add(xval[i]);
        }
        for (int i = 0; i < yval.length - 1; i++) {
            double tt = yval[i + 1] - yval[i];
            yval_t.add(Math.pow(tt, 2));
            //yval_t.add(tt);
        }
        for (int i = 0; i < yval.length; i++) {
        }

        xval_tt.add(xval[xval.length - 1]);

        //yval_tt.add(yval[yval.length - 1]);
        ArrayList<Double> temp_t = new ArrayList<Double>();
        for (int i = 0; i < xval_t.size(); i++) {
            temp_t.add(Math.sqrt(xval_t.get(i) + yval_t.get(i)));
        }
        double[] tt = new double[temp_t.size()];

        for (int i = 0; i < temp_t.size(); i++) {
            tt[i] = temp_t.get(i);
        }

        double[] t1 = makeCumul(tt);

        ArrayList<Double> t = new ArrayList<Double>();
        t.add(0.0);
        for (int i = 0; i < t1.length; i++) {
            t.add(t1[i]);
        }
        /*  double[] tt1 = new double[t.size()];
        for (int i = 0; i < tt1.length; i++) {
            tt1[i] = t.get(i);
        }*/
        double tmax = Collections.max(t);
        double tmin = Collections.min(t);
        double dx = (tmax - tmin) / (nSplinePts - 1);
        ArrayList<Double> tTemp = new ArrayList<Double>();
        int xx = 0;
        for (double i = tmin; i <= tmax; i = i + dx) {
            tTemp.add(i);
        }
        ArrayList<Double> tTemp_val = new ArrayList<Double>();
        for (int i = 0; i < t.size(); i++) {
            tTemp_val.add(t.get(i));
        }

        // ArrayList<Double> yvalTemp=new ArrayList<Double>();
        //  spap2(tTemp, xval, knots);
        //    Tempx = spVal(coefs_x, knots_aug, tTemp);
        //ArrayList<Double> calloc_y = spCol(knots_aug, tt1, k_t);
        // double[] cof_x = spap2(xval_tt, tTemp_val, knots);
        //  Tempx = spVal(cof_x, knots_aug, tTemp);
        double[] cof_y = spap2(tTemp_val, yval, knots);

        Tempy = spVal(cof_y, knots_aug, tTemp);
        //for (int i = 0; i < Tempy.length; i++) {
        //     System.out.println("kno=  " + Tempy[i]);
        // }

    }

    public double[] spap2(ArrayList<Double> xval, double[] yval, int knots) throws IOException {
        double d1 = 1;
        double d2 = xval.size();
        double d3 = knots - 2.0 + k_t;
        double[] y_t = new double[(int) d3 + 1];
        y_t[0] = d1 - 1;
        for (int i = 1; i < y_t.length - 1; i++) {
            double xx = (1 + (i * (d2 - d1) / d3));
            y_t[i] = xx - 1;
        }
        y_t[y_t.length - 1] = d2 - 1;

        int k = 0;
        double temp_avknt[][] = new double[y_t.length][k_t - 1];
        for (int i = 0; i < temp_avknt[0].length; i++) {
            k = 0;
            for (int j = 0; j < temp_avknt.length; j++) {
                //temp_avknt[j][i] = y_t[k++];
                temp_avknt[j][i] = xval.get((int) Math.round(y_t[k++]));

            }
        }
        k = 0;
        double temp_avknt1[] = new double[y_t.length * (k_t - 1) + (k_t - 1)];
        for (int i = 0; i < temp_avknt[0].length; i++) {
            for (int j = 0; j < temp_avknt.length; j++) {
                temp_avknt1[k] = temp_avknt[j][i];
                k++;
            }
        }
        k = 0;
        double sum_avknt2[] = new double[temp_avknt1.length / (k_t - 1)];
        double sum = 0;
        int len = (y_t.length - k_t) + k_t + 1;
        int yy = 0;
        double temp_avknt11[][] = new double[(temp_avknt1.length / (k_t - 1)) - 1][k_t - 1];
        for (int i = 0; i < temp_avknt11[0].length; i++) {
            for (int j = 0; j < temp_avknt11.length; j++) {
                temp_avknt11[j][i] = temp_avknt1[k++];
            }
        }
        k = 0;
        double temp_tt[] = new double[temp_avknt11.length * temp_avknt11[0].length + 3];
        for (int i = 0; i < temp_avknt11[0].length; i++) {
            for (int j = 0; j < temp_avknt11.length; j++) {
                temp_tt[k] = temp_avknt11[j][i];
                k++;
            }
        }
        double temp_tt1[][] = new double[k_t - 1][temp_tt.length / (k_t - 1)];
        k = 0;
        for (int i = 0; i < temp_tt1.length; i++) {
            for (int j = 0; j < temp_tt1[0].length; j++) {
                temp_tt1[i][j] = temp_tt[k];
                k++;
            }
        }
        for (int i = 0; i < temp_tt1[0].length; i++) {
            sum = 0;
            for (int j = 0; j < temp_tt1.length; j++) {
                sum = sum + temp_tt1[j][i];
            }
            sum_avknt2[yy++] = sum / (k_t - 1);
        }

        double tstar[] = new double[k_t - 1];
        for (int jj = 1, i = 0; i < k_t - 1; jj++, i++) {
            tstar[i] = sum_avknt2[jj];
        }
        double aptknt[] = new double[tstar.length + 2];
        aptknt[0] = xval.get((int) Math.round(y_t[0]));
        int jk = 1;
        for (int i = 0; i < tstar.length; jk++, i++) {
            aptknt[jk] = tstar[i];
        }
        aptknt[jk] = xval.get((int) Math.round(y_t[y_t.length - 1]));

        ArrayList<Double> knots_aug_temp = augknt(aptknt, k_t);
        knots_aug = new double[knots_aug_temp.size()];
        for (int i = 0; i < knots_aug.length; i++) {
            knots_aug[i] = knots_aug_temp.get(i);
        }
        //   double[] tt1 = new double[tTemp.size()];

        ArrayList<Double> calloc_x = spCol(knots_aug, xval, k_t);

        double[] coefs_x = slvblk_fun(calloc_x, yval);
        //for(int i=0;i<coefs_x.length;i++)
        //System.out.println("coef= "+coefs_x[i]);
        //System.exit(0);
        return coefs_x;

// return coefs_x;
    }

    public ArrayList<Double> spCol(double[] knots, ArrayList<Double> tau, int k) {

        int npk = knots.length;
        int n = npk - k;
        int slvblk = 1, noderiv = 0;
        int nrows = tau.size();
        int[] index = new int[tau.size()];
        index[0] = 0;
        for (int i = 0, m = 1; i < index.length - 1; i++, m++) {
            if ((tau.get(i + 1) - tau.get(i)) > 0) {
                index[m] = i + 1;
            }
        }

        int[] m = new int[tau.size()];
        for (int i = 0; i < tau.size(); i++) {
            m[i] = nrows + 1 - index.length;
        }
        int nd = 0;
        for (int i = 0; i < tau.size(); i++) {
            if (m[i] > nd) {
                nd = m[i];
            }
        }
        double[] pts = new double[tau.size()];

        for (int i = 0; i < tau.size(); i++) {
            pts[i] = tau.get(index[i]);
        }
        int km1 = k - 1;
        ArrayList<Double> augknot = new ArrayList<Double>();
        augknot = augknt(knots, k);
        int naug = augknot.size() - k;
        ArrayList<Double> sites = new ArrayList<Double>();
        int y = 0;
        for (; y < naug; y++) {
            sites.add(augknot.get(y));
        }
        for (int i = 0; i < pts.length; i++) {
            sites.add(pts[i] * 1.0);
            y++;
        }
        double index_st[] = new double[(naug + pts.length)];
        ArrayList<Double> sites_t = new ArrayList<Double>();
        sites_t = (ArrayList<Double>) sites.clone();
        Collections.sort(sites);
        for (int d = 0; d < sites.size(); d++) {
            index_st[d] = sites_t.indexOf(sites.get(d)) + 1;
            sites_t.set(sites_t.indexOf(sites.get(d)), null);
        }
        ArrayList<Double> pointer = new ArrayList<Double>();
        ArrayList<Integer> point_tt = new ArrayList<Integer>();
        for (int i = 0; i < pts.length; i++) {
            point_tt.add(i);
        }
        for (int i = 0, j = 0; i < index_st.length; i++) {
            if (index_st[i] > naug) {
                double tt = i - point_tt.get(j);
                pointer.add(tt);
                j++;
            }
        }
        ArrayList<Double> sav = new ArrayList<Double>();
        for (int i = 0; i < pointer.size(); i++) {
            if (pointer.get(i) < k) {
                sav.add(k * 1.0);
            } else {
                sav.add(pointer.get(i));
            }
        }
        bb = new double[nrows][k];
        ArrayList<Integer> index1 = new ArrayList<Integer>();
        for (int i = 0; i < m.length; i++) {
            if (m[i] == 1) {
                index1.add(i);
            }
        }
        if (index1.size() > 0) {
            ArrayList<Double> pt1s = new ArrayList<Double>();
            for (int i = 0; i < pts.length; i++) {
                pt1s.add(1.0 * pts[index1.get(i)]);
            }
            ArrayList<Double> sav1s = new ArrayList<Double>();
            for (int i = 0; i < sav.size(); i++) {
                sav1s.add(sav.get(index1.get(i)));
            }
            int lpt1 = index1.size();
            ArrayList<Double> lpt1s = new ArrayList<Double>();
            for (int i = 0; i < index.length; i++) {
                lpt1s.add(1.0 * index1.get(i));
            }
            for (int j = 0; j < bb.length; j++) {
                bb[j][0] = 1;
            }
            double[] saved = new double[lpt1s.size()];
            for (int j = 0; j < km1; j++) {
                saved = new double[lpt1s.size()];
                for (int r = 0; r <= j; r++) {
                    ArrayList<Double> tr = new ArrayList<Double>();
                    ArrayList<Double> t1 = new ArrayList<Double>();
                    ArrayList<Double> term = new ArrayList<Double>();

                    for (int i = 0; i < sav1s.size(); i++) {
                        double tt = sav1s.get(i);
                        double tt1 = augknot.get((int) tt + r);
                        tr.add(tt1 - pt1s.get(i));
                    }

                    for (int i = 0; i < sav1s.size(); i++) {
                        double tt = sav1s.get(i);
                        double tt1 = augknot.get((int) tt + r - j - 1);
                        t1.add(pt1s.get(i) - tt1);
                    }

                    for (int i = 0; i < tr.size(); i++) {
                        int x = lpt1s.get(i).intValue();
                        double tt = bb[x][r];
                        double tt1 = (tr.get(i) + t1.get(i));
                        term.add(tt / tt1);
                    }

                    for (int i = 0; i < lpt1s.size(); i++) {
                        int x = lpt1s.get(i).intValue();
                        bb[x][r] = saved[i] + tr.get(i) * term.get(i);
                    }

                    for (int i = 0; i < saved.length; i++) {
                        saved[i] = t1.get(i) * term.get(i);

                    }
                }
                for (int i = 0; i < lpt1s.size(); i++) {
                    int x = lpt1s.get(i).intValue();
                    bb[x][j + 1] = saved[i];
                }

            }

        }
        ArrayList<Double> sav11 = new ArrayList<Double>();
        for (int i = 0; i < sav.size(); i++) {
            int pp = -1 * addl;
            if (0 > (-1 * addl)) {
                pp = 0;
            }
            sav11.add(sav.get(i) + pp);
        }
        if (slvblk == 1) {
            double yy = sav.get(0);
            int xx = 0;
            if (xx < addl) {
                xx = addl;
            }
            yy = yy - k;
            double last0 = 0;
            if (last0 < yy) {
                last0 = yy;
            }
            ArrayList<Double> ds = new ArrayList<Double>();
            for (int i = 0; i < sav.size() - 1; i++) {
                ds.add(sav.get(i + 1) - sav.get(i));
            }
            ArrayList<Double> index_xx = new ArrayList<Double>();
            index_xx.add(0.0);
            for (int i = 0; i < ds.size(); i++) {
                if (ds.get(i) > 0) {
                    index_xx.add(i + 1.0);
                }
            }
            index_xx.add(nrows * 1.0);
            rows = new ArrayList<Double>();
            for (int i = 0; i < index_xx.size() - 1; i++) {
                rows.add(index_xx.get(i + 1) - index_xx.get(i));
            }
            nb = index_xx.size() - 1;
            last = new ArrayList<Double>();
            double sum = 0;
            for (int i = 1; i < nb; i++) {
                double ff = index_xx.get(i);
                last.add(ds.get((int) ff - 1));
                sum = sum + ds.get((int) ff - 1);
            }
            last.add(n - sum);
            calloc = new ArrayList<Double>();
            calloc.add(41.0);
            calloc.add(nb * 1.0);
            for (int i = 0; i < rows.size(); i++) {
                calloc.add(rows.get(i));
            }
            calloc.add(k * 1.0);
            for (int i = 0; i < last.size(); i++) {
                calloc.add(last.get(i));
            }
            // calloc.add(n - sum);
            for (int i = 0; i < bb[0].length; i++) {
                for (int j = 0; j < bb.length; j++) {
                    calloc.add(bb[j][i]);
                }
            }
        }
        return (calloc);
    }

    public ArrayList<Double> augknt(double[] knots, int k) {
        double[] dk = new double[knots.length];
        for (int i = 0; i < dk.length - 1; i++) {
            dk[i] = knots[i + 1] - knots[i];
        }
        ArrayList<Double> j = new ArrayList<Double>();
        for (int i = 0; i < dk.length; i++) {
            if (dk[i] > 0) {
                j.add(i * 1.0);
            }
        }
        addl = (int) (k - j.get(0));
        ArrayList<Double> interior = new ArrayList<Double>();
        for (int i = 0; i < j.size() - 1; i++) {
            interior.add(j.get(i) + 1);
        }
        double[] mults = new double[interior.size() + 2];
        mults[0] = k;
        for (int i = 1; i < mults.length - 1; i++) {
            mults[i] = 1;
        }
        mults[mults.length - 1] = k;
        ArrayList<Double> breaks = new ArrayList<Double>();
        breaks.add(knots[(int) (interior.get(0) - 1)]);
        for (int i = 0; i < interior.size(); i++) {
            breaks.add(knots[(int) (interior.get(i) + 0)]);
        }
        breaks.add(knots[(int) (interior.get(interior.size() - 1) + 1)]);
        double s = 0;
        for (int i = 0; i < mults.length; i++) {
            s = s + mults[i];
        }
        int li = breaks.size();
        if (mults.length != li) {
            s = mults[0] * li;
        }
        double[] mm = new double[(int) s];
        double[] cum_temp = new double[mults.length];
        cum_temp[0] = 1;
        for (int i = 0; i < mults.length - 1; i++) {
            cum_temp[i + 1] = cum_temp[i] + mults[i];
        }
        for (int i = 0; i < cum_temp.length; i++) {
            mm[(int) cum_temp[i] - 1] = 1.0;
        }
        ArrayList<Double> t = new ArrayList<Double>();
        double[] cum_t = makeCumul(mm);
        for (int i = 0; i < cum_t.length; i++) {
            t.add(breaks.get((int) cum_t[i] - 1));
        }
        return t;
    }

    public double[] makeCumul(double[] in) {
        double[] out = new double[in.length];
        double total = 0;
        for (int i = 0; i < in.length; i++) {
            total += in[i];
            out[i] = total;
        }
        return out;
    }

    public double[] slvblk_fun(ArrayList<Double> blokmat, double[] b) throws FileNotFoundException, IOException {
        double ne = 0.0, nu = 0.0;
        for (int i = 0; i < rows.size(); i++) {
            ne = ne + rows.get(i);
        }
        for (int i = 0; i < last.size(); i++) {
            nu = nu + last.get(i);
        }
        int brow = b.length;
        int bcol = 1;
        double blocks[][] = new double[bb.length][bb[0].length + 1];
        for (int i = 0; i < bb.length; i++) {
            for (int j = 0; j < bb[0].length; j++) {
                //bb[i][j] -= bb[i][j] * 0.06;

                blocks[i][j] = bb[i][j];
            }
        }
        for (int i = 0; i < blocks.length; i++) {
            //b[i] -= b[i] * 0.3;
            blocks[i][blocks[0].length - 1] = b[i];
        }
        /*for (int ii = 0; ii < blocks.length; ii++) {
            for (int jj = 0; jj < blocks[0].length; jj++) {
                System.out.print("  " + blocks[ii][jj]);
            }
            System.out.println(" ");
        }*/
        // System.exit(0);
        /*  BufferedReader br = new BufferedReader(new FileReader("blocks.txt"));
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
        for (int x = 0; x < blocks.length; x++) {
            for (int y = 0; y < blocks[0].length; y++) {
                blocks[x][y] = Temp_upper_x.get(pp++);
            }
        }*/

        DecimalFormat df = new DecimalFormat("#.#####");

        int ccols = k_t + bcol;
        double f = 0, l = -1, elim = 0;
        for (int j = 0; j < nb; j++) {

            if (f <= l) {
                //  System.out.println("f= " + f + "l= " + l + "elim= " + elim);
                for (int i = (int) f; i <= l; i++) {
                    int p = 0;
                    while (p < k_t - 1) {
                        //System.out.println("first= "+blocks[i][p-1]);
                        blocks[i][p] = blocks[i][p + 1];
                        // System.out.println("second= "+blocks[i][p-1]);

                        p++;
                    }
                    //System.exit(0);
                    blocks[i][k_t - 1] = 0;
                }

            }
            l = l + rows.get(j);
            elim = last.get(j);
            for (int k = 0; k < elim; k++) {
                double sum = 0;
                double sum_t = 0;
                for (int u = (int) f; u <= l; u++) {
                    sum = sum + Math.pow(blocks[u][k], 2);
                    sum_t = sum_t + blocks[u][k];
                    // System.out.println("blo= " + blocks[u][k]);
                }
                double aa = Math.sqrt(sum);
                double vv = Math.abs(blocks[(int) f][k]) + aa;

                double c = vv * aa;
                // c-=c*0.08;

                // System.out.println("block= "+blocks[(int) f][k]);
                if (blocks[(int) f][k] < 0) {
                    vv = -vv;
                }
                ArrayList<Double> q = new ArrayList<Double>();
                q.add(Double.parseDouble(df.format(vv)));

                for (int u = (int) f + 1; u <= l; u++) {

                    q.add(Double.parseDouble(df.format(blocks[u][k])));
                }
                double q_rep[][] = new double[q.size()][ccols];
                for (int u = 0; u < q_rep.length; u++) {
                    for (int p = 0; p < q_rep[0].length; p++) {
                        q_rep[u][p] = Double.parseDouble(df.format(q.get(u) / c));
                    }
                }
                double q_rep_1[][] = new double[q.size()][q_rep[0].length];
                ArrayList<Double> q_temp = new ArrayList<Double>();

                /*  for (int kk = (int) f; kk <= l;  kk++) {
                    double ss = 0;
                    for (int yy = 0,u=0; yy < blocks[0].length; yy++,u++) {
                        ss += q.get(u) * blocks[kk][yy];
                    }
                    for (int e = 0; e < q_rep_1.length; e++) {
                        q_temp.add(ss);
                    }

                }*/
                for (int yy = 0; yy < blocks[0].length; yy++) {
                    double ss = 0;
                    for (int u = 0, kk = (int) f; kk <= l; kk++, u++) {
                        ss += q.get(u) * blocks[kk][yy];
                        // ss-=ss*0.08;
                    }
                    for (int e = 0; e < q_rep_1.length; e++) {
                        q_temp.add(Double.parseDouble(df.format(ss)));
                    }
                }
                int pq = 0;
                for (int yy = 0; yy < q_rep_1[0].length; yy++) {
                    for (int zz = 0; zz < q_rep_1.length; zz++) {
                        q_rep_1[zz][yy] = q_temp.get(pq);
                        pq++;

                    }
                }

                /*System.out.println("******************************");
                for (int ii = 0; ii < blocks.length; ii++) {
                    for (int jj = 0; jj < blocks[0].length; jj++) {
                        System.out.print("  " + blocks[ii][jj]);

                    }
                    System.out.println(" ");
                }

                System.out.println("c= " + c + "aa= " + aa + "vv=" + vv + "sum_t= " + sum_t);
                System.out.println("q= " + q);

                System.out.println("f= " + f + "l= " + l + "c= " + c);
                for (int ii = 0; ii < q_rep.length; ii++) {
                    for (int jj = 0; jj < q_rep[0].length; jj++) {
                        System.out.print("  " + q_rep[ii][jj]);

                    }
                    System.out.println(" ");
                }
                System.out.println("");
                for (int ii = 0; ii < q_rep_1.length; ii++) {
                    for (int jj = 0; jj < q_rep_1[0].length; jj++) {
                        System.out.print("  " + q_rep_1[ii][jj]);

                    }
                    System.out.println(" ");
                    // System.exit(0);
                }
                /*   System.out.println("----------------------");

                for (int ii = (int) f; ii < l; ii++) {
                    for (int jj = 0; jj < blocks[0].length; jj++) {
                        System.out.print("  " + blocks[ii][jj]);

                    }
                    System.out.println(" ");
                }
                System.out.println("--------------------------");*/
                for (int u = (int) f, z = 0; u <= l; z++, u++) {
                    for (int y = 0; y < q_rep_1[0].length; y++) {
                        //  System.out.println("blo= " + Double.parseDouble(df.format(blocks[u][y])));
                        // System.out.println("mul= " + Double.parseDouble(df.format(q_rep[z][y] * q_rep_1[z][y])));

                        blocks[u][y] = Double.parseDouble(df.format(blocks[u][y])) - Double.parseDouble(df.format(q_rep[z][y] * q_rep_1[z][y]));
                        //blocks[u][y] = Double.parseDouble(df.format(blocks[u][y] - q_rep[z][y] * q_rep_1[z][y]));

                    }
                }
                /*System.out.println("******************************");
                for (int ii = 0; ii < blocks.length; ii++) {
                    for (int jj = 0; jj < blocks[0].length; jj++) {
                        System.out.print("  " + blocks[ii][jj]);

                    }
                    System.out.println(" ");
                    // System.exit(0);
                }*/

                f = f + 1;
                // System.exit(0);
            }

        }
        double x[] = new double[(int) (f - elim + k_t)];
        for (int j = nb - 1; j >= 0; j--) {
            elim = last.get(j);
            l = f - 1;
            f = f - elim;
            /* System.out.println("----Before---");
            for (int ii = 0; ii < blocks.length; ii++) {
                for (int jj = 0; jj < blocks[0].length; jj++) {
                    System.out.print("  " + blocks[ii][jj]);
                }
                System.out.println("");
            }*/
            if (elim < k_t) {
                //     System.out.println("f= "+f+" l= "+l+"elim= "+elim);
                //     System.out.println("last= "+last);
                for (int i = (int) f; i <= l; i++) {
                    double sum = 0;
                    for (int jj = k_t; jj < ccols; jj++) {
                        //            System.out.println("****************");
                        for (int kk = (int) (elim), yy = (int) (f - 1 + elim + 1); kk < k_t; kk++, yy++) {
                            sum += blocks[i][kk] * x[yy];
                            //          System.out.print("  "+blocks[i][kk]);
                        }
                        blocks[i][jj] = blocks[i][jj] - sum;
                    }
                }

                //   System.exit(0);
            }

            double temp[][] = new double[(int) (l - f + 1)][(int) elim];
            //  System.out.println("f= " + f + "l= " + l);
            //  System.out.println("-----Val---------");
            for (int ii = 0, i = (int) f; i <= l; i++, ii++) {
                for (int jj = 0; jj < elim; jj++) {
                    temp[ii][jj] = blocks[i][jj];
                    // System.out.print("  " + blocks[i][jj]);
                }
                //  System.out.println("");
            }
            double[][] temp_new = areaVal.spline_interp.invert(temp);
            /* System.out.println("--------Invert---------");
            for (int ii = 0; ii < temp_new.length; ii++) {
                for (int jj = 0; jj < temp_new[0].length; jj++) {
                    System.out.print("  " + temp_new[ii][jj]);
                }
                System.out.println("");
            }
            // System.exit(0);*/

            //  System.out.println("-------Multiply--------------");
            for (int ii = 0, k1 = (int) f; ii < temp_new.length; ii++, k1++) {
                double sum = 0;
                for (int jj = 0, i = (int) f, kk = k_t; jj < temp_new[0].length; jj++, i++) {
                    sum = sum + blocks[i][kk] * temp_new[ii][jj];
                    // System.out.print("   " + blocks[i][kk]);
                }
                //   System.out.println("");
                x[k1] = sum;
            }

        }

        return x;
    }

    public double[] spVal(double[] a, double[] t, ArrayList<Double> xs) {
        int lx = xs.size();
        //[t,a,n,k,d] = spbrk(sp);
        int nn = a.length;
        ArrayList<Double> index = new ArrayList<Double>();

        for (int i = 0; i < t.length - 1; i++) {
            double yy = t[i + 1] - t[i];
            if (yy > 0) {
                index.add(i * 1.0);
            }
        }
        double add1 = (k_t - 1 - index.get(0));
//        double addr = (index.get(index.size() - k_t - 2));
        double inf = Double.POSITIVE_INFINITY;
        double Ninf = Double.NEGATIVE_INFINITY;
        double tt[] = new double[nn - k_t + 2];
        tt[0] = Ninf;
        for (int i = k_t, jj = 1; i <= nn; i++, jj++) {
            tt[jj] = t[i];
        }
        tt[tt.length - 1] = inf;
        int[] histc = histogramCount(xs, tt);
        ArrayList<Integer> NaNx = new ArrayList<Integer>();
        for (int i = 0; i < histc.length; i++) {
            if (histc[i] == 0) {
                NaNx.add(i);
            }
        }
        double[][] tx_rep1 = new double[lx][2 * (k_t - 1)];
        int vv = -2;
        for (int i = 0; i < tx_rep1[0].length; i++) {
            for (int j = 0; j < tx_rep1.length; j++) {
                tx_rep1[j][i] = vv;
            }
            vv++;
        }
        double[][] tx_rep2 = new double[lx][2 * (k_t - 1)];
        int xx = 0;
        for (int i = 0; i < tx_rep2.length; i++) {
            for (int j = 0; j < tx_rep2[0].length; j++) {
                tx_rep2[i][j] = histc[xx] + k_t;

            }
            xx++;
        }

        double[][] tx = new double[tx_rep1.length][tx_rep1[0].length];
        for (int i = 0; i < tx_rep2.length; i++) {
            for (int j = 0; j < tx_rep2[0].length; j++) {
                tx[i][j] = t[((int) tx_rep1[i][j] + (int) tx_rep2[i][j]) - 1];
            }

        }
        for (int i = 0; i < tx_rep2.length; i++) {
            for (int j = 0; j < tx_rep2[0].length; j++) {
                tx[i][j] = t[((int) tx_rep1[i][j] + (int) tx_rep2[i][j]) - 1];
            }
        }
        int yy = 0;
        for (int i = 0; i < tx_rep2.length; i++) {
            for (int j = 0; j < tx_rep2[0].length; j++) {
                tx[i][j] = tx[i][j] - xs.get(yy);
            }
            yy++;
        }
        double b_rep1[][] = new double[lx][k_t];
        vv = 1 - k_t;
        for (int i = 0; i < b_rep1[0].length; i++) {
            for (int j = 0; j < b_rep1.length; j++) {
                b_rep1[j][i] = vv;
            }
            vv++;
        }
        double b_rep2[][] = new double[lx][k_t];
        xx = 0;
        for (int i = 0; i < b_rep2.length; i++) {
            for (int j = 0; j < b_rep2[0].length; j++) {
                b_rep2[i][j] = histc[xx] + k_t;

            }
            xx++;
        }
        double b[][] = new double[lx][k_t];

        for (int i = 0; i < b_rep2.length; i++) {
            for (int j = 0; j < b_rep2[0].length; j++) {
                b[i][j] = b_rep1[i][j] + b_rep2[i][j];
            }
        }
        for (int i = 0; i < b_rep2.length; i++) {
            for (int j = 0; j < b_rep2[0].length; j++) {
                b[i][j] = a[(int) b[i][j] - 1];
            }
        }
        for (int r = 0; r < k_t - 1; r++) {
            for (int i = 0; i < k_t - r - 1; i++) {
                for (int jj = 0; jj < tx.length; jj++) {
                    b[jj][i] = (tx[jj][i + k_t - 1] * b[jj][i] - tx[jj][i + r] * b[jj][i + 1]) / (tx[jj][i + k_t - 1] - tx[jj][i + r]);
                }
            }
        }
        double[] v = new double[lx];
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < b.length; j++) {
                v[j] = b[j][i];
            }

        }
        return v;

    }

    public int[] histogramCount(ArrayList<Double> xs, double[] t) {
        int[] ind = new int[xs.size()];
        for (int i = 0; i < t.length - 1; i++) {
            for (int j = 0; j < xs.size(); j++) {
                if (xs.get(j) > t[i] && xs.get(j) < t[i + 1]) {
                    ind[j] = i;
                }
            }
        }
        return ind;
    }

}
