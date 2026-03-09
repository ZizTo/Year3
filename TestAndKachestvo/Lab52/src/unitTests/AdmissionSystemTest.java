package unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import by.fiftenth.abitur.Abiturient;
import by.fiftenth.abitur.AdmissionSystem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class AdmissionSystemTest {

    private AdmissionSystem system;

    @BeforeEach
    public void setUp() {
        system = new AdmissionSystem();
    }

    @Test
    public void testAbiturientTotalScore() {
        Abiturient a = new Abiturient("Иванов");
        a.addGrade(10);
        a.addGrade(8);
        a.addGrade(9);
        assertEquals(27, a.getTotalScore(), "Сумма баллов должна быть 27");
    }

    @Test
    public void testNegativePlacesThrowException() {
        List<Abiturient> list = Arrays.asList(new Abiturient("Иванов"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            system.getAdmittedList(list, -1);
        });

        assertTrue(exception.getMessage().contains("отрицательным"));
    }

    @Test
    public void testAdmittedListWhenPlacesLessThanApplicants() {
        Abiturient a1 = new Abiturient("Слабый");
        a1.addGrade(3);
        a1.addGrade(3);
        Abiturient a2 = new Abiturient("Сильный");
        a2.addGrade(7);
        a2.addGrade(7);
        Abiturient a3 = new Abiturient("Средний");
        a3.addGrade(5);
        a3.addGrade(5);

        List<Abiturient> applicants = Arrays.asList(a1, a2, a3);

        List<Abiturient> admitted = system.getAdmittedList(applicants, 2);

        assertEquals(2, admitted.size());
        assertEquals("Сильный", admitted.get(0).getName());
        assertEquals("Средний", admitted.get(1).getName());
    }

    @Test
    public void testAdmittedListWhenPlacesMoreThanApplicants() {
        Abiturient a1 = new Abiturient("Один");
        a1.addGrade(5);
        Abiturient a2 = new Abiturient("Два");
        a2.addGrade(4);

        List<Abiturient> applicants = Arrays.asList(a1, a2);

        List<Abiturient> admitted = system.getAdmittedList(applicants, 5);

        assertEquals(2, admitted.size(), "Должны поступить все двое");
    }
}
