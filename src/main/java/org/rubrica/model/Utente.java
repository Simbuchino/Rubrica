package org.rubrica.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Utente {
    private String username;
    private String passwordHash;
    private String encryptionSalt;
}
