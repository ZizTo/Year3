#include <omp.h>
#include <iostream>
#include <vector>
#include <iomanip>
#include <cmath>

using namespace std;

double pi_spmd(long long num_steps, int num_threads)
{
    const double step = 1.0 / static_cast<double>(num_steps);

    omp_set_num_threads(num_threads);
    vector<double> partial(num_threads);
    for (auto& x : partial) x = 0.0;

    int actual_threads = 0;

#pragma omp parallel shared(partial, actual_threads, num_steps, step)
    {
        int id = omp_get_thread_num();
        int nthrds = omp_get_num_threads();
        double local_sum = 0.0;

#pragma omp single
        actual_threads = nthrds;

        for (long long i = id; i < num_steps; i += nthrds)
        {
            double x = (i + 0.5) * step;
            local_sum += 4.0 / (1.0 + x * x);
        }

        partial[id] = local_sum;
    }

    double sum = 0.0;
    for (int i = 0; i < actual_threads; ++i)
        sum += partial[i];

    return step * sum;
}

int main()
{
    const double REF_PI = acos(-1.0);
    vector<int> threads = { 1, 2, 4, 8 };
    vector<long long> steps = { 500000000LL, 1000000000LL, 2000000000LL };

    cout << fixed << setprecision(15);
    cout << "threads | steps | time_sec | pi | abs_error\n";

    for (int t : threads)
    {
        for (long long n : steps)
        {
            double t0 = omp_get_wtime();
            double pi = pi_spmd(n, t);
            double t1 = omp_get_wtime();

            cout << t << " "
                << n << " "
                << (t1 - t0) << " "
                << pi << " "
                << fabs(pi - REF_PI) << '\n';
        }
    }
}