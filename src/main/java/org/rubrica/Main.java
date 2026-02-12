package org.rubrica;

import org.rubrica.persistence.UtenteRepository;
import org.rubrica.util.CryptoUtil;
import org.rubrica.view.LoginWindow;
import org.rubrica.view.MainWindow;

import javax.crypto.SecretKey;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            LoginWindow loginWindow = new LoginWindow(null);
            loginWindow.setVisible(true);

            String username = loginWindow.getUsernameAutenticato();

            if (username == null) {
                // Login fallito o finestra chiusa - esci dall'app
                System.exit(0);
                return;
            }

            UtenteRepository utenteRepository = new UtenteRepository();
            String encryptionSalt = utenteRepository.getEncryptionSalt(username);

            if (encryptionSalt == null) {
                JOptionPane.showMessageDialog(null,
                        "Errore: impossibile recuperare i dati di cifratura dell'utente.",
                        "Errore critico",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
                return;
            }

            String password = loginWindow.getPasswordInserita();

            if (password == null || password.isEmpty()) {
                // Password non inserita - esci
                System.exit(0);
                return;
            }

            try {
                SecretKey encryptionKey = CryptoUtil.deriveKey(password, encryptionSalt);

                MainWindow window = new MainWindow(username, encryptionKey);
                window.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Errore durante la derivazione della chiave di cifratura: " + e.getMessage(),
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}