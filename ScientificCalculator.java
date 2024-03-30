import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScientificCalculator extends JFrame implements ActionListener {
    private JTextField displayField;
    private String currentInput = "";

    public ScientificCalculator() {
        setTitle("Scientific Calculator");
        setSize(300, 500); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        displayField = new JTextField();
        displayField.setEditable(false);
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setPreferredSize(new Dimension(0, 80)); 
        add(displayField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(6, 4));
        String[] buttonLabels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "cube", "+", 
            "sin", "√", "x²", "C",
            "cos", "tan", "³√", "=" 
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(this);
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        switch (actionCommand) {
            case "cube": // Changed action command to "cube"
                currentInput = Double.toString(Math.pow(Double.parseDouble(currentInput), 3));
                displayField.setText(currentInput);
                break;
            case "=":
                evaluateExpression();
                break;
            case "C":
                clearDisplay();
                break;
            case "√":
                currentInput = Double.toString(Math.sqrt(Double.parseDouble(currentInput)));
                displayField.setText(currentInput);
                break;
            case "x²":
                currentInput = Double.toString(Math.pow(Double.parseDouble(currentInput), 2));
                displayField.setText(currentInput);
                break;
            case "sin":
                currentInput = Double.toString(Math.sin(Math.toRadians(Double.parseDouble(currentInput))));
                displayField.setText(currentInput);
                break;
            case "cos":
                currentInput = Double.toString(Math.cos(Math.toRadians(Double.parseDouble(currentInput))));
                displayField.setText(currentInput);
                break;
            case "tan":
                currentInput = Double.toString(Math.tan(Math.toRadians(Double.parseDouble(currentInput))));
                displayField.setText(currentInput);
                break;
            case "³√": // Cube root calculation
                currentInput = Double.toString(Math.cbrt(Double.parseDouble(currentInput)));
                displayField.setText(currentInput);
                break;
            default:
                currentInput += actionCommand;
                displayField.setText(currentInput);
                break;
        }
    }

    private void evaluateExpression() {
        try {
            currentInput = Double.toString(eval(currentInput));
            displayField.setText(currentInput);
        } catch (Exception ex) {
            displayField.setText("Error");
        }
    }

    private void clearDisplay() {
        currentInput = "";
        displayField.setText("");
    }

    private double eval(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
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
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') {
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = expression.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        new ScientificCalculator();
    }
}
