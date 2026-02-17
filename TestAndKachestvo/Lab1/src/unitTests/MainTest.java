package unitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.math.app.Main;

/**
 * Тесты для демонстрационного класса Main.
 * Проверяют отсутствие критических ошибок при запуске.
 */
class MainTest {

    @Test
    @DisplayName("Позитивный: Запуск метода main без ошибок")
    void testMainMethodExecution() {
        try {
            Main.main(new String[] {});
        } catch (Exception e) {
            throw new RuntimeException("Метод main выбросил исключение: " + e.getMessage());
        }
    }
}
