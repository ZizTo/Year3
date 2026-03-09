package by.fiftenth.app;

import java.util.ArrayList;
import java.util.List;

import by.fiftenth.abitur.Abiturient;
import by.fiftenth.abitur.AdmissionSystem;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Система зачисления абитуриентов ===\n");

        Abiturient a1 = new Abiturient("Иванов Иван");
        a1.addGrade(10);
        a1.addGrade(9);
        a1.addGrade(8);

        Abiturient a2 = new Abiturient("Петров Петр");
        a2.addGrade(5);
        a2.addGrade(6);
        a2.addGrade(5);

        Abiturient a3 = new Abiturient("Сидорова Анна");
        a3.addGrade(10);
        a3.addGrade(10);
        a3.addGrade(10);

        Abiturient a4 = new Abiturient("Смирнов Алексей");
        a4.addGrade(8);
        a4.addGrade(7);
        a4.addGrade(8);

        List<Abiturient> applicants = new ArrayList<>();
        applicants.add(a1);
        applicants.add(a2);
        applicants.add(a3);
        applicants.add(a4);

        System.out.println("Список всех подавших документы:");
        applicants.forEach(System.out::println);
        System.out.println();

        AdmissionSystem system = new AdmissionSystem();
        int availablePlaces = 2;

        System.out.println("Доступно бюджетных мест: " + availablePlaces);
        System.out.println("------------------------------------------------");

        try {
            List<Abiturient> admitted = system.getAdmittedList(applicants, availablePlaces);

            System.out.println("СПИСОК ПОСТУПИВШИХ:");
            for (int i = 0; i < admitted.size(); i++) {
                System.out.println((i + 1) + ". " + admitted.get(i).toString());
            }

            System.out.println("\n--- Демонстрация работы исключений ---");
            system.getAdmittedList(applicants, -5);

        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка при зачислении: " + e.getMessage());
        }
    }
}
