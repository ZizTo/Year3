#include <iostream>
#include <vector>
#include <cmath>
#include <cstdlib>
#include <ctime>
#include <iomanip>
#include <fstream>
using namespace std;

// Константы для задания
const int n = 1000; // Количество реализаций
const double r = 4; // Параметр отрицательного биномиального распределения (число успехов)
const double p_binom = 0.2; // Вероятность успеха для отрицательного биномиального
const double p_geom = 0.25; // Вероятность успеха для геометрического

// Функция для генерации равномерного распределения U(0, 1)
// Используется как базовый источник случайности
double generateUniform() {
    return static_cast<double>(rand()) / RAND_MAX;
}

// Моделирование отрицательного биномиального распределения
// Определение: количество неудач до r-го успеха в серии испытаний Бернулли
int generateNegativeBinomial(double r, double p) {
    int failures = 0; // Счетчик неудач
    int successes = 0; // Счетчик успехов

    // Продолжаем испытания, пока не получим r успехов
    while (successes < r) {
        double u = generateUniform(); // Генерируем БСВ
        if (u < p) { // Успех
            successes++;
        }
        else { // Неудача
            failures++;
        }
    }

    return failures; // Возвращаем количество неудач до r-го успеха
}

// Моделирование геометрического распределения
// Определение: количество неудач до первого успеха (Geometric_0(p))
int generateGeometric(double p) {
    int failures = 0; // Счетчик неудач

    // Продолжаем испытания, пока не произойдет первый успех
    while (true) {
        double u = generateUniform(); // Генерируем БСВ
        if (u < p) { // Успех — выходим из цикла
            break;
        }
        failures++; // Неудача — увеличиваем счетчик
    }

    return failures; // Возвращаем количество неудач до первого успеха
}

// Функция для вычисления несмещенных оценок математического ожидания и дисперсии
pair<double, double> calculateStats(const vector<int>& data) {
    int size = static_cast<int>(data.size());
    double mean = 0.0;
    for (int x : data) {
        mean += x;
    }
    mean /= size; // Среднее значение

    double variance = 0.0;
    for (int x : data) {
        variance += pow(x - mean, 2);
    }
    variance /= (size - 1); // Деление на (n-1) — несмещенная оценка дисперсии

    return make_pair(mean, variance);
}

// χ²-критерий Пирсона для проверки соответствия эмпирического распределения теоретическому
bool chiSquareTest(const vector<int>& data, const vector<double>& probabilities, double epsilon) {
    int bins = static_cast<int>(probabilities.size()); // Количество интервалов (значений)
    vector<int> observed(bins, 0);   // Эмпирические частоты

    // Подсчитываем, сколько раз каждое значение встретилось в выборке
    for (int x : data) {
        if (x < bins) { // Если значение попадает в диапазон [0, bins-1]
            observed[x]++;
        }
        // Значения, выходящие за пределы, игнорируются (можно обработать по-другому)
    }

    double chiSquare = 0.0;
    int totalCount = static_cast<int>(data.size());

    // Вычисляем статистику χ²
    for (int i = 0; i < bins; ++i) {
        double expected = totalCount * probabilities[i]; // Ожидаемая частота
        if (expected > 0) { // Избегаем деления на ноль
            chiSquare += pow(observed[i] - expected, 2) / expected;
        }
    }

    // Критическое значение для уровня значимости ε = 0.05 и числа степеней свободы = bins - 1
    // Для bins = 10, степеней свободы = 9, критическое значение ≈ 16.919
    double criticalValue = 16.919;

    return chiSquare <= criticalValue; // Если χ² ≤ критического — гипотеза принимается
}

// Функция для записи данных в файл
void saveDataToFile(const vector<int>& data, const string& filename) {
    ofstream file(filename);
    if (!file) {
        cerr << "Ошибка при открытии файла '" << filename << "' для записи.\n";
        return;
    }

    for (int x : data) {
        file << x << "\n"; // Записываем каждое значение на новую строку
    }

    file.close();
    cout << "Данные успешно сохранены в файл: " << filename << "\n";
}

int main() {
    setlocale(LC_ALL, "ru"); // Установка локали для корректного вывода русских символов
    srand(static_cast<unsigned int>(time(0))); // Инициализация генератора случайных чисел

    // Генерация выборок
    vector<int> negativeBinomialData(n); // Выборка из отрицательного биномиального распределения
    vector<int> geometricData(n);         // Выборка из геометрического распределения

    cout << "Генерация данных...\n";

    for (int i = 0; i < n; ++i) {
        negativeBinomialData[i] = generateNegativeBinomial(r, p_binom);
        geometricData[i] = generateGeometric(p_geom);
    }

    // Сохранение данных в файлы для последующей визуализации в Python
    saveDataToFile(negativeBinomialData, "negative_binomial_data.txt");
    saveDataToFile(geometricData, "geometric_data.txt");

    // Расчет статистик (несмещенные оценки)
    pair<double, double> statsNB = calculateStats(negativeBinomialData);
    double meanNB = statsNB.first;
    double varNB = statsNB.second;

    pair<double, double> statsGeom = calculateStats(geometricData);
    double meanGeom = statsGeom.first;
    double varGeom = statsGeom.second;

    // Теоретические значения для сравнения
    // Отрицательное биномиальное: E[X] = r*(1-p)/p, D[X] = r*(1-p)/p²
    double trueMeanNB = r * (1 - p_binom) / p_binom;
    double trueVarNB = r * (1 - p_binom) / (p_binom * p_binom);

    // Геометрическое (количество неудач): E[X] = (1-p)/p, D[X] = (1-p)/p²
    double trueMeanGeom = (1 - p_geom) / p_geom;
    double trueVarGeom = (1 - p_geom) / (p_geom * p_geom);

    // Вывод результатов
    cout << fixed << setprecision(4);
    cout << "\n=== Результаты моделирования ===\n";

    cout << "\nОтрицательное биномиальное распределение (r=" << r << ", p=" << p_binom << "):\n";
    cout << "  Оценка математического ожидания: " << meanNB << "\n";
    cout << "  Теоретическое математическое ожидание: " << trueMeanNB << "\n";
    cout << "  Оценка дисперсии: " << varNB << "\n";
    cout << "  Теоретическая дисперсия: " << trueVarNB << "\n";

    cout << "\nГеометрическое распределение (p=" << p_geom << "):\n";
    cout << "  Оценка математического ожидания: " << meanGeom << "\n";
    cout << "  Теоретическое математическое ожидание: " << trueMeanGeom << "\n";
    cout << "  Оценка дисперсии: " << varGeom << "\n";
    cout << "  Теоретическая дисперсия: " << trueVarGeom << "\n";

    // Подготовка теоретических вероятностей для χ²-критерия
    // Для отрицательного биномиального: P(X=k) = C(k+r-1, k) * p^r * (1-p)^k
    vector<double> probabilitiesNB(10, 0.0);
    for (int k = 0; k < 10; ++k) {
        // Вычисляем биномиальный коэффициент C(k + r - 1, k)
        double binomCoeff = 1.0;
        for (int i = 1; i <= k; ++i) {
            binomCoeff *= (r + i - 1) / static_cast<double>(i);
        }
        probabilitiesNB[k] = binomCoeff * pow(p_binom, r) * pow(1 - p_binom, k);
    }

    // Для геометрического: P(X=k) = (1-p)^k * p
    vector<double> probabilitiesGeom(10, 0.0);
    for (int k = 0; k < 10; ++k) {
        probabilitiesGeom[k] = pow(1 - p_geom, k) * p_geom;
    }

    // Проверка по χ²-критерию
    bool chiSquareNBPassed = chiSquareTest(negativeBinomialData, probabilitiesNB, 0.05);
    bool chiSquareGeomPassed = chiSquareTest(geometricData, probabilitiesGeom, 0.05);

    cout << "\n=== Проверка по chi^2-критерию Пирсона (alpha = 0.05) ===\n";
    cout << "  Отрицательное биномиальное: " << (chiSquareNBPassed ? "Пройден" : "Не пройден") << "\n";
    cout << "  Геометрическое: " << (chiSquareGeomPassed ? "Пройден" : "Не пройден") << "\n";

    cout << "\nРабота завершена. Данные сохранены в файлы для построения гистограмм.\n";

    return 0;
}