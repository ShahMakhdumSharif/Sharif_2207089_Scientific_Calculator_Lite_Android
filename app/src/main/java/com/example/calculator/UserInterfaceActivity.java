package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserInterfaceActivity extends AppCompatActivity {

    private String username;
    private DatabaseReference usersRef;
    private EditText etDisplay;
    private TextView tvInstruction;

    private String lastResult = ""; // stores last calculation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        username = getIntent().getStringExtra("USERNAME");
        if (username == null) username = "Unknown";

        etDisplay = findViewById(R.id.et_display);
        tvInstruction = findViewById(R.id.tv_instruction);

        loadAssignedOperation();

        // Buttons
        Button btnLogout = findViewById(R.id.btn_logout);
        Button btnHistory = findViewById(R.id.btn_history);

        Button btnAdd = findViewById(R.id.btn_add);
        Button btnSubtract = findViewById(R.id.btn_subtract);
        Button btnMultiply = findViewById(R.id.btn_multiply);
        Button btnDivide = findViewById(R.id.btn_divide);
        Button btnEqual = findViewById(R.id.btn_equal);
        Button btnAns = findViewById(R.id.btn_ans);

        Button btnDel = findViewById(R.id.btn_del);
        Button btnAC = findViewById(R.id.btn_ac);

        Button btn0 = findViewById(R.id.btn_0);
        Button btn1 = findViewById(R.id.btn_1);
        Button btn2 = findViewById(R.id.btn_2);
        Button btn3 = findViewById(R.id.btn_3);
        Button btn4 = findViewById(R.id.btn_4);
        Button btn5 = findViewById(R.id.btn_5);
        Button btn6 = findViewById(R.id.btn_6);
        Button btn7 = findViewById(R.id.btn_7);
        Button btn8 = findViewById(R.id.btn_8);
        Button btn9 = findViewById(R.id.btn_9);
        Button btnDot = findViewById(R.id.btn_dot);

        Button btnPower = findViewById(R.id.btn_power);
        Button btnSqrt = findViewById(R.id.btn_sqrt);
        Button btnMod = findViewById(R.id.btn_mod);
        Button btnExp = findViewById(R.id.btn_exp);

        Button btnMat = findViewById(R.id.btn_mat);
        Button btnPoly = findViewById(R.id.btn_poly);
        Button btnLin = findViewById(R.id.btn_lin);

        // Logout and history
        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(UserInterfaceActivity.this, UserLoginActivity.class));
            finish();
        });
        btnHistory.setOnClickListener(v -> Toast.makeText(this, "History clicked", Toast.LENGTH_SHORT).show());

        // Number and dot buttons
        btn0.setOnClickListener(v -> etDisplay.append("0"));
        btn1.setOnClickListener(v -> etDisplay.append("1"));
        btn2.setOnClickListener(v -> etDisplay.append("2"));
        btn3.setOnClickListener(v -> etDisplay.append("3"));
        btn4.setOnClickListener(v -> etDisplay.append("4"));
        btn5.setOnClickListener(v -> etDisplay.append("5"));
        btn6.setOnClickListener(v -> etDisplay.append("6"));
        btn7.setOnClickListener(v -> etDisplay.append("7"));
        btn8.setOnClickListener(v -> etDisplay.append("8"));
        btn9.setOnClickListener(v -> etDisplay.append("9"));
        btnDot.setOnClickListener(v -> etDisplay.append("."));

        // Operators
        btnAdd.setOnClickListener(v -> etDisplay.append("+"));
        btnSubtract.setOnClickListener(v -> etDisplay.append("-"));
        btnMultiply.setOnClickListener(v -> etDisplay.append("*"));
        btnDivide.setOnClickListener(v -> etDisplay.append("/"));
        btnMod.setOnClickListener(v -> etDisplay.append("%"));
        btnExp.setOnClickListener(v -> etDisplay.append("E")); // ×10^x
        btnSqrt.setOnClickListener(v -> etDisplay.append("√")); // sqrt
        btnPower.setOnClickListener(v -> {
            String text = etDisplay.getText().toString();
            if (!text.isEmpty() && !text.endsWith("^")) {
                etDisplay.append("^");
            }
        });

        // DEL and AC
        btnDel.setOnClickListener(v -> {
            String text = etDisplay.getText().toString();
            if (!text.isEmpty()) etDisplay.setText(text.substring(0, text.length() - 1));
        });
        btnAC.setOnClickListener(v -> etDisplay.setText(""));

        // Ans
        btnAns.setOnClickListener(v -> {
            if (!lastResult.isEmpty()) etDisplay.append(lastResult);
        });

        // Equal
        btnEqual.setOnClickListener(v -> calculateResult());

        // Advanced operation dialogs
        btnMat.setOnClickListener(v -> saveOperation("MATRIX"));
        btnPoly.setOnClickListener(v -> {
            // open polynomial dialog and set result into display
            PolynomialDialog.show(this, result -> {
                etDisplay.setText(result);
                // save history entry for this computed result
                try {
                    java.util.HashMap<String, Object> h = new java.util.HashMap<>();
                    h.put("expression", "POLY:" + "coeffs@x");
                    h.put("result", result);
                    h.put("ts", System.currentTimeMillis());
                    if (usersRef != null && username != null) {
                        usersRef.child(username).child("history").push().setValue(h);
                    }
                } catch (Exception ignored) { }
            });
        });
        btnLin.setOnClickListener(v -> saveOperation("LINEAR"));
    }

    private void loadAssignedOperation() {
        usersRef.child(username).child("limited_operation").get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String op = snapshot.getValue(String.class);
                        tvInstruction.setText("You are asked to do the operation: " + (op != null ? op : "None"));
                    } else {
                        tvInstruction.setText("You are asked to do the operation: None");
                    }
                })
                .addOnFailureListener(e -> tvInstruction.setText("You are asked to do the operation: None"));
    }

    private void saveOperation(String operation) {
        usersRef.child(username).child("limited_operation").setValue(operation)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Operation saved: " + operation, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save operation", Toast.LENGTH_SHORT).show());
    }

    // Calculation logic including sqrt and ^ operator
    private void calculateResult() {
        String expr = etDisplay.getText().toString();
        if (expr.isEmpty()) return;

        try {
            double result = eval(expr);
            lastResult = String.valueOf(result);
            etDisplay.setText(lastResult);

            // Save calculation to Firebase under users/<username>/history
            try {
                java.util.HashMap<String, Object> h = new java.util.HashMap<>();
                h.put("expression", expr);
                h.put("result", lastResult);
                h.put("ts", System.currentTimeMillis());
                if (usersRef != null && username != null) {
                    usersRef.child(username).child("history").push().setValue(h)
                            .addOnSuccessListener(aVoid -> {
                                // saved successfully (no-op)
                            })
                            .addOnFailureListener(e -> {
                                // ignore save failure for now
                            });
                }
            } catch (Exception ignored) { }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid expression", Toast.LENGTH_SHORT).show();
        }
    }

    // Expression evaluator supporting + - * / ^ % √
    private double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else if (eat('%')) x %= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;

                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if (ch == '√') {
                    nextChar();
                    x = Math.sqrt(parseFactor());
                } else if (Character.isDigit(ch) || ch == '.') {
                    while (Character.isDigit(ch) || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }
}