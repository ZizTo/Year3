#include <iostream>
#include <vector>
#include <numeric>
#include <thread>
#include <chrono>
#include <iomanip>
#include <random>

template<typename Iterator, typename T>
struct accumulate_block
{
    void operator()(Iterator first, Iterator last, T& result)
    {
        result = std::accumulate(first, last, result);
    }
};

template<typename Iterator, typename T>
T parallel_accumulate(Iterator first, Iterator last, T init)
{
    unsigned long const length = std::distance(first, last);
    if (!length)
        return init;

    unsigned long const min_per_thread = 25;
    unsigned long const max_threads =
        (length + min_per_thread - 1) / min_per_thread;

    unsigned long const hardware_threads =
        std::thread::hardware_concurrency();

    unsigned long const num_threads = 4;
        //std::min(hardware_threads != 0 ? hardware_threads : 2, max_threads);
    //std::cout << "Threads: " << num_threads;

    unsigned long const block_size = length / num_threads;

    std::vector<T> results(num_threads);
    std::vector<std::thread> threads(num_threads - 1);

    Iterator block_start = first;
    for (unsigned long i = 0; i < (num_threads - 1); ++i)
    {
        Iterator block_end = block_start;
        std::advance(block_end, block_size);

        threads[i] = std::thread(
            accumulate_block<Iterator, T>(),
            block_start, block_end, std::ref(results[i])
        );

        block_start = block_end;
    }

    accumulate_block<Iterator, T>()(block_start, last, results[num_threads - 1]);

    for (auto& entry : threads)
        entry.join();

    return std::accumulate(results.begin(), results.end(), init);
}

template<typename T>
T sequential_accumulate(const std::vector<T>& data, T init)
{
    return std::accumulate(data.begin(), data.end(), init);
}

int main()
{
    std::vector<size_t> sizes = { 1000000, 5000000, 10000000, 20000000 };

    std::cout << std::left
        << std::setw(12) << "N"
        << std::setw(20) << "Seq sum"
        << std::setw(20) << "Par sum"
        << std::setw(15) << "Seq ms"
        << std::setw(15) << "Par ms"
        << std::setw(15) << "Uskor"
        << std::setw(15) << "Effect"
        << "\n";

    for (size_t n : sizes)
    {
        std::vector<long long> data(n);
        for (size_t i = 0; i < n; ++i)
            data[i] = 1;

        auto t1 = std::chrono::high_resolution_clock::now();
        long long seq_sum = sequential_accumulate(data, 0LL);
        auto t2 = std::chrono::high_resolution_clock::now();

        auto t3 = std::chrono::high_resolution_clock::now();
        long long par_sum = parallel_accumulate(data.begin(), data.end(), 0LL);
        auto t4 = std::chrono::high_resolution_clock::now();

        double seq_ms = std::chrono::duration<double, std::milli>(t2 - t1).count();
        double par_ms = std::chrono::duration<double, std::milli>(t4 - t3).count();
        double speedup = seq_ms / par_ms;

        std::cout << std::left
            << std::setw(12) << n
            << std::setw(20) << seq_sum
            << std::setw(20) << par_sum
            << std::setw(15) << seq_ms
            << std::setw(15) << par_ms
            << std::setw(15) << speedup
            << std::setw(15) << speedup / 4
            << "\n";
    }

    return 0;
}