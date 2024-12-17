package model;

import javax.swing.*;

public class ClassInfo {
    private String classID;
    private JTextField classField;
    
    public ClassInfo(String classID){
        this.classID = classID;
        this.classField = new JTextField(3);
    } 

    public String getID(){  
        return classID;
    }

    public JTextField getClassField(){
        return classField;
    }

    public String getGrade(){
        return classField.getText().trim().toUpperCase();
    }

}
