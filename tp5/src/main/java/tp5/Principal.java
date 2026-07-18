package tp5;

public class Principal {

    public static void main(String[] args) {

        GestorNodos gestor = new GestorNodos();

        PersistenciaBinaria binario = new PersistenciaBinaria();

        PersistenciaJSON json = new PersistenciaJSON();

        while (true) {

            System.out.println("===== MONITOR =====");

            System.out.println("1. Cargar Binario");
            System.out.println("2. Alta");
            System.out.println("3. Baja");
            System.out.println("4. Modificar");
            System.out.println("5. Simular");
            System.out.println("6. Guardar");
            System.out.println("7. Exportar JSON");
            System.out.println("8. Importar JSON");
            System.out.println("9. Mostrar");
            System.out.println("0. Salir");

     

        }

    }

}