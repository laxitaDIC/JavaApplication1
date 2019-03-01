package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/* Interpolation
 * Input VTArea.java object 
 * Output as object of AreaValue.java used in 'AreaSpline.java'
 *

 * @author Laxita
 */
public class AreaValue {

    /**
     * @param args the command line arguments
     */
    public VTArea vt_area;
    public SplineInterpolator spline_interp;
    public double[][] ag_q_spl;

    public void area() throws Exception {

        vt_area = new VTArea();
        vt_area.vt_value();

        ArrayList<Double> ag_a = new ArrayList<Double>();
        int lpc_ord = 12;

        for (int i = 0; i < vt_area.a.size(); i++) {
            ag_a.add(Math.sqrt(vt_area.a.get(i)));
        }

        ArrayList<Double> ag_x = new ArrayList<Double>();
        for (int i = 0; i < 12; i++) {
            ag_x.add(i * 1.0);
        }
        ArrayList<Double> ag_z = new ArrayList<Double>();
        for (double i = 0; i < (vt_area.a.size() / 12); i++) {
            ag_z.add(i * 1.0);
        }
        double[][] ag_q = new double[12][(int) (Math.round(vt_area.value.calcArea.pitchOrder.pointDetect.pitch.Filelen / 50))];
        int k = 0;
        for (int i = 0; i < vt_area.a.size() / lpc_ord; i++) {
            for (int j = 0; j < ag_q.length; j++) {
                ag_q[j][i] = ag_a.get(k);
                k++;
            }
        }

        ArrayList<Double> ag_x_spl = new ArrayList<Double>();

        for (double i = 1; i < lpc_ord; i = i + ((lpc_ord - 1) / 40.0)) {
            ag_x_spl.add(i * 1.0);
        }

        spline_interp = new SplineInterpolator();

        int m = ag_q.length;
        int n = ag_q[0].length;
        double temp[][] = new double[n][m];
        double tt[] = new double[m];
        double[] x1 = new double[ag_x_spl.size()];

        ag_q_spl = new double[ag_x_spl.size()][n];

        for (int c = 0; c < n; c++) {
            for (int d = 0; d < m; d++) {
                temp[c][d] = ag_q[d][c];
            }
        }
        for (int c = 0; c < n; c++) {
            for (int d = 0; d < m; d++) {
            }
        }
        for (int c = 0; c < n; c++) {
            for (int d = 0; d < m; d++) {
                tt[d] = temp[c][d];
            }
        }
        double[][] ag_q_spl_temp = spline_interp.spline(ag_x, temp, ag_x_spl);
        ag_q_spl = new double[ag_q_spl_temp[0].length][ag_q_spl_temp.length];
        for (int c = 0; c < ag_q_spl[0].length; c++) {
            for (int d = 0; d < ag_q_spl.length; d++) {
                ag_q_spl[d][c] = ag_q_spl_temp[c][d];
            }
        }

    }

}
