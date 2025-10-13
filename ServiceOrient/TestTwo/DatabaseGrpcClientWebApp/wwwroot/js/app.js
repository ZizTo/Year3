const API_BASE_URL = '/api';

const tablesList = document.getElementById('tables-list');
const createStatusDiv = document.getElementById('create-status');
const insertStatusDiv = document.getElementById('insert-status');
const dataContainer = document.getElementById('data-container');
const columnsContainer = document.getElementById('columns-container');

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('add-column-btn').addEventListener('click', addColumnRow);
    document.getElementById('create-table-btn').addEventListener('click', createTable);
    document.getElementById('insert-data-btn').addEventListener('click', insertData);

    loadTables();
    addColumnRow();
});

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

async function showDataForTable(tableName) {
    try {
        const [schema, data] = await Promise.all([
            apiFetch(`/tables/${tableName}/schema`),
            apiFetch(`/tables/${tableName}/data`)
        ]);

        displayTableData(schema, data);
        displayDataEntryForm(tableName, schema);

        dataContainer.style.display = 'block';
        dataContainer.scrollIntoView({ behavior: 'smooth' });

    } catch (error) {
        alert(`Не удалось загрузить данные для таблицы ${tableName}: ${error.message}`);
    }
}

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
        loadTables();
    } catch (error) {
        showStatus(createStatusDiv, `Ошибка создания таблицы: ${error.message}`, true);
    }
}

async function insertData() {
    const tableName = dataContainer.dataset.tableName;
    if (!tableName) {
        showStatus(insertStatusDiv, 'Имя таблицы не определено.', true);
        return;
    }

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
        await apiFetch(`/data`, {
            method: 'POST',
            body: {
                tableName: tableName,
                data: data
            }
        });
        showStatus(insertStatusDiv, 'Данные успешно добавлены!', false);
        showDataForTable(tableName);
    } catch (error) {
        showStatus(insertStatusDiv, `Ошибка вставки данных: ${error.message}`, true);
    }
}


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

function displayTableData(schema, data) {
    const container = document.getElementById('data-display');
    if (data.length === 0) {
        container.innerHTML = '<p>В этой таблице пока нет данных.</p>';
        return;
    }

   
    const headers = schema.map(col => `<th>${col.name}</th>`).join('');
    const rows = data.map(rowAsObject => {
        const cells = schema.map(col => {
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

function displayDataEntryForm(tableName, schema) {
    dataContainer.dataset.tableName = tableName;
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
        input.dataset.columnType = col.type;
        p.appendChild(input);
        form.appendChild(p);
    });
}

function showStatus(element, message, isError = false) {
    element.textContent = message;
    element.className = isError ? 'status error' : 'status success';
    element.style.display = 'block';
}

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
        const errorData = await response.json();
        const errorMessage = errorData?.Message || `HTTP Error: ${response.status}`;
        throw new Error(errorMessage);
    }

    if (response.status === 204 || response.headers.get("Content-Length") === "0") {
        return null;
    }

    return response.json();
}
