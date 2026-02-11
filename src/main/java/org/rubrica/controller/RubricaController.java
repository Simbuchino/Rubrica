package org.rubrica.controller;

import org.rubrica.model.Persona;
import org.rubrica.persistence.RubricaRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RubricaController {

    private final List<Persona> persone;
    private final RubricaRepository repository;

    public RubricaController() {
        this.repository = new RubricaRepository();
        List<Persona> caricate = new ArrayList<>();
        try {
            caricate = repository.carica();
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento dei dati: " + e.getMessage());
        }
        this.persone = caricate;
    }

    public void aggiungiPersona(Persona p) {
        if (p == null) throw new IllegalArgumentException("Persona non può essere null");
        persone.add(p);
        try {
            repository.savePersona(p);
        } catch (Exception e) {
            persone.remove(p);
            throw new RuntimeException("Errore durante il salvataggio della persona", e);
        }
    }

    public void modificaPersona(int index, Persona nuovaPersona) {
        if (nuovaPersona == null) throw new IllegalArgumentException("Persona non può essere null");
        if (index < 0 || index >= persone.size()) throw new IndexOutOfBoundsException("Indice non valido: " + index);

        Persona vecchia = persone.get(index);
        persone.set(index, nuovaPersona);
        try {
            repository.savePersona(nuovaPersona);
        } catch (Exception e) {
            persone.set(index, vecchia); // rollback in memoria
            throw new RuntimeException("Errore durante la modifica della persona", e);
        }
    }

    public void eliminaPersona(int index) {
        if (index < 0 || index >= persone.size()) throw new IndexOutOfBoundsException("Indice non valido: " + index);

        Persona p = persone.get(index);
        persone.remove(index);
        try {
            repository.eliminaPersona(p);
        } catch (Exception e) {
            persone.add(index, p); // rollback in memoria
            throw new RuntimeException("Errore durante l'eliminazione della persona", e);
        }
    }

    public List<Persona> getPersone() {
        return Collections.unmodifiableList(persone);
    }
}