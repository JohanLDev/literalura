package com.example.literalura.model;


public enum Idioma {
    ESPANOL ("es", "español"),
    INGLES ("en", "inglés"),
    FRANCES("fr", "francés"),
    PORTUGUES("pt", "portugués");

    private String idiomaAPI;
    private String idiomaCompleto;

    Idioma(String idiomaAPI, String idiomaCompleto) {
        this.idiomaAPI = idiomaAPI;
        this.idiomaCompleto = idiomaCompleto;
    }


    public static String obtenerIdiomaCompleto(String idiomaAPI){
        for (Idioma idioma : Idioma.values()){
            if(idioma.idiomaAPI.equals(idiomaAPI)){
                return idioma.idiomaCompleto;
            }
        }
        return idiomaAPI;
    }
}
