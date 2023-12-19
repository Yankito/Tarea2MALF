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

    public static String reemplazarVariables(String linea, HashMap<String, BigInteger> tablaVariables) { //Remplaza valores de variables por valor en la tabla
        Pattern pattern = Pattern.compile("\\$[a-zA-Z][a-zA-Z0-9_]*");
        Matcher matcher = pattern.matcher(linea);

        while (matcher.find()) {
            if (!tablaVariables.containsKey(matcher.group().replace("$", ""))) {
                System.out.println("Error: variable no declarada");
                return null;
            } else {
                linea = linea.replace(matcher.group(),
                tablaVariables.get(matcher.group().replace("$", "")).toString());
            }
        }
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

    public static String transformaAPostijo(String expresion) { //Transforma a postfijo para evaluar expresion con pila
        expresion = expresion.replaceAll("\\s", "");

        StringBuilder postFijo = new StringBuilder();
        Stack<String> pila = new Stack<>();
        String[] tokens = expresion.split("(?<=[+*/%\\-])|(?=[+*/%\\-])");

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

    public static Double evaluarExpresionPostfija(String expresionPostfija) { //Evalua expresion postfija con la pila
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
                    throw new ArithmeticException("Division por cero");
                }
                return a / b;
            case "%":
                if (b == 0) {
                    throw new ArithmeticException("Modulo por cero");
                }
                return a % b;
            default:
                throw new IllegalArgumentException("Operador no valido: " + operador);
        }
    }

    public static boolean evaluarExpresion(String linea){
        linea = linea.replaceAll("^[\\s]+|[\\s]+$", "");
        String[] valores = linea.split("\\s*([<>=!]+)\\s*"); 

        Pattern pattern = Pattern.compile("\\s*([<>=!]+)\\s*");
        Matcher matcher = pattern.matcher(linea);
        matcher.find();
        String operador = matcher.group(1);


        switch (operador) {
            case "==":
                if(Integer.parseInt(valores[0]) == Integer.parseInt(valores[1]))
                    return true;
                break;
            case "!=":
                if(Integer.parseInt(valores[0]) != Integer.parseInt(valores[1]))
                    return true;
                break;
            case "<":
                if(Integer.parseInt(valores[0]) < Integer.parseInt(valores[1]))
                    return true;
                break;
            case ">":
                if(Integer.parseInt(valores[0]) > Integer.parseInt(valores[1]))
                    return true;
                break;
            case "<=":
                if(Integer.parseInt(valores[0]) <= Integer.parseInt(valores[1]))
                    return true;
                break;
            case ">=":
                if(Integer.parseInt(valores[0]) >= Integer.parseInt(valores[1]))
                    return true;
                break;
            default:
                break;
        }
        return false;
    }
}
