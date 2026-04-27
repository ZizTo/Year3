#include <omp.h>
#include <iostream>
#include <vector>
#include <iomanip>
#include <cmath>

using namespace std;

double sum_range(long long left, long long right, double step, long long min_block)
{
    long long len = right - left;

    if (len <= min_block)
    {
        double s = 0.0;
        for (long long i = left; i < right; ++i)
        {
            double x = (i + 0.5) * step;
            s += 4.0 / (1.0 + x * x);
        }
        return s;
    }

    long long mid = left + len / 2;
    double s1 = sum_range(left, mid, step, min_block);
    double s2 = sum_range(mid, right, step, min_block);
    return s1 + s2;
}

double pi_divide_conquer(long long num_steps, int num_threads)
{
    const double step = 1.0 / static_cast<double>(num_steps);
    double total = 0.0;
    long long min_block = max(1000000LL, num_steps / (num_threads * 8));

    omp_set_num_threads(num_threads);

#pragma omp parallel reduction(+:total)
    {
        int id = omp_get_thread_num();
        int nthrds = omp_get_num_threads();

        long long block = num_steps / nthrds;
        long long left = id * block;
        long long right = (id == nthrds - 1) ? num_steps : left + block;

        total = sum_range(left, right, step, min_block);
    }

    return step * total;
}

int main()
{
    const double REF_PI = acos(-1.0);
    vector<int> threads = { 1, 2, 4, 8 };
    vector<long long> steps = { 1000000000LL, 2000000000LL, 5000000000LL };

    cout << fixed << setprecision(15);
    cout << "threads | steps | time_sec | pi | abs_error\n";

    for (int t : threads)
    {
        for (long long n : steps)
        {
            double t0 = omp_get_wtime();
            double pi = pi_divide_conquer(n, t);
            double t1 = omp_get_wtime();

            cout << t << " "
                << n << " "
                << (t1 - t0) << " "
                << pi << " "
                << fabs(pi - REF_PI) << '\n';
        }
    }
}