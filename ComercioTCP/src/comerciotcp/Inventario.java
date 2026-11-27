package comerciotcp;

import java.text.Normalizer;
import java.util.Locale;

final class Inventario {
    private final String[] nombres = {"teclado", "mouse", "monitor", "auriculares", "webcam"};
    private final int[] cantidades = {12, 20, 5, 0, 8};

    String consultar(String producto) {
        for (int i = 0; i < nombres.length; i++) {
            if (nombres[i].equals(normalizar(producto))) {
                return "Stock de " + nombres[i] + ": " + cantidades[i] + " unidades.";
            }
        }
        return "Producto desconocido. Disponibles: " + String.join(", ", nombres) + ".";
    }

    static String normalizar(String texto) {
        return Normalizer.normalize(texto.strip().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
    }
}
