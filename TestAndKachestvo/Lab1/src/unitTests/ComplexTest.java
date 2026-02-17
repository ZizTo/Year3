package unitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Arrays;

import ru.math.complex.Complex;

/**
 * Модульные тесты для класса Complex.
 * Проверяют конструкторы, арифметику, индексатор, итератор и сортировку.
 *
 * @author Матиевский Павел
 * @version 1.0
 */
public class ComplexTest {

    private static final double DELTA = 0.000001; // Погрешность для сравнения double

    // --- КОНСТРУКТОРЫ ---

    @Test
    @DisplayName("Позитивный: Конструктор по умолчанию")
    void testDefaultConstructor() {
        Complex z = new Complex();
        assertEquals(0.0, z.getReal(), DELTA);
        assertEquals(0.0, z.getImaginary(), DELTA);
    }

    @Test
    @DisplayName("Позитивный: Конструктор с параметрами")
    void testParameterizedConstructor() {
        Complex z = new Complex(1.5, -2.5);
        assertEquals(1.5, z.getReal(), DELTA);
        assertEquals(-2.5, z.getImaginary(), DELTA);
    }

    @Test
    @DisplayName("Позитивный: Конструктор из строки")
    void testStringConstructor() {
        Complex z = new Complex("3.5 4.5");
        assertEquals(3.5, z.getReal(), DELTA);
        assertEquals(4.5, z.getImaginary(), DELTA);
    }

    @Test
    @DisplayName("Негативный: Конструктор из строки (Null)")
    void testStringConstructorNull() {
        assertThrows(NullPointerException.class, () -> {
            new Complex(null);
        });
    }

    @Test
    @DisplayName("Негативный: Конструктор из строки (Неверный формат чисел)")
    void testStringConstructorInvalidFormat() {
        assertThrows(NumberFormatException.class, () -> {
            new Complex("abc def");
        });
    }

    @Test
    @DisplayName("Негативный: Конструктор из строки (Недостаточно данных)")
    void testStringConstructorNotEnoughTokens() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Complex("1.0");
        });
    }

    // --- АРИФМЕТИКА ---

    @Test
    @DisplayName("Позитивный: Сложение")
    void testAdd() {
        Complex a = new Complex(1.0, 2.0);
        Complex b = new Complex(3.0, 4.0);
        Complex res = a.add(b);
        assertEquals(4.0, res.getReal(), DELTA);
        assertEquals(6.0, res.getImaginary(), DELTA);
    }

    @Test
    @DisplayName("Позитивный: Вычитание")
    void testSubtract() {
        Complex a = new Complex(5.0, 5.0);
        Complex b = new Complex(2.0, 3.0);
        Complex res = a.subtract(b);
        assertEquals(3.0, res.getReal(), DELTA);
        assertEquals(2.0, res.getImaginary(), DELTA);
    }

    @Test
    @DisplayName("Позитивный: Умножение")
    void testMultiply() {
        Complex a = new Complex(1.0, 2.0);
        Complex b = new Complex(3.0, 4.0);
        Complex res = a.multiply(b);
        assertEquals(-5.0, res.getReal(), DELTA);
        assertEquals(10.0, res.getImaginary(), DELTA);
    }

    @Test
    @DisplayName("Негативный: Арифметика с null")
    void testArithmeticNull() {
        Complex a = new Complex(1, 1);
        assertThrows(NullPointerException.class, () -> a.add(null));
        assertThrows(NullPointerException.class, () -> a.subtract(null));
        assertThrows(NullPointerException.class, () -> a.multiply(null));
    }

    // --- ИНДЕКСАТОР ---

    @Test
    @DisplayName("Позитивный: Получение компонентов по индексу")
    void testGetComponent() {
        Complex z = new Complex(10.0, 20.0);
        assertEquals(10.0, z.getComponent(0), DELTA);
        assertEquals(20.0, z.getComponent(1), DELTA);
    }

    @Test
    @DisplayName("Негативный: Неверный индекс")
    void testGetComponentInvalidIndex() {
        Complex z = new Complex(1, 1);
        assertThrows(IndexOutOfBoundsException.class, () -> z.getComponent(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> z.getComponent(2));
    }

    // --- TO STRING ---

    @Test
    @DisplayName("Позитивный: toString")
    void testToString() {
        Complex z = new Complex(1.1, 2.2);
        assertEquals("1,1 + 2,2i", z.toString());
    }

    // --- COMPARABLE ---

    @Test
    @DisplayName("Позитивный: Comparable (compareTo)")
    void testCompareTo() {
        Complex small = new Complex(1, 1);
        Complex big = new Complex(10, 10);

        assertTrue(small.compareTo(big) < 0, "Small должен быть меньше Big");
        assertTrue(big.compareTo(small) > 0, "Big должен быть больше Small");
        assertEquals(0, small.compareTo(new Complex(1, 1)), "Равные объекты должны возвращать 0");
    }

    // --- ITERABLE ---

    @Test
    @DisplayName("Позитивный: Iterator")
    void testIterator() {
        Complex z = new Complex(5.0, 6.0);
        Iterator<Double> it = z.iterator();

        assertTrue(it.hasNext());
        assertEquals(5.0, it.next(), DELTA);

        assertTrue(it.hasNext());
        assertEquals(6.0, it.next(), DELTA);

        assertFalse(it.hasNext());
    }

    @Test
    @DisplayName("Негативный: Iterator (выход за границы)")
    void testIteratorOutOfBounds() {
        Complex z = new Complex(1, 1);
        Iterator<Double> it = z.iterator();
        it.next();
        it.next();

        assertThrows(NoSuchElementException.class, () -> {
            it.next();
        });
    }

    // --- COMPARATOR ---

    @Test
    @DisplayName("Позитивный: Сортировка по Real")
    void testComparatorReal() {
        Complex[] arr = {
                new Complex(10, 1),
                new Complex(1, 10)
        };

        Arrays.sort(arr, Complex.getComparator(Complex.SortField.REAL));

        assertEquals(1.0, arr[0].getReal(), DELTA);
        assertEquals(10.0, arr[1].getReal(), DELTA);
    }

    @Test
    @DisplayName("Позитивный: Сортировка по Imaginary")
    void testComparatorImaginary() {
        Complex[] arr = {
                new Complex(1, 10),
                new Complex(10, 1)
        };

        Arrays.sort(arr, Complex.getComparator(Complex.SortField.IMAGINARY));

        assertEquals(1.0, arr[0].getImaginary(), DELTA);
        assertEquals(10.0, arr[1].getImaginary(), DELTA);
    }
}
