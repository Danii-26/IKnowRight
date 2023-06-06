package myProject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GUI {
    private static final int tiempoDeJuego = 5000; // Tiempo de cada palabra
    private static final int tiempoDeRespuesta = 7000; //Tiempo de respuesta

    private static final List<String> palabras = new Vector<>(); //Lista de palabras
    private static final Random random = new Random(); //Para elegir una palabra al azar
    private static int nivel = 1; // Nivel inicial

    //Reglas tabla de niveles:
    private static final int[] numPalabrasNivel = {10, 20, 25, 30, 35, 40, 50, 60, 70, 100};
    private static final int[] numPalabrasNivelTotal = {20, 40, 50, 60, 70, 80, 100, 120, 140, 200};
    private static final double[] porcentajeAciertosNivel = {0.7, 0.7, 0.75, 0.8, 0.8, 0.85, 0.9, 0.9, 0.95, 1.0};

    public static void main(String[] args) {
        listaPalabras();

        String jugador = "";
        int nivelGuardado = 0;

        if (existeArchivo("save.txt")) {
            String[] datosGuardados = getDatosGuardados();
            if (datosGuardados.length == 2) {
                jugador = datosGuardados[0];
                nivelGuardado = Integer.parseInt(datosGuardados[1]);
            }
        }

        Scanner scanner = new Scanner(System.in);

        if (!jugador.isEmpty()){
            System.out.println("Hola " + jugador + "!");

            System.out.print("¿Quieres continuar desde el nivel guardado (" + nivelGuardado + ")? (S/N): ");
            String respuesta = scanner.nextLine().toUpperCase();
            if (respuesta.equals("S")) {
                nivel = nivelGuardado;
            }else{
                jugador = getNuevoJugador(scanner);
            }
        }else{
            jugador = getNuevoJugador(scanner);
        }

        System.out.println("Bienvenido " + jugador + "!");

        boolean continuar = true; //Comienza el juego. Si !continuar, entonces se deja de cumplir el while.

        while (continuar) {
            if (nivel > 10) {
                break;
            }

            System.out.println("----lvl." + nivel + "----");
            play();

            System.out.print("Desea seguir jugando? (S/N): ");
            String playNextLevel = scanner.nextLine().toUpperCase();
            if (!playNextLevel.equals("S")) {
                continuar = false;
            } else {
                nivel++;
            }
        }

        System.out.println("Game over.");
        guardarJugador(jugador, nivel); //Guarda el nombre y nivel
        scanner.close();
    }

    private static String getNuevoJugador(Scanner scanner) {
        System.out.print("Ingrese su nombre: ");
        return scanner.nextLine();
    }
    //Revisa si existe el save.txt
    private static boolean existeArchivo(String nombreArchivo){
        return Files.exists(Paths.get(nombreArchivo));
    }

    //Obtiene los datos de guardado de la anterior partida.
    private static String[] getDatosGuardados(){
        try {
            List<String> lines = Files.readAllLines(Paths.get("save.txt"));
            if (!lines.isEmpty()) {
                String jugadorSaved = lines.get(0);
                String nivelSaved = lines.get(1);
                String[] datos = new String[2];
                datos[0] = jugadorSaved.substring(jugadorSaved.indexOf(":") + 2);
                datos[1] = nivelSaved.substring(nivelSaved.indexOf(":") + 2);
                return datos;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    private static void listaPalabras(){
        try {
            List<String> lines = Files.readAllLines(Paths.get("palabras.txt"));
            palabras.addAll(lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void guardarJugador(String nombre, int nivel){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("save.txt"))) {
            writer.write("Nombre: " + nombre + "\n");
            writer.write("Nivel: " + nivel + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ocurrió un error al guardar los datos del jugador.");
        }
    }


    private static void play(){
        int numPalabrasMemorizar1 = numPalabrasNivel[nivel - 1];
        int numPalabrasNivelTotal1 = numPalabrasNivelTotal[nivel - 1];
        double porcentajeAciertosNivel1 = porcentajeAciertosNivel[nivel - 1];

        Vector<String> secuencia = crearSecuencia(numPalabrasMemorizar1);
        System.out.println("Memoriza las siguientes palabras: ");
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

    private static Vector<String> crearListaPalabras(Vector<String> sequence, int numPalabrasNivelTotal) {
        Set<String> wordSet = new HashSet<>();
        Vector<String> wordListToGuess = new Vector<>();
        int remainingWords = numPalabrasNivelTotal - sequence.size();

        // Agregar las palabras de la secuencia memorizada al conjunto
        wordSet.addAll(sequence);

        // Generar palabras aleatorias adicionales hasta alcanzar el número requerido
        while (wordSet.size() < numPalabrasNivelTotal) {
            int randomIndex = random.nextInt(palabras.size());
            String randomWord = palabras.get(randomIndex);
            wordSet.add(randomWord);
        }

        // Agregar las palabras del conjunto a la lista de palabras a adivinar
        wordListToGuess.addAll(wordSet);

        // Mezclar la lista de palabras
        Collections.shuffle(wordListToGuess);

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
                Runtime.getRuntime().exec("/clear");
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (final Exception e){
            e.printStackTrace();
        }
    }
}