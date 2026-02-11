package org.rubrica.view;

import org.rubrica.controller.RubricaController;
import org.rubrica.model.Persona;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainWindow extends JFrame {

    private final DefaultTableModel tableModel;
    private final JTable jTable;
    private final JButton btnNuovo = new JButton("Nuovo");
    private final JButton btnModifica = new JButton("Modifica");
    private final JButton btnElimina = new JButton("Elimina");
    private final RubricaController rubricaController;

    public MainWindow() {
        this.rubricaController = new RubricaController();

        setTitle("Rubrica Telefonica v1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("Nome");
        tableModel.addColumn("Cognome");
        tableModel.addColumn("Telefono");

        jTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(jTable);
        this.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnNuovo);
        btnPanel.add(btnModifica);
        btnPanel.add(btnElimina);
        this.add(btnPanel, BorderLayout.SOUTH);

        btnNuovo.addActionListener(e -> onNuovo());
        btnModifica.addActionListener(e -> onModifica());
        btnElimina.addActionListener(e -> onElimina());

        aggiornaTabella();
    }

    private void onNuovo() {
        EditorPersona editor = new EditorPersona(this, new PersonaEditorListener() {
            @Override
            public void onPersonaSaved(Persona persona) {
                try {
                    rubricaController.aggiungiPersona(persona);
                    aggiornaTabella();
                } catch (Exception ex) {
                    mostraErrore("Errore durante il salvataggio: " + ex.getMessage());
                }
            }

        });
        editor.setVisible(true);
    }

    private void onModifica() {
        int selectedRow = jTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selezionare una persona da modificare.",
                    "Nessuna selezione",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        EditorPersona editor = new EditorPersona(this, new PersonaEditorListener() {
            @Override
            public void onPersonaSaved(Persona persona) {
                try {
                    rubricaController.modificaPersona(selectedRow, persona);
                    aggiornaTabella();
                } catch (Exception ex) {
                    mostraErrore("Errore durante la modifica: " + ex.getMessage());
                }
            }

        }, rubricaController.getPersone().get(selectedRow));
        editor.setVisible(true);
    }

    private void onElimina() {
        int selectedRow = jTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selezionare una persona da eliminare.",
                    "Nessuna selezione",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Persona persona = rubricaController.getPersone().get(selectedRow);
        int conferma = JOptionPane.showConfirmDialog(this,
                "Eliminare la persona " + persona.getNome() + " " + persona.getCognome() + "?",
                "Conferma eliminazione",
                JOptionPane.YES_NO_OPTION);

        if (conferma == JOptionPane.YES_OPTION) {
            try {
                rubricaController.eliminaPersona(selectedRow);
                aggiornaTabella();
            } catch (Exception ex) {
                mostraErrore("Errore durante l'eliminazione: " + ex.getMessage());
            }
        }
    }

    private void aggiornaTabella() {
        tableModel.setRowCount(0);
        for (Persona p : rubricaController.getPersone()) {
            tableModel.addRow(new Object[]{
                    p.getNome(),
                    p.getCognome(),
                    p.getTelefono()
            });
        }
    }

    private void mostraErrore(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio, "Errore", JOptionPane.ERROR_MESSAGE);
    }
}