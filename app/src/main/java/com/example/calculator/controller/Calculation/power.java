package com.example.calculator.controller.Calculation;

public class power {

    public static double pow(double base, double exp) {
        return Math.pow(base, exp);
    }

    public static String computeFromString(String expr) {
        String[] parts = expr.split("[,\\s]+");
        if (parts.length != 2) throw new IllegalArgumentException("Expect base,exponent");
        double base = Double.parseDouble(parts[0]);
        double exp = Double.parseDouble(parts[1]);
        double r = pow(base, exp);
        if (Math.abs(r - Math.round(r)) < 1e-10) return String.valueOf((long)Math.round(r));
        return new java.text.DecimalFormat("0.##########").format(r);
    }
}
