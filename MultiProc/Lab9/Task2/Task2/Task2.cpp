#include <iostream>
#include <omp.h>

using namespace std;

int main() {
    omp_set_num_threads(4);

    cout << "Thread | Iteration\n";
    cout << "------------------\n";

#pragma omp parallel for
    for (int n = 0; n < 10; ++n)
    {
#pragma omp critical
        {
            cout << "   " << omp_get_thread_num() << "   |    " << n << "\n";
        }
    }

    return 0;
}
