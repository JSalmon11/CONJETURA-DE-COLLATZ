package teorema.pkg3x.pkg1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

/**
* Clase compartida para comunicar los diferentes hilos entre sí.
*/
public class Datos {

    private HashMap<BigInteger, BigInteger> calculados = new HashMap<BigInteger, BigInteger>();
    //Hace la misma funcion que el hashmap de repetidos en los hilos, se usa para
    //poder sacar la secuencia mas larga parando al llegar a un bucle
    //y para devolver el resto de la secuencia dado un numero ya calculado
    private HashMap<BigInteger, BigInteger> repetido = new HashMap<BigInteger, BigInteger>();

    private BigInteger finINICIAL;
    private BigInteger inicioRango;
    private BigInteger finRango;
    private BigInteger reparto;
    private int numHilos;

    public Datos(BigInteger inicioRango, BigInteger finRango, int numHilos) {
        this.inicioRango = inicioRango;
        this.finRango = finRango;
        finINICIAL = finRango;
        this.numHilos = numHilos;
        //Reparte los numeros que corresponden por hilo para repartir por rangos
        reparto = (finRango.subtract(inicioRango)).divide(BigInteger.valueOf(numHilos));
    }// Datos()

    private BigInteger Contador = BigInteger.ZERO;
    /**
	 * Cada vez que se llame a este metodo desde cualquier hilo le dara un numero entre la semilla inicial y la final.
	 * @return El número al que aplicar el cáculo o -1 si se han acabado todos los números.
	 */
    public synchronized BigInteger PedirNumero() {
        BigInteger ACalcular;
        ACalcular = inicioRango.add(Contador);
        Contador = Contador.add(BigInteger.ONE);
        if (ACalcular.compareTo(finRango) <= 0) {
            return ACalcular;
        } else {
            return new BigInteger("-1");
        }
    }// PedirNumero()

    private int contadorHilos = 0;

    /**
	 * El hilo pide el inicio de su rango.
	 * @return El inicio del rango de números que calculará el hilo.
	 */
    public synchronized BigInteger pedirInicio() {
        //Synchronized porque cambio el valor
        //del inicio del rango para el siguiente hilo
        if (contadorHilos == 0) {
            inicioRango = inicioRango;
        } else {
            //Suma uno al reparto porque si no dos hilos calcularian dos vecs el
            //mismo numero, el rpimero como el final de su rango y el siguiente como su inicio
            inicioRango = inicioRango.add(reparto).add(BigInteger.ONE);
        }
        //Contador de hilos para que cuando ya no sea el primer hilo vaya al else
        ++contadorHilos;
        return inicioRango;
    }// pedirInicio()

    /**
	 * El hilo pide el fin de su rango.
	 * @return El fin del rango de números que calculará el hilo.
	 */
    public BigInteger pedirFin(BigInteger inicio) {
        //El hilo pide el fin de su rango, no es synchronized porque, a pesar de
        //que se cambian valores, como recibe por parametro un inicio al que le
        //suma los numeros necesarios para calcular el fin del rango, nunca va a dar problemas
        if (contadorHilos == numHilos) {
            //Si es el ultimo hilo el fin de su rango es el fin inicial del rango
            //que se comenzo a repartir, hecho para evitar que no se llega el
            //rango solicitado en casos de repartos no igualess
            finRango = finINICIAL;
        } else {
            finRango = inicio.add(reparto);
        }
        return finRango;
    }// pedirFin()

    private BigInteger numMax = BigInteger.ZERO;

    public BigInteger getNumMax() {
        return numMax;
    }// getNumMax()

    public synchronized void setNumMax(BigInteger numMax) {
        this.numMax = numMax;
    }// setNumMax()

    public boolean calculados(BigInteger calculado) {
        return calculados.get(calculado) != null;
    }// calculados()

    /**
	 * Actualiza los números ya calculados por algún hilo.
	 * @param secuencia Secuencia resultrado de los cáculos, todos los números en ella ya cuentan como números calculados.
	 */
    public synchronized void actualizarCalculados(String secuencia) {
        //Recibe un string con una secuencia, separa los numeros y los
        //mete en el hashmap en su lugar correspondiente
        String[] arraySecuencia = secuencia.split("-");
        BigInteger key;
        BigInteger value = BigInteger.ZERO;
        for (int i = 0; i < arraySecuencia.length - 1; ++i) {
            key = new BigInteger(arraySecuencia[i]);
            if (calculados.get(key) == null) {
                if (i < arraySecuencia.length - 1) {
                    value = new BigInteger(arraySecuencia[i + 1]);
                }
                calculados.put(key, value);
            }
        }
    }// actualizarCalculados()

    /**
	 * Obtiene el resto de la secuencia de un número que ya ha sido calculado.
     * @param num Número al que se ha llegado y ya está calculado del hilo.
	 * @return El resto de la longitud de la secuencia.
	 */
    public synchronized int getSecuencia(BigInteger num) {
        int contExtra = 0;
        BigInteger numActual;
        while (repetido.get(num) == null) {
            numActual = num;
            ++contExtra;
            num = calculados.get(num);
            repetido.put(numActual, num);
        }
        repetido.clear();
        return contExtra;
    }// getSecuencia()

    private int longitudSecuenciaMayor = 0;
    private ArrayList<BigInteger> semillaSecuenciaMayor = new ArrayList<BigInteger>();
    /**
	 * Comprueba y obtiene la secuencia más larga alcanzada por una semilla.
     * @param semilla Número que provoca la posible secuencia calculada más larga.
     * @param tamanioSecuencia Tamaño de la secuencia obtenida por la semilla.
	 */
    public synchronized void secuenciaMasLarga(BigInteger semilla, int tamanioSecuencia) {
        boolean semillaRepetida = false;
        //Diferencia si la semilla de secuencia mas larga ya le ha llegado antes para aniadirla o no
        if (tamanioSecuencia > longitudSecuenciaMayor) {
            semillaSecuenciaMayor.clear();
            semillaSecuenciaMayor.add(semilla);
            longitudSecuenciaMayor = tamanioSecuencia;
        } else if (tamanioSecuencia == longitudSecuenciaMayor) {
            for (int i = 0; i < semillaSecuenciaMayor.size(); ++i) {
                if (semillaSecuenciaMayor.contains(semilla)) {
                    semillaRepetida = true;
                } else {
                    semillaRepetida = false;
                }
            }
            if (!semillaRepetida) {
                semillaSecuenciaMayor.add(semilla);
            }
        }
    }// secuenciaMasLarga()

    /**
	 * Escribe la mayor secuencia encontrada.
     * @param siguienteNum key para conseguir el value del siguiente número en la secuencia.
	 */
    public String EscribirSecuenciaMasLarga(BigInteger siguienteNum) {
        repetido.clear();
        String secuenciaMayor = "";
        //Recorre el hashmap comenzando por la semilla que da lugar a la mas larga
        for (int i = 0; i < semillaSecuenciaMayor.size(); ++i) {
            BigInteger numActual;
            while (repetido.get(siguienteNum) == null) {
                numActual = siguienteNum;
                secuenciaMayor += siguienteNum + "-";
                siguienteNum = calculados.get(siguienteNum);
                repetido.put(numActual, siguienteNum);
            }
        }
        return secuenciaMayor;
    }// EscribirSecuenciaMasLarga()

    private boolean existeNuevoBucle = false;
    private BigInteger semillaNuevoBucle = BigInteger.ZERO;

    /**
	 * Escribe el nuevo bucle encontrado, en caso de existir.
	 * @param nuevoBucle determina si existe un nuevo bucle.
     * @param semillaNuevoBucle Número que da lugar al nuevo bucle.
	 */
    public synchronized void setBucle(boolean nuevoBucle, BigInteger semillaNuevoBucle) {
        existeNuevoBucle = nuevoBucle;
        this.semillaNuevoBucle = semillaNuevoBucle;
    }// setBucle()

    /**
	 * Saca por pantalla toda la información obtenido de los cálculos realizados.
	 */
    public void end(long TiempoEnEjecucion) {
        System.out.println("Tiempo de procesado: " + TiempoEnEjecucion + " milisegundos");

        //Diferencia si hay una o mas semillas con la mayor longitud de secuencia
        if (semillaSecuenciaMayor.size() > 1) {
            String semillas = "";
            for (int i = 0; i < semillaSecuenciaMayor.size(); ++i) {
                semillas += semillaSecuenciaMayor.get(i) + ".";
            }
            System.out.println("Semillas de las mayores secuencias encontradas: " + semillas);
            System.out.println("Longitud de las secuencias: " + (longitudSecuenciaMayor));
            for (int i = 0; i < semillaSecuenciaMayor.size(); ++i) {
                String secuenciaMayor = EscribirSecuenciaMasLarga(semillaSecuenciaMayor.get(i));
                System.out.println("Secuencia de " + semillaSecuenciaMayor.get(i) + ": " + secuenciaMayor.substring(0, secuenciaMayor.length() - 1));
            }
        } else {
            String secuenciaMayor = EscribirSecuenciaMasLarga(semillaSecuenciaMayor.get(0));
            System.out.println("Semilla de la mayor secuencia encontrada: " + semillaSecuenciaMayor.get(0));
            System.out.println("Longitud de la secuencia de " + semillaSecuenciaMayor.get(0) + ": " + (longitudSecuenciaMayor));
            //substring para quitar el ultimo - al sacar la secuencia por pantalla
            System.out.println("Secuencia de " + semillaSecuenciaMayor.get(0) + ": " + secuenciaMayor.substring(0, secuenciaMayor.length() - 1));
        }

        System.out.println("Numero maximo alcanzado: " + numMax);

        if (existeNuevoBucle) {
            System.out.println("Nuevo bucle encontrado!!");
            System.out.println("Generado por la semilla: " + semillaNuevoBucle);
            for (int i = 0; i < semillaSecuenciaMayor.size(); ++i) {
                String secuenciaMayor = EscribirSecuenciaMasLarga(semillaNuevoBucle);
                System.out.println("Secuencia del nuevo bucle de " + semillaNuevoBucle + ": " + secuenciaMayor.substring(0, secuenciaMayor.length() - 1));
            }
        } else {
            System.out.println("No existe nuevo bucle.");
        }
    }// end()

}// Datos
