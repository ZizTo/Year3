package ru.math.app;

import ru.math.complex.Complex;

import java.util.Arrays;

/**
 * Демонстрационное приложение для класса Complex.
 *
 * @author Матиевский Павел
 * @version 1.0
 */
public class Main {

    /**
     * Точка входа в программу.
     * @param args Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        try {
            System.out.println("=== Демонстрация класса Complex ===\n");

            // 1. Создание объектов и арифметика
            Complex c1 = new Complex(2.0, 5.0);
            Complex c2 = new Complex("1.0 -3.0"); // Инициализация из строки

            System.out.println("Число 1: " + c1);
            System.out.println("Число 2: " + c2);

            System.out.println("Сложение: " + c1.add(c2));
            System.out.println("Вычитание: " + c1.subtract(c2));
            System.out.println("Умножение: " + c1.multiply(c2));

            // 2. Использование индексатора
            System.out.println("\n--- Индексатор ---");
            System.out.println("c1[0] (Real): " + c1.getComponent(0));
            System.out.println("c1[1] (Imag): " + c1.getComponent(1));

            // 3. Итерация (Iterable)
            System.out.println("\n--- Итерация по полям c1 ---");
            for (Double val : c1) {
                System.out.println("Компонент: " + val);
            }

            // 4. Сортировка массива
            System.out.println("\n--- Сортировка ---");
            Complex[] arr = {
                new Complex(10, 1),
                new Complex(1, 10),
                new Complex(5, 5),
                new Complex(2, 2)
            };

            System.out.println("Исходный массив:");
            printArray(arr);

            // Сортировка по Comparable (по модулю)
            Arrays.sort(arr);
            System.out.println("Отсортировано по модулю (Comparable):");
            printArray(arr);

            // Сортировка по Real (через Comparator)
            Arrays.sort(arr, Complex.getComparator(Complex.SortField.REAL));
            System.out.println("Отсортировано по Действительной части (Comparator):");
            printArray(arr);

            // Сортировка по Imaginary (через Comparator)
            Arrays.sort(arr, Complex.getComparator(Complex.SortField.IMAGINARY));
            System.out.println("Отсортировано по Мнимой части (Comparator):");
            printArray(arr);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Вывод массива на экран.
     * @param arr Массив чисел.
     */
    private static void printArray(Complex[] arr) {
        for (Complex c : arr) {
            System.out.print("[" + c + "] ");
        }
        System.out.println();
    }
}
