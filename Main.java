import java.util.HashMap;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static HashMap<String, Integer> tablaVariables = new HashMap<>();

    public static void agregarVariable(String nombre, int valor) {
        tablaVariables.put(nombre, valor);
    }

    public static void leerLinea(String linea) {
        // linea= linea.replaceAll("\\s", "");
        linea = linea.replaceFirst("^[\\s]+", "");
        linea = linea.replaceAll("^[\\s]+|[\\s]+$", "");
        /*
         * if (linea.charAt(linea.length() - 1) != ';') {
         * return;
         * }
         */
        if (!linea.endsWith(";")) {
            return;
        }
        linea = linea.substring(0, linea.length() - 1);
        System.out.println(linea);

        if (linea.charAt(0) == '$') {

            String[] partes = linea.split("=", 2);
            partes[0] = partes[0].trim();
            if (partes[0].matches("\\$[a-zA-Z][a-zA-Z0-9_]*")) {
                partes[0] = partes[0].replaceAll("\\s", "");
                partes[0] = partes[0].replace("$", "");

                Integer valorAsignado = calcularAsignacion(partes[1]);
                if (valorAsignado != null)
                    agregarVariable(partes[0], valorAsignado);
                else
                    System.out.println("Error: variable no declarada");
            } else {
                System.out.println("Error: variable no válida");
            }
        } else if (linea.contains("read ")) {
            linea = linea.replace("read", "");
            linea = linea.replaceAll("\\s", "");
            if (linea.matches("\\$[a-zA-Z][a-zA-Z0-9_]*")) {
                linea = linea.replace("$", "");
                System.out.println("Ingrese un valor para " + linea);
                tablaVariables.put(linea, sc.nextInt());
            } else {
                System.out.println("Error: variable no valida");
            }
        } else if (linea.contains("write ")) {
            linea = linea.replace("write", "");
            linea = linea.replaceFirst("^[\\s]+", "");
            System.out.println("linea: " + linea);
            System.out.println(calcularAsignacion(linea));
        }

    }

    public static Integer calcularAsignacion(String linea) {
        if (linea.matches("\\s*\\$[a-zA-Z][a-zA-Z0-9_]*\\s*")) {
            if (tablaVariables.containsKey(linea.replace("$", "")))
                return tablaVariables.get(linea.replace("$", ""));
            else
                return null;
        } else if (linea.matches("-?\\d+"))
            return Integer.parseInt(linea);
        else
            return Operaciones.resultadoExpresion(linea);

    }

    public static void main(String[] args) {

        leerLinea("     $hola =2;");
        leerLinea("write $hola*5;");
        leerLinea(" $hola2=7;");
        leerLinea(" $hola3=$hola2*$hola;");
        leerLinea("read $hola4;");
        leerLinea("$hola5=$hola4+$hola;");
        // System.out.println(tablaVariables);
    }
}
