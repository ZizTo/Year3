#include <iostream>
#include <vector>
#include <cmath>
#include <cstdlib>
#include <ctime>
#include <iomanip>
#include <fstream>
using namespace std;

// Константы для задания (вариант друга)
const int n = 1000; // Количество реализаций
const double p_bernoulli = 0.7; // Параметр распределения Бернулли
const int m_binomial = 5;       // Число испытаний для биномиального распределения
const double p_binomial = 0.25; // Вероятность успеха для биномиального

// Функция для генерации равномерного распределения U(0, 1)
// Используется как базовый источник случайности
double generateUniform() {
    return static_cast<double>(rand()) / RAND_MAX;
}

// === Генерация одного испытания Бернулли ===
// Случайная величина X ~ Bi(1, p): 
//   если alpha < p → X = 1 (успех),
//   иначе → X = 0 (неудача)
int generateBernoulli(double p) {
    double alpha = generateUniform();
    return (alpha < p) ? 1 : 0;
}

// === Генерация массива испытаний Бернулли (для демонстрации или внутреннего использования) ===
// Преподаватель просил реализовать массив, где:
//   если alpha < p → 1 (успех),
//   иначе → 0 (неудача)
vector<int> generateBernoulliArray(int size, double p) {
    vector<int> bernoulli(size);
    for (int i = 0; i < size; ++i) {
        double alpha = generateUniform();
        bernoulli[i] = (alpha < p) ? 1 : 0; // Фигурная E: 1 при успехе, 0 при неудаче
    }
    return bernoulli;
}

// === Моделирование биномиального распределения Bi(m, p) ===
// Определение: количество успехов в m независимых испытаниях Бернулли
// Метод: сумма m независимых испытаний Бернулли с параметром p
int generateBinomial(int m, double p) {
    int successes = 0;
    for (int i = 0; i < m; ++i) {
        successes += generateBernoulli(p); // Каждое испытание даёт 0 или 1
    }
    return successes; // Общее число успехов в m испытаниях
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
    // Для bins = 6 (значения 0..5), степеней свободы = 5, критическое значение ≈ 11.070
    double criticalValue = (bins == 6) ? 11.070 : 16.919;

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
    vector<int> bernoulliData(n); // Выборка из распределения Бернулли Bi(1, 0.7)
    vector<int> binomialData(n);  // Выборка из биномиального распределения Bi(5, 0.25)

    cout << "Генерация данных...\n";

    for (int i = 0; i < n; ++i) {
        bernoulliData[i] = generateBernoulli(p_bernoulli);
        binomialData[i] = generateBinomial(m_binomial, p_binomial);
    }

    // Сохранение данных в файлы для последующей визуализации в Python
    saveDataToFile(bernoulliData, "bernoulli_data.txt");
    saveDataToFile(binomialData, "binomial_data.txt");

    // Расчет статистик (несмещенные оценки)
    pair<double, double> statsBern = calculateStats(bernoulliData);
    double meanBern = statsBern.first;
    double varBern = statsBern.second;

    pair<double, double> statsBinom = calculateStats(binomialData);
    double meanBinom = statsBinom.first;
    double varBinom = statsBinom.second;

    // Теоретические значения для сравнения
    // Бернулли: E[X] = p, D[X] = p(1-p)
    double trueMeanBern = p_bernoulli;
    double trueVarBern = p_bernoulli * (1 - p_bernoulli);

    // Биномиальное: E[Y] = m*p, D[Y] = m*p*(1-p)
    double trueMeanBinom = m_binomial * p_binomial;
    double trueVarBinom = m_binomial * p_binomial * (1 - p_binomial);

    // Вывод результатов
    cout << fixed << setprecision(4);
    cout << "\n=== Результаты моделирования ===\n";

    cout << "\nРаспределение Бернулли (p=" << p_bernoulli << "):\n";
    cout << "  Оценка математического ожидания: " << meanBern << "\n";
    cout << "  Теоретическое математическое ожидание: " << trueMeanBern << "\n";
    cout << "  Оценка дисперсии: " << varBern << "\n";
    cout << "  Теоретическая дисперсия: " << trueVarBern << "\n";

    cout << "\nБиномиальное распределение (m=" << m_binomial << ", p=" << p_binomial << "):\n";
    cout << "  Оценка математического ожидания: " << meanBinom << "\n";
    cout << "  Теоретическое математическое ожидание: " << trueMeanBinom << "\n";
    cout << "  Оценка дисперсии: " << varBinom << "\n";
    cout << "  Теоретическая дисперсия: " << trueVarBinom << "\n";

    // Подготовка теоретических вероятностей для пси²-критерия
    // Для Бернулли: P(X=0) = 1-p, P(X=1) = p
    vector<double> probabilitiesBern(2, 0.0);
    probabilitiesBern[0] = 1 - p_bernoulli;
    probabilitiesBern[1] = p_bernoulli;

    // Для биномиального: P(Y=k) = C(m, k) * p^k * (1-p)^(m-k)
    vector<double> probabilitiesBinom(m_binomial + 1, 0.0);
    for (int k = 0; k <= m_binomial; ++k) {
        // Вычисляем биномиальный коэффициент C(m, k)
        double binomCoeff = 1.0;
        for (int i = 1; i <= k; ++i) {
            binomCoeff *= (m_binomial - i + 1) / static_cast<double>(i);
        }
        probabilitiesBinom[k] = binomCoeff * pow(p_binomial, k) * pow(1 - p_binomial, m_binomial - k);
    }

    // Проверка по χ²-критерию
    bool chiSquareBernPassed = chiSquareTest(bernoulliData, probabilitiesBern, 0.05);
    bool chiSquareBinomPassed = chiSquareTest(binomialData, probabilitiesBinom, 0.05);

    cout << "\n=== Проверка по chi^2-критерию Пирсона (alpha = 0.05) ===\n";
    cout << "  Распределение Бернулли: " << (chiSquareBernPassed ? "Пройден" : "Не пройден") << "\n";
    cout << "  Биномиальное распределение: " << (chiSquareBinomPassed ? "Пройден" : "Не пройден") << "\n";

    cout << "\nРабота завершена. Данные сохранены в файлы для построения гистограмм.\n";

    return 0;
}