package org.rubrica.persistence;

import org.rubrica.model.Persona;
import org.rubrica.util.CryptoUtil;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Stream;

public class RubricaRepository {

    private static final String CARTELLA_BASE = "./informazioni";
    private static final String ESTENSIONE = ".txt";
    private static final String SEPARATORE = ";";

    private final Path folderPath;
    private final SecretKey encryptionKey;  // ← Chiave AES per cifratura

    public RubricaRepository(String username, SecretKey encryptionKey) {
        this.encryptionKey = encryptionKey;
        this.folderPath = Paths.get(CARTELLA_BASE, username);  // informazioni/username/

        try {
            if (Files.notExists(folderPath)) {
                Files.createDirectories(folderPath);  // createDirectories = crea anche cartelle parent
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossibile creare la cartella dati: " + folderPath.toAbsolutePath(), e);
        }
    }

    public List<Persona> carica() throws IOException {
        List<Persona> output = new ArrayList<>();

        try (Stream<Path> files = Files.list(folderPath)) {
            files.filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString().endsWith(ESTENSIONE))
                    .forEach(file -> {
                        try {
                            Persona p = caricaFile(file);
                            if (p != null) output.add(p);
                        } catch (Exception e) {
                            System.err.println("File ignorato (formato non valido o chiave errata): " +
                                    file.getFileName() + " - " + e.getMessage());
                        }
                    });
        }
        return output;
    }

    private Persona caricaFile(Path file) throws Exception {
        String nomeFile = file.getFileName().toString().replace(ESTENSIONE, "");
        UUID id = UUID.fromString(nomeFile);

        try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8)) {
            if (!scanner.hasNextLine()) {
                System.err.println("File vuoto ignorato: " + file.getFileName());
                return null;
            }

            String rigaCifrata = scanner.nextLine();

            // ← DECIFRA il contenuto
            String rigaDecifrata = CryptoUtil.decrypt(rigaCifrata, encryptionKey);

            String[] dati = rigaDecifrata.split(SEPARATORE);

            if (dati.length != 5) {
                throw new IllegalArgumentException("Formato non valido, attesi 5 campi, trovati: " + dati.length);
            }

            return new Persona(
                    id,
                    dati[0],
                    dati[1],
                    dati[2],
                    dati[3],
                    Integer.parseInt(dati[4])
            );
        }
    }

    public void savePersona(Persona persona) {
        if (persona == null) throw new IllegalArgumentException("Persona non può essere null");

        Path filePath = folderPath.resolve(persona.getId().toString() + ESTENSIONE);

        try {
            // Crea stringa in chiaro
            String plaintext = String.join(SEPARATORE,
                    persona.getNome(),
                    persona.getCognome(),
                    persona.getIndirizzo(),
                    persona.getTelefono(),
                    String.valueOf(persona.getEta())
            );

            // ← CIFRA il contenuto
            String ciphertext = CryptoUtil.encrypt(plaintext, encryptionKey);

            // Scrivi solo il ciphertext nel file
            try (PrintStream printStream = new PrintStream(filePath.toFile(), StandardCharsets.UTF_8)) {
                printStream.println(ciphertext);
            }

        } catch (Exception e) {
            throw new RuntimeException("Errore salvataggio persona: " + persona.getId(), e);
        }
    }

    public void eliminaPersona(Persona persona) {
        if (persona == null) throw new IllegalArgumentException("Persona non può essere null");

        Path filePath = folderPath.resolve(persona.getId().toString() + ESTENSIONE);

        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Errore eliminazione persona: " + persona.getId(), e);
        }
    }
}