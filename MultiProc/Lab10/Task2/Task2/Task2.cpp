#include <omp.h>
#include <iostream>
#include <vector>
#include <iomanip>
#include <cmath>

using namespace std;

double pi_loop(long long num_steps, int num_threads)
{
    const double step = 1.0 / static_cast<double>(num_steps);

    double sum = 0.0;

    omp_set_num_threads(num_threads);

#pragma omp parallel for reduction(+:sum) schedule(dynamic)
    for (long long i = 0; i < num_steps; ++i)
    {
        double x = (i + 0.5) * step;
        sum += 4.0 / (1.0 + x * x);
    }

    return step * sum;
}

int main()
{
    const double REF_PI = acos(-1.0);
    vector<int> threads = { 1, 2, 4, 8 };
    vector<long long> steps = { 100000000LL, 200000000LL, 500000000LL };

    cout << fixed << setprecision(15);
    cout << "threads | steps | time_sec | pi | abs_error\n";
        
    for (int t : threads)
    {
        for (long long n : steps)
        {
            double t0 = omp_get_wtime();
            double pi = pi_loop(n, t);
            double t1 = omp_get_wtime();

            cout << t << " "
                << n << " "
                << (t1 - t0) << " "
                << pi << " "
                << fabs(pi - REF_PI) << '\n';
        }
    }
}