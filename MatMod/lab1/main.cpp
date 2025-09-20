#include <iostream>
#include <vector>
#include <numeric>
#include <algorithm>
#include <cmath>
#include <ctime>
#include <fstream>

using namespace std;

void save_to_file(const vector<double>& data, const string& filename) {
    ofstream file(filename);
    if (file.is_open()) {
        for (double val : data) {
            file << val << "\n";
        }
        file.close();
        cout << "Данные успешно сохранены в файл: " << filename << endl;
    } else {
        cerr << "Ошибка: не удалось открыть файл " << filename << endl;
    }
}

// МКМ
vector<double> mkm(long long a0, long long beta, long long m, int n) {
    vector<double> sequence;
    long long current_a = a0;
    for (int i = 0; i < n; i++) {
        current_a = (beta * current_a) % m;
        sequence.push_back(static_cast<double>(current_a) / m);
    }
    return sequence;
}

// Макларен
vector<double> maclaren(int k, int n) {
    long long beta1 = 65539;
    long long m1 = 2147483648;
    long long a01 = 65539; 

    long long beta2 = 46555;
    long long m2 = 2147483647;
    long long a02 = 46555;

    vector<double> table = mkm(a01, beta1, m1, k);

    long long current_a1 = a01;
    for(int i=0; i<k; ++i) {
        current_a1 = (beta1 * current_a1) % m1;
    }
    
    long long current_a2 = a02;
    vector<double> answ;

    for (int i = 0; i < n; i++)
    {
        current_a2 = (beta2 * current_a2) % m2;
        int j = static_cast<int>((static_cast<double>(current_a2) / m2) * k);
        
        answ.push_back(table[j]);
        
        current_a1 = (beta1 * current_a1) % m1;
        
        table[j] = static_cast<double>(current_a1) / m1;
    }
    return answ;
}

// Х квадрат
void xkvadrat(const vector<double>& data, int k, double epsilon) {
    int n = data.size();
    vector<int> observed(k, 0);

    for (double val : data) {
        int interval = static_cast<int>(val * k);
        if (interval < k) {
            observed[interval]++;
        }
    }

    double expected = static_cast<double>(n) / k;
    double x_stat = 0.0;
    for (int count : observed) {
        x_stat += pow(count - expected, 2) / expected;
    }

    double critical_value = 30.144;
    cout << "--- x kvadrat ---" << endl;
    cout << "x stat: " << x_stat << endl;
    cout << "crit value: " << critical_value << endl;
    if (x_stat < critical_value) {
        cout << "Good" << endl;
    } else {
        cout << "Bad" << endl;
    }
}

// Колмогоров
void kolmogorov(const vector<double>& data, double epsilon) {
    int n = data.size();
    vector<double> sorted_data = data;
    sort(sorted_data.begin(), sorted_data.end());

    double max_d = 0.0;
    for (int i = 0; i < n; ++i) {
        double d1 = abs(sorted_data[i] - static_cast<double>(i) / n);
        double d2 = abs(sorted_data[i] - static_cast<double>(i + 1) / n);
        if (d1 > max_d) max_d = d1;
        if (d2 > max_d) max_d = d2;
    }

    double critical_value = 1.36 / sqrt(n);

    cout << "--- Kolmogorov ---" << endl;
    cout << "max d: " << max_d << endl;
    cout << "crit value: " << critical_value << endl;
    if (max_d < critical_value) {
        cout << "Good" << endl;
    } else {
        cout << "Bad" << endl;
    }
}

int main() {
    const int N = 1000;
    const double EPSILON = 0.05;

    cout << "=== Testing MKM ===" << endl;
    long long beta_mkm = 65539;
    long long m_mkm = 2147483648; 
    long long a0 = 65539;
    vector<double> mkm_f = mkm(a0, beta_mkm, m_mkm, N);
    
    xkvadrat(mkm_f, 20, EPSILON);
    kolmogorov(mkm_f, EPSILON);
    save_to_file(mkm_f, "mkm_data.txt"); 
    cout << "\n================================================\n" << endl;

    cout << "=== Testing Maclaren ===" << endl;
    const int K = 128;
    vector<double> macl = maclaren(K, N);

    xkvadrat(macl, 20, EPSILON);
    kolmogorov(macl, EPSILON);
    save_to_file(macl, "maclaren_data.txt");
    return 0;
}
