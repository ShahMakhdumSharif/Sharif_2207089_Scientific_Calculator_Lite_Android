package com.example.calculator.controller.Calculation;

public class polynomial {

    public static double evaluate(double[] coeffs, double x) {
        double res = 0;
        double pow = 1;
        for (int i = 0; i < coeffs.length; i++) {
            res += coeffs[i] * pow;
            pow *= x;
        }
        return res;
    }

    public static double[] parseCoeffs(String s) {
        String[] parts = s.trim().split("[,\\s]+");
        double[] c = new double[parts.length];
        for (int i = 0; i < parts.length; i++) c[i] = Double.parseDouble(parts[i]);
        return c;
    }

    public static String computeFromString(String expr, int maxDegree) {
        String[] parts = expr.split("@");
        if (parts.length != 2) throw new IllegalArgumentException("Expect coeffs@x");
        double[] coeffs = parseCoeffs(parts[0]);
        double x = Double.parseDouble(parts[1].trim());
        if (coeffs.length - 1 > maxDegree) throw new IllegalArgumentException("Degree mismatch");
        double res = evaluate(coeffs, x);
        if (Math.abs(res - Math.round(res)) < 1e-10) return String.valueOf((long) Math.round(res));
        return new java.text.DecimalFormat("0.##########").format(res);
    }
}
