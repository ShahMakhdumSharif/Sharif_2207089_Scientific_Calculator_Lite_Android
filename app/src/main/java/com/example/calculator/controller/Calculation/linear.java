package com.example.calculator.controller.Calculation;

public class linear {

    public static String computeFromString(String expr) {
        String[] rows = expr.split(";");
        int n = rows.length;
        if (n < 2 || n > 3) throw new IllegalArgumentException("Only 2 or 3 eq supported");
        double[][] A = new double[n][n];
        double[] b = new double[n];
        for (int i=0;i<n;i++) {
            String[] vals = rows[i].trim().split("[,\\s]+");
            if (vals.length != n+1) throw new IllegalArgumentException("Wrong row length");
            for (int j=0;j<n;j++) A[i][j] = Double.parseDouble(vals[j]);
            b[i] = Double.parseDouble(vals[n]);
        }
        double[] sol = solveLinear(A, b);
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<sol.length;i++) {
            if (i>0) sb.append(',');
            double v = sol[i];
            if (Math.abs(v - Math.round(v)) < 1e-10) sb.append((long)Math.round(v)); else sb.append(v);
        }
        return sb.toString();
    }

    public static double[] solveLinear(double[][] A, double[] b) {
        int n = b.length;
        double[][] M = new double[n][n+1];
        for (int i=0;i<n;i++) {
            System.arraycopy(A[i],0,M[i],0,n);
            M[i][n] = b[i];
        }
        for (int i=0;i<n;i++) {
            int max = i;
            for (int j=i+1;j<n;j++) if (Math.abs(M[j][i]) > Math.abs(M[max][i])) max = j;
            if (Math.abs(M[max][i]) < 1e-12) throw new IllegalArgumentException("Singular");
            double[] tmp = M[i]; M[i] = M[max]; M[max] = tmp;
            double piv = M[i][i];
            for (int j=i;j<=n;j++) M[i][j] /= piv;
            for (int r=0;r<n;r++) if (r!=i) {
                double factor = M[r][i];
                for (int c=i;c<=n;c++) M[r][c] -= factor * M[i][c];
            }
        }
        double[] x = new double[n];
        for (int i=0;i<n;i++) x[i] = M[i][n];
        return x;
    }
}
