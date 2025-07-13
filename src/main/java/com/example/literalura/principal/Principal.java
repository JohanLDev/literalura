package com.example.literalura.principal;

import java.util.Scanner;

/**
 * Clase principal encargada de implementar el menu con las opciones.
 *
 * @author Johan Rivera
 */
public class Principal {
    private Scanner teclado = new Scanner(System.in);


    public void hola(){
        System.out.println("Ingresa");
        var texto = teclado.nextLine();
        System.out.println("Has ingresado " + texto);

    }
}

