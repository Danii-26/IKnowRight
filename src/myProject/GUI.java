package myProject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

/**
 * This class is used for ...
 * @autor Daniel Arias Castrillón 2222205 & Venus Juliana Paipilla 2177134
 * @version v.0.0.1 date:30/05/2023
 */
public class GUI{
    private static final int tiempoDeJuego = 5000; // Tiempo de cada palabra
    private static final int tiempoDeRespuesta = 7000; // Tiempo de respuesta

    private static final List<String> palabras = new Vector<>(); // Vector de palabras
    private static final Random random = new Random();
    private static int nivel = 1; // Nivel actual

    //Reglas tabla de niveles
    private static final int[] numPalabrasNivel = {10, 20, 25, 30, 35, 40, 50, 60, 70, 100};
    private static final int[] numPalabrasNivelTotal = {20, 40, 50, 60, 70, 80, 100, 120, 140, 200};
    private static final double[] porcentajeAciertosNivel = {0.7, 0.7, 0.75, 0.8, 0.8, 0.85, 0.9, 0.9, 0.95, 1.0};

    public static void main(String[] args){
        listaPalabras();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese su nombre: ");
        String jugador = scanner.nextLine();

        System.out.println("Bienvenido " + jugador + "!");

        boolean continuar = true;

        while (continuar) {
            if (nivel > 10) {
                break;
            }

            System.out.println("----lvl." + nivel + "----");
            play();

            System.out.print("Desea seguir jugando?(S/N): ");
            String playNextLevel = scanner.nextLine().toUpperCase();
            if (!playNextLevel.equals("S")) {
                continuar = false;
            } else {
                nivel++;
            }
        }

        System.out.println("Game over.");

        scanner.close();
    }

    private static void listaPalabras(){
        try {
            List<String> lines = Files.readAllLines(Paths.get("palabras.txt"));
            palabras.addAll(lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void play(){
        int numPalabrasMemorizar1 = numPalabrasNivel[nivel - 1];
        int numPalabrasNivelTotal1 = numPalabrasNivelTotal[nivel - 1];
        double porcentajeAciertosNivel1 = porcentajeAciertosNivel[nivel - 1];

        Vector<String> secuencia = crearSecuencia(numPalabrasMemorizar1);
        System.out.println("Memoriza las siguientes palabras:");
        mostrarSecuencia(secuencia);

        tiempoLímite(tiempoDeJuego);

        Vector<String> listaPalabras = crearListaPalabras(secuencia, numPalabrasNivelTotal1);
        System.out.println("\nPalabras a adivinar:");
        mostrarLista(listaPalabras);

        int respuestasCorrectas = 0;
        for (String word : secuencia) {
            System.out.print("\nLa palabra \"" + word + "\" estaba en la lista? (S/N): ");
            String respuesta = getTiempoUsuario(tiempoDeRespuesta);

            if (respuesta.equalsIgnoreCase("S") && listaPalabras.contains(word)) {
                respuestasCorrectas++;
            } else if (respuesta.equalsIgnoreCase("N") && !listaPalabras.contains(word)) {
                respuestasCorrectas++;
            }
        }

        int numAciertosNecesarios = (int) Math.ceil(porcentajeAciertosNivel1 * secuencia.size());
        System.out.println("\nAdivinaste " + respuestasCorrectas + " de " + secuencia.size() + " palabras.");
        if (respuestasCorrectas >= numAciertosNecesarios) {
            System.out.println("Pasas de nivel.");
        } else {
            System.out.println("Has perdido.");
        }
    }

    private static Vector<String> crearSecuencia(int numPalabrasMemorizar){
        Vector<String> sequence = new Vector<>();
        for (int i = 0; i < numPalabrasMemorizar; i++) {
            int randomIndex = random.nextInt(palabras.size());
            String word = palabras.get(randomIndex);
            sequence.add(word);
        }
        return sequence;
    }

    private static void mostrarSecuencia(Vector<String> sequence){
        for (String word : sequence) {
            System.out.println(word);
            tiempoLímite(tiempoDeJuego);
            clearConsole();
        }
    }

    private static Vector<String> crearListaPalabras(Vector<String> sequence, int numPalabrasNivelTotal){
        Vector<String> wordListToGuess = new Vector<>(sequence);
        int remainingWords = numPalabrasNivelTotal - sequence.size();
        for (int i = 0; i < remainingWords; i++) {
            int randomIndex = random.nextInt(palabras.size());
            String randomWord = palabras.get(randomIndex);
            wordListToGuess.add(randomWord);
        }
        return wordListToGuess;
    }

    private static void mostrarLista(Vector<String> wordList){
        for (String word : wordList){
            System.out.println(word);
        }
    }

    private static String getTiempoUsuario(int timeLimit){
        Scanner scanner = new Scanner(System.in);
        final String[] userInput = {""};
        Thread inputThread = new Thread(() -> {
            userInput[0] = scanner.nextLine();
        });

        inputThread.start();
        try {
            Thread.sleep(timeLimit);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        inputThread.interrupt(); // Interrumpir el hilo de entrada
        return userInput[0];
    }

    private static void tiempoLímite(int milliseconds){
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private static void clearConsole(){
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (final Exception e){
            e.printStackTrace();
        }
    }
}