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
import android.widget.TextView;

public class LinearDialog {
    public interface Callback { void onResult(String expr, String result); }

    public static void show(Context ctx, Callback cb) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.dialog_linear, null);

        LinearLayout container = view.findViewById(R.id.container_augmented);
        Spinner spSize = view.findViewById(R.id.sp_linear_size);

        final EditText[][] grid = new EditText[3][4]; // max 3 vars + augmented

        ArrayAdapter<Integer> sizeAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, new Integer[]{2,3});
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSize.setAdapter(sizeAdapter);

        spSize.setSelection(0);
        spSize.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view1, int position, long id) {
                int n = (Integer) spSize.getSelectedItem();
                buildAugmentedGrid(ctx, container, grid, n);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        AlertDialog d = new AlertDialog.Builder(ctx)
                .setTitle("Linear System")
                .setView(view)
                .setPositiveButton("Compute", (dialog, which) -> {
                    int n = (Integer) spSize.getSelectedItem();
                    // validate inputs
                    if (!validateAugmentedGrid(ctx, grid, n)) return;
                    String expr = readAugmentedGrid(grid, n);
                    try {
                        String res = com.example.calculator.controller.Calculation.linear.computeFromString(expr);
                        if (cb != null) cb.onResult(expr, res);
                    } catch (Exception ex) { Toast.makeText(ctx, "Syntax Error", Toast.LENGTH_SHORT).show(); }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();

        // initialize
        buildAugmentedGrid(ctx, container, grid, (Integer) spSize.getSelectedItem());
        d.show();
    }

    private static void buildAugmentedGrid(Context ctx, LinearLayout container, EditText[][] grid, int n) {
        container.removeAllViews();
        int padding = (int) (4 * ctx.getResources().getDisplayMetrics().density);
        for (int r = 0; r < n; r++) {
            LinearLayout row = new LinearLayout(ctx);
            row.setOrientation(LinearLayout.HORIZONTAL);
                for (int c = 0; c < n + 1; c++) {
                EditText et = new EditText(ctx);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                lp.setMargins(padding, padding, padding, padding);
                et.setLayoutParams(lp);
                et.setSingleLine();
                et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                et.setHint(c == n ? "b" : "a");
                row.addView(et);
                grid[r][c] = et;
            }
            container.addView(row);
        }
    }

    private static boolean validateAugmentedGrid(Context ctx, EditText[][] grid, int n) {
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n + 1; c++) {
                EditText et = grid[r][c];
                if (et == null) { Toast.makeText(ctx, "Missing cell at " + r + "," + c, Toast.LENGTH_SHORT).show(); return false; }
                String t = et.getText().toString().trim();
                if (t.isEmpty()) { Toast.makeText(ctx, "All augmented matrix cells are required", Toast.LENGTH_SHORT).show(); return false; }
                try { Double.parseDouble(t); } catch (NumberFormatException nfe) { Toast.makeText(ctx, "Invalid number at row " + (r+1) + ", col " + (c+1), Toast.LENGTH_SHORT).show(); return false; }
            }
        }
        return true;
    }

    private static String readAugmentedGrid(EditText[][] grid, int n) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n + 1; c++) {
                String v = grid[r][c] != null ? grid[r][c].getText().toString().trim() : "";
                if (v.isEmpty()) return "";
                sb.append(v);
                if (c < n) sb.append(",");
            }
            if (r < n - 1) sb.append(";");
        }
        return sb.toString();
    }
}
