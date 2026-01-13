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

public class MatrixDialog {

    public interface Callback { void onResult(String expr, String result); }

    public static void show(Context ctx, Callback cb) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.dialog_matrix, null);

    LinearLayout containerA = view.findViewById(R.id.container_matrix_a);
    LinearLayout containerB = view.findViewById(R.id.container_matrix_b);
    Spinner spSize = view.findViewById(R.id.sp_size);
    Spinner spOp = view.findViewById(R.id.sp_op);

    // helper to build grid
    final EditText[][] gridA = new EditText[3][3];
    final EditText[][] gridB = new EditText[3][3];

    ArrayAdapter<Integer> sizeAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, new Integer[]{2,3});
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSize.setAdapter(sizeAdapter);

        ArrayAdapter<String> opAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, new String[]{"ADD","SUB","MUL"});
        opAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOp.setAdapter(opAdapter);

        // when size selected, build grids
        spSize.setSelection(0);
        spSize.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view1, int position, long id) {
                int size = (Integer) spSize.getSelectedItem();
                buildMatrixGrid(ctx, containerA, gridA, size);
                buildMatrixGrid(ctx, containerB, gridB, size);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        AlertDialog d = new AlertDialog.Builder(ctx)
                .setTitle("Matrix")
                .setView(view)
                .setPositiveButton("Compute", (dialog, which) -> {
                    int size = (Integer) spSize.getSelectedItem();
                    String op = (String) spOp.getSelectedItem();
                    // validate inputs
                    if (!validateMatrixGrid(ctx, gridA, size)) return;
                    if (!validateMatrixGrid(ctx, gridB, size)) return;
                    String a = readGridToString(gridA, size);
                    String b = readGridToString(gridB, size);
                    String expr = a + "|" + b;
                    try {
                        String res = com.example.calculator.controller.Calculation.matrix.computeFromString(expr, size, op);
                        if (cb != null) cb.onResult(expr, res);
                    } catch (Exception ex) {
                        Toast.makeText(ctx, "Syntax Error", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();

        // initialize grids for default size
        int initialSize = (Integer) spSize.getSelectedItem();
        buildMatrixGrid(ctx, containerA, gridA, initialSize);
        buildMatrixGrid(ctx, containerB, gridB, initialSize);

        d.show();
    }

    private static void buildMatrixGrid(Context ctx, LinearLayout container, EditText[][] grid, int size) {
        container.removeAllViews();
        int padding = (int) (4 * ctx.getResources().getDisplayMetrics().density);
        for (int r = 0; r < size; r++) {
            LinearLayout row = new LinearLayout(ctx);
            row.setOrientation(LinearLayout.HORIZONTAL);
                for (int c = 0; c < size; c++) {
                EditText et = new EditText(ctx);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                lp.setMargins(padding, padding, padding, padding);
                et.setLayoutParams(lp);
                et.setSingleLine();
                // numeric keyboard and allow signed/decimal
                et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                et.setHint("0");
                row.addView(et);
                grid[r][c] = et;
            }
            container.addView(row);
        }
    }

    private static boolean validateMatrixGrid(Context ctx, EditText[][] grid, int size) {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                EditText et = grid[r][c];
                if (et == null) { Toast.makeText(ctx, "Missing cell at " + r + "," + c, Toast.LENGTH_SHORT).show(); return false; }
                String t = et.getText().toString().trim();
                if (t.isEmpty()) { Toast.makeText(ctx, "All matrix cells are required", Toast.LENGTH_SHORT).show(); return false; }
                try { Double.parseDouble(t); } catch (NumberFormatException nfe) { Toast.makeText(ctx, "Invalid number at row " + (r+1) + ", col " + (c+1), Toast.LENGTH_SHORT).show(); return false; }
            }
        }
        return true;
    }

    private static String readGridToString(EditText[][] grid, int size) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                String v = grid[r][c] != null ? grid[r][c].getText().toString().trim() : "";
                if (v.isEmpty()) return ""; // require full grid
                sb.append(v);
                if (c < size - 1) sb.append(",");
            }
            if (r < size - 1) sb.append(";");
        }
        return sb.toString();
    }
}
