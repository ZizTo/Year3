package by.fiftenth.abitur;

import java.util.ArrayList;
import java.util.List;

public class Abiturient {
    private String name;
    private List<Integer> grades;

    public Abiturient(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        this.name = name;
        this.grades = new ArrayList<>();
    }

    public void addGrade(int grade) {
        if (grade < 1 || grade > 10) {
            throw new IllegalArgumentException("Оценка должна быть от 1 до 10");
        }
        this.grades.add(grade);
    }

    public int getTotalScore() {
        return grades.stream().mapToInt(Integer::intValue).sum();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Абитуриент: " + name + " | Оценки: " + grades + " | Сумма баллов: " + getTotalScore();
    }
}
