import java.util.HashMap;

public class Main {
    static HashMap<String, Integer> tablaVariables = new HashMap<>();

    public static void agregarVariable(String nombre, int valor) {
        tablaVariables.put(nombre, valor);
    }

    public static void leerLinea(String linea){
        linea= linea.replaceAll("\\s", "");
        System.out.println(linea);
        if(linea.charAt(0) == '$') {
            String[] partes = linea.split("=", 2);
            partes[0] = partes[0].replace("$", "");
            agregarVariable(partes[0], calcularAsignacion(partes[1]));
        }
    }

    public static int calcularAsignacion(String linea){
        if(linea.charAt(0) == '$'){
            return tablaVariables.get(linea.replace("$", ""));
        }
        else if(linea.matches("-?\\d+"))
            return Integer.parseInt(linea);

        return 0;
    }


    public static void main(String[] args) {
        leerLinea(" $hola=5");
        leerLinea(" $hola2=7");
        leerLinea(" $hola3=$hola");
        System.out.println(tablaVariables);
    }
}
