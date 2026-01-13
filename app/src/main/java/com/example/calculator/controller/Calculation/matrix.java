package com.example.calculator.controller.Calculation;

public class matrix {

    public static double[][] parseMatrix(String s) {
        String[] rows = s.split(";");
        int n = rows.length;
        double[][] m = new double[n][];
        for (int i = 0; i < n; i++) {
            String row = rows[i].trim();
            if (row.isEmpty()) continue;
            String[] elems = row.split("[,\\s]+");
            m[i] = new double[elems.length];
            for (int j = 0; j < elems.length; j++) m[i][j] = Double.parseDouble(elems[j]);
        }
        return m;
    }

    public static double[][] add(double[][] A, double[][] B) {
        int n = A.length; double[][] R = new double[n][n];
        for (int i = 0;i<n;i++) for (int j=0;j<n;j++) R[i][j] = A[i][j] + B[i][j];
        return R;
    }

    public static double[][] sub(double[][] A, double[][] B) {
        int n = A.length; double[][] R = new double[n][n];
        for (int i = 0;i<n;i++) for (int j=0;j<n;j++) R[i][j] = A[i][j] - B[i][j];
        return R;
    }

    public static double[][] mul(double[][] A, double[][] B) {
        int n = A.length; double[][] R = new double[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) {
            double sum = 0;
            for (int k=0;k<n;k++) sum += A[i][k]*B[k][j];
            R[i][j] = sum;
        }
        return R;
    }

    public static String toString(double[][] M) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<M.length;i++) {
            for (int j=0;j<M[i].length;j++) {
                if (j>0) sb.append(',');
                double v = M[i][j];
                if (Math.abs(v - Math.round(v)) < 1e-10) sb.append((long)Math.round(v)); else sb.append(v);
            }
            if (i < M.length-1) sb.append(';');
        }
        return sb.toString();
    }

    public static String computeFromString(String expr, int matrixSize, String matrixOp) {
        String[] parts = expr.split("\\|");
        if (parts.length != 2) throw new IllegalArgumentException("Need two matrices separated by |");
        double[][] A = parseMatrix(parts[0].trim());
        double[][] B = parseMatrix(parts[1].trim());
        if (A.length != matrixSize || A[0].length != matrixSize) throw new IllegalArgumentException("Matrix A size mismatch");
        if (B.length != matrixSize || B[0].length != matrixSize) throw new IllegalArgumentException("Matrix B size mismatch");
        double[][] R;
        switch (matrixOp) {
            case "ADD": R = add(A, B); break;
            case "SUB": R = sub(A, B); break;
            default: R = mul(A, B); break;
        }
        return toString(R);
    }
}
