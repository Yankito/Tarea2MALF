import java.util.Stack;

public class Operaciones {

    public static int resultadoExpresion(String expresion){
        expresion = transformaAPostijo(expresion);
        return (int)evaluarExpresionPostfija(expresion);
    }

    public static String transformaAPostijo(String expresion) {
        StringBuilder postFijo = new StringBuilder();
        Stack<String> pila = new Stack<>();
        System.out.println(expresion);
        String[] tokens = expresion.split("(?<=[+*/-])|(?=[+*/-])");
        for (int i = 0; i < tokens.length; i++) {
            System.out.println(tokens[i]);
        }

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (token.matches("-?\\d+")) {
                postFijo.append(token+" ");
            } else if (token.equals(("("))) {
                pila.push(token);
            } else if (token.equals( ")")) {
                while (!pila.isEmpty() && !pila.peek().equals("(") ) {
                    postFijo.append(pila.pop()+" ");
                }
                pila.pop();
            } else if (esOperador(token)) {
                while (!pila.isEmpty() && precedencia(pila.peek()) >= precedencia(token)) {
                    postFijo.append(pila.pop()+" ");
                }
                pila.push(token);
            }
        }

        while (!pila.isEmpty()) {
            postFijo.append(pila.pop()+" ");
        }

        return postFijo.toString();
    }

    public static double evaluarExpresionPostfija(String expresionPostfija) {
        System.out.println(expresionPostfija);
        String[] tokens = expresionPostfija.split("\\s+");
        Stack<Double> numeros = new Stack<>();
        System.out.println(expresionPostfija);
        for (String token : tokens) {
            if (Character.isDigit(token.charAt(0))) {
                numeros.push(Double.parseDouble(token));
            } else if (esOperador(token)) {
                double segundoNumero = numeros.pop();
                double primerNumero = numeros.pop();
                double resultado = aplicarOperacion(primerNumero, segundoNumero, token.charAt(0));
                numeros.push(resultado);
            }
        }

        return numeros.pop();
    }

    public static boolean esOperador(String c) {
        return c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/");
    }

    public static int precedencia(String operador) {
        if (operador.equals("+") || operador.equals("-")) {
            return 1;
        } else if (operador.equals("*") || operador.equals("/")) {
            return 2;
        }
        return 0;
    }

    public static double aplicarOperacion(double a, double b, char operador) {
        switch (operador) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("División por cero");
                }
                return a / b;
            default:
                throw new IllegalArgumentException("Operador no válido: " + operador);
        }
    }
}
