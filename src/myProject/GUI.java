package myProject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import javax.swing.*;

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

        if (!jugador.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Hola " + jugador + "!");
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Quieres continuar desde el nivel guardado (" + nivelGuardado + ")?", "Continuar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                nivel = nivelGuardado;
            } else {
                jugador = getNuevoJugador();
            }
        } else {
            jugador = getNuevoJugador();
        }

        JOptionPane.showMessageDialog(null, "Bienvenido " + jugador + "!");

        boolean continuar = true; //Comienza el juego. Si !continuar, entonces se deja de cumplir el while.

        while (continuar) {
            if (nivel > 10) {
                break;
            }

            JOptionPane.showMessageDialog(null, "----lvl." + nivel + "----");
            play();

            int playNextLevel = JOptionPane.showConfirmDialog(null, "Desea seguir jugando?", "Continuar", JOptionPane.YES_NO_OPTION);
            if (playNextLevel == JOptionPane.NO_OPTION) {
                continuar = false;
            } else {
                nivel++;
            }
        }

        JOptionPane.showMessageDialog(null, "Game over.");
        guardarJugador(jugador, nivel); //Guarda el nombre y nivel
    }

    private static String getNuevoJugador() {
        return JOptionPane.showInputDialog("Ingrese su nombre:");
    }

    //Revisa si existe el save.txt
    private static boolean existeArchivo(String nombreArchivo) {
        return Files.exists(Paths.get(nombreArchivo));
    }

    //Obtiene los datos de guardado de la anterior partida.
    private static String[] getDatosGuardados() {
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

    private static void listaPalabras() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("palabras.txt"));
            palabras.addAll(lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void guardarJugador(String nombre, int nivel) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("save.txt"))) {
            writer.write("Nombre: " + nombre + "\n");
            writer.write("Nivel: " + nivel + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ocurrió un error al guardar los datos del jugador.");
        }
    }


    private static void play() {
        int numPalabrasMemorizar1 = numPalabrasNivel[nivel - 1];
        int numPalabrasNivelTotal1 = numPalabrasNivelTotal[nivel - 1];
        double porcentajeAciertosNivel1 = porcentajeAciertosNivel[nivel - 1];

        Vector<String> secuencia = crearSecuencia(numPalabrasMemorizar1);
        JOptionPane.showMessageDialog(null, "Memoriza las siguientes palabras:\n" + secuencia);

        tiempoLímite(tiempoDeJuego);

        Vector<String> listaPalabras = crearListaPalabras(secuencia, numPalabrasNivelTotal1);
        JOptionPane.showMessageDialog(null, "Palabras a adivinar:\n" + listaPalabras);

        int respuestasCorrectas = 0;
        for (String word : secuencia) {
            int respuesta = JOptionPane.showConfirmDialog(null, "La palabra \"" + word + "\" estaba en la lista?", "Respuesta", JOptionPane.YES_NO_OPTION);
            if ((respuesta == JOptionPane.YES_OPTION && listaPalabras.contains(word))
                    || (respuesta == JOptionPane.NO_OPTION && !listaPalabras.contains(word))) {
                respuestasCorrectas++;
            }
        }

        int numAciertosNecesarios = (int) Math.ceil(porcentajeAciertosNivel1 * secuencia.size());
        JOptionPane.showMessageDialog(null, "Adivinaste " + respuestasCorrectas + " de " + secuencia.size() + " palabras.");
        if (respuestasCorrectas >= numAciertosNecesarios) {
            JOptionPane.showMessageDialog(null, "Pasas de nivel.");
        } else {
            JOptionPane.showMessageDialog(null, "Has perdido.");
        }
    }

    private static Vector<String> crearSecuencia(int numPalabrasMemorizar) {
        Vector<String> sequence = new Vector<>();
        for (int i = 0; i < numPalabrasMemorizar; i++) {
            int randomIndex = random.nextInt(palabras.size());
            String word = palabras.get(randomIndex);
            sequence.add(word);
        }
        return sequence;
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

    private static void tiempoLímite(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
