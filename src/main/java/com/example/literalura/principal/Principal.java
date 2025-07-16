package com.example.literalura.principal;

import com.example.literalura.model.*;
import com.example.literalura.repository.AutorRepository;
import com.example.literalura.repository.LenguajeRepository;
import com.example.literalura.repository.LibroRepository;
import com.example.literalura.service.ConsumoAPI;
import com.example.literalura.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Scanner;

/**
 * Clase principal encargada de implementar el menu con las opciones.
 *
 * @author Johan Rivera
 */
public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private static String URL_BASE = "https://gutendex.com";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;
    private LenguajeRepository lenguajeRepository;


    public Principal(LibroRepository libroRepository, AutorRepository autorRepository, LenguajeRepository lenguajeRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
        this.lenguajeRepository = lenguajeRepository;
    }

    /**
     * Método encargado de mostrar el menú de opciones y además ejecutar la opción escogida por el usuario.
     */
    public void inicio(){
        boolean terminar = false;

        while(!terminar){
            System.out.println("""
                    Seleccione una opción: 
                    1) Buscar libro por título
                    2) Sali
                    
                    """);
            int opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion){
                case 1:
                    buscarLibro();
                    terminar = true;
                    break;
                case 2:
                    System.out.println("Hasta luego...");
                    terminar = true;
                    break;
                default:
                    System.out.println("Seleccione una opción valida.");
            }
        }

    }


    /**
     * Busca libros en la API por título.
     */
    private void buscarLibro(){

        System.out.println("Ingrese el nombre del libro a buscar: ");
        String nombreLibro = teclado.nextLine();
        String json = consumoAPI.obtenerDatos( URL_BASE+"/books/?search=" + nombreLibro.replace(" ","+"));

        System.out.println("Nombre ingresado: " + nombreLibro);

        if(json.isBlank()){
            System.out.println("No se logró obtener un libro con ese nombre");
            return;
        }

        DatosPrincipal datosPrincipal = convierteDatos.obtenerDatos(json, DatosPrincipal.class);

        if(datosPrincipal == null){
            System.out.println("No se logró convertir a objeto DatosLibro");
            return;
        }

        Optional<DatosLibro> datosLibro = datosPrincipal.libros().stream()
                .filter(d -> {
                    boolean coincide = d.titulo().contains(nombreLibro);
                    if (coincide) {
                        System.out.println("Texto coincide con: " + nombreLibro);
                        System.out.println(d.titulo());
                    }
                    return coincide;
                })
                .findFirst();

        if (datosLibro.isPresent()) {
            Libro libro = new Libro(datosLibro.get());
            Libro libroExistente = libroRepository.findByTitulo(libro.getTitulo());

            if(libroExistente != null){
                System.out.println("Libro ya existe en base de datos");
                return;
            }



            Autor autor = autorRepository.findByNombre(libro.getAutor().getNombre());
            if (autor != null) {
                libro.setAutor(autor);
            } else {
                autorRepository.save(libro.getAutor());
            }


            Lenguaje lenguaje = lenguajeRepository.findByIdioma(libro.getLenguaje().getIdioma());
            if (lenguaje != null) {
                libro.setLenguaje(lenguaje);
            } else {
                lenguajeRepository.save(libro.getLenguaje());
            }

            libroRepository.save(libro);
        }

    }






}

