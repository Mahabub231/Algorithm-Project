/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quizzes;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SubjectSelectionUI extends JFrame {

    private JComboBox<String> subjectComboBox;
    private JButton submitButton;
    private JLabel selectedSubjectLabel;
    private Connection conn;

    public SubjectSelectionUI() {
        initComponents();
    }

    private void initComponents() {
        // Initialize components
        subjectComboBox = new JComboBox<>();
        submitButton = new JButton("Submit");
        selectedSubjectLabel = new JLabel("Selected Subject: ");
        
        // Set up the JFrame layout
        setTitle("Select Subject");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        // Load subjects into the combo box
        loadSubjects();

        // Add components to the frame
        add(new JLabel("Select a Subject:"));
        add(subjectComboBox);
        add(submitButton);
        add(selectedSubjectLabel);

        // Set up button action listener
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitSubjectSelection();
            }
        });
    }

    private void loadSubjects() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/last?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC", "root", "");
            String sql = "SELECT name FROM subjects";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            // Populate the combo box with subject names
            while (rs.next()) {
                subjectComboBox.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading subjects: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitSubjectSelection() {
        String selectedSubject = (String) subjectComboBox.getSelectedItem();
        if (selectedSubject != null) {
            selectedSubjectLabel.setText("Selected Subject: " + selectedSubject);
            JOptionPane.showMessageDialog(this, "You have selected: " + selectedSubject, "Subject Selected", JOptionPane.INFORMATION_MESSAGE);
            // You can add additional logic to load quizzes or other actions related to the subject
             if (selectedSubject.equalsIgnoreCase("CSE")) {
                new Questions();
                this.dispose(); // Close the current subject selection window
            }
        }else {
            JOptionPane.showMessageDialog(this, "Please select a subject.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Run the UI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SubjectSelectionUI().setVisible(true);
            }
        });
    }
}
