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

    public static boolean buscarFinWhile(int indice){
        for(int i = indice; i<codigo.size(); i++){
            if(codigo.get(i).contains("wend")){
                pc = i;
                return true;
            }
        }
        return false;
    }

    public static boolean leerLinea(String linea) {
        
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
                if(linea == null) return false;

                if(linea.matches("\\s*-?\\d+\\s*(<|>|<=|>=|==|!=)\\s*-?\\d+\\s*")){
                    //System.out.println(Condicionales.evaluarExpresion(linea));
                    if (!Condicionales.evaluarExpresion(linea)) {
                        ejecucion = false;
                        if(!buscarFinWhile(pc)){
                            System.out.println("No cierra while");
                            return false;
                        }
                    }
                    else{
                       Ciclos.cicloWhile();   
                    }
                    pc++;
                    return true;
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

                if(linea==null) return false;

                if(linea.matches("\\s*-?\\d+\\s*(<|>|<=|>=|==|!=)\\s*-?\\d+\\s*")){
                    //System.out.println(Condicionales.evaluarExpresion(linea));
                    if (!Condicionales.evaluarExpresion(linea)) {
                        ejecucion = false;
                    }
                    return true;
                }
            }
            
            System.out.println("Error en expresion");
            return false;
        }
        else if(linea.equals("else")){
            ejecucion = !ejecucion;
            return true;
        }


        if (!linea.endsWith(";")) {
            System.out.println("falta ;");
            return false;
        }
        linea = linea.substring(0, linea.length() - 1);
        //System.out.println(linea);

        if(linea.equals("endif")){  
            ejecucion = true;
            return true;
        }

        if(linea.equals("wend")){
            if(ejecucion){
                pc = Ciclos.inicio;
            }
            else{
                ejecucion = !ejecucion;
            }
            return true;
        }

        if(!ejecucion) return true;

        
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
                else{
                    System.out.println("Error: variable no declarada");
                    return false;
                }
            } else {
                System.out.println("Error: variable no válida");
                return false;
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
                return false;
            }
        } else if (linea.contains("write ")) {
            linea = linea.replace("write", "");
            linea = linea.replaceFirst("^[\\s]+", "");
            //System.out.println("linea: " + linea);
            Integer resultado = calcularAsignacion(linea);
            if(resultado!=null)
                System.out.println(resultado);
            else
                return false;
        }
        return true;

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
                linea = linea.replaceAll("^[\\s]+|[\\s]+$", "");
                codigo.add(linea);
            }
        }
    }

    public static void ejecutaCodigo(){
        while(pc<codigo.size()){
 
            if(codigo.get(pc).isEmpty()){
                pc++;
                continue;
            }
                    
            if(!leerLinea(codigo.get(pc))){
                System.out.println("Error linea "+pc);
                return;
            }
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
