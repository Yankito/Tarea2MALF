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
            
            Integer valorAsignado = calcularAsignacion(partes[1]);
            if(valorAsignado != null)
                agregarVariable(partes[0], valorAsignado);
            else
                System.out.println("Error: variable no declarada");
        
        }
    }

    public static Integer calcularAsignacion(String linea) {
        if(linea.charAt(0) == '$'){
            if(tablaVariables.containsKey(linea.replace("$", "")))
                return tablaVariables.get(linea.replace("$", ""));
            else
                return null;
        }
        else if(linea.matches("-?\\d+"))
            return Integer.parseInt(linea);
        else
            return Operaciones.resultadoExpresion(linea);
        
    }

    

    public static void main(String[] args) {
        

        leerLinea(" $hola=5");
        leerLinea(" $hola2=7");
        leerLinea(" $hola3=22+5* 8/2");
        System.out.println(tablaVariables);
    }
}
