package batallanaval;

import java.util.ArrayList;

public class Tablero {
    public static final int TAMANIO = 10;
    static final String POSICION_INVALIDA = "Posición fuera del tablero.";
    static final String DISPARO_REPETIDO = "Ya disparaste a esa casilla.";
    private final ArrayList<ArrayList<Celda>> tablero = new ArrayList<>();
    private final ArrayList<Barco> barcos = new ArrayList<>();

    public Tablero() {
        inicializarTablero();
    }

    private void inicializarTablero() {
        for (int fila = 0; fila < TAMANIO; fila++) {
            ArrayList<Celda> celdas = new ArrayList<>();
            for (int col = 0; col < TAMANIO; col++) {
                celdas.add(new Celda());
            }
            tablero.add(celdas);
        }
    }

    public boolean colocarBarco(String nombre, int fila, int col, int longitud, boolean horizontal) {
        if (nombre == null || longitud < 1 || longitud > TAMANIO || !posicionValida(fila, col)) {
            return false;
        }
        int ultimaFila = horizontal ? fila : fila + longitud - 1;
        int ultimaCol = horizontal ? col + longitud - 1 : col;
        if (!posicionValida(ultimaFila, ultimaCol)) {
            return false;
        }
        for (int paso = 0; paso < longitud; paso++) {
            if (celdaBarco(fila, col, paso, horizontal).getTipo() != TipoCelda.AGUA) {
                return false;
            }
        }
        for (int paso = 0; paso < longitud; paso++) {
            celdaBarco(fila, col, paso, horizontal).ocupar(nombre);
        }
        barcos.add(new Barco(nombre, longitud));
        return true;
    }

    public String disparar(int fila, int col) {
        if (!posicionValida(fila, col)) {
            return POSICION_INVALIDA;
        }
        Celda celda = tablero.get(fila).get(col);
        if (celda.isDisparada()) {
            return DISPARO_REPETIDO;
        }
        celda.recibirDisparo();
        if (celda.getTipo() == TipoCelda.FALLO) {
            return "Agua";
        }
        for (Barco barco : barcos) {
            if (barco.getNombre().equals(celda.getNombreBarco())) {
                barco.recibirImpacto();
                return barco.estaHundido() ? "¡HUNDIDO! " + barco.getNombre() : "¡IMPACTO! Tocado";
            }
        }
        throw new IllegalStateException("La celda no pertenece a un barco registrado.");
    }

    public void mostrarTablero(boolean ocultarBarcos) {
        System.out.print(formatearTablero(ocultarBarcos));
    }

    String formatearTablero(boolean ocultarBarcos) {
        StringBuilder texto = new StringBuilder("    1  2  3  4  5  6  7  8  9 10\n");
        for (int fila = 0; fila < TAMANIO; fila++) {
            texto.append((char) ('A' + fila)).append("   ");
            for (Celda celda : tablero.get(fila)) {
                String simbolo = switch (celda.getTipo()) {
                    case AGUA -> Colores.CIAN + "~";
                    case BARCO -> ocultarBarcos ? Colores.CIAN + "~" : Colores.VERDE + "B";
                    case IMPACTO -> Colores.ROJO + "X";
                    case FALLO -> Colores.AMARILLO + "O";
                };
                texto.append(simbolo).append(Colores.RESET).append("  ");
            }
            texto.append('\n');
        }
        return texto.toString();
    }

    public void mostrarBarcos() {
        System.out.print(formatearBarcos());
    }

    String formatearBarcos() {
        StringBuilder texto = new StringBuilder();
        for (Barco barco : barcos) {
            texto.append('\t').append(barco.getNombre())
                 .append(" | Longitud: ").append(barco.getLongitud())
                 .append(" | Impactos: ").append(barco.getImpactos())
                 .append(" | ").append(barco.estaHundido() ? "HUNDIDO" : "ACTIVO").append('\n');
        }
        return texto.toString();
    }

    public boolean todosLosBarcosCaidos() {
        if (barcos.isEmpty()) {
            return false;
        }
        for (Barco barco : barcos) {
            if (!barco.estaHundido()) {
                return false;
            }
        }
        return true;
    }

    private boolean posicionValida(int fila, int col) {
        return fila >= 0 && fila < TAMANIO && col >= 0 && col < TAMANIO;
    }

    private Celda celdaBarco(int fila, int col, int paso, boolean horizontal) {
        return tablero.get(horizontal ? fila : fila + paso).get(horizontal ? col + paso : col);
    }
}
