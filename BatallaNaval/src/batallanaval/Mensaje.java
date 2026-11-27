package batallanaval;

import java.io.Serializable;

public class Mensaje implements Serializable {
    private static final long serialVersionUID = 1L;

    static final String LOGIN = "LOGIN";
    static final String COLOCAR = "COLOCAR";
    static final String DISPARAR = "DISPARAR";
    static final String AVISO = "AVISO";
    static final String FIN = "FIN";
    static final String SALIR = "SALIR";

    final String tipo;
    final String texto;
    final String codigo;
    final int fila;
    final int col;
    final boolean horizontal;

    Mensaje(String tipo, String texto) {
        this(tipo, texto, null, -1, -1, false);
    }

    Mensaje(String tipo, String texto, String codigo) {
        this(tipo, texto, codigo, -1, -1, false);
    }

    Mensaje(String tipo, int fila, int col, boolean horizontal) {
        this(tipo, null, null, fila, col, horizontal);
    }

    private Mensaje(String tipo, String texto, String codigo, int fila, int col, boolean horizontal) {
        this.tipo = tipo;
        this.texto = texto;
        this.codigo = codigo;
        this.fila = fila;
        this.col = col;
        this.horizontal = horizontal;
    }
}
