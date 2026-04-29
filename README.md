# XMESS
Minecraft Paper Server Plugin 1.18.2 launcher https://github.com/xtls/xray-core | Плагин для сервера майнкрафт paper 1.18.2 запускающий https://github.com/xtls/xray-core 

---

## 1. Установка плагина

1. Скачай файл плагина `xmess-1.0.0.jar` (готовый JAR, собранный вручную).  
2. Перенеси его в папку `plugins/` твоего сервера Minecraft:

   ```text
   server/
   ├── plugins/
   │   └── xmess-1.0.0.jar
   ├── ...
   ```

3. Запусти сервер.

---

## 2. Первый запуск и папка `xmess`

После первого запуска плагин создаст:

```text
plugins/xmess/
├── config.yml
└── main.db      ← копия ядра (если была в JAR)
```

Конфиг `config.yml` будет выглядеть так:

```yaml
settings:
  logs: true
  db: "main.db"
  json: "ваш_base64_здесь"
```

---

## 3. Назначение параметров в `config.yml`

- `logs: true|false`  
  - Если `true` — логи Xray‑ядра видны в консоли сервера с префиксом `[xmess]`.  
- `db: "main.db"`  
  - Имя файла Xray‑ядра, который лежит в `plugins/xmess/` (или `libs/` в исходниках).  
- `json: "..."`  
  - Base64‑конфиг Xray (VLESS, порт 25566 и т.п.).  
  - Файл `socket-config.json` будет создан автоматически и затем удалён.

---

## 4. Как работает плагин при включении

При старте сервера:

1. Плагин читает `config.yml`.  
2. Если `json` не пустой:
   - создаёт `plugins/xmess/socket-config.json` из Base64,  
   - передаёт его ядру `vault.db`.  
3. Запускает `vault.db` с этим конфигом и выводит в консоль:

   ```text
   [xmess] xMess socket started
   [xmess] [socket] ... (логи Xray)
   ```

4. Через 1 секунду (`60 ticks`) файл `socket-config.json` удаляется.

---

## 5. Как сменить имя ядра или конфиг

- Чтобы использовать другое ядро:

  В `plugins/xmess/config.yml`:

  ```yaml
  settings:
    db: "xray.db"
  ```

  И помести файл `xray.db` в `plugins/xmess/` (или собрать его в JAR как ресурс).

- Чтобы изменить Base64‑конфиг — просто обнови `json` в `config.yml` и перезапусти сервер.

---

## 6. Логирование

Логи плагина всегда имеют вид:

```text
[xmess] ...
```


(P.S.> Если хотите собрать плагин сами, то не забудьте скачать последюю всерсию xray-core https://github.com/XTLS/Xray-core/releases под ваш сервер и переимионвать её в main.db и положите в ./src/main/resources) 
