package teorema.pkg3x.pkg1;
import java.math.BigInteger;
import java.util.Scanner;

public class Principal {
    private static BigInteger Inicio;
    private static BigInteger Fin;
    
    public static void main(String[] args) {
        int Metodo;
        int NumHilos;
        Scanner teclado = new Scanner(System.in);
        System.out.println("Bienvenido al programa calculadora de casos del Teorema 3x+1");
        System.out.println("Que cantiad de hilos quiere utilizar");
        NumHilos = teclado.nextInt();teclado.nextLine();
        System.out.println("Que metodo quieres utilizar:\n \t DeUnoEnUno-1 \n\t SubIntervalos-2");
        Metodo = teclado.nextInt(); teclado.nextLine();
        System.out.println("Dime el inicio de las semillas a calcular");
        Inicio = new BigInteger(teclado.nextLine());
        System.out.println("Por ultimo dime el final de las semillas a calcular");
        Fin= new BigInteger(teclado.nextLine());
        
        if (Metodo==2) {
            creadorHilos(NumHilos, Metodo);
        }else if (Metodo==1) {
            creadorHilos(NumHilos, Metodo);
        }
    }// main()
    
    /**
	 * Crea hilos para ejecutar el programa de manera más rápida.
	 * @param numHilos El número de hilos que se crearán.
	 * @param metodo El método de ejecución del programa elegido.
     * @throws InterruptedException En caso de que se interrumpa la creación de hilos.
	 */
    private static void creadorHilos(int numHilos, int metodo) {
        Datos datos=new Datos(Inicio, Fin, numHilos);
        Thread[] hilos=new Thread[numHilos];       
        for (int i = 0; i < hilos.length; ++i) {
            Hilo h = new Hilo(datos, metodo);
            hilos[i] = new Thread(h);
            hilos[i].start();
        }
        Long InicioTiempo = System.currentTimeMillis();
        for (int i = 0; i < hilos.length; ++i) {
            try {
                hilos[i].join();
            } catch (InterruptedException e) {}
        }
        Long FinTiempo = System.currentTimeMillis();
        long TiempoEnEjecucion = FinTiempo - InicioTiempo;
        datos.end(TiempoEnEjecucion);
    }// creadorHilos()
    
}// Principal