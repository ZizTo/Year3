import numpy as np
import matplotlib.pyplot as plt

# Функция для загрузки данных из текстового файла
def load_data(filename):
    """
    Загружает данные из файла, где каждая строка содержит одно целое число.
    """
    with open(filename, 'r') as file:
        data = [int(line.strip()) for line in file]
    return data

# Функция для построения гистограммы
def plot_histogram(data, title, bins=10, color='skyblue'):
    """
    Строит гистограмму для переданных данных.
    :param data: список целых чисел
    :param title: заголовок графика
    :param bins: количество интервалов
    :param color: цвет столбцов
    """
    plt.figure(figsize=(10, 6))
    plt.hist(data, bins=bins, edgecolor='black', color=color, alpha=0.7)
    plt.title(title, fontsize=14, fontweight='bold')
    plt.xlabel('Значение случайной величины', fontsize=12)
    plt.ylabel('Частота', fontsize=12)
    plt.grid(True, alpha=0.3)
    plt.xticks(range(max(data)+1))  # Показываем все возможные значения на оси X
    plt.show()

# Основной код
if __name__ == "__main__":
    # Загрузка данных из файлов, созданных C++
    try:
        negative_binomial_data = load_data("negative_binomial_data.txt")
        geometric_data = load_data("geometric_data.txt")
    except FileNotFoundError:
        print("Ошибка: файлы данных не найдены. Убедитесь, что программа на C++ была запущена и создала файлы.")
        exit(1)

    # Построение гистограммы для отрицательного биномиального распределения
    plot_histogram(
        negative_binomial_data,
        title="Гистограмма: Отрицательное биномиальное распределение (r=4, p=0.2)",
        bins=max(negative_binomial_data)+1,
        color='lightcoral'
    )

    # Построение гистограммы для геометрического распределения
    plot_histogram(
        geometric_data,
        title="Гистограмма: Геометрическое распределение (p=0.25)",
        bins=max(geometric_data)+1,
        color='lightgreen'
    )