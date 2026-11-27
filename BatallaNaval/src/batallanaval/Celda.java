package batallanaval;

public class Celda {
    private TipoCelda tipo;
    private boolean disparada;
    private String nombreBarco;

    public Celda() {
        this.tipo = TipoCelda.AGUA;
        this.disparada = false;
        this.nombreBarco = null;
    }

    public TipoCelda getTipo() { return tipo; }
    public boolean isDisparada() { return disparada; }
    public String getNombreBarco() { return nombreBarco; }

    public void ocupar(String nombreBarco) {
        this.nombreBarco = nombreBarco;
        tipo = TipoCelda.BARCO;
    }

    public void recibirDisparo() {
        tipo = tipo == TipoCelda.BARCO ? TipoCelda.IMPACTO : TipoCelda.FALLO;
        disparada = true;
    }
}
