#include <iostream>
#include <vector>
#include <random>
#include <cmath>
#include <algorithm>
#include <numeric>
#include <map>
#include <iomanip>
#include <locale>

using namespace std;

// Функция для вычисления биномиального коэффициента C(n, k)
int binomial_coeff(int n, int k) {
    if (k < 0 || k > n) return 0;
    if (k == 0 || k == n) return 1;

    k = min(k, n - k);
    long long result = 1;
    for (int i = 1; i <= k; i++) {
        result = result * (n - k + i) / i;
    }
    return result;
}

// Функция для вычисления вероятности биномиального распределения
double binomial_pmf(int k, int m, double p) {
    if (k < 0 || k > m) return 0.0;
    return binomial_coeff(m, k) * pow(p, k) * pow(1 - p, m - k);
}

// Функция для вычисления вероятности геометрического распределения
double geometric_pmf(int k, double p) {
    if (k < 1) return 0.0;
    return p * pow(1 - p, k - 1);
}

// Собственная функция для генерации биномиальной случайной величины
vector<int> generate_binomial(int n, int m, double p, mt19937& gen) {
    vector<int> samples(n);
    uniform_real_distribution<double> dist(0.0, 1.0);

    for (int i = 0; i < n; i++) {
        int success = 0;
        for (int j = 0; j < m; j++) {
            if (dist(gen) < p) {
                success++;
            }
        }
        samples[i] = success;
    }
    return samples;
}

// Собственная функция для генерации геометрической случайной величины
vector<int> generate_geometric(int n, double p, mt19937& gen) {
    vector<int> samples(n);
    uniform_real_distribution<double> dist(0.0, 1.0);

    for (int i = 0; i < n; i++) {
        int count = 1;
        while (dist(gen) > p) {
            count++;
        }
        samples[i] = count;
    }
    return samples;
}

// Вычисление выборочного среднего
double calculate_mean(const vector<int>& samples) {
    return accumulate(samples.begin(), samples.end(), 0.0) / samples.size();
}

// Вычисление несмещенной оценки дисперсии
double calculate_variance(const vector<int>& samples) {
    double mean = calculate_mean(samples);
    double sum_sq = 0.0;
    for (int x : samples) {
        sum_sq += (x - mean) * (x - mean);
    }
    return sum_sq / (samples.size() - 1);
}

// Структура для возврата результата критерия хи-квадрат
struct Chi2Result {
    double statistic;
    int df;
};

// Функция для вычисления критического значения хи-квадрат распределения
double chi2_quantile(double p, int df) {
    // Табличные значения для уровня значимости 0.05
    static map<int, double> table = {
        {1, 3.8415}, {2, 5.9915}, {3, 7.8147}, {4, 9.4877}, {5, 11.0705},
        {6, 12.5916}, {7, 14.0671}, {8, 15.5073}, {9, 16.9190}, {10, 18.3070}
    };

    if (table.find(df) != table.end()) {
        return table[df];
    }

    // Для df > 10 используем приближение
    double z = 1.64485; // Для p = 0.95
    double chi2_val = df * pow(1.0 - 2.0 / (9 * df) + z * sqrt(2.0 / (9 * df)), 3);
    return chi2_val;
}

// Функция для объединения категорий с малыми ожидаемыми частотами
void combine_categories(const vector<int>& observed, const vector<double>& expected,
    vector<int>& new_observed, vector<double>& new_expected) {
    new_observed.clear();
    new_expected.clear();

    int n = observed.size();
    int temp_obs = 0;
    double temp_exp = 0.0;

    for (int i = 0; i < n; i++) {
        temp_obs += observed[i];
        temp_exp += expected[i];

        if (temp_exp >= 5.0 || i == n - 1) {
            new_observed.push_back(temp_obs);
            new_expected.push_back(temp_exp);
            temp_obs = 0;
            temp_exp = 0.0;
        }
    }

    // Если остались необработанные данные, добавляем их в последнюю категорию
    if (temp_obs > 0 && !new_observed.empty()) {
        new_observed[new_observed.size() - 1] += temp_obs;
        new_expected[new_expected.size() - 1] += temp_exp;
    }
}

// Критерий хи-квадрат для биномиального распределения
Chi2Result chi2_test_binomial(const vector<int>& samples, int m, double p) {
    Chi2Result result;

    // Создаем гистограмму наблюдаемых частот
    vector<int> observed(m + 1, 0);
    for (int x : samples) {
        if (x >= 0 && x <= m) {
            observed[x]++;
        }
    }

    // Вычисляем ожидаемые частоты
    vector<double> expected(m + 1);
    for (int i = 0; i <= m; i++) {
        expected[i] = samples.size() * binomial_pmf(i, m, p);
    }

    // Объединяем категории с малыми ожидаемыми частотами
    vector<int> new_observed;
    vector<double> new_expected;
    combine_categories(observed, expected, new_observed, new_expected);

    // Вычисляем статистику хи-квадрат
    double chi2 = 0.0;
    for (size_t i = 0; i < new_observed.size(); i++) {
        if (new_expected[i] > 1e-10) {
            chi2 += pow(new_observed[i] - new_expected[i], 2) / new_expected[i];
        }
    }

    result.statistic = chi2;
    result.df = new_observed.size() - 1;

    return result;
}

// Критерий хи-квадрат для геометрического распределения
Chi2Result chi2_test_geometric(const vector<int>& samples, double p) {
    Chi2Result result;

    // Находим максимальное значение в выборке
    int max_val = *max_element(samples.begin(), samples.end());

    // Вычисляем cumulative вероятность для определения количества категорий
    double cumulative = 0.0;
    int max_k = 0;
    while (cumulative < 0.999 && max_k < 1000) {
        max_k++;
        cumulative += geometric_pmf(max_k, p);
    }

    // Создаем векторы для наблюдаемых и ожидаемых частот
    vector<int> observed_vec(max_k, 0);
    vector<double> expected_vec(max_k, 0.0);

    // Заполняем ожидаемые частоты
    double total_prob = 0.0;
    for (int i = 0; i < max_k; i++) {
        int k = i + 1;
        expected_vec[i] = samples.size() * geometric_pmf(k, p);
        total_prob += geometric_pmf(k, p);
    }

    // Корректируем последнюю категорию для учета хвоста распределения
    expected_vec[max_k - 1] += samples.size() * (1.0 - total_prob);

    // Заполняем наблюдаемые частоты
    for (int x : samples) {
        if (x <= max_k) {
            observed_vec[x - 1]++;
        }
        else {
            observed_vec[max_k - 1]++;
        }
    }

    // Объединяем категории с малыми ожидаемыми частотами
    vector<int> new_observed;
    vector<double> new_expected;
    combine_categories(observed_vec, expected_vec, new_observed, new_expected);

    // Вычисляем статистику хи-квадрат
    double chi2 = 0.0;
    for (size_t i = 0; i < new_observed.size(); i++) {
        if (new_expected[i] > 1e-10) {
            chi2 += pow(new_observed[i] - new_expected[i], 2) / new_expected[i];
        }
    }

    result.statistic = chi2;
    result.df = new_observed.size() - 1;

    return result;
}

// Оценка вероятности ошибки I рода
double estimate_type1_error(int n_tests, int m, double p_binom, double p_geom, double alpha, mt19937& gen) {
    int rejections_binom = 0;
    int rejections_geom = 0;

    for (int i = 0; i < n_tests; i++) {
        vector<int> binom_sample = generate_binomial(1000, m, p_binom, gen);
        vector<int> geom_sample = generate_geometric(1000, p_geom, gen);

        Chi2Result binom_result = chi2_test_binomial(binom_sample, m, p_binom);
        Chi2Result geom_result = chi2_test_geometric(geom_sample, p_geom);

        double critical_value_binom = chi2_quantile(1 - alpha, binom_result.df);
        double critical_value_geom = chi2_quantile(1 - alpha, geom_result.df);

        if (binom_result.statistic > critical_value_binom) rejections_binom++;
        if (geom_result.statistic > critical_value_geom) rejections_geom++;
    }

    cout << "Биномиальное распределение - отвергнуто: " << rejections_binom
        << " из " << n_tests << " тестов" << endl;
    cout << "Геометрическое распределение - отвергнуто: " << rejections_geom
        << " из " << n_tests << " тестов" << endl;

    return (rejections_binom + rejections_geom) / (2.0 * n_tests);
}

int main() {
    // Установка локали для русского языка
    setlocale(LC_ALL, "Russian");

    // Параметры
    const int n = 1000;
    const int m = 6;
    const double p_binom = 0.75;
    const double p_geom = 0.7;
    const double alpha = 0.05;

    // Инициализация генератора случайных чисел
    random_device rd;
    mt19937 gen(rd());

    cout << fixed << setprecision(4);

    // Истинные параметры распределений
    double true_mean_binom = m * p_binom;
    double true_var_binom = m * p_binom * (1 - p_binom);
    double true_mean_geom = 1.0 / p_geom;
    double true_var_geom = (1 - p_geom) / (p_geom * p_geom);

    cout << "Биномиальное распределение Bi(" << m << ", " << p_binom << "):" << endl;
    cout << "Истинное мат. ожидание: " << true_mean_binom << ", дисперсия: " << true_var_binom << endl;

    cout << "\nГеометрическое распределение G(" << p_geom << "):" << endl;
    cout << "Истинное мат. ожидание: " << true_mean_geom << ", дисперсия: " << true_var_geom << endl << endl;

    // Генерация выборок
    vector<int> binom_sample = generate_binomial(n, m, p_binom, gen);
    vector<int> geom_sample = generate_geometric(n, p_geom, gen);

    // Оценки для биномиального распределения
    double mean_binom = calculate_mean(binom_sample);
    double var_binom = calculate_variance(binom_sample);

    cout << "Биномиальное распределение:" << endl;
    cout << "Оценка мат. ожидания: " << mean_binom << ", истинное: " << true_mean_binom << endl;
    cout << "Оценка дисперсии: " << var_binom << ", истинное: " << true_var_binom << endl;

    // Оценки для геометрического распределения
    double mean_geom = calculate_mean(geom_sample);
    double var_geom = calculate_variance(geom_sample);

    cout << "\nГеометрическое распределение:" << endl;
    cout << "Оценка мат. ожидания: " << mean_geom << ", истинное: " << true_mean_geom << endl;
    cout << "Оценка дисперсии: " << var_geom << ", истинное: " << true_var_geom << endl;

    // Критерий хи-квадрат
    Chi2Result binom_result = chi2_test_binomial(binom_sample, m, p_binom);
    Chi2Result geom_result = chi2_test_geometric(geom_sample, p_geom);

    double critical_value_binom = chi2_quantile(1 - alpha, binom_result.df);
    double critical_value_geom = chi2_quantile(1 - alpha, geom_result.df);

    cout << "\nКритерий хи-квадрат (уровень значимости " << alpha << "):" << endl;
    cout << "Биномиальное: χ² = " << binom_result.statistic << ", крит. значение = " << critical_value_binom;
    cout << ", df = " << binom_result.df;
    cout << " - " << (binom_result.statistic > critical_value_binom ? "ОТВЕРГАЕТСЯ" : "принимается") << endl;

    cout << "Геометрическое: χ² = " << geom_result.statistic << ", крит. значение = " << critical_value_geom;
    cout << ", df = " << geom_result.df;
    cout << " - " << (geom_result.statistic > critical_value_geom ? "ОТВЕРГАЕТСЯ" : "принимается") << endl;

    // Оценка ошибки I рода
    cout << "\nОценка вероятности ошибки I рода (100 тестов):" << endl;
    double type1_error = estimate_type1_error(100, m, p_binom, p_geom, alpha, gen);
    cout << "Средняя вероятность ошибки I рода: " << type1_error << endl;
    cout << "Ожидаемая вероятность: " << alpha << endl;

    return 0;
}