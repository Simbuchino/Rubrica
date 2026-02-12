package org.rubrica.persistence;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.rubrica.model.Utente;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UtenteRepository {

    private static final String FILE_UTENTI = "./utenti.txt";
    private static final String SEPARATORE = ";";
    private final Path filePath = Paths.get(FILE_UTENTI);

    public List<Utente> caricaUtenti() {
        List<Utente> utenti = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return utenti;
        }

        try (Scanner scanner = new Scanner(filePath, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                String riga = scanner.nextLine();
                String[] dati = riga.split(SEPARATORE);
                if (dati.length == 3) {  // ← Ora sono 3 campi!
                    utenti.add(new Utente(dati[0], dati[1], dati[2]));
                }
            }
        } catch (IOException e) {
            System.err.println("Errore caricamento utenti: " + e.getMessage());
        }
        return utenti;
    }

    public void salvaUtente(Utente utente) {
        List<Utente> utenti = caricaUtenti();

        utenti.removeIf(u -> u.getUsername().equals(utente.getUsername()));
        utenti.add(utente);

        try (PrintStream ps = new PrintStream(filePath.toFile(), StandardCharsets.UTF_8)) {
            for (Utente u : utenti) {
                ps.println(u.getUsername() + SEPARATORE +
                        u.getPasswordHash() + SEPARATORE +
                        u.getEncryptionSalt());  // ← Terzo campo
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore salvataggio utente", e);
        }
    }

    // Nuovo metodo per ottenere salt di un utente
    public String getEncryptionSalt(String username) {
        List<Utente> utenti = caricaUtenti();
        for (Utente u : utenti) {
            if (u.getUsername().equals(username)) {
                return u.getEncryptionSalt();
            }
        }
        return null;
    }

    public boolean verificaCredenziali(String username, String password) {
        List<Utente> utenti = caricaUtenti();
        for (Utente u : utenti) {
            if(!u.getUsername().equals(username)) continue;
            // Verifica password con bcrypt
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), u.getPasswordHash());
            return result.verified;
        }
        return false;
    }

    public String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public boolean usernameEsiste(String username) {
        List<Utente> utenti = caricaUtenti();
        return utenti.stream().anyMatch(u -> u.getUsername().equals(username));
    }
}