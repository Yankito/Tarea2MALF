import java.math.BigInteger;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operaciones {

    public static boolean compruebaExpresion(String linea) {
        if (linea == null || !linea.matches("^\\s*-?\\d+(\\s*[+\\-*/%]\\s*-?\\d+)*\\s*$")) {
            System.out.println("Error: expresión no válida");
            return false;
        }
        return true;
    }

    public static String reemplazarVariables(String linea, HashMap<String, BigInteger> tablaVariables) {
        Pattern pattern = Pattern.compile("\\$[a-zA-Z][a-zA-Z0-9_]*");
        Matcher matcher = pattern.matcher(linea);

        while (matcher.find()) {
            // System.out.println("Variable encontrada: " + matcher.group());
            if (!tablaVariables.containsKey(matcher.group().replace("$", ""))) {
                // System.out.println(matcher.group());
                System.out.println("Error: variable no declarada");
                return null;
            } else {
                linea = linea.replace(matcher.group(),
                tablaVariables.get(matcher.group().replace("$", "")).toString());
            }
        }
        // System.out.println(linea);
        return linea;
    }

    public static BigInteger resultadoExpresion(String expresion, HashMap<String, BigInteger> tablaVariables) {
        expresion = reemplazarVariables(expresion, tablaVariables);
        if (compruebaExpresion(expresion)) {
            expresion = transformaAPostijo(expresion);
            return BigInteger.valueOf(evaluarExpresionPostfija(expresion).intValue());
        }
        return null;
    }

    public static String transformaAPostijo(String expresion) {
        expresion = expresion.replaceAll("\\s", "");

        StringBuilder postFijo = new StringBuilder();
        Stack<String> pila = new Stack<>();
        //System.out.println(expresion);
        String[] tokens = expresion.split("(?<=[+*/%\\-])|(?=[+*/%\\-])");
        /*
         * for (int i = 0; i < tokens.length; i++) {
         * System.out.println(tokens[i]);
         * }
         */

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (token.matches("-?\\d+")) {
                postFijo.append(token + " ");
            } else if (token.equals(("("))) {
                pila.push(token);
            } else if (token.equals(")")) {
                while (!pila.isEmpty() && !pila.peek().equals("(")) {
                    postFijo.append(pila.pop() + " ");
                }
                pila.pop();
            } else if (esOperador(token)) {
                while (!pila.isEmpty() && precedencia(pila.peek()) >= precedencia(token)) {
                    postFijo.append(pila.pop() + " ");
                }
                pila.push(token);
            }
        }

        while (!pila.isEmpty()) {
            postFijo.append(pila.pop() + " ");
        }

        return postFijo.toString();
    }

    public static Double evaluarExpresionPostfija(String expresionPostfija) {
        // System.out.println("a: " + expresionPostfija);
        String[] tokens = expresionPostfija.split("\\s+");
        Stack<Double> numeros = new Stack<>();
        for (String token : tokens) {
            if (token.matches("-?\\d+")) {
                numeros.push(Double.parseDouble(token));
            } else if (esOperador(token)) {
                double segundoNumero = numeros.pop();
                double primerNumero;
                if (!numeros.isEmpty())
                    primerNumero = numeros.pop();
                else
                    return null;
                double resultado = aplicarOperacion(primerNumero, segundoNumero, token);
                numeros.push(resultado);
            }
        }

        return numeros.pop();
    }

    public static boolean esOperador(String c) {
        return c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("%");
    }

    public static int precedencia(String operador) {
        if (operador.equals("+") || operador.equals("-")) {
            return 1;
        } else if (operador.equals("*") || operador.equals("/") || operador.equals("%")) {
            return 2;
        }
        return 0;
    }

    public static double aplicarOperacion(double a, double b, String operador) {
        switch (operador) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) {
                    throw new ArithmeticException("División por cero");
                }
                return a / b;
            case "%":
                if (b == 0) {
                    throw new ArithmeticException("Módulo por cero");
                }
                return a % b;
            default:
                throw new IllegalArgumentException("Operador no válido: " + operador);
        }
    }
}
