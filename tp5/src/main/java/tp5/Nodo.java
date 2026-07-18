package tp5;

import java.io.Serializable;

public class Nodo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ip;
    private String nombre;
    private String endpoint;
    private int codigoHttp;
    private int tiempoRespuesta;

    public Nodo() {

    }

    public Nodo(String ip, String nombre, String endpoint, int codigoHttp, int tiempoRespuesta) {
        this.ip = ip;
        this.nombre = nombre;
        this.endpoint = endpoint;
        this.codigoHttp = codigoHttp;
        this.tiempoRespuesta = tiempoRespuesta;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getCodigoHttp() {
        return codigoHttp;
    }

    public void setCodigoHttp(int codigoHttp) {
        this.codigoHttp = codigoHttp;
    }

    public int getTiempoRespuesta() {
        return tiempoRespuesta;
    }

    public void setTiempoRespuesta(int tiempoRespuesta) {
        this.tiempoRespuesta = tiempoRespuesta;
    }

}