package batallanaval;

final class Sala {
    private static final String[] BARCOS = {"Portaaviones", "Acorazado", "Crucero", "Submarino", "Destructor"};
    private static final int[] LONGITUDES = {5, 4, 3, 3, 2};

    private final int numero;
    private final Jugador primero;
    private final Jugador segundo;
    private Jugador turno;
    private boolean terminada;

    Sala(int numero, Jugador primero, Jugador segundo) {
        this.numero = numero;
        this.primero = primero;
        this.segundo = segundo;
    }

    synchronized void iniciar() {
        pedirBarco(primero, "Sala " + numero + " | Rival: " + segundo.nombre);
        pedirBarco(segundo, "Sala " + numero + " | Rival: " + primero.nombre);
    }

    synchronized void procesar(Jugador jugador, Mensaje mensaje) {
        if (terminada) {
            return;
        }
        if (turno == null) {
            if (jugador.barcosColocados == BARCOS.length) {
                jugador.enviar(new Mensaje(Mensaje.AVISO, "Esperá a que tu rival termine de colocar sus barcos."));
                return;
            }
            if (!Mensaje.COLOCAR.equals(mensaje.tipo)) {
                pedirBarco(jugador, "Primero colocá tu flota.");
                return;
            }
            colocar(jugador, mensaje);
            return;
        }
        if (jugador != turno) {
            jugador.enviar(new Mensaje(Mensaje.AVISO, "Todavía es el turno de " + turno.nombre + "."));
            return;
        }
        if (!Mensaje.DISPARAR.equals(mensaje.tipo)) {
            pedirDisparo(jugador, "En tu turno tenés que disparar.");
            return;
        }
        disparar(jugador, mensaje.fila, mensaje.col);
    }

    private void colocar(Jugador jugador, Mensaje mensaje) {
        int indice = jugador.barcosColocados;
        if (!jugador.tablero.colocarBarco(BARCOS[indice], mensaje.fila, mensaje.col,
                LONGITUDES[indice], mensaje.horizontal)) {
            pedirBarco(jugador, "La posición se sale del tablero o se superpone con otro barco.");
            return;
        }
        jugador.barcosColocados++;
        if (jugador.barcosColocados < BARCOS.length) {
            pedirBarco(jugador, BARCOS[indice] + " colocado.");
        } else if (rival(jugador).barcosColocados == BARCOS.length) {
            turno = primero;
            pedirDisparo(primero, "¡Empieza la partida! Va tu turno.");
            avisarEspera(segundo, "¡Empieza la partida! Esperá el disparo de " + primero.nombre + ".");
        } else {
            avisarEspera(jugador, "Flota lista. Esperando a " + rival(jugador).nombre + ".");
        }
    }

    private void disparar(Jugador atacante, int fila, int col) {
        Jugador defensor = rival(atacante);
        String resultado = defensor.tablero.disparar(fila, col);
        if (Tablero.POSICION_INVALIDA.equals(resultado) || Tablero.DISPARO_REPETIDO.equals(resultado)) {
            pedirDisparo(atacante, resultado);
            return;
        }
        String coordenada = String.valueOf((char) ('A' + fila)) + (col + 1);
        String tiroPropio = "Disparaste a " + coordenada + " → " + resultado;
        String tiroEnemigo = atacante.nombre + " disparó a " + coordenada + " → " + resultado;
        String victoria = verificarVictoria(atacante);
        if (victoria != null) {
            terminada = true;
            finalizar(atacante, tiroPropio + "\n" + victoria);
            finalizar(defensor, tiroEnemigo + "\n" + verificarDerrota(defensor));
            System.out.println("Sala " + numero + " | Ganó " + atacante.nombre);
            return;
        }
        turno = defensor;
        avisarEspera(atacante, tiroPropio + "\nTurno de " + defensor.nombre + ".");
        pedirDisparo(defensor, tiroEnemigo + "\nEs tu turno.");
    }

    private String verificarVictoria(Jugador jugador) {
        return rival(jugador).tablero.todosLosBarcosCaidos()
                ? Colores.VERDE + "¡VICTORIA! Hundiste toda la flota rival." + Colores.RESET : null;
    }

    private String verificarDerrota(Jugador jugador) {
        return jugador.tablero.todosLosBarcosCaidos()
                ? Colores.ROJO + "DERROTA. Tu rival hundió toda tu flota." + Colores.RESET : null;
    }

    synchronized void desconectar(Jugador jugador) {
        if (!terminada) {
            terminada = true;
            finalizar(rival(jugador), jugador.nombre + " se desconectó. Partida terminada.");
        }
    }

    private void pedirBarco(Jugador jugador, String texto) {
        int indice = jugador.barcosColocados;
        publicar(jugador, Mensaje.COLOCAR, texto + "\nColocá " + BARCOS[indice]
                + " (" + LONGITUDES[indice] + " casillas). Ejemplo: A1 H (H horizontal, V vertical).");
    }

    private void pedirDisparo(Jugador jugador, String texto) {
        publicar(jugador, Mensaje.DISPARAR, texto + "\nIngresá una coordenada. Ejemplo: A1.");
    }

    private void avisarEspera(Jugador jugador, String texto) {
        publicar(jugador, Mensaje.AVISO, texto);
    }

    private void finalizar(Jugador jugador, String texto) {
        publicar(jugador, Mensaje.FIN, texto);
        jugador.cerrar();
    }

    private void publicar(Jugador jugador, String tipo, String texto) {
        String vista = "\n" + Colores.VERDE + "TU TABLERO" + Colores.RESET + "\n"
                + jugador.tablero.formatearTablero(false)
                + jugador.tablero.formatearBarcos()
                + "\n" + Colores.MORADO + "TABLERO RIVAL" + Colores.RESET + "\n"
                + rival(jugador).tablero.formatearTablero(true) + "\n" + texto;
        jugador.enviar(new Mensaje(tipo, vista));
    }

    private Jugador rival(Jugador jugador) {
        return jugador == primero ? segundo : primero;
    }
}
