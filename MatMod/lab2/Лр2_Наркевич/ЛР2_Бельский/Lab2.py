import numpy as np
import matplotlib.pyplot as plt

def load_data(filename):
    with open(filename, 'r') as file:
        data = [int(line.strip()) for line in file]
    return data

def plot_histogram(data, title, color='skyblue'):
    plt.figure(figsize=(8, 5))
    # Для дискретных распределений используем bins = max+1 и align='left'
    counts = [data.count(i) for i in range(max(data)+1)]
    plt.bar(range(len(counts)), counts, color=color, edgecolor='black', alpha=0.7)
    plt.title(title, fontsize=13, fontweight='bold')
    plt.xlabel('Значение случайной величины')
    plt.ylabel('Частота')
    plt.xticks(range(len(counts)))
    plt.grid(axis='y', alpha=0.3)
    plt.show()

if __name__ == "__main__":
    try:
        bernoulli_data = load_data("bernoulli_data.txt")
        binomial_data = load_data("binomial_data.txt")
    except FileNotFoundError:
        print("Ошибка: файлы не найдены.")
        exit(1)

    plot_histogram(bernoulli_data, "Гистограмма: Распределение Бернулли (p=0.7)", 'lightcoral')
    plot_histogram(binomial_data, "Гистограмма: Биномиальное распределение (m=5, p=0.25)", 'lightgreen')