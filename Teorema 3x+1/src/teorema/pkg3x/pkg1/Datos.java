package teorema.pkg3x.pkg1;

import java.math.BigInteger;
import java.util.HashMap;

public class Datos {
    private HashMap <BigInteger, BigInteger> calculados = new HashMap ();
    //Hace la misma funcion que el hashmap de repetidos en los hilos, se usa para
    //poder sacar la secuencia mas larga parando al llegar a un bucle
    //y para devolver el resto de la secuencia dado un numero ya calculado
    private HashMap <BigInteger, BigInteger> repetido = new HashMap ();
    
    private BigInteger finINICIAL;
    private BigInteger inicioRango;
    private BigInteger finRango;
    private BigInteger reparto;
    private int numHilos;
    public Datos (BigInteger inicioRango, BigInteger finRango, int numHilos) {
        this.inicioRango = inicioRango;
        this.finRango = finRango;
        finINICIAL=finRango;
        this.numHilos = numHilos;
        //Reparte los numeros que corresponden por hilo
        reparto = (finRango.subtract(inicioRango)).divide(BigInteger.valueOf(numHilos));
    }// DatosRangos()
    
    public Datos (BigInteger inicioRango, BigInteger finRango) {
        this.inicioRango = inicioRango;
        this.finRango = finRango;
    }// DatosDe1En1()
    
    BigInteger Contador = BigInteger.ZERO;
    BigInteger ACalcular;
    public synchronized BigInteger PedirNumero(){
        //cada vez que se llame a este metodo desde cualquier hilo le dara un numero entre la semilla inicial y la final
        ACalcular = inicioRango.add(Contador);
        Contador = Contador.add(BigInteger.ONE);
        if(ACalcular.compareTo(finRango) < 0){
            return ACalcular;
        }else{//en el caso que ya se acabven los nuemro devuelte al hilo 0 y se muere el hilo
            return BigInteger.ZERO;
        }
    }
        
    private BigInteger numMax=BigInteger.ZERO;
    public BigInteger getNumMax() {
        return numMax;
    }
    public synchronized void setNumMax(BigInteger numMax) {
        this.numMax = numMax;
    }
    
    private int contadorHilos = 0;
    public synchronized BigInteger pedirInicio(){
        //El hilo pide el inicio de su rango, synchronized porque cambio
        //el valor del inicio del rango para el siguiente hilo
        if (contadorHilos==0) {
            inicioRango=inicioRango;
        }else{
            //Suma uno al reparto porque si no dos hilos calcularian dos vecs el
            //mismo numero, el rpimero como el final de su rango y el siguiente como su inicio
            inicioRango=inicioRango.add(reparto).add(BigInteger.ONE);
        }
        //Contador de hilos para que cuando ya no se el primer hilo vaya al else
        ++contadorHilos;
        return inicioRango;
    }
    
    public BigInteger pedirFin(BigInteger inicio) {
        //El hilo pide el fin de su rango, no es synchronized porque, a pesar de
        //que se cambian valores, como recibe por parametro un inicio al que le
        //suma los numeros necesarios para calcular el fin del rango, nunca va a dar problemas
        if (contadorHilos==numHilos) {
            //Si es el ultimo hilo el fin de su rango es el fin inicial del rango
            //que se comenzo a repartir, hecho para evitar que no se llega el
            //rango solicitado en casos de repartos no igualess
            finRango=finINICIAL;
        }else{
            finRango=inicio.add(reparto);
        }
        return finRango;
    }// pedirFin()
    
    public boolean calculados(BigInteger calculado) {
        return calculados.get(calculado)!= null;
    }// calculados()
    
    public synchronized void actualizarCalculados(String secuencia) {
        //Recibe un string con una secuencia, separa los numeros y los
        //mete en el hashmap en su lugar correspondiente
        String[] arraySecuencia=secuencia.split("-");
        BigInteger key;
        BigInteger value = BigInteger.ZERO;
        for (int i = 0; i < arraySecuencia.length-1; ++i) {
            key=new BigInteger(arraySecuencia[i]);
            if (calculados.get(key)==null){
                if (i<arraySecuencia.length-1){
                    value=new BigInteger(arraySecuencia[i+1]);
                }
            calculados.put(key, value);
            }
        }
    }// actualizarCalculados()
    
    public synchronized int getSecuencia(BigInteger num){
        //Recibe el numero al que se ha llegado y ya esta calculado del hilo
        int contExtra=0;
        BigInteger numActual;
        while (repetido.get(num)==null) {
            numActual=num;
            ++contExtra;       
            num=calculados.get(num);
            repetido.put(numActual, num);
        }
        repetido.clear();
        //se devuelve el resto de la longitud de la secuencia
        return contExtra;
    }
    
    private int longitudSecuenciaMayor=0;
    private BigInteger semillaSecuenciaMayor=BigInteger.ZERO;
    private String secuenciaMayor="";
    public synchronized void secuenciaMasLarga(BigInteger semilla, int tamanioSecuencia) {
        if (tamanioSecuencia>longitudSecuenciaMayor){
            semillaSecuenciaMayor=semilla;
            longitudSecuenciaMayor=tamanioSecuencia;
        }
    }// secuenciaMasLarga()
    
    public void EscribirSecuenciaMasLarga(){
        //Recorre el hashmap con comenzando por la semilla que da lugar a la mas larga
        BigInteger siguienteNum=semillaSecuenciaMayor;
        BigInteger numActual;
        while (repetido.get(siguienteNum)==null) {
            numActual=siguienteNum;
            secuenciaMayor+=siguienteNum+"-";        
            siguienteNum=calculados.get(siguienteNum);
            repetido.put(numActual, siguienteNum);
        }
    }
    private void ActualizarSecuenciaMasLarga() {
        //Realizar una sola iteracion igual que el hilo pero para actualizar la variable "secuenciaMayor" 
        //y asi que ontenga todos los nuemros generados por la semillla
        BigInteger ValoesSecuancia = semillaSecuenciaMayor;
        BigInteger NuevoValor;
        while(repetido.get(ValoesSecuancia)==null){
            if(ValoesSecuancia.remainder(new BigInteger("2")) == BigInteger.ZERO){
                NuevoValor = ValoesSecuancia.divide(new BigInteger("2"));
            }else{
                NuevoValor = ValoesSecuancia.multiply(new BigInteger("3")).add(BigInteger.ONE);
            }
            repetido.put(ValoesSecuancia,NuevoValor);
            secuenciaMayor += ValoesSecuancia + "-";
            ValoesSecuancia = NuevoValor;
        }
        repetido.clear();
    }
    
    private boolean existeNuevoBucle = false;
    private BigInteger semillaNuevoBucle=BigInteger.ZERO;
    public synchronized void setBucle(boolean nuevoBucle, BigInteger semillaNuevoBucle){
        existeNuevoBucle=nuevoBucle;
        this.semillaNuevoBucle=semillaNuevoBucle;
    }
    public void end(int metodo){
        //Dependiendo del metodo que sea actualiza la variable de secuenciaMayor de una forma u otra
        if(metodo==1){
            EscribirSecuenciaMasLarga();
        }else{
            ActualizarSecuenciaMasLarga();
        }
        System.out.println("Semilla de la mayor secuencia encontrada: "+semillaSecuenciaMayor);
        //El -1 es porque sino sacaria la longitud de la secuencia contando con el
        //numero que se repite(4 o x) que genera el bucle 4-2-1 u otro
        System.out.println("Longitud de la secuencia de "+semillaSecuenciaMayor+": "+(longitudSecuenciaMayor-1));
        //substring para quitar el ultimo - al sacar la secuencia por pantalla
        System.out.println("Secuencia de "+semillaSecuenciaMayor+": "+secuenciaMayor.substring(0, secuenciaMayor.length()-1));
        System.out.println("Numero maximo alcanzado: "+numMax);
        if (existeNuevoBucle) {
            System.out.println("Nuevo bucle encontrado!!");
        }else{
            System.out.println("No existe nuevo bucle.");
        }
        if (!semillaNuevoBucle.equals(BigInteger.ZERO)){
            System.out.println("Generado por la semilla: "+semillaNuevoBucle);
        }
     
    }

    
}// Datos