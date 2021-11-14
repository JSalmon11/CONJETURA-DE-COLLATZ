package teorema.pkg3x.pkg1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Hilo_De_Uno_En_Uno implements Runnable {
    Datos Datos;
    HashMap<BigInteger,BigInteger> EstaRepetido = new HashMap<>();
    boolean posibleNuevoBucle;
    public Hilo_De_Uno_En_Uno(Datos Datos) {
        this.Datos = Datos;
    }
    
    
    @Override
    public void run(){
        BigInteger Semilla = Datos.PedirNumero();
        BigInteger DOS = new BigInteger("2");
        BigInteger TRES = new BigInteger("3");
        //Mientras la clase datos no te devuelba 0 es que aun quedan numeros por calcular
        while(Semilla.compareTo(BigInteger.ZERO) != 0 ){
            BigInteger NuevoValor = BigInteger.ZERO;
            BigInteger Numero = Semilla;
            String CadenaNumerosCalculados = "";
            int LogitudCadena = 0;
            //mientras en el hashmap no este calculado x numero; es decir no encuentra un bucle
            while( EstaRepetido.get(Numero) == null ){
                //si esta calculado anteriormente
                if(!Datos.calculados(Numero)){
                    if(Numero.remainder(DOS) == BigInteger.ZERO){
                        //si es par se divide entre 2
                        NuevoValor = Numero.divide(DOS);
                    }else{
                        //si es impar se multiplica por 3 y se le suma 1
                        NuevoValor = Numero.multiply(TRES).add(BigInteger.ONE);
                    }
                    //guardamos el valor y su siguietne resultado en el hashmap
                    EstaRepetido.put(Numero, NuevoValor);
                    Numero = NuevoValor;
                    //aumentamos la longitud de la cadena y cuargamos su valor en un string para posteriormente darselo a la clase datos
                    ++LogitudCadena;
                    CadenaNumerosCalculados += Numero + "-";
                    //si ese valor calculado es mayor que el que hay en la clase datos lo sustituimos
                    if( NuevoValor.compareTo( Datos.getNumMax() )== 1 ){
                        Datos.setNumMax(NuevoValor);
                    }
                    
                    posibleNuevoBucle = true;
                }else{
                    //como ya enta calculado ese numero añadimos su longitud a la ariable del hilo
                    LogitudCadena += Datos.getSecuencia(Numero);
                    //guardamos un el valor antiguo del numero para poder salir del bucle
                    EstaRepetido.put(Numero, NuevoValor);
                    posibleNuevoBucle = false;
                }
            }
            CadenaNumerosCalculados += NuevoValor;
            //si ha calculado un nuevo numero puede que haya llegado a un bucle nuevo pero ya estaba calculado significa que el bucle de esa semilla es 4-2-1-4
            if(posibleNuevoBucle){
                if (!Numero.equals(BigInteger.ZERO)
                    && !EstaRepetido.get(Numero).equals(DOS.add(DOS))
                    && !EstaRepetido.get(Numero).equals(DOS)
                    && !EstaRepetido.get(Numero).equals(BigInteger.ONE)) {
                    Datos.setBucle(true, Semilla);
                }
            }
            //vaciamos el array de EstaRepetido y  mandamos la secuencia mas larga al que actualizamos el hashmap de datos con los valores nuevos calculados
            EstaRepetido.clear();
            Datos.secuenciaMasLarga(Semilla, LogitudCadena);
            Datos.actualizarCalculados(CadenaNumerosCalculados);
            //pedimos de nuevo un numero para seguir calculando
            Semilla = Datos.PedirNumero();
        }
    }
}// Hilo_De_Uno_En_Uno
