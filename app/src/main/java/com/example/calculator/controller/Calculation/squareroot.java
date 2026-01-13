package com.example.calculator.controller.Calculation;

public class squareroot {
    public static double sqrt(double x) {
        return Math.sqrt(x);
    }

    public static String computeFromString(String expr) {
        double v = Double.parseDouble(expr.trim());
        if (v < 0) return "NaN";
        double r = sqrt(v);
        if (Math.abs(r - Math.round(r)) < 1e-10) return String.valueOf((long)Math.round(r));
        return new java.text.DecimalFormat("0.##########").format(r);
    }
}
