#include <iostream>
#include <omp.h>

using namespace std;

int main() {
    omp_set_num_threads(4);

#pragma omp parallel
    {
        int threadNum = omp_get_thread_num();
        int totalThreads = omp_get_num_threads();

#pragma omp critical
        {
            cout << "Hello from thread " << threadNum
                << " of " << totalThreads << " threads!" << endl;
        }
    }

    cout << "Parallel region finished." << endl;

    return 0;
}
