package backend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * Cubic Spline Interpolation
 * Input 'x', 'y' ,'xx', 'h' from AreaValue.java  
 * Interpolates the values of the y at the points in the vector xx. The vector x specifies the points at which the data Y is given.
 * Output as  vector 'v' used in 'AreaValue.java'
 *
 *
 * @author Laxita
 */
public class SplineInterpolator {

    private double[][] res1;
    private double[][] b;
    private ArrayList<Double> dx;
    private double[][] divdif;
    int hh;
    private double[][] res2;
    double[][] v;

    /**
     * @param args the command line arguments
     */
    public double[][] spline(ArrayList<Double> x, double[][] y, ArrayList<Double> xx) {
        try {
            int n = x.size();
            int yd = y.length;
            double dd[] = new double[yd];
            dd[0] = 1;

            dx = new ArrayList<Double>();

            for (int i = 0; i < x.size() - 1; i++) {
                double t = x.get(i + 1) - x.get(i);
                dx.add(t);
            }

            double[][] diff = new double[yd][n - 1];
            for (int j = 0; j < y.length; j++) {
                for (int i = 0; i < y[0].length - 1; i++) {
                    double t = y[j][i + 1] - y[j][i];
                    diff[j][i] = (t);
                }
            }

            divdif = new double[yd][n - 1];
            for (int j = 0; j < diff.length; j++) {
                for (int i = 0; i < diff[0].length - 1; i++) {
                    divdif[j][i] = diff[j][i] / dx.get(i);
                }
            }

            double x31 = x.get(3) - x.get(1);
            double xn = x.get(n - 1) - x.get(n - 3);
            b = new double[yd][n];
            for (int p = 0; p < b.length; p++) {
                for (int j = 0, i = 1; i < n - 1; i++, j++) {
                    double t1 = (dx.get(i) * divdif[p][j]);
                    double t2 = dx.get(j) * divdif[p][i];
                    double t3 = 3 * (t1 + t2);
                    b[p][i] = (t3);
                }
            }

            for (int p = 0; p < b.length; p++) {
                b[p][0] = ((((dx.get(0) + 2 * x31) * dx.get(1) * divdif[p][0] + Math.pow(dx.get(0), 2) * divdif[p][1])) / x31);
            }
            ArrayList<Double> temp_mat = new ArrayList<Double>();
            temp_mat.add(x31);
            for (int i = 1; i < dx.size(); i++) {
                temp_mat.add(dx.get(i));
            }
            temp_mat.add(0.0);

            temp_mat.add(dx.get(2));

            for (int i = 0, j = 1; i < n - 2; i++, j++) {
                temp_mat.add(2 * (dx.get(i) + dx.get(j)));
            }
            temp_mat.add(dx.get(n - 2));
            temp_mat.add(0.0);
            for (int i = 1; i < n - 1; i++) {
                temp_mat.add(dx.get(i));
            }
            temp_mat.add(xn);

            double temp_spd[][] = new double[n][3];
            int k = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < n; j++) {
                    temp_spd[j][i] = temp_mat.get(k);
                    k++;
                }
            }
            double[] tx = new double[]{-1, 0, 1};
            spdiags(temp_spd, tx, n);
            double[][] c = new double[b.length][b[0].length];

            for (int row = 0; row < b.length; row++) {
                for (int col = 0; col < res2[0].length; col++) {
                    double theSum = 0;
                    for (int k1 = 0; k1 < b[0].length; k1++) {
                        theSum = theSum + b[row][k1] * res2[k1][col];
                    }
                    c[row][col] = theSum;
                }
            }
            double[][] dzzdx = new double[b.length][b[0].length - 1];
            double[][] dxd = new double[b.length][b[0].length - 1];
            for (int i = 0; i < divdif.length; i++) {
                for (int j = 0; j < divdif[0].length; j++) {
                    dxd[i][j] = dx.get(j);
                }
            }
            for (int i = 0; i < divdif.length; i++) {
                for (int j = 0; j < divdif[0].length - 1; j++) {
                    dzzdx[i][j] = ((divdif[i][j] - c[i][j]) / dxd[i][j]);
                }
            }
            double[][] dzdxdx = new double[b.length][b[0].length - 1];
            for (int i = 0; i < divdif.length; i++) {
                for (int j = 0; j < divdif[0].length - 1; j++) {
                    dzdxdx[i][j] = ((c[i][j + 1] - divdif[i][j]) / dxd[i][j]);
                }
            }

            int dmn1 = y.length * (n - 1);

            double coeff[][] = new double[dmn1][4];

            double Temp_upper_y1[][] = new double[dzdxdx.length][dzdxdx[0].length];
            double Temp_upper_y2[][] = new double[dzdxdx.length][dzdxdx[0].length];
            double Temp_upper_y3[][] = new double[dzdxdx.length][dzdxdx[0].length];
            double Temp_upper_y4[][] = new double[dzdxdx.length][dzdxdx[0].length];
            for (int m = 0; m < dzdxdx.length; m++) {
                for (int nm = 0; nm < dzdxdx[0].length; nm++) {
                    Temp_upper_y1[m][nm] = ((dzdxdx[m][nm] - dzzdx[m][nm]) / dxd[m][nm]);
                    Temp_upper_y2[m][nm] = (2 * dzzdx[m][nm]) - dzdxdx[m][nm];
                    Temp_upper_y3[m][nm] = (c[m][nm]);
                    Temp_upper_y4[m][nm] = (y[m][nm]);

                }
            }
            int h = 0;
            for (int i = 0; i < Temp_upper_y1[0].length - 1; i++) {
                for (int j = 0; j < Temp_upper_y1.length; j++) {
                    coeff[h++][0] = Temp_upper_y1[j][i];
                }
            }
            int h1 = 0;
            for (int i = 0; i < Temp_upper_y2[0].length - 1; i++) {
                for (int j = 0; j < Temp_upper_y2.length; j++) {
                    coeff[h1++][1] = Temp_upper_y2[j][i];
                }
            }

            h = 0;
            for (int i = 0; i < Temp_upper_y3[0].length - 1; i++) {
                for (int j = 0; j < Temp_upper_y3.length; j++) {
                    coeff[h++][2] = Temp_upper_y3[j][i];
                }
            }
            h = 0;
            for (int i = 0; i < Temp_upper_y4[0].length - 1; i++) {
                for (int j = 0; j < Temp_upper_y4.length; j++) {
                    coeff[h++][3] = Temp_upper_y4[j][i];
                }
            }

            int sizexx = xx.size();

            int lx = sizexx;
            int[] index_t = new int[lx];
            double[] breaks = new double[12];

            ArrayList<Double> xs = new ArrayList<Double>();
            for (int i = 0; i < xx.size(); i++) {
                xs.add(xx.get(i));
            }

            for (int i = 0; i < breaks.length; i++) {
                breaks[i] = i + 1;
            }
            index_t = histogramCount(xs, breaks);

     
            for (int i = 0; i < xx.size(); i++) {
                double t = xs.get(i);
                xs.remove(i);
                xs.add(i, t - breaks[index_t[i]]);
            }

            int d = b.length;
            ArrayList<Double> xs_temp = new ArrayList<Double>();
            for (int i = 0; i < lx; i++) {
                double t = xs.get(i);
                for (int j = 0; j < d; j++) {
                    xs_temp.add(t);

                }
            }
            for (int i = 0; i < index_t.length; i++) {
                index_t[i] = (index_t[i] + 1) * d;
            }
            double[] temp = new double[d];
            int q = 0;
            for (int i = -d; i < 0; i++) {
                temp[q++] = i;

            }

            double[][] index_sav = new double[d][lx];
            double[][] temp_sav = new double[lx][d];

            double[] new_index = new double[d * lx];
            int p = 0;
            for (int i = 0; i < lx; i++) {
                double t = 1 + index_t[i];
                for (int j = 0; j < d; j++) {
                    index_sav[j][i] = t;

                }
            }
            
            for (int i = 0; i < d; i++) {
                double t = temp[i];
                for (int j = 0; j < lx; j++) {
                    temp_sav[j][i] = t;
                }
            }
            ArrayList<Double> index1_temp = new ArrayList<Double>();
            ArrayList<Double> sav1_temp = new ArrayList<Double>();

            for (int i = 0; i < index_sav[0].length; i++) {
                for (int j = 0; j < index_sav.length; j++) {
                    index1_temp.add(index_sav[j][i]);
                }
            }
            for (int i = 0; i < temp_sav.length; i++) {
                for (int j = 0; j < temp_sav[0].length; j++) {
                    sav1_temp.add(temp_sav[i][j]);
                }
            }
            for (int i = 0; i < lx; i++) {
                for (int j = 0; j < d; j++) {
                    new_index[p] = index1_temp.get(p) + sav1_temp.get(p);
                    p++;
                }
            }
           

            ArrayList<Double> v_temp = new ArrayList<Double>();
            p = 0;
            for (int j = 0; j < new_index.length; j++) {
                if (new_index[j] - 1 < coeff.length) {
                    v_temp.add(coeff[(int) new_index[j] - 1][0]);
                }
            }
            ArrayList<Double> v1_temp = new ArrayList<Double>();
            v = new double[d][lx];

            for (int i = 1; i < coeff[0].length; i++) {
                v1_temp = new ArrayList<Double>();
                for (int j = 0; j < new_index.length; j++) {
                    v1_temp.add((xs_temp.get(j) * v_temp.get(j)) + coeff[(int) new_index[j] - 1][i]);
                }
                v_temp = new ArrayList<Double>();
                v_temp = v1_temp;
            }
            p = 0;
            for (int i = 0; i < v[0].length; i++) {
                for (int j = 0; j < v.length; j++) {
                    v[j][i] = v1_temp.get(p++);
                }
            }
        } catch (Exception e) {
            System.out.println(" " + e);
        }
        return v;

    }

    public void spdiags(double[][] arg1, double[] arg2, double arg3) {
        int p = arg2.length;
        int len[] = new int[p + 1];
        double max_min[] = new double[p - 1];
        int n = arg1.length;

        for (int i = 0; i < max_min.length; i++) {

        }

        for (int i = 0; i < p; i++) {
            double max = 1, min = arg3;
            for (int j = 0; j < arg2.length - 1; j++) {
                if (max < arg2[i]) {
                    max = arg2[i];
                }
            }
            for (int j = (int) arg3; j < arg3 - arg2.length; j++) {
                if (min > arg2[i]) {
                    min = arg2[i];
                }
            }
            len[i + 1] = (int) (len[i] + (min - max) + 1);
            //len[i+1]=len[i]+
        }
        ArrayList<Double> a = new ArrayList<Double>();

//        double a[][]=new double[][3];
        for (int k = 0; k < p; k++) {
            double max = 1, min = arg3;
            if (max < 1 - arg2[k]) {
                max = 1 - arg2[k];
            }
            if (min > n - arg2[k]) {
                min = n - arg2[k];

            }
            double i[] = new double[(int) (min - max) + 1];
            for (int x = 0, y = (int) max; x < i.length; x++, y++) {
                i[x] = y;
                a.add(i[x]);
                a.add(i[x] + arg2[k]);
                a.add(arg1[(int) (i[x] + 1 * arg2[k] - 1)][k]);

            }
        }
        double temp_a[][] = new double[a.size() / 3][3];
        int k = 0;
        int row[] = new int[temp_a.length];
        int column[] = new int[temp_a.length];
        int val[] = new int[temp_a.length];
        int x = 0, y = 0, z = 0;

        for (int i = 0; i < temp_a.length; i++) {
            for (int j = 0; j < 3; j++) {
                temp_a[i][j] = a.get(k);
                k++;
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < temp_a.length; j++) {
                if (i == 0) {
                    row[x] = (int) temp_a[j][i];
                    x++;
                }
                if (i == 1) {
                    column[y] = (int) temp_a[j][i];
                    y++;
                }
                if (i == 2) {
                    val[z] = (int) temp_a[j][i];
                    z++;
                }
                k++;
            }
        }
        for (int i = 0; i < row.length; i++) {
            row[i] = row[i] - 1;
        }
        for (int i = 0; i < column.length; i++) {
            column[i] = column[i] - 1;
        }

        res1 = new double[n][n];
        create_sparse(row, column, temp_a, val, n);
        res2 = new double[n][n];

        res2 = invert(res1);

    }

    public double[][] invert(double a[][]) {
        int n = a.length;
        double x[][] = new double[n][n];
        double b[][] = new double[n][n];
        int ind[] = new int[n];
        for (int i = 0; i < n; ++i) {
            b[i][i] = 1;
        }

        // Transform the matrix into an upper triangle
        gaussian(a, ind);

        // Update the matrix b[i][j] with the ratios stored
        for (int i = 0; i < n - 1; ++i) {
            for (int j = i + 1; j < n; ++j) {
                for (int k = 0; k < n; ++k) {
                    b[ind[j]][k]
                            -= a[ind[j]][i] * b[ind[i]][k];
                }
            }
        }

        // Perform backward substitutions
        for (int i = 0; i < n; ++i) {
            x[n - 1][i] = b[ind[n - 1]][i] / a[ind[n - 1]][n - 1];
            for (int j = n - 2; j >= 0; --j) {
                x[j][i] = b[ind[j]][i];
                for (int k = j + 1; k < n; ++k) {
                    x[j][i] -= a[ind[j]][k] * x[k][i];
                }
                x[j][i] /= a[ind[j]][j];
            }
        }
        return x;
    }

// Method to carry out the partial-pivoting Gaussian
// elimination.  Here index[] stores pivoting order.
    public void gaussian(double a[][], int ind[]) {
        int n = ind.length;
        double c[] = new double[n];

        // Initialize the index
        for (int i = 0; i < n; ++i) {
            ind[i] = i;
        }

        // Find the rescaling factors, one from each row
        for (int i = 0; i < n; ++i) {
            double c1 = 0;
            for (int j = 0; j < n; ++j) {
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) {
                    c1 = c0;
                }
            }
            c[i] = c1;
        }

        // Search the pivoting element from each column
        int k = 0;
        for (int j = 0; j < n - 1; ++j) {
            double pi1 = 0;
            for (int i = j; i < n; ++i) {
                double pi0 = Math.abs(a[ind[i]][j]);
                pi0 /= c[ind[i]];
                if (pi0 > pi1) {
                    pi1 = pi0;
                    k = i;
                }
            }

            // Interchange rows according to the pivoting order
            int itmp = ind[j];
            ind[j] = ind[k];
            ind[k] = itmp;
            for (int i = j + 1; i < n; ++i) {
                double pj = a[ind[i]][j] / a[ind[j]][j];

                // Record pivoting ratios below the diagonal
                a[ind[i]][j] = pj;

                // Modify other elements accordingly
                for (int l = j + 1; l < n; ++l) {
                    a[ind[i]][l] -= pj * a[ind[j]][l];
                }
            }
        }
    }

    public void create_sparse(int row[], int column[], double a[][], int val[], int n) {
        double[][] b1 = new double[n][n];
        /*int k = 0, index_r = 0, index_c = 0;
        int sum = 0;
        for (int i = 0; i < row.length; i++) {
            sum = 0;
            index_r = 0;
            index_c = 0;
            for (int j = i + 1; j < row.length; j++) {
                if ((row[i] == row[j])) {
                    index_r = j;
                }
                if (column[i] == column[j]) {
                    index_c = j;
                }
                if (index_c != 0 && index_r != 0) {
                    if (index_r == index_c) {
                        sum += val[i];
                    }
                }
                if (sum == 0) {
                    sum = val[i];
                }

            }
            res1[row[i]][column[i]] = sum;
        }*/
        int sum = 0;
        for (int i = 0; i < row.length; i++) {
            sum = 0;
            int ind_r = row[i];
            int ind_c = column[i];
            for (int j = i + 1; j < column.length; j++) {
                if (row[j] != -1 && column[j] != -1) {
                    if (ind_r == row[j]) {
                        if (ind_c == column[j]) {
                            row[j] = -1;
                            column[j] = -1;
                            sum = sum + val[i];
                        }
                    }
                }
            }
            if (sum == 0) {
                sum = val[i];
            }
            if (row[i] != -1 && column[i] != -1) {
                res1[row[i]][column[i]] = sum;
            }
        }

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
