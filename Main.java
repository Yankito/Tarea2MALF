import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static int pc=0;
    static boolean ejecucion = true;
    static Scanner sc = new Scanner(System.in);
    static HashMap<String, Integer> tablaVariables = new HashMap<>();
    static ArrayList<String> codigo = new ArrayList<>();

    public static void agregarVariable(String nombre, int valor) {
        tablaVariables.put(nombre, valor);
    }

    public static void leerLinea(String linea) {
        
        // linea= linea.replaceAll("\\s", "");
        linea = linea.replaceAll("^[\\s]+|[\\s]+$", "");
        //System.out.println(linea);

        if(linea.contains("while") && linea.contains("do")){
            
            linea = linea.replace("while", "");
            linea = linea.replace("do", "");
            linea = linea.replaceFirst("^[\\s]+", "");
            if(linea.contains("(") && linea.contains(")")){
                linea = linea.replace("(", "");
                linea = linea.replace(")", "");
                linea = reemplazarVariables(linea);
                if(linea.matches("\\s*-?\\d+\\s*(<|>|<=|>=|==|!=)\\s*-?\\d+\\s*")){
                    //System.out.println(Condicionales.evaluarExpresion(linea));
                    if (!Condicionales.evaluarExpresion(linea)) {
                        ejecucion = false;
                    }
                    else{
                       Ciclos.cicloWhile(); 
                       
                    }
                    pc++;
                    return;
                }

            }
        }
        pc++;

        if(linea.contains("if") && linea.contains("then")){
            linea = linea.replace("if", "");
            linea = linea.replace("then", "");
            linea = linea.replaceFirst("^[\\s]+", "");
            if(linea.contains("(") && linea.contains(")")){
                linea = linea.replace("(", "");
                linea = linea.replace(")", "");
                linea = reemplazarVariables(linea);
                if(linea.matches("\\s*-?\\d+\\s*(<|>|<=|>=|==|!=)\\s*-?\\d+\\s*")){
                    //System.out.println(Condicionales.evaluarExpresion(linea));
                    if (!Condicionales.evaluarExpresion(linea)) {
                        ejecucion = false;
                    }
                    return;
                }
            }
            
            System.out.println("Error en expresion");
        }
        else if(linea.equals("else")){
            ejecucion = !ejecucion;
        }


        if (!linea.endsWith(";")) {
            return;
        }
        linea = linea.substring(0, linea.length() - 1);
        //System.out.println(linea);

        if(linea.equals("endif")){
            
            ejecucion = true;
        }

        if(linea.equals("wend")){
            if(ejecucion){
                pc = Ciclos.inicio;
            }
            else{
                ejecucion = !ejecucion;
            }

        }

        if(!ejecucion) return;

        
        /*
         * if (linea.charAt(linea.length() - 1) != ';') {
         * return;
         * }
         */
        


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
            //System.out.println("linea: " + linea);
            Integer resultado = calcularAsignacion(linea);
            if(resultado!=null)
                System.out.println(resultado);
        }






    }


    public static String reemplazarVariables(String linea) {
        Pattern pattern = Pattern.compile("\\$[a-zA-Z][a-zA-Z0-9_]*");
        Matcher matcher = pattern.matcher(linea);

        while (matcher.find()) {
            // System.out.println("Variable encontrada: " + matcher.group());
            if (!Main.tablaVariables.containsKey(matcher.group().replace("$", ""))) {
                // System.out.println(matcher.group());
                System.out.println("Error: variable no declarada");
                return null;
            } else {
                linea = linea.replace(matcher.group(),
                Main.tablaVariables.get(matcher.group().replace("$", "")).toString());
            }
        }
        // System.out.println(linea);
        return linea;
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


    public static void leerArchivoLineaPorLinea(String rutaArchivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                codigo.add(linea);
            }
        }
    }

    public static void ejecutaCodigo(){
        while(pc<codigo.size()){
            leerLinea(codigo.get(pc));
        }
    }

    public static void main(String[] args) {

        /*leerLinea("     $hola =2;");
        
        leerLinea(" $hola2=7;");
        leerLinea(" $hola3=$hola2*$hola;");
        //leerLinea("read $hola4;");
        leerLinea("$hola5=$hola3+$hola;");
        System.out.println(tablaVariables);
        leerLinea("write $hola5;");
        leerLinea("if( $hola <$hola5)then");
        leerLinea("write 1;");
        leerLinea("write 2;");

            leerLinea("if( 4 <4)then");
            leerLinea("write 3;");
            leerLinea("else");
            leerLinea("write 8;");
            leerLinea("endif;");

        leerLinea("write 4;");
        leerLinea("else");
        leerLinea("write 5;");
        leerLinea("write 6;");
        leerLinea("endif;");
        leerLinea("write 10;");*/

        String rutaArchivo = "program.txt";

        try {
            leerArchivoLineaPorLinea(rutaArchivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ejecutaCodigo();  

        System.out.println(tablaVariables);

    }
}
