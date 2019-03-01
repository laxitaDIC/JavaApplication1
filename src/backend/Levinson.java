package backend;

/**Levinson calculation
 * Input 'r' variable of ValueselectedFrame.java
 * Output as 'k_temp' as column vector of type double call in ValueSelectedFrame.java
*/
public class Levinson {


    public double[] levinson(double[] r) throws Exception {

        int n = r.length;
        double[] a = new double[n];
        double[] k = new double[n];

        double a_temp[] = new double[n];
        double alpha, epsilon;
        /* n <= N = constant  */
        int i, j;

       // k[0] = 0.0;
        /* unused */

        a[0] = 1.0;
        a_temp[0] = 1.0;
        /* unnecessary but consistent */

        alpha = r[0];

        for (i = 1; i < n; i++) {
            epsilon = r[i];
            /* epsilon = a[0]*r[i]; */
            for (j = 1; j < i; j++) {
                epsilon += a[j] * r[i - j];
            }

            a[i] = k[i] = -epsilon / alpha;
            alpha = alpha * (1.0 - k[i] * k[i]);

            for (j = 1; j < i ; j++) {
                a_temp[j] = a[j] + k[i] * a[i - j];
                /* update a[] array into temporary array */
            }
            for (j = 1; j < i; j++) {
                a[j] = a_temp[j];
                /* update a[] array */
            }
        }
        double k_temp[]=new double[k.length];    
        for (j = 0; j < k_temp.length; j++) {
            k_temp[j]=k[j];
        }
        
        return k_temp;
    }

}
