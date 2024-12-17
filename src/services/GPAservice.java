package services;

import java.util.List;

import model.ClassInfo;

public class GPAservice {
    
    public double convertGradeToGPA(String grade){
        switch(grade) {
            case "A" : return 4.0;
            case "B" : return 3.0;
            case "C" : return 2.0;
            case "D" : return 1.0;
            case "F" : return 0.0;
            default: return -1.0; // invalid grade
        }
}

    public double calculateGPA(List<ClassInfo> ClassInfo) {
        double totalGPA = 0.0;
        double totalClasses = 0.0;

        for (ClassInfo c : ClassInfo) {
            String grade = c.getGrade();
            double valueGrade = convertGradeToGPA(grade);
            
            if (valueGrade >= 0) {
                totalGPA += valueGrade;
                totalClasses ++;
            }
        }
        if (totalClasses == 0) {
            throw new IllegalArgumentException("No valid classes provided.");
    }
    return totalGPA / totalClasses;
    }

    public String calculateAverageLetterGrade(List<ClassInfo> ClassInfo) {
        double roundedGPA = Math.round(calculateGPA(ClassInfo));
        switch ((int) roundedGPA) {
            case 4: return "A";
            case 3: return "B";
            case 2: return "C";
            case 1: return "D";
            case 0: return "F";
            default: return "Invalid"; // invalid grade
        }
    }
}







