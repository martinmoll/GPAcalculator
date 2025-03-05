package ui;

import model.ClassInfo;
import services.GPAservice;
import db.DatabaseService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GPAapp {

    private JFrame frame;
    private JPanel classPanel;
    private JButton calculateButton, addClassButton, removeClassButton;
    private JPanel outputPanel;
    private JLabel averageGradeLabel;
    private JLabel gpaLabel;
    private List<ClassInfo> classes;
    private GPAservice gpaService;
    private DatabaseService dbService;
    private String studentID;

    public GPAapp() {
        gpaService = new GPAservice();
        dbService = new DatabaseService();
        dbService.initDatabase(); // Initialize the database

        // Show the login screen first
        showLoginScreen();
    }

    private void showLoginScreen() {
        JFrame loginFrame = new JFrame("Student Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 200);
        loginFrame.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel idLabel = new JLabel("Student ID:");
        JTextField idField = new JTextField();
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JButton loginButton = new JButton("Login");

        loginFrame.add(idLabel);
        loginFrame.add(idField);
        loginFrame.add(nameLabel);
        loginFrame.add(nameField);
        loginFrame.add(new JLabel()); // Spacer
        loginFrame.add(loginButton);

        loginButton.addActionListener(e -> {
            String enteredID = idField.getText().trim();
            String enteredName = nameField.getText().trim();
            if (enteredID.isEmpty() || enteredName.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Please enter both Student ID and Name.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                studentID = enteredID;
                if (!dbService.studentExists(studentID)) {
                    dbService.saveStudent(studentID, enteredName);
                }
                loginFrame.dispose();
                initializeMainApp();
            }
        });

        loginFrame.setVisible(true);
    }

    private void initializeMainApp() {
        // Frame
        frame = new JFrame("GPA Calculator - Student ID: " + studentID);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Class panel
        classPanel = new JPanel();
        classPanel.setLayout(new GridLayout(0, 3, 10, 10));
        classes = new ArrayList<>();

        // Load existing grades or add default classes
        List<ClassInfo> savedClasses = dbService.loadGrades(studentID);
        if (savedClasses.isEmpty()) {
            addInitialClasses();
        } else {
            classes = savedClasses;
            refreshClassPanel();
        }

        // Buttons
        calculateButton = new JButton("CALCULATE GPA");
        addClassButton = new JButton("ADD CLASS");
        removeClassButton = new JButton("REMOVE CLASS");

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 20));
        buttonPanel.add(addClassButton);
        buttonPanel.add(removeClassButton);
        buttonPanel.add(calculateButton);

        // Output
        outputPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        averageGradeLabel = new JLabel("Average Grade: ");
        gpaLabel = new JLabel("GPA: ");
        outputPanel.add(averageGradeLabel);
        outputPanel.add(gpaLabel);

        // Button functionality
        calculateButton.addActionListener(this::handleCalculateGPA);
        addClassButton.addActionListener(this::handleAddClass);
        removeClassButton.addActionListener(this::handleRemoveClass);

        // Add components to the frame
        frame.add(new JScrollPane(classPanel), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(outputPanel, BorderLayout.EAST);

        frame.setVisible(true);
    }

    private void addInitialClasses() {
        classes.add(new ClassInfo("MAT105"));
        classes.add(new ClassInfo("ECON116"));
        classes.add(new ClassInfo("INF100"));
        classes.add(new ClassInfo("MNF130"));
        classes.add(new ClassInfo("INF101"));
        classes.add(new ClassInfo("ECON110"));
        classes.add(new ClassInfo("INF102"));
        classes.add(new ClassInfo("ECON210"));
        classes.add(new ClassInfo("STAT110"));

        refreshClassPanel();
    }

    private void handleCalculateGPA(ActionEvent e) {
        try {
            String averageGrade = gpaService.calculateAverageLetterGrade(classes);
            averageGradeLabel.setText("Average Grade: " + averageGrade);
            double gpa = gpaService.calculateGPA(classes);
            gpaLabel.setText("GPA: " + String.format("%.2f", gpa));

            // Save grades to the database
            dbService.saveGrades(studentID, classes);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid grade input.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddClass(ActionEvent e) {
        String newClassName = JOptionPane.showInputDialog(frame, "Enter class name:", "Add Class", JOptionPane.PLAIN_MESSAGE);
        if (newClassName != null && !newClassName.trim().isEmpty()) {
            ClassInfo newClass = new ClassInfo(newClassName.trim());
            classes.add(newClass);
            classPanel.add(new JLabel(newClass.getID()));
            classPanel.add(newClass.getClassField());
            classPanel.add(new JLabel()); // Spacer
            classPanel.revalidate();
            classPanel.repaint();
        }
    }

    private void handleRemoveClass(ActionEvent e) {
        String classNameToRemove = JOptionPane.showInputDialog(frame, "Enter class name to remove:", "Remove Class", JOptionPane.PLAIN_MESSAGE);
        if (classNameToRemove != null && !classNameToRemove.trim().isEmpty()) {
            ClassInfo classToRemove = null;
            for (ClassInfo c : classes) {
                if (c.getID().equalsIgnoreCase(classNameToRemove.trim())) {
                    classToRemove = c;
                    break;
                }
            }
            if (classToRemove != null) {
                classes.remove(classToRemove);
                refreshClassPanel();
            } else {
                JOptionPane.showMessageDialog(frame, "Class not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshClassPanel() {
        classPanel.removeAll();
        for (ClassInfo c : classes) {
            classPanel.add(new JLabel(c.getID()));
            classPanel.add(c.getClassField());
            classPanel.add(new JLabel()); // Spacer
        }
        classPanel.revalidate();
        classPanel.repaint();
    }
}