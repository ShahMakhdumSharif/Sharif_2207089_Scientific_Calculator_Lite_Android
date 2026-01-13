package com.example.calculator;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DecimalFormat;

public class PolynomialDialog {

    public interface Callback { void onResult(String expr, String result); }

    public static void show(android.content.Context ctx, Callback cb) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.dialog_polynomial, null);

        Spinner spDegree = view.findViewById(R.id.sp_degree);
        LinearLayout container = view.findViewById(R.id.container_coeffs);

        ArrayAdapter<Integer> degreeAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, new Integer[]{1,2});
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDegree.setAdapter(degreeAdapter);

        final EditText[] coeffInputs = new EditText[4]; // support up to degree 3 if we extend later

        spDegree.setSelection(1); // default degree 2
        spDegree.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view1, int position, long id) {
                int deg = (Integer) spDegree.getSelectedItem();
                buildCoeffInputs(ctx, container, coeffInputs, deg);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        // initialize
        buildCoeffInputs(ctx, container, coeffInputs, (Integer) spDegree.getSelectedItem());

        AlertDialog d = new AlertDialog.Builder(ctx)
                .setTitle("Polynomial Solver")
                .setView(view)
                .setPositiveButton("Solve", (dialog, which) -> {
                    int deg = (Integer) spDegree.getSelectedItem();
                    double[] coeffs = new double[deg + 1];
                    try {
                        for (int i = 0; i <= deg; i++) {
                            String t = coeffInputs[i].getText().toString().trim();
                            if (t.isEmpty()) { Toast.makeText(ctx, "All coefficients required", Toast.LENGTH_SHORT).show(); return; }
                            coeffs[i] = Double.parseDouble(t);
                        }
                        // highest-degree coefficient must not be zero
                        if (Math.abs(coeffs[deg]) < 1e-12) { Toast.makeText(ctx, "Leading coefficient (highest degree) cannot be zero", Toast.LENGTH_SHORT).show(); return; }
                        String res = solvePolynomial(coeffs);
                        // build expression string to represent coefficients, e.g. "a0,a1,a2@deg"
                        StringBuilder exprBuilder = new StringBuilder();
                        for (int i = 0; i <= deg; i++) {
                            if (i > 0) exprBuilder.append(",");
                            exprBuilder.append(coeffs[i]);
                        }
                        String expr = exprBuilder.toString() + "@deg=" + deg;
                        if (cb != null) cb.onResult(expr, res);
                    } catch (NumberFormatException nfe) {
                        Toast.makeText(ctx, "Invalid number format", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();

        d.show();
    }

    private static void buildCoeffInputs(android.content.Context ctx, LinearLayout container, EditText[] inputs, int deg) {
        container.removeAllViews();
        int padding = (int) (4 * ctx.getResources().getDisplayMetrics().density);
        for (int i = 0; i <= deg; i++) {
            LinearLayout row = new LinearLayout(ctx);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lpRow);

            EditText et = new EditText(ctx);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            lp.setMargins(padding, padding, padding, padding);
            et.setLayoutParams(lp);
            et.setSingleLine(true);
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            et.setHint("a" + i);
            row.addView(et);
            container.addView(row);
            inputs[i] = et;
        }
    }

    private static String solvePolynomial(double[] coeffs) {
        int deg = coeffs.length - 1;
        DecimalFormat fmt = new DecimalFormat("0.##########");
        if (deg == 1) {
            // a0 + a1 x = 0 => x = -a0 / a1
            double a0 = coeffs[0], a1 = coeffs[1];
            if (Math.abs(a1) < 1e-12) return "No solution (a1 == 0)";
            double x = -a0 / a1;
            return fmt.format(x);
        } else if (deg == 2) {
            // a0 + a1 x + a2 x^2 = 0 -> ax^2 + bx + c = 0 where a= a2, b=a1, c=a0
            double c = coeffs[0], b = coeffs[1], a = coeffs[2];
            if (Math.abs(a) < 1e-12) {
                // fallback to linear
                if (Math.abs(b) < 1e-12) return "No solution";
                double x = -c / b;
                return fmt.format(x);
            }
            double disc = b * b - 4 * a * c;
            if (disc >= 0) {
                double r1 = (-b + Math.sqrt(disc)) / (2 * a);
                double r2 = (-b - Math.sqrt(disc)) / (2 * a);
                return fmt.format(r1) + " , " + fmt.format(r2);
            } else {
                double real = -b / (2 * a);
                double imag = Math.sqrt(-disc) / (2 * a);
                return fmt.format(real) + " + " + fmt.format(imag) + "i , " + fmt.format(real) + " - " + fmt.format(imag) + "i";
            }
        } else {
            return "Degree >2 solver not implemented";
        }
    }
}
