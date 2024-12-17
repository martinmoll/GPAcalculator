package ui;

import model.ClassInfo;
import services.GPAservice;

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

    public GPAapp() {
        gpaService = new GPAservice();

        // Frame
        frame = new JFrame("GPA Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Class panel
        classPanel = new JPanel();
        classPanel.setLayout(new GridLayout(0, 3, 10, 10));
        classes = new ArrayList<>();
        addInitialClasses();

        //Buttons
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

        for (ClassInfo ClassInfo : classes) {
            classPanel.add(new JLabel(ClassInfo.getID()));
            classPanel.add(ClassInfo.getClassField());
            classPanel.add(new JLabel());
        }
    }

    private void handleCalculateGPA(ActionEvent e) {
        try {
            String averageGrade = gpaService.calculateAverageLetterGrade(classes);
            averageGradeLabel.setText("Average Grade: " + averageGrade);
            double gpa = gpaService.calculateGPA(classes);
            gpaLabel.setText("GPA: " + String.format("%.2f", gpa));
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
