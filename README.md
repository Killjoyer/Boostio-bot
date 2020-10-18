# Функции

На текущем этапе разработки бот будет сообщать билды героев по запросу в чате. Далее возможны расширения, в первую очередь добавление мультимедийного функционала.

# Команды бота

- `echo <msg>` - пишет в чат `msg`
- `authors` - показывает создателей бота
- `build <hero>` - показывает билд на героя

# Задачи

- [ ] Билды героев Heroes of the storm в дискорде
    - [x] Базовое взаимодействие с дискордом
        - [x] Получение сообщений от дискорда
        - [x] Хранение состояний для связки канал+пользователь
        - [x] Обработка "echo", "authors"
        - [x] Обработка "build"
    - [ ] Загрузка талантов героев с icyVeins
    - [ ] Толерантность к ошибкам
