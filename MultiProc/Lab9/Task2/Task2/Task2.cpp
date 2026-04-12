#include <iostream>
#include <omp.h>
#include <time.h>

using namespace std;

const double M_PI = 3.141592653589;
const int sizeOf = 100000000;
double sinTable[sizeOf];

int main() {
    clock_t start_time, end_time;
    double elapsed_time;

    start_time = clock();

    omp_set_num_threads(4);

    cout << "Thread | Iteration\n";
    cout << "------------------\n";

#pragma omp parallel for
    for (int n = 0; n < sizeOf; ++n)
    {

        sinTable[n] = std::sin(2 * M_PI * n / sizeOf);
        {
            //cout << "   " << omp_get_thread_num() << "   |    " << sinTable[n] << "\n";
        }
    }

    end_time = clock();
    elapsed_time = (double)(end_time - start_time) / CLOCKS_PER_SEC;
    printf("Time %.6f secs\n", elapsed_time);
    return 0;
}
