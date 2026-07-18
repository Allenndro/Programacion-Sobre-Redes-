package tp5;

import java.util.Scanner;

public class Utilidades {

    public static Scanner teclado = new Scanner(System.in);

    public static void pausa() {

        System.out.println("\nPresione ENTER...");
        teclado.nextLine();

    }

}