// js/app.js

// --- Конфигурация ---
// URL нашего WCF REST-сервиса
const API_BASE_URL = '/api';

// --- Элементы DOM ---
const tablesList = document.getElementById('tables-list');
const createStatusDiv = document.getElementById('create-status');
const insertStatusDiv = document.getElementById('insert-status');
const dataContainer = document.getElementById('data-container');
const columnsContainer = document.getElementById('columns-container');

// --- Инициализация ---
document.addEventListener('DOMContentLoaded', () => {
    // Привязываем события к кнопкам
    document.getElementById('add-column-btn').addEventListener('click', addColumnRow);
    document.getElementById('create-table-btn').addEventListener('click', createTable);
    document.getElementById('insert-data-btn').addEventListener('click', insertData);

    // Загружаем начальные данные
    loadTables();
    addColumnRow(); // Сразу добавляем одну строку для новой таблицы
});

// --- Функции для работы с API ---

/** Загружает и отображает список существующих таблиц */
async function loadTables() {
    try {
        const tables = await apiFetch('/tables');
        tablesList.innerHTML = '';
        if (tables.length > 0) {
            tables.forEach(tableName => {
                const li = document.createElement('li');
                li.textContent = tableName;
                li.onclick = () => showDataForTable(tableName);
                tablesList.appendChild(li);
            });
        } else {
            tablesList.innerHTML = '<p>Таблиц пока нет.</p>';
        }
    } catch (error) {
        showStatus(createStatusDiv, `Ошибка загрузки таблиц: ${error.message}`, true);
    }
}

/** Загружает схему и данные для выбранной таблицы и отображает их */
async function showDataForTable(tableName) {
    try {
        // Загружаем схему и данные одновременно
        const [schema, data] = await Promise.all([
            apiFetch(`/tables/${tableName}/schema`),
            apiFetch(`/tables/${tableName}/data`)
        ]);

        // Отображаем данные и форму
        displayTableData(schema, data);
        displayDataEntryForm(tableName, schema);

        dataContainer.style.display = 'block';
        dataContainer.scrollIntoView({ behavior: 'smooth' });

    } catch (error) {
        alert(`Не удалось загрузить данные для таблицы ${tableName}: ${error.message}`);
    }
}

/** Отправляет запрос на создание новой таблицы */
async function createTable() {
    const tableName = document.getElementById('new-table-name').value;
    const columns = [];
    document.querySelectorAll('.column-row').forEach(row => {
        const name = row.querySelector('.column-name').value;
        const type = row.querySelector('.column-type').value;
        if (name && type) {
            columns.push({ name, type });
        }
    });

    try {
        await apiFetch('/tables', {
            method: 'POST',
            body: { tableName, columns }
        });
        showStatus(createStatusDiv, `Таблица '${tableName}' успешно создана!`, false);
        document.getElementById('new-table-name').value = '';
        columnsContainer.innerHTML = '';
        addColumnRow();
        loadTables(); // Обновляем список таблиц
    } catch (error) {
        showStatus(createStatusDiv, `Ошибка создания таблицы: ${error.message}`, true);
    }
}

/** Отправляет запрос на вставку данных в таблицу */
async function insertData() {
    const tableName = dataContainer.dataset.tableName;
    if (!tableName) {
        showStatus(insertStatusDiv, 'Имя таблицы не определено.', true);
        return;
    }

    // Собираем данные в обычный объект, как и раньше
    const data = {};
    const form = document.getElementById('data-entry-form');
    form.querySelectorAll('[data-column-name]').forEach(input => {
        const name = input.dataset.columnName;
        const type = input.dataset.columnType.toUpperCase();
        let value = input.value;

        if (value === '') { value = null; }
        else if (type === 'BIT') { value = (value === 'true'); }
        else if (type === 'INT' || type === 'BIGINT' || type === 'FLOAT') { value = Number(value); }
        data[name] = value;
    });

    try {
        // ++ ФИНАЛЬНОЕ ИСПРАВЛЕНИЕ ++
        // Мы БОЛЬШЕ НЕ ПРЕОБРАЗУЕМ объект в массив.
        // Строка `const dataForApi = ...` полностью удалена.

        await apiFetch(`/data`, {
            method: 'POST',
            body: {
                tableName: tableName,
                data: data // <-- Отправляем простой объект `data` напрямую!
            }
        });
        showStatus(insertStatusDiv, 'Данные успешно добавлены!', false);
        showDataForTable(tableName);
    } catch (error) {
        showStatus(insertStatusDiv, `Ошибка вставки данных: ${error.message}`, true);
    }
}


// --- Вспомогательные функции для UI ---

/** Добавляет новую строку для определения колонки в форме создания таблицы */
function addColumnRow() {
    const row = document.createElement('div');
    row.className = 'column-row';
    row.innerHTML = `
        <input type="text" placeholder="Имя колонки" class="column-name" />
        <select class="column-type">
            <option value="NVARCHAR(255)">Текст (до 255 симв.)</option>
            <option value="NVARCHAR(MAX)">Длинный текст</option>
            <option value="INT">Целое число (INT)</option>
            <option value="BIGINT">Большое целое число (BIGINT)</option>
            <option value="FLOAT">Дробное число (FLOAT)</option>
            <option value="BIT">Да/Нет (BIT)</option>
            <option value="DATETIME2">Дата и время</option>
        </select>
        <button class="btn-danger" onclick="this.parentElement.remove()">X</button>
    `;
    columnsContainer.appendChild(row);
}

/** Отображает существующие данные в виде таблицы */
function displayTableData(schema, data) {
    const container = document.getElementById('data-display');
    if (data.length === 0) {
        container.innerHTML = '<p>В этой таблице пока нет данных.</p>';
        return;
    }

    // schema теперь будет приходить не пустой!
    const headers = schema.map(col => `<th>${col.name}</th>`).join('');

    // ++ УПРОЩАЕМ ЛОГИКУ ЗДЕСЬ ++
    // data - это уже массив правильных объектов [ {smth: "a", ...}, ... ]
    // Нам больше не нужно его преобразовывать!
    const rows = data.map(rowAsObject => {

        const cells = schema.map(col => {
            // Ищем значение в объекте, игнорируя регистр
            let value = '(пусто)';
            const foundKey = Object.keys(rowAsObject).find(key => key.toLowerCase() === col.name.toLowerCase());

            if (foundKey !== undefined) {
                const foundValue = rowAsObject[foundKey];
                value = (foundValue === null) ? '(пусто)' : foundValue;
            }

            return `<td>${value}</td>`;
        }).join('');

        return `<tr>${cells}</tr>`;
    }).join('');

    container.innerHTML = `<table><thead><tr>${headers}</tr></thead><tbody>${rows}</tbody></table>`;
}

/** Создает и отображает форму для ввода новой записи */
function displayDataEntryForm(tableName, schema) {
    dataContainer.dataset.tableName = tableName; // Сохраняем имя таблицы
    const form = document.getElementById('data-entry-form');
    form.innerHTML = '';
    document.getElementById('current-table-title').textContent = `Таблица: ${tableName}`;

    schema.forEach(col => {
        const p = document.createElement('p');
        p.textContent = `${col.name} (${col.type}):`;
        let input;
        const type = col.type.toUpperCase();

        if (type === 'BIT') {
            input = document.createElement('select');
            input.innerHTML = `<option value="">(не указано)</option><option value="true">Да</option><option value="false">Нет</option>`;
        } else {
            input = document.createElement('input');
            if (type === 'INT' || type === 'BIGINT') input.type = 'number';
            else if (type === 'DATETIME2') input.type = 'datetime-local';
            else input.type = 'text';
        }

        input.dataset.columnName = col.name;
        input.dataset.columnType = col.type; // Сохраняем тип для парсинга
        p.appendChild(input);
        form.appendChild(p);
    });
}

/** Отображает сообщение о статусе операции */
function showStatus(element, message, isError = false) {
    element.textContent = message;
    element.className = isError ? 'status error' : 'status success';
    element.style.display = 'block';
}

// --- Универсальная функция для отправки запросов к API ---

/**
 * Отправляет запрос к WCF API и обрабатывает ответы.
 * @param {string} endpoint - Путь к ресурсу (например, '/tables').
 * @param {object} [options] - Настройки fetch (method, body, etc.).
 * @returns {Promise<any>} - Распарсенный JSON-ответ.
 */
async function apiFetch(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    console.log(url);
    const config = {
        method: options.method || 'GET',
        headers: {
            'Content-Type': 'application/json',
            ...options.headers,
        },
    };

    if (options.body) {
        config.body = JSON.stringify(options.body);
    }

    const response = await fetch(url, config);

    if (!response.ok) {
        // Если сервер вернул ошибку, пытаемся извлечь наше кастомное сообщение
        const errorData = await response.json();
        const errorMessage = errorData?.Message || `HTTP Error: ${response.status}`;
        throw new Error(errorMessage);
    }

    // Для POST-запросов без ответа (204 No Content) или других успешных статусов без тела
    if (response.status === 204 || response.headers.get("Content-Length") === "0") {
        return null;
    }

    return response.json();
}
