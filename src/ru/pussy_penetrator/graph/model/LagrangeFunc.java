package ru.pussy_penetrator.graph.model;

/**
 * Created by Pussy_penetrator on 20.09.2016.
 */
public abstract class LagrangeFunc {

    public final double a, b;
    public final int n;

    private final double[] x, y;

    public LagrangeFunc(double a, double b, int n) {
        if (a >= b || n < 2)
            throw new IllegalArgumentException();

        this.a = a;
        this.b = b;
        this.n = n;

        x = new double[n + 1];
        y = new double[n + 1];

        for (int i = 0; i <= n; i++) {
            x[i] = interpolate(i);
            y[i] = get(x[i]);
        }
    }

    public double getWidth() {
        return b - a;
    }

    abstract protected double interpolate(int i);

    abstract public double get(double x);

    public double getApprox(double arg) {
        double result = 0;

        for (int i = 0; i < x.length; i++) {
            double k = 1;
            for (int j = 0; j < y.length; j++) {
                if (j != i)
                    k *= (arg - x[j]) / (x[i] - x[j]);
            }
            result += k * y[i];
        }

        return result;
    }

}
