package com.example.literalura.service;

import com.example.literalura.model.Autor;
import com.example.literalura.model.DatosAutor;
import com.example.literalura.model.Lenguaje;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase creada para fábricas entidades desde un objeto
 */
public class Fabrica {


    public static Autor obtenerSoloUnActor(List<DatosAutor> listaDatosAutores   ){

        if(listaDatosAutores.size() == 0){
            System.out.println("No hay autores en la lista.");
            return null;
        }

        // Obtenemos solo el primero
        DatosAutor datosAutor = listaDatosAutores.get(0);

        return new Autor(datosAutor.nombre(), datosAutor.anoNacimiento(),datosAutor.anoMuerte());
    }

    public static Lenguaje obtenerSoloUnLenguaje(List<String> lenguajes){

        if(lenguajes.size() == 0){
            System.out.println("No hay lenguajes en la lista.");
            return null;
        }

        return new Lenguaje(lenguajes.get(0));

    }

}
