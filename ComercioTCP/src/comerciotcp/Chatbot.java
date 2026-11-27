package comerciotcp;

final class Chatbot {
    private static final String MENU = "Menú de Soporte: 1. Estado de Envío | 2. Devoluciones | "
            + "3. Reclamos. Por favor, introduzca un comando válido.";
    private static final String[] FRASES = {
        "Estado de envío: buscá tu número de seguimiento en el correo de confirmación.",
        "Devoluciones: guardá el embalaje y el comprobante. Escribinos con el número de pedido.",
        "Reclamos: indicá tu número de pedido y describí qué pasó. Podés adjuntar un comprobante."
    };

    String responder(String texto) {
        return switch (Inventario.normalizar(texto)) {
            case "1", "estado de envio" -> FRASES[0];
            case "2", "devoluciones" -> FRASES[1];
            case "3", "reclamos" -> FRASES[2];
            case "ayuda" -> MENU + "\nComandos: autenticar | ayuda | ver stock de un producto"
                    + " | subir comprobante | ver historial de chat | salir"
                    + "\nTambién podés responder 1, 2 o 3.";
            default -> MENU;
        };
    }
}
