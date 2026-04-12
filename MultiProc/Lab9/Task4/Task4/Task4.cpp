#include <iostream>
#include <vector>
#include <omp.h>
#include <climits>

using namespace std;

int main() {
    const int SIZE = 500000000;
    vector<int> a(SIZE, 10);
    a[500] = -5;

    clock_t start_time, end_time;
    double elapsed_time;

    start_time = clock();


    int min_val = a[0];

#pragma omp parallel for num_threads(4)
    for (int i = 1; i < SIZE; i++) {
        if (a[i] < min_val) {
#pragma omp critical
            {
                if (a[i] < min_val) {
                    min_val = a[i];
                }
            }
        }
    }

    cout << "Min value: " << min_val << endl;
    end_time = clock();
    elapsed_time = (double)(end_time - start_time) / CLOCKS_PER_SEC;
    printf("Time %.6f secs\n", elapsed_time);

    return 0;
}