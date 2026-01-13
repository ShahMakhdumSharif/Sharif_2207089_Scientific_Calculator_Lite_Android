package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class UserInterfaceActivity extends AppCompatActivity {

    private EditText etDisplay;
    private TextView tvInstruction;
    private TextView tvWelcome;

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
        tvInstruction = findViewById(R.id.tv_instruction);
        tvWelcome = findViewById(R.id.tv_welcome);

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

        // Show username in the welcome header
        if (tvWelcome != null) {
            String welcomeName = (username != null && !username.isEmpty()) ? username : userKey;
            tvWelcome.setText("Welcome " + welcomeName);
        }

        // Try to load an assigned operation for this user (optional field in DB)
        if (userRef != null) {
            userRef.child("assigned_operation")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String op = String.valueOf(snapshot.getValue());
                            if (tvInstruction != null) {
                                if (op != null && !op.isBlank())
                                    tvInstruction.setText("You are asked to do the operation: '" + op + "'");
                                else
                                    tvInstruction.setText("You are asked to do the operation:");
                            }
                        }
                    })
                    .addOnFailureListener(e -> { /* ignore - keep default text */ });
        }

        initButtons();
        loadAllowedOperations();
    }


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
        // Append operator to the current expression (Casio-like behavior)
        try {
            String cur = etDisplay.getText().toString();
            if (cur == null) cur = "";
            // prevent duplicate operators at the end
            if (!cur.isEmpty() && "+-*/".indexOf(cur.charAt(cur.length() - 1)) >= 0) {
                cur = cur.substring(0, cur.length() - 1);
            }
            cur = cur + op;
            etDisplay.setText(cur);
            // update header to show pending expression
            if (tvInstruction != null) tvInstruction.setText(cur);
        } catch (Exception e) {
            showError();
        }
    }

    private void calculate() {
        // Evaluate the full expression entered in the display using infix->RPN
        try {
            String expr = etDisplay.getText().toString();
            if (expr == null || expr.trim().isEmpty()) return;
            expr = expr.trim();
            // trim trailing operators
            while (!expr.isEmpty() && "+-*/".indexOf(expr.charAt(expr.length() - 1)) >= 0) {
                expr = expr.substring(0, expr.length() - 1).trim();
            }
            if (expr.isEmpty()) { showError(); return; }

            List<String> tokens = preprocessTokens(tokenize(expr));
            List<String> rpn = toRPN(tokens);
            double result = evalRPN(rpn);

            String out;
            if (Math.abs(result - Math.round(result)) < 1e-10) {
                out = String.valueOf((long) Math.round(result));
            } else {
                DecimalFormat df = new DecimalFormat("0.##########");
                out = df.format(result);
            }

            lastResult = out;
            etDisplay.setText(out);
            // save to history
            try {
                String exprStr = expr;
                java.util.HashMap<String, Object> h = new java.util.HashMap<>();
                h.put("expression", exprStr);
                h.put("result", out);
                h.put("ts", System.currentTimeMillis());
                if (userRef != null) userRef.child("history").push().setValue(h);
            } catch (Exception ignored) { }
            if (tvInstruction != null) tvInstruction.setText(expr + " = " + out);
            currentOperator = "";
        } catch (IllegalArgumentException iae) {
            showError();
        } catch (Exception e) {
            showError();
        }
    }

    /* ---------------- FIREBASE OPERATION LIMIT ---------------- */

    private void loadAllowedOperations() {
        // First check for an explicit 'limited_operation' node (admin sets allowed operations there).
        userRef.child("limited_operation")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Map<String, Object> limited = (Map<String, Object>) snapshot.getValue();
                        // If limited_operation exists, treat its keys as the allowed set (whitelist).
                        setEnabled(R.id.btn_add, limited != null && limited.containsKey("+"));
                        setEnabled(R.id.btn_subtract, limited != null && limited.containsKey("-"));
                        setEnabled(R.id.btn_multiply, limited != null && limited.containsKey("*"));
                        setEnabled(R.id.btn_divide, limited != null && limited.containsKey("/"));
                        setEnabled(R.id.btn_sqrt, limited != null && limited.containsKey("sqrt"));
                        setEnabled(R.id.btn_power, limited != null && limited.containsKey("^"));
                    } else {
                        // Fallback to legacy 'allowed_operations' node if present
                        userRef.child("allowed_operations")
                                .get()
                                .addOnSuccessListener(snap -> {
                                    if (!snap.exists()) return;
                                    Map<String, Boolean> ops = (Map<String, Boolean>) snap.getValue();
                                    if (ops == null) return;
                                    setEnabled(R.id.btn_add, ops.containsKey("+"));
                                    setEnabled(R.id.btn_subtract, ops.containsKey("-"));
                                    setEnabled(R.id.btn_multiply, ops.containsKey("*"));
                                    setEnabled(R.id.btn_divide, ops.containsKey("/"));
                                    setEnabled(R.id.btn_sqrt, ops.containsKey("sqrt"));
                                    setEnabled(R.id.btn_power, ops.containsKey("^"));
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load permissions", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load permissions", Toast.LENGTH_SHORT).show());
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

    /* ---------------- Expression parsing helpers (infix -> RPN) ---------------- */

    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        int idx = 0;
        while (idx < expr.length()) {
            char c = expr.charAt(idx);
            if (Character.isWhitespace(c)) { idx++; continue; }
            if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' || c == '^') {
                tokens.add(String.valueOf(c));
                idx++;
            } else {
                int j = idx;
                while (j < expr.length() && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) j++;
                tokens.add(expr.substring(idx, j));
                idx = j;
            }
        }
        return tokens;
    }

    private List<String> preprocessTokens(List<String> tokens) {
        List<String> merged = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            String tk = tokens.get(i);
            if ("-".equals(tk) || "+".equals(tk)) {
                boolean isUnary = (i == 0) || tokens.get(i - 1).matches("[+\\-*/(]");
                if (isUnary) {
                    if (i + 1 < tokens.size() && tokens.get(i + 1).matches("^[0-9]*\\.?[0-9]+$")) {
                        String num = tokens.get(i + 1);
                        if ("-".equals(tk)) merged.add("-" + num);
                        else merged.add(num);
                        i++; 
                        continue;
                    } else {
                        throw new IllegalArgumentException("Invalid unary operator");
                    }
                }
            }
            merged.add(tk);
        }

        boolean expectNumber = true;
        int paren = 0;
        for (String tk : merged) {
            if (expectNumber) {
                if (tk.matches("^[+-]?[0-9]*\\.?[0-9]+$")) {
                    expectNumber = false;
                } else if ("(".equals(tk)) {
                    paren++;
                    expectNumber = true;
                } else {
                    throw new IllegalArgumentException("Unexpected token: " + tk);
                }
            } else {
                if (tk.matches("[+\\-*/]")) {
                    expectNumber = true;
                } else if (")".equals(tk)) {
                    paren--;
                    if (paren < 0) throw new IllegalArgumentException("Mismatched parentheses");
                    expectNumber = false;
                } else {
                    throw new IllegalArgumentException("Unexpected token: " + tk);
                }
            }
        }
        if (paren != 0) throw new IllegalArgumentException("Mismatched parentheses");
        if (expectNumber) throw new IllegalArgumentException("Expression ends with operator");
        return merged;
    }

    private int precedence(String op) {
        switch (op) {
            case "+":
            case "-": return 1;
            case "*":
            case "/": return 2;
            case "^": return 3;
        }
        return 0;
    }

    private List<String> toRPN(List<String> tokens) {
        List<String> out = new ArrayList<>();
        Stack<String> ops = new Stack<>();
        for (String t : tokens) {
            if (t.matches("^[+-]?[0-9]*\\.?[0-9]+$")) {
                out.add(t);
            } else if (t.equals("(")) {
                ops.push(t);
            } else if (t.equals(")")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) out.add(ops.pop());
                if (!ops.isEmpty() && ops.peek().equals("(")) ops.pop();
            } else if (t.matches("[+\\-*/^]")) {
                while (!ops.isEmpty() && !ops.peek().equals("(") &&
                        (precedence(ops.peek()) > precedence(t) ||
                                (precedence(ops.peek()) == precedence(t) && !isRightAssociative(t)))) {
                    out.add(ops.pop());
                }
                ops.push(t);
            }
        }
        while (!ops.isEmpty()) out.add(ops.pop());
        return out;
    }

    private boolean isRightAssociative(String op) {
        return "^".equals(op);
    }

    private double evalRPN(List<String> rpn) {
        Stack<Double> st = new Stack<>();
        for (String t : rpn) {
            if (t.matches("^[+-]?[0-9]*\\.?[0-9]+$")) {
                st.push(Double.parseDouble(t));
            } else {
                double b = st.pop();
                double a = st.isEmpty() ? 0 : st.pop();
                switch (t) {
                    case "+": st.push(a + b); break;
                    case "-": st.push(a - b); break;
                    case "*": st.push(a * b); break;
                    case "/": st.push(a / b); break;
                    case "^": st.push(Math.pow(a, b)); break;
                }
            }
        }
        return st.isEmpty() ? 0 : st.pop();
    }
}