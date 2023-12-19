import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class T2 {
    static int inicioCiclo = 0;
    static int pc=0;
    static boolean ejecucion = true;
    static Scanner sc = new Scanner(System.in);
    static HashMap<String, BigInteger> tablaVariables = new HashMap<>();
    static ArrayList<String> codigo = new ArrayList<>();

    public static void agregarVariable(String nombre, BigInteger valor) {
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

    public static boolean buscarElse(int indice){
        for(int i = indice; i<codigo.size(); i++){
            if(codigo.get(i).contains("else")){
                pc = i;
                return true;
            }
        }
        return false;
    }

    public static boolean buscarFinIf(int indice){
        for(int i = indice; i<codigo.size(); i++){
            if(codigo.get(i).contains("endif")){
                pc = i;
                return true;
            }
        }
        return false;
    }

    public static boolean leerLinea(String linea) {
        linea = linea.replaceAll("^[\\s]+|[\\s]+$", "");

        if(linea.contains("while") && linea.contains("do")){ //Lee ciclo while
            
            linea = linea.replace("while", "");
            linea = linea.replace("do", "");
            linea = linea.replaceAll("^[\\s]+|[\\s]+$", "");
            if(linea.contains("(") && linea.contains(")")){
                linea = linea.replace("(", "");
                linea = linea.replace(")", "");
                linea = Operaciones.reemplazarVariables(linea, tablaVariables);
                if(linea == null) return false;

                if(linea.matches("\\s*-?\\d+\\s*(<|>|<=|>=|==|!=)\\s*-?\\d+\\s*")){
                    if (!Operaciones.evaluarExpresion(linea)) { //no cumple condicion de while por lo que salta al fin de este
                        if(!buscarFinWhile(pc)){
                            System.out.println("No cierra while");
                            return false;
                        }
                    }
                    else{
                        inicioCiclo = pc; 
                    }
                    pc++;
                    return true;
                }

            }
        }
        else if(linea.contains("if") && linea.contains("then")){ //Lee condicionales
            linea = linea.replace("if", "");
            linea = linea.replace("then", "");
            linea = linea.replaceAll("^[\\s]+|[\\s]+$", "");
            if(linea.contains("(") && linea.contains(")")){
                linea = linea.replace("(", "");
                linea = linea.replace(")", "");
                linea = Operaciones.reemplazarVariables(linea, tablaVariables);

                if(linea==null) return false;

                if(linea.matches("\\s*-?\\d+\\s*(<|>|<=|>=|==|!=)\\s*-?\\d+\\s*")){
                    if (!Operaciones.evaluarExpresion(linea)) {
                        if(!buscarElse(pc)){ //no cumple condicion y busca si hay un else
                            if(!buscarFinIf(pc)){ //si no hay else salta al fin del bloque de condicion
                                System.out.println("No cierra if");
                                return false;
                            }
                        }
                    }
                    
                    pc++;
                    return true;
                }
            }
            System.out.println("Error en expresion");
            return false;
        }
        else if(linea.equals("else")){
            if(!buscarFinIf(pc)){
                System.out.println("No cierra if");
                return false;
            }
            pc++;
            return true;
        }


        if (!linea.endsWith(";")) { //Comprueba fin de linea con ';' segun corresponda
            System.out.println("falta ;");
            return false;
        }
        linea = linea.substring(0, linea.length() - 1);

        if(linea.equals("endif")){  
           
        }
        else if(linea.equals("wend")){ //salto a inicio de while si la condicion de este es verdadera
            pc = inicioCiclo;
            return true;
        }
        else if (linea.charAt(0) == '$') { //Caso de asignacion de variable

            String[] partes = linea.split("=", 2);
            partes[0] = partes[0].replaceAll("^[\\s]+|[\\s]+$", "");
            if (partes[0].matches("\\$[a-zA-Z][a-zA-Z0-9_]*")) {
                partes[0] = partes[0].replace("$", "");

                partes[1] = partes[1].replaceAll("^[\\s]+|[\\s]+$", "");
                BigInteger valorAsignado = calcularAsignacion(partes[1]); //Calcula asignacion
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
        } else if (linea.contains("read ")) { //Lectura de datos
            linea = linea.replace("read", "");
            linea = linea.replaceAll("^[\\s]+|[\\s]+$", "");
            if (linea.matches("\\$[a-zA-Z][a-zA-Z0-9_]*")) {
                linea = linea.replace("$", "");
                BigInteger valor = BigInteger.valueOf(Integer.parseInt(sc.nextLine()));
                tablaVariables.put(linea, valor);
            } else {
                System.out.println("Error: variable no valida");
                return false;
            }
        } else if (linea.contains("write ")) { //Impresion de datos
            
            linea = linea.replace("write", "");
            linea = linea.replaceAll("^[\\s]+|[\\s]+$", "");
            BigInteger resultado = calcularAsignacion(linea);
            if(resultado!=null)
                System.out.println(resultado);
            else
                return false;
        }
        else{ //Error de sintaxis en linea
            pc++;
            System.out.println("Error en linea: "+pc);
            System.out.println(linea);
            System.out.println("error de sintaxis");
        }
        pc++;
        return true;

    }




    public static BigInteger calcularAsignacion(String linea) { //Calcula asignacion
        if (linea.matches("\\s*\\$[a-zA-Z][a-zA-Z0-9_]*\\s*")) { //caso de ser variable
            if (tablaVariables.containsKey(linea.replace("$", "")))
                return tablaVariables.get(linea.replace("$", ""));
            else
                return null;
        } else if (linea.matches("-?\\d+")) //Caso de entero
            return BigInteger.valueOf(Integer.parseInt(linea));
        else //caso de operacion matematica
            return Operaciones.resultadoExpresion(linea, tablaVariables);

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
                System.out.println("Error linea "+(pc+1));
                System.out.println(codigo.get(pc));
                return;
            }
        }
    }

    public static void main(String[] args) {

        String rutaArchivo = null;

        if(args.length>0){
            rutaArchivo = args[0];
            try {
                leerArchivoLineaPorLinea(rutaArchivo);
                ejecutaCodigo(); 
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("Ingrese un archivo para ejecutar");
        }
         
        //System.out.println(tablaVariables);

    }
}
