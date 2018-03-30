package ru.pussy_penetrator.graph.model;

/**
 * Created by Pussy_penetrator on 27.09.2016.
 */
abstract class NewtonFunc {

    private final double[]   x;
    private final double[][] f;

    public NewtonFunc(double a, double b, int n) {
        //calculate x
        x = new double[n + 1];
        for (int i = 0; i <= n; i++)
            x[i] = a + i * (b - a) / n;

        //calculate table
        f = new double[x.length][x.length];

        for (int i = 0; i < x.length; i++)
            f[i][0] = get(x[i]);

        for (int j = 1; j < x.length; j++)
            for (int i = 0; i < x.length - j; i++)
                f[i][j] = (f[i + 1][j - 1] - f[i][j - 1]) / (x[i + j] - x[i]);
    }

    abstract public double get(double x);

    public double getApprox(double arg) {
        double m = 1;

        double ans = 0;
        for (int j = 0; j < x.length; j++) {
            if (j != 0)
                m *= (arg - x[j - 1]);
            ans += m * f[0][j];
        }

        return ans;
    }

}
