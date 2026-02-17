package ru.math.complex;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Класс, представляющий комплексное число вида z = a + bi.
 * Поддерживает основные арифметические операции, сравнение и итерацию по полям.
 *
 * @author Матиевский Павел
 * @version 1.0
 */
public class Complex implements Comparable<Complex>, Iterable<Double> {

    /** Действительная часть комплексного числа. */
    private double real;

    /** Мнимая часть комплексного числа. */
    private double imaginary;

    /**
     * Конструктор по умолчанию. Инициализирует число нулем (0 + 0i).
     */
    public Complex() {
        this.real = 0.0;
        this.imaginary = 0.0;
    }

    /**
     * Конструктор с параметрами.
     *
     * @param real Действительная часть.
     * @param imaginary Мнимая часть.
     */
    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    /**
     * Конструктор, создающий объект из строкового представления.
     * Ожидает формат, совместимый с методом {@link #toString()} (например, "3.5 4.2").
     *
     * @param source Строка, содержащая два числа, разделенных пробелом.
     * @throws NumberFormatException Если строка не содержит валидных чисел.
     * @throws IllegalArgumentException Если формат строки неверен.
     */
    public Complex(String source) {
        StringTokenizer st = new StringTokenizer(source, " ");
        if (st.countTokens() < 2) {
            throw new IllegalArgumentException("Строка должна содержать два числа, разделенных пробелом");
        }
        this.real = Double.parseDouble(st.nextToken());
        this.imaginary = Double.parseDouble(st.nextToken());
    }

    // --- Арифметические методы ---

    /**
     * Сложение комплексных чисел.
     * Алгоритм: (a + bi) + (c + di) = (a+c) + (b+d)i.
     *
     * @param other Другое комплексное число.
     * @return Новый объект Complex, являющийся суммой.
     */
    public Complex add(Complex other) {
        return new Complex(this.real + other.real, this.imaginary + other.imaginary);
    }

    /**
     * Вычитание комплексных чисел.
     * Алгоритм: (a + bi) - (c + di) = (a-c) + (b-d)i.
     *
     * @param other Вычитаемое комплексное число.
     * @return Новый объект Complex, являющийся разностью.
     */
    public Complex subtract(Complex other) {
        return new Complex(this.real - other.real, this.imaginary - other.imaginary);
    }

    /**
     * Умножение комплексных чисел.
     * Алгоритм: (a + bi) * (c + di) = (ac - bd) + (ad + bc)i.
     *
     * @param other Множитель.
     * @return Новый объект Complex, являющийся произведением.
     */
    public Complex multiply(Complex other) {
        double newReal = (this.real * other.real) - (this.imaginary * other.imaginary);
        double newImag = (this.real * other.imaginary) + (this.imaginary * other.real);
        return new Complex(newReal, newImag);
    }

    // --- Индексатор ---

    /**
     * Индексатор для доступа к полям объекта.
     *
     * @param index Индекс поля (0 - real, 1 - imaginary).
     * @return Значение соответствующего поля.
     * @throws IndexOutOfBoundsException Если индекс выходит за пределы [0, 1].
     */
    public double getComponent(int index) {
        if (index == 0) return real;
        if (index == 1) return imaginary;
        throw new IndexOutOfBoundsException("Индекс должен быть 0 (Real) или 1 (Imaginary)");
    }

    // --- Методы Object ---

    /**
     * Возвращает строковое представление объекта.
     * Формат: "Real Imaginary" (через пробел).
     *
     * @return Строка с значениями полей.
     */
    @Override
    public String toString() {
        return String.format("%.1f %s %.1fi", 
        real, (imaginary < 0 ? "-" : "+"), Math.abs(imaginary));
    }

    // --- Интерфейсы ---

    /**
     * Сравнивает текущий объект с другим по модулю.
     * Модуль |z| = sqrt(a^2 + b^2).
     *
     * @param o Объект для сравнения.
     * @return Отрицательное число, если этот объект меньше; 0, если равны; положительное, если больше.
     */
    @Override
    public int compareTo(Complex o) {
        double modulusSelf = Math.sqrt(real * real + imaginary * imaginary);
        double modulusOther = Math.sqrt(o.real * o.real + o.imaginary * o.imaginary);
        return Double.compare(modulusSelf, modulusOther);
    }

    /**
     * Реализация Iterable. Позволяет использовать объект в цикле foreach.
     * Перебирает сначала действительную, затем мнимую часть.
     *
     * @return Итератор по компонентам числа.
     */
    @Override
    public Iterator<Double> iterator() {
        return new Iterator<Double>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < 2;
            }

            @Override
            public Double next() {
                if (currentIndex == 0) {
                    currentIndex++;
                    return real;
                } else if (currentIndex == 1) {
                    currentIndex++;
                    return imaginary;
                }
                throw new NoSuchElementException();
            }
        };
    }

    // --- Компараторы ---

    /** Перечисление полей для выбора сортировки. */
    public enum SortField { REAL, IMAGINARY }

    /**
     * Статический метод для получения компаратора по выбранному полю.
     *
     * @param field Поле, по которому нужно сравнивать (REAL или IMAGINARY).
     * @return Компаратор для класса Complex.
     */
    public static Comparator<Complex> getComparator(SortField field) {
        return (c1, c2) -> {
            switch (field) {
                case REAL:
                    return Double.compare(c1.real, c2.real);
                case IMAGINARY:
                    return Double.compare(c1.imaginary, c2.imaginary);
                default:
                    return 0;
            }
        };
    }

    // Геттеры
    public double getReal() { return real; }
    public double getImaginary() { return imaginary; }
}
