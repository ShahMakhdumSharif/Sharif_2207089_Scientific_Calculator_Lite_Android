package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class UserInterfaceActivity extends AppCompatActivity {

    private EditText etDisplay;

    private String lastResult = "0";
    private String currentOperator = "";
    private double firstValue = 0;

    private DatabaseReference userRef;
    private String username;
    private String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);

        etDisplay = findViewById(R.id.et_display);

    // Get user key and username from login
    userKey = getIntent().getStringExtra("USER_KEY");
    username = getIntent().getStringExtra("USERNAME");
        if (username == null) username = userKey;

        if (userKey == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userKey);

        initButtons();
        loadAllowedOperations();
    }

    /* ---------------- BUTTON INIT ---------------- */

    private void initButtons() {

        // Number buttons
        int[] numbers = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9
        };

        for (int id : numbers) {
            Button btn = findViewById(id);
            btn.setOnClickListener(v -> appendNumber(((Button) v).getText().toString()));
        }

    // Operators (use ids defined in layout: snake_case)
    findViewById(R.id.btn_add).setOnClickListener(v -> setOperator("+"));
    findViewById(R.id.btn_subtract).setOnClickListener(v -> setOperator("-"));
    findViewById(R.id.btn_multiply).setOnClickListener(v -> setOperator("*"));
    findViewById(R.id.btn_divide).setOnClickListener(v -> setOperator("/"));

    // Equal
    findViewById(R.id.btn_equal).setOnClickListener(v -> calculate());

        // Clear
        findViewById(R.id.btn_ac).setOnClickListener(v -> etDisplay.setText(""));

        // Delete last char
        findViewById(R.id.btn_del).setOnClickListener(v -> {
            String s = etDisplay.getText().toString();
            if (!s.isEmpty()) {
                etDisplay.setText(s.substring(0, s.length() - 1));
            }
        });

    // Ans
    findViewById(R.id.btn_ans).setOnClickListener(v ->
        etDisplay.setText(lastResult));

    // Square root
    findViewById(R.id.btn_sqrt).setOnClickListener(v -> {
            try {
                double val = Double.parseDouble(etDisplay.getText().toString());
                double res = Math.sqrt(val);
                lastResult = format(res);
                etDisplay.setText(lastResult);
            } catch (Exception e) {
                showError();
            }
        });

    // Power x^
    findViewById(R.id.btn_power).setOnClickListener(v -> setOperator("^"));

        // Logout
        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            startActivity(new Intent(this, UserLoginActivity.class));
            finish();
        });

        // History button - open user's history
        findViewById(R.id.btn_history).setOnClickListener(v -> {
            try {
                Intent i = new Intent(UserInterfaceActivity.this, UserHistoryActivity.class);
                // pass the username (raw if available) so UserHistoryActivity can display and lookup
                i.putExtra("username", username != null ? username : userKey);
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(this, "Unable to open history", Toast.LENGTH_SHORT).show();
            }
        });

        // Advanced operations: Matrix / Polynomial / Linear
        findViewById(R.id.btn_mat).setOnClickListener(v -> {
            MatrixDialog.show(this, (expr, result) -> {
                etDisplay.setText(result);
                try {
                    java.util.HashMap<String, Object> h = new java.util.HashMap<>();
                    h.put("expression", expr);
                    h.put("result", result);
                    h.put("ts", System.currentTimeMillis());
                    if (userRef != null) userRef.child("history").push().setValue(h);
                } catch (Exception ignored) { }
            });
        });

        findViewById(R.id.btn_poly).setOnClickListener(v -> {
            PolynomialDialog.show(this, (expr, result) -> {
                etDisplay.setText(result);
                try {
                    java.util.HashMap<String, Object> h = new java.util.HashMap<>();
                    h.put("expression", expr);
                    h.put("result", result);
                    h.put("ts", System.currentTimeMillis());
                    if (userRef != null) userRef.child("history").push().setValue(h);
                } catch (Exception ignored) { }
            });
        });

        findViewById(R.id.btn_lin).setOnClickListener(v -> {
            LinearDialog.show(this, (expr, result) -> {
                etDisplay.setText(result);
                try {
                    java.util.HashMap<String, Object> h = new java.util.HashMap<>();
                    h.put("expression", expr);
                    h.put("result", result);
                    h.put("ts", System.currentTimeMillis());
                    if (userRef != null) userRef.child("history").push().setValue(h);
                } catch (Exception ignored) { }
            });
        });
    }


    private void setOperator(String op) {
        try {
            firstValue = Double.parseDouble(etDisplay.getText().toString());
            currentOperator = op;
            etDisplay.setText("");
        } catch (Exception e) {
            showError();
        }
    }

    private void calculate() {
        try {
            double secondValue = Double.parseDouble(etDisplay.getText().toString());
            double result;

            switch (currentOperator) {
                case "+": result = firstValue + secondValue; break;
                case "-": result = firstValue - secondValue; break;
                case "*": result = firstValue * secondValue; break;
                case "/":
                    if (secondValue == 0) {
                        showError();
                        return;
                    }
                    result = firstValue / secondValue;
                    break;
                case "^":
                    result = Math.pow(firstValue, secondValue);
                    break;
                default:
                    return;
            }

            lastResult = format(result);
            etDisplay.setText(lastResult);
            // Save exact expression (constructed from entered operands) and result to history
            try {
                String exprStr = format(firstValue) + " " + currentOperator + " " + format(secondValue);
                java.util.HashMap<String, Object> h = new java.util.HashMap<>();
                h.put("expression", exprStr);
                h.put("result", lastResult);
                h.put("ts", System.currentTimeMillis());
                if (userRef != null) userRef.child("history").push().setValue(h);
            } catch (Exception ignored) { }
            currentOperator = "";

        } catch (Exception e) {
            showError();
        }
    }

    /* ---------------- FIREBASE OPERATION LIMIT ---------------- */

    private void loadAllowedOperations() {

        userRef.child("allowed_operations")
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.exists()) return;

                    Map<String, Boolean> ops =
                            (Map<String, Boolean>) snapshot.getValue();

                    if (ops == null) return;

                    setEnabled(R.id.btn_add, ops.containsKey("+"));
                    setEnabled(R.id.btn_subtract, ops.containsKey("-"));
                    setEnabled(R.id.btn_multiply, ops.containsKey("*"));
                    setEnabled(R.id.btn_divide, ops.containsKey("/"));
                    setEnabled(R.id.btn_sqrt, ops.containsKey("sqrt"));
                    setEnabled(R.id.btn_power, ops.containsKey("^"));

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load permissions", Toast.LENGTH_SHORT).show());
    }

    private void setEnabled(int id, boolean enabled) {
        Button b = findViewById(id);
        if (b != null) b.setEnabled(enabled);
    }

    /* ---------------- HELPERS ---------------- */

    private void showError() {
        Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
        etDisplay.setText("");
    }

    private String format(double d) {
        if (d == (long) d)
            return String.valueOf((long) d);
        return String.valueOf(d);
    }

    // Append a digit or dot to the display with simple validation (prevents multiple dots)
    private void appendNumber(String s) {
        if (etDisplay == null || s == null) return;
        String cur = etDisplay.getText().toString();
        if (".".equals(s)) {
            if (cur.contains(".")) return; // avoid multiple decimal points
            if (cur.isEmpty()) etDisplay.setText("0"); // leading dot -> 0.
        }
        etDisplay.append(s);
    }
}