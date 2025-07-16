package com.example.literalura.repository;

import com.example.literalura.model.Lenguaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LenguajeRepository  extends JpaRepository<Lenguaje, Long> {
    Lenguaje findByIdioma(String idioma);
}
