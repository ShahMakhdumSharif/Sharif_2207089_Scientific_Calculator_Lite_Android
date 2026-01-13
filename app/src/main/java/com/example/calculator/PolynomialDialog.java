package com.example.calculator;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;

public class PolynomialDialog {

    public interface Callback {
        void onResult(String result);
    }

    public static void show(Context ctx, Callback cb) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.dialog_polynomial, null);

        EditText etCoeffs = view.findViewById(R.id.et_coeffs);
        EditText etX = view.findViewById(R.id.et_xvalue);

        AlertDialog d = new AlertDialog.Builder(ctx)
                .setTitle("Polynomial")
                .setView(view)
                .setPositiveButton("Compute", (dialog, which) -> {
                    String coeffText = etCoeffs.getText().toString().trim();
                    String xText = etX.getText().toString().trim();
                    if (coeffText.isEmpty() || xText.isEmpty()) {
                        Toast.makeText(ctx, "Both fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double x = Double.parseDouble(xText);
                        double[] coeffs = parseCoeffs(coeffText);
                        if (coeffs.length == 0) {
                            Toast.makeText(ctx, "Invalid coefficients", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        double res = evaluate(coeffs, x);
                        String out;
                        if (Math.abs(res - Math.round(res)) < 1e-10) out = String.valueOf((long)Math.round(res));
                        else out = new DecimalFormat("0.##########").format(res);
                        if (cb != null) cb.onResult(out);
                    } catch (NumberFormatException nfe) {
                        Toast.makeText(ctx, "Invalid number format", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();

        d.show();
    }

    // Port of desktop parseCoeffs + evaluate
    private static double[] parseCoeffs(String s) {
        String[] parts = s.trim().split("[,\\s]+");
        double[] c = new double[parts.length];
        for (int i = 0; i < parts.length; i++) c[i] = Double.parseDouble(parts[i]);
        return c;
    }

    private static double evaluate(double[] coeffs, double x) {
        double res = 0;
        double pow = 1;
        for (int i = 0; i < coeffs.length; i++) {
            res += coeffs[i] * pow;
            pow *= x;
        }
        return res;
    }
}
