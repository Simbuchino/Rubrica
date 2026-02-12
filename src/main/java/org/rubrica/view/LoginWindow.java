package org.rubrica.view;

import lombok.Getter;
import org.rubrica.persistence.UtenteRepository;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JDialog {

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton btnLogin = new JButton("Login");
    private final JButton btnRegistra = new JButton("Registra nuovo utente");

    private final UtenteRepository utenteRepository;
    @Getter
    private String usernameAutenticato = null;
    @Getter
    private String passwordInserita = null;

    public LoginWindow(JFrame parent) {
        super(parent, "Login - Rubrica Telefonica", true);
        this.utenteRepository = new UtenteRepository();
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        fieldsPanel.add(new JLabel("Username:"));
        fieldsPanel.add(usernameField);
        fieldsPanel.add(new JLabel("Password:"));
        fieldsPanel.add(passwordField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnRegistra);
        buttonPanel.add(btnLogin);

        this.add(fieldsPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> onLogin());
        btnRegistra.addActionListener(e -> onRegistra());

        // Enter sul password field = login
        passwordField.addActionListener(e -> onLogin());

        getRootPane().setDefaultButton(btnLogin);  // Enter da qualsiasi campo = login
    }

    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username e password sono obbligatori.",
                    "Campi vuoti",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (utenteRepository.verificaCredenziali(username, password)) {
            usernameAutenticato = username;
            passwordInserita = password;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Credenziali non valide.",
                    "Login fallito",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");  // Pulisci password
            passwordField.requestFocus();
        }
    }

    private void onRegistra() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username e password sono obbligatori.",
                    "Campi vuoti",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.contains(";")) {
            JOptionPane.showMessageDialog(this,
                    "L'username non può contenere il carattere ';'.",
                    "Username non valido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (utenteRepository.usernameEsiste(username)) {
            JOptionPane.showMessageDialog(this,
                    "Username già esistente. Sceglierne un altro.",
                    "Username duplicato",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String passwordHash = utenteRepository.hashPassword(password);
        String encryptionSalt = org.rubrica.util.CryptoUtil.generateRandomSalt();  // ← Genera salt

        utenteRepository.salvaUtente(
                new org.rubrica.model.Utente(username, passwordHash, encryptionSalt)
        );

        JOptionPane.showMessageDialog(this,
                "Utente registrato con successo!",
                "Registrazione completata",
                JOptionPane.INFORMATION_MESSAGE);

        //usernameField.setText("");
        //passwordField.setText("");
        usernameAutenticato = username;
        passwordInserita = password;
        //usernameField.requestFocus();
        dispose();
    }
}