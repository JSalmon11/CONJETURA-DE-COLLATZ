package teorema.pkg3x.pkg1;

import java.math.BigInteger;
import java.util.HashMap;

public class HiloRangos implements Runnable {
    private Datos datos;
    private BigInteger inicioRango;
    private BigInteger finRango;
    //Para hacer la líneas de cáculo más cortas creo estas
    //dos variables BigInteger de un 2 y un 3
    private BigInteger DOS=new BigInteger("2");
    private BigInteger TRES=new BigInteger("3");
    //Con este HashMap podre comprobar si el resultado de una operacion se
    //esta repitiendo y asi el programa detecta bucles
    private HashMap <BigInteger, BigInteger> repetido = new HashMap ();
    //String para mandar la secuencia completa de un numero tras haberla calculado
    private String secuencia="";
    
    public HiloRangos(Datos datos) {
        this.datos = datos;
    }// HiloRangos()

    @Override
    public void run() {
        inicioRango=datos.pedirInicio();
        finRango=datos.pedirFin(inicioRango);
        BigInteger numActual = BigInteger.ZERO;
        BigInteger resultado = BigInteger.ZERO;
        boolean posibleNuevoBucle = false;
        //for que recorre los numeros del rango que le corresponde al hilo
        for (BigInteger i = inicioRango; i.compareTo(finRango)==-1 || i.compareTo(finRango)==0; i=i.add(BigInteger.ONE)) {
            secuencia="";
            resultado=i;
            //creo un contador para saber la secuencia mas larga
            int contSecuencias=0;
            //Mientras el value del numero que se va a calcular es nulo los calculos,
            //cuando el value no es nulo significa que se ha entrado en un bucle
            while (repetido.get(resultado) == null) {
                numActual=resultado;
                //Compruebo desde el hashmap de datos si este numero ya esta calculado
                if (datos.calculados(numActual)==false) {
                    if (numActual.remainder(DOS)==BigInteger.ZERO){
                        resultado=numActual.divide(DOS);
                    }else{
                        resultado=(numActual.multiply(TRES)).add(BigInteger.ONE);
                    }
                    //Aumento el contador de la longitud de la secuencia en cada interaccion
                    ++contSecuencias;
                    //Al acabar la secuencia pongo la variable a true, por si fuese un bucle nuevo
                    posibleNuevoBucle=true;
                }else{
                    //Si el numero ya se ha calculado, no hago mas operaciones
                    //y le pido a datos el resto de la longitud de la secuencia
                    contSecuencias+=datos.getSecuencia(resultado);
                    //Dado que esta secuencia ya ha sido explorada, ya se ha comprobado si
                    //es el bucle 4,2,1 o es un nuevo bucle la primera vez que se calculo
                    //la secuencia, asi que dejo la variable a false 
                    posibleNuevoBucle=false;
                }
                secuencia+=numActual+"-";
                //Se va rellenando el hashmap que comprueba el primer numero que se repite y por tanto el bucle
                repetido.put(numActual, resultado);
                if (resultado.compareTo(datos.getNumMax())==1){
                    datos.setNumMax(resultado);
                }    
            }
            //Si la variable esta en true, significa que esa secuencia no se ha comprobado si desemboca en un nuevo bucle
            if (posibleNuevoBucle){
                comprobarPosibleNuevoBucle(numActual, i);
            }
            secuencia+=resultado;
            //Manda la secuencia completa del numero, o si llego a un numero ya calculado solo
            //se manda la parte que aun no ha sido calculada para actualizar el hashmap de calculados
            datos.actualizarCalculados(secuencia);
            //Manda el contador de la secuencia junto con la semilla que provoca esa secuencia para comprobar si es la mas larga
            datos.secuenciaMasLarga(i, contSecuencias);
            repetido.clear();
        }
    }// run()
    
    private void comprobarPosibleNuevoBucle(BigInteger numRepetido, BigInteger semilla) {
        //Si el numero que se repite no es el 4 ni el 2 ni el 1 y no es el 0, para
        //controlar errores, significa que se ha encontrado un bucle nuevo
        if (!numRepetido.equals(BigInteger.ZERO)
        && !repetido.get(numRepetido).equals(DOS.add(DOS))
        && !repetido.get(numRepetido).equals(DOS)
        && !repetido.get(numRepetido).equals(BigInteger.ONE)) {
            datos.setBucle(true, semilla);
        }
    }// comprobarPosibleNuevoBucle()
    
}// HiloRangos
