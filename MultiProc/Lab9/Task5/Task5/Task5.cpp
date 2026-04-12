#include <iostream>
#include <vector>
#include <omp.h>

using namespace std;

int main() {
    clock_t start_time, end_time;
    double elapsed_time;

    start_time = clock();

    omp_set_num_threads(4);

    int number = 10, fac = 1;
#pragma omp parallel for reduction(*:fac)
    for (int n = 2; n <= number; ++n) {
        fac *= n;
    }
    cout << "Factorial 10! = " << fac << endl;

    long num_steps = 1000000;
    double step = 1.0 / (double)num_steps;
    double pi = 0.0;

#pragma omp parallel for reduction(+:pi)
    for (int i = 0; i < num_steps; i++) {
        double x = (i + 0.5) * step;
        pi += 4.0 / (1.0 + x * x);
    }
    pi *= step;
    cout << "Pi = " << pi << endl;

    const int SIZE = 100000;
    vector<int> v1(SIZE, 2), v2(SIZE, 3);
    long long dot_product = 0;

#pragma omp parallel for reduction(+:dot_product)
    for (int i = 0; i < SIZE; i++) {
        dot_product += v1[i] * v2[i];
    }
    cout << "Dot product = " << dot_product << endl;

    end_time = clock();
    elapsed_time = (double)(end_time - start_time) / CLOCKS_PER_SEC;
    printf("Time %.6f secs\n", elapsed_time);


    return 0;
}