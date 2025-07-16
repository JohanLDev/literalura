package com.example.literalura.principal;

import com.example.literalura.model.*;
import com.example.literalura.repository.AutorRepository;
import com.example.literalura.repository.LenguajeRepository;
import com.example.literalura.repository.LibroRepository;
import com.example.literalura.service.ConsumoAPI;
import com.example.literalura.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Scanner;

/**
 * Clase principal encargada de implementar el menu con las opciones.
 *
 * @author Johan Rivera
 */
public class Principal {
    private final Scanner teclado = new Scanner(System.in);
    private static final String URL_BASE = "https://gutendex.com";
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConvierteDatos convierteDatos = new ConvierteDatos();
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
                    2) Listar libros registrados
                    3) Listar autores registrados
                    4) Listar autores vivos en un determinado año
                    5) Listar libros por idioma
                    0) Salir
                    
                    """);
            int opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion){
                case 0:
                    System.out.println("Hasta luego...");
                    terminar = true;
                    break;
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnAnio();
                    break;
                case 5:
                    listarLibrosPorIdioma();
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

                    // se valida que si no existe entonces busque otro libro
                    coincide = libroRepository.findByTitulo(d.titulo()) == null;

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
            mostrarDatosDeLibro(libro);
        }
    }


    private void listarLibrosRegistrados(){
        libroRepository.findAll().forEach(this::mostrarDatosDeLibro);
    }

    private void listarAutoresRegistrados(){
        autorRepository.findAll().forEach(this::mostrarDatosDeAutor);
    }

    private void listarAutoresVivosEnAnio(){
        System.out.println("Ingrese el año vivo de autor(es) que desea buscar:");
        int anio = teclado.nextInt();
        teclado.nextLine();

        autorRepository.obtenerAutoresVivosEnAnio(anio).forEach(this::mostrarDatosDeAutor);
    }

    private void listarLibrosPorIdioma(){
        List<Lenguaje> lenguajes = lenguajeRepository.findAll();

        System.out.println("Seleccione el idioma del cual obtener libros: ");
        lenguajes.forEach( l->{
            System.out.println(l.getId() + ") " + Idioma.obtenerIdiomaCompleto(l.getIdioma()));
        });

        int id = teclado.nextInt();
        teclado.nextLine();

        Optional<Lenguaje> lenguajeSeleccionado = lenguajes.stream()
                .filter(l -> {
                    boolean coincide = l.getId() == id;
                    return coincide;
                })
                .findFirst();

        if(lenguajeSeleccionado.isEmpty()){
            System.out.println("No has seleccionado un idioma valido");
            return;
        }

        libroRepository.obtenerLibrosPorIdioma(lenguajeSeleccionado.get().getIdioma()).forEach(this::mostrarDatosDeLibro);
    }

    private void mostrarDatosDeLibro(Libro libro){
        System.out.println("*************LIBRO***********");
        System.out.println("Título: " + libro.getTitulo());
        System.out.println("Autor: " + libro.getAutor().getNombre());
        System.out.println("Idioma: " + libro.getLenguaje().getIdioma());
        System.out.println("Número de descargas: " + libro.getNumeroDeDescargas());
        System.out.println("*****************************\n\n");
    }

    private void mostrarDatosDeAutor(Autor autor){
        System.out.println("*************AUTOR***********");
        System.out.println("Nombre: " + autor.getNombre());
        System.out.println("Año Nacimiento: " + autor.getAnioNacimiento());
        System.out.println("Año Fallecimiento: " + autor.getAnioMuerte());
        System.out.println("Libros: " + autor.getLibros());
        System.out.println("*****************************\n\n");
    }





}

