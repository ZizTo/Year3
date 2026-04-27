#include <iostream>
#include <vector>
#include <thread>
#include <functional>
#include <chrono>
#include <iomanip>
#include <cmath>

using namespace std;

double integrate_sequential(const function<double(double)>& f,
    double a, double b, size_t n)
{
    double h = (b - a) / static_cast<double>(n);
    double sum = 0.0;

    for (size_t i = 0; i < n; ++i)
    {
        double x = a + i * h;
        sum += f(x);
    }

    return sum * h;
}

void integrate_block(const function<double(double)>& f,
    double a, double h,
    size_t start, size_t end,
    double& result)
{
    result = 0.0;
    for (size_t i = start; i < end; ++i)
    {
        double x = a + i * h;
        result += f(x);
    }
}

double integrate_parallel(const function<double(double)>& f,
    double a, double b, size_t n,
    size_t num_threads)
{
    double h = (b - a) / static_cast<double>(n);

    vector<thread> threads;
    vector<double> partial_sums(num_threads, 0.0);

    size_t block_size = n / num_threads;
    size_t remainder = n % num_threads;

    size_t current = 0;
    for (size_t t = 0; t < num_threads; ++t)
    {
        size_t start = current;
        size_t end = start + block_size + (t < remainder ? 1 : 0);

        threads.emplace_back(
            integrate_block,
            cref(f),
            a, h,
            start, end,
            ref(partial_sums[t])
        );

        current = end;
    }

    for (auto& th : threads)
        th.join();

    double total = 0.0;
    for (double part : partial_sums)
        total += part;

    return total * h;
}

struct FunctionInfo
{
    string name;
    function<double(double)> func;
    double a;
    double b;
};

int main()
{
    vector<FunctionInfo> functions = {
        {"x^2", [](double x) { return x * x; }, 0.0, 10.0},
        {"sin(x)", [](double x) { return sin(x); }, 0.0, 3.1415926535},
        {"exp(-x)", [](double x) { return exp(-x); }, 0.0, 5.0}
    };

    vector<size_t> dimensions = { 10000, 100000, 1000000, 10000000 };
    vector<size_t> thread_counts = { 2, 4 };

    cout << fixed << setprecision(8);
    cout << left
        << setw(12) << "Function"
        << setw(12) << "N"
        << setw(12) << "Threads"
        << setw(18) << "Seq result"
        << setw(18) << "Par result"
        << setw(15) << "Seq ms"
        << setw(15) << "Par ms"
        << setw(12) << "Uskor"
        << setw(12) << "Effect"
        << "\n";

    for (const auto& f_info : functions)
    {
        for (size_t n : dimensions)
        {
            volatile double warmup = integrate_sequential(f_info.func, f_info.a, f_info.b, 1000);
            (void)warmup;

            const int RUNS = 5;
            double seq_ms = numeric_limits<double>::max();
            double seq_result = 0.0;
            for (int r = 0; r < RUNS; ++r)
            {
                auto t1 = chrono::high_resolution_clock::now();
                seq_result = integrate_sequential(f_info.func, f_info.a, f_info.b, n);
                auto t2 = chrono::high_resolution_clock::now();
                seq_ms = min(seq_ms,
                    chrono::duration<double, milli>(t2 - t1).count());
            }

            for (size_t threads : thread_counts)
            {
                (void)warmup;
                double par_ms = numeric_limits<double>::max();
                double par_result = 0.0;
                for (int r = 0; r < RUNS; ++r)
                {
                    auto t1 = chrono::high_resolution_clock::now();
                    par_result = integrate_parallel(f_info.func, f_info.a, f_info.b, n, threads);
                    auto t2 = chrono::high_resolution_clock::now();
                    par_ms = min(par_ms,
                        chrono::duration<double, milli>(t2 - t1).count());
                }

                double speedup = seq_ms / par_ms;

                cout << left
                    << setw(12) << f_info.name
                    << setw(12) << n
                    << setw(12) << threads
                    << setw(18) << seq_result
                    << setw(18) << par_result
                    << setw(15) << seq_ms
                    << setw(15) << par_ms
                    << setw(12) << speedup
                    << setw(12) << speedup / threads
                    << "\n";
            }
        }
    }
}