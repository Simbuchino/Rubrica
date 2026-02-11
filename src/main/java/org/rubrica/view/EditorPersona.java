package org.rubrica.view;

import org.rubrica.model.Persona;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class EditorPersona extends JDialog {

    private final JTextField nome = new JTextField();
    private final JTextField cognome = new JTextField();
    private final JTextField indirizzo = new JTextField();
    private final JTextField telefono = new JTextField();
    private final JTextField eta = new JTextField();
    private UUID idPersona = null;

    private final JButton btnSalva = new JButton("Salva");
    private final JButton btnAnnulla = new JButton("Annulla");

    private final PersonaEditorListener personaEditorListener;

    public EditorPersona(JFrame parent, PersonaEditorListener personaEditorListener) {
        super(parent, "Editor Persona", true);
        this.personaEditorListener = personaEditorListener;
        initComponents(parent);
    }

    public EditorPersona(JFrame parent, PersonaEditorListener personaEditorListener, Persona persona) {
        this(parent, personaEditorListener);
        precompila(persona);
    }

    private void initComponents(JFrame parent) {
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fieldsPanel.add(new JLabel("Nome:"));
        fieldsPanel.add(nome);
        fieldsPanel.add(new JLabel("Cognome:"));
        fieldsPanel.add(cognome);
        fieldsPanel.add(new JLabel("Indirizzo:"));
        fieldsPanel.add(indirizzo);
        fieldsPanel.add(new JLabel("Telefono:"));
        fieldsPanel.add(telefono);
        fieldsPanel.add(new JLabel("Età:"));
        fieldsPanel.add(eta);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAnnulla);
        buttonPanel.add(btnSalva);

        this.add(fieldsPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.setSize(400, 300);
        this.setLocationRelativeTo(parent);

        btnSalva.addActionListener(e -> onSalva());
        btnAnnulla.addActionListener(e -> dispose());
    }

    private void precompila(Persona persona) {
        this.idPersona = persona.getId();
        nome.setText(persona.getNome());
        cognome.setText(persona.getCognome());
        indirizzo.setText(persona.getIndirizzo());
        telefono.setText(persona.getTelefono());
        eta.setText(String.valueOf(persona.getEta()));
    }

    private void onSalva() {
        String errore = validaInput();
        if (errore != null) {
            JOptionPane.showMessageDialog(this, errore, "Dati non validi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Persona p = new Persona(
                nome.getText().trim(),
                cognome.getText().trim(),
                indirizzo.getText().trim(),
                telefono.getText().trim(),
                Integer.parseInt(eta.getText().trim())
        );

        if (this.idPersona != null) {
            p.setId(this.idPersona);
        }

        personaEditorListener.onPersonaSaved(p);
        dispose();
    }

    private String validaInput() {
        if (nome.getText().trim().isEmpty()) return "Il nome è obbligatorio.";
        if (cognome.getText().trim().isEmpty()) return "Il cognome è obbligatorio.";
        if (telefono.getText().trim().isEmpty()) return "Il telefono è obbligatorio.";
        if (eta.getText().trim().isEmpty()) return "L'età è obbligatoria.";

        try {
            int etaVal = Integer.parseInt(eta.getText().trim());
            if (etaVal < 0 || etaVal > 101) return "L'età deve essere compresa tra 0 e 100.";
        } catch (NumberFormatException e) {
            return "L'età deve essere un numero intero positivo.";
        }

        return null;
    }
}