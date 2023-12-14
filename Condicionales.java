import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Condicionales {
    public static boolean evaluarExpresion(String linea){
        //System.out.println(linea);
        linea = linea.replaceAll("^[\\s]+|[\\s]+$", "");
        String[] valores = linea.split("\\s*([<>=!]+)\\s*"); 
        //System.out.println(valores[0]);
        //System.out.println(valores[1]);

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
