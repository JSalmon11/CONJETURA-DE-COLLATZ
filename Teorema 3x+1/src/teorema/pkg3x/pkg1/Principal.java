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
            creadorHilosRangos(NumHilos);
        }else if (Metodo==1) {
            creadorHilosDe1En1(NumHilos);
            //Aqui hace todos los sout que pide el enunciado
        }
    }// main()
    
    private static void creadorHilosRangos(int numHilos) {
        Datos datos=new Datos(Inicio, Fin, numHilos);
        Thread[] hilos=new Thread[numHilos];       
        for (int i = 0; i < hilos.length; ++i) {
            HiloRangos h = new HiloRangos(datos);
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
        System.out.println("Tiempo de procesado: "+ TiempoEnEjecucion+" milisegundos");
        datos.end(1);
    }// creadorHilosRangos()
    
    private static void creadorHilosDe1En1(int numHilos){
        Datos datos = new Datos(Inicio,Fin);
        Thread[] hilos = new Thread[numHilos];
        for (int i = 0; i < hilos.length; ++i) {
            Hilo_De_Uno_En_Uno h = new Hilo_De_Uno_En_Uno(datos);
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
        System.out.println("Ha tardado "+ (FinTiempo-InicioTiempo) + " milisegundos");
        datos.end(2);
    }// creadorHiloDe1En1
    
}// Principal