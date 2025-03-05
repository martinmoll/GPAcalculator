# GPA calculator 4.0 scale

### Functionality

At first one should add Student id and name which will be stored in the gpa_database.db database using SQLite - storing the information in a "Students" table and in a "Grades" table

The calculator allows for adding and removing classes by specifying the class id of the class to be added, or specifying the name for the class to be removed(not case sensitive).

Upon clicking the `calculate GPA` button, both GPA and average grade(letter form) will be dislayed. Where the average letter grade rounds the gpa to the closest whole number, and thereafter converts it to a letter grade. Naturally, this is more accurate when the GPA is closer to a whole number, and will paint a less accurate pictures closer to .5 values --> for example: GPA: 2.5 will provide an average letter grade of B due to rounding. 

### Visuals

**Entering id and name:**



**Grades, classes and calculation of gpa:**

