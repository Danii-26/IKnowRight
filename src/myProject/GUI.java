package myProject;

import java.util.Random;
import java.util.Scanner;
import java.util.Vector;


/**
 * This class is used for ...
 * @autor Daniel Arias Castrillón 2222205 & Venus Juliana Paipilla 2177134
 * @version v.0.0.1 date:30/05/2023
 */
public class GUI{
    private static final int tiempoDeJuego = 5000; //Tiempo de cada palabra
    private static final int tiempoDeRespuesta = 7000; //Tiempo de respuesta

    private static final Vector palabras = new Vector(); //Vector de palabras
    private static final Random random = new Random();
    private static int nivel = 1; // Nivel actual

    public static void main(String[] args) {

        listaPalabras();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese su nombre: ");
        String jugador = scanner.nextLine();

        int nivelActual = getNivel(jugador);
        if (nivelActual > 1) {
            System.out.println(jugador + ", estás en el lvl." + nivelActual);
            nivel = nivelActual;
        } else {
            System.out.println("Bienvenido " + jugador + "!");
        }

        boolean continuar = true;

        while (continuar) {
            System.out.println("----lvl." + nivel + " ----");
            play();
            actualizarNivel(nivel);

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

    private static void listaPalabras() {
        palabras.add("java");
        palabras.add("python");
        palabras.add("listener");
        palabras.add("gui");
        palabras.add("scanner");
        palabras.add("eventos");
        palabras.add("programador");
        palabras.add("hacker");
    }
    private static int getNivel(String playerName) {
        // Lógica para obtener el nivel del jugador desde el almacenamiento (por ejemplo, una base de datos)
        // Si no se encuentra el jugador, se asume que es nivel 1
        // En este ejemplo, simplemente se devuelve 1
        return 1;
    }
    private static void actualizarNivel(int level) {
        // Lógica para actualizar el nivel del jugador en el almacenamiento (por ejemplo, una base de datos)
        // En este ejemplo, no se hace nada ya que no se guarda ningún estado
    }

    private static void play() {
        Vector secuencia = crearSecuencia(nivel);
        System.out.println("Memoriza las siguientes palabras:");
        mostrarSecuencia(secuencia);

        tiempoLímite(tiempoDeJuego);

        Vector listaPalabras = crearListaPalabras(secuencia);
        System.out.println("\nPalabras a adivinar:");
        mostrarLista(listaPalabras);

        int respuestasCorrectas = 0;
        for (Object word : secuencia) {
            System.out.print("\nLa palabra \"" + word + "\" estaba en la lista? (S/N): ");
            String respuesta = getTiempoUsuario(tiempoDeRespuesta);

            if (respuesta.equalsIgnoreCase("S") && listaPalabras.contains(word)) {
                respuestasCorrectas++;
            } else if (respuesta.equalsIgnoreCase("N") && !listaPalabras.contains(word)) {
                respuestasCorrectas++;
            }
        }

        System.out.println("\nAdivinaste " + respuestasCorrectas + " de " + secuencia.size() + " palabras.");
        if (respuestasCorrectas == secuencia.size()) {
            System.out.println("Pasas de nivel nivel.");
        } else {
            System.out.println("Has perdido.");
        }
    }

    private static Vector crearSecuencia(int level) {
        Vector sequence = new Vector();
        for (int i = 0; i < level; i++) {
            int randomIndex = random.nextInt(palabras.size());
            String word = (String) palabras.get(randomIndex);
            sequence.add(word);
        }
        return sequence;
    }

    private static void mostrarSecuencia(Vector<String> sequence) {
        for (String word : sequence) {
            System.out.println(word);
            tiempoLímite(tiempoDeJuego);
            clearConsole();
        }
    }

    private static Vector<String> crearListaPalabras(Vector<String> sequence) {
        Vector<String> wordListToGuess = new Vector<>(sequence);
        for (String word : sequence) {
            int randomIndex = random.nextInt(palabras.size());
            String randomWord = (String) palabras.get(randomIndex);
            wordListToGuess.add(randomWord);
        }
        return wordListToGuess;
    }

    private static void mostrarLista(Vector<String> wordList) {
        for (String word : wordList) {
            System.out.println(word);
        }
    }

    private static String getTiempoUsuario(int timeLimit) {
        Scanner scanner = new Scanner(System.in);
        final String[] userInput = {""};
        Thread inputThread = new Thread(() -> {
            userInput[0] = scanner.nextLine();
        });

        inputThread.start();
        try {
            Thread.sleep(timeLimit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inputThread.interrupt(); // Interrumpir el hilo de entrada
        return userInput[0];
    }

    private static void tiempoLímite(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}