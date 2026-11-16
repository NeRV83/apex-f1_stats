# Apex — F1 Stats

Android-приложение для просмотра календаря и результатов гонок Формулы-1. Дипломный проект.

## О проекте

Приложение получает данные о сезонах, этапах, командах, пилотах и результатах гонок
через [Jolpica F1 API](https://api.jolpi.ca/) (Alpha и Ergast-совместимые эндпоинты). Данные
кэшируются локально в Room, чтобы календарь и результаты были доступны без подключения к сети.

Тема диплома: «Проектирование и разработка Android-приложения “Apex” для отображения календаря и
результатов гонок Формулы-1».

## Возможности

- Просмотр календаря гонок за выбранный сезон (2020–2026).
- Раскрывающиеся карточки гонок с отображением схемы трассы, флага страны и количества кругов.
- Экран детального просмотра результатов гонки с выделением подиума (топ-3), очков и статуса пилота.
- Кнопка «Поделиться» на экране результатов (системный Intent Android).
- Просмотр списка команд с их цветами и национальностями.
- Таблица лидеров пилотов (Standings) с позициями, очками и связью с командами.
- Pull-to-Refresh для принудительного обновления списка гонок.
- Полноценный офлайн-режим (данные из Room).
- Настройки (DataStore):
    - Тема (Системная / Светлая / Тёмная) — по умолчанию тёмная.
    - Язык (Системный / English / Русский).
    - Сезон по умолчанию.
- Локализация (en / ru) с переключением на лету.
- Нижняя панель навигации (Гонки, Команды, Пилоты, Настройки, О приложении).

## Стек

- **Язык:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Архитектура:** Single Activity, MVVM
- **DI:** Dagger Hilt
- **Сеть:** Retrofit + Gson, OkHttp
- **Локальное хранилище:** Room
- **Асинхронность:** Kotlin Coroutines, Flow
- **Сборка:** Gradle (Kotlin DSL), Version Catalog
- **Минимальная версия Android:** 8.0 (API 26)

## Требования

- Android Studio с поддержкой AGP 9.x
- JDK 17
- Android SDK 37 (compileSdk)

## Сборка и запуск

```bash
git clone https://github.com/NeRV83/ApexF1Stats.git
cd ApexF1Stats
./gradlew assembleDebug
```

Или откройте проект в Android Studio и запустите конфигурацию `app`.

## Текущая структура проекта

```
app/src/main/java/ru/nerv/apex/
├── ApexApplication.kt              # @HiltAndroidApp
├── MainActivity.kt                 # Single Activity, NavHost, BottomBar
├── data/
│   ├── local/
│   │   ├── ApexDatabase.kt         # Room database (races, results, teams, drivers)
│   │   ├── dao/RaceDao.kt          # CRUD для всех сущностей
│   │   └── entity/
│   │       ├── RaceEntity.kt
│   │       ├── ResultEntity.kt
│   │       ├── TeamEntity.kt
│   │       └── DriverEntity.kt
│   ├── remote/
│   │   ├── JolpicaApi.kt           # Retrofit (schedule, results, teams, drivers, sessions)
│   │   ├── UserAgentInterceptor.kt
│   │   └── dto/                    # DTO под ответы Jolpica (Alpha + Ergast)
│   ├── repository/
│   │   ├── RaceRepository.kt
│   │   └── RaceRepositoryImpl.kt   # сеть → маппинг → Room
│   └── settings/
│       └── SettingsRepository.kt   # DataStore
├── di/                             # Hilt-модули
│   ├── NetworkModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
└── ui/
    ├── about/
    │   └── AboutScreen.kt          # экран "О приложении"
    ├── drivers/
    │   ├── DriversScreen.kt        # таблица лидеров пилотов
    │   └── DriversViewModel.kt     # загрузка и кэширование пилотов
    ├── navigation/
    │   └── BottomNavItem.kt        # модель вкладок нижнего меню
    ├── races/
    │   ├── DateFormatting.kt       # форматирование даты гонки
    │   ├── RacesScreen.kt          # список гонок (карточки, Pull-to-Refresh)
    │   ├── RacesUiState.kt         # Loading / Success / Error
    │   ├── RacesViewModel.kt       # логика списка гонок и выбора года
    │   ├── RaceDetailScreen.kt     # таблица результатов гонки
    │   ├── RaceDetailViewModel.kt  # загрузка результатов
    │   └── TrackImageMapper.kt     # сопоставление трасс и схем
    ├── settings/
    │   ├── SettingsScreen.kt       # настройки (тема, язык, сезон)
    │   └── SettingsViewModel.kt    # DataStore и применение настроек
    ├── teams/
    │   ├── TeamsScreen.kt          # список команд
    │   └── TeamsViewModel.kt       # загрузка команд
    └── theme/
        ├── Color.kt                # палитра (F1-стиль)
        └── Theme.kt                # ApexTheme (светлая/тёмная)
```

По мере развития проекта будут внесены изменения в структуру.

## API

Используются Alpha-эндпоинты Jolpica: `https://api.jolpi.ca/f1/alpha/...`, а также
Ergast-совместимый
путь для таблицы лидеров.

Текущие эндпоинты:

- `GET /f1/alpha/schedules/{year}/` — календарь сезона.
- `GET /f1/alpha/results/{round_id}/R/` — результаты гонки.
- `GET /f1/alpha/core/teams/` — список команд.
- `GET /f1/alpha/core/sessions/{session_id}/` — данные сессии (для ленивой подгрузки
  scheduled_laps).
- `GET /ergast/f1/{year}/driverstandings/` — таблица лидеров пилотов (Ergast-совместимый формат).

Jolpica требует передавать кастомный заголовок `User-Agent`. В приложении это делает
`UserAgentInterceptor`.

## Как работает загрузка данных

1. RacesViewModel при смене года или Pull-to-Refresh вызывает RaceRepository.refreshRaces (year).
2. RaceRepositoryImpl запрашивает JolpicaApi.getSchedule (year), маппит ScheduleEvent → RaceEntity и
   пишет в Room.
3. RaceDao.observeRaces (year) возвращает Flow<List<RaceEntity>>, который UI собирает через
   collectAsStateWithLifecycle.
4. При раскрытии карточки гонки, если scheduledLaps равен null, ViewModel вызывает refreshRaceLaps
   (roundId, sessionId), который делает запрос к /core/sessions/ и сохраняет результат в Room. Это
   позволяет избежать N+1 запросов при загрузке списка.
5. Для пилотов используется Ergast-эндпоинт driverstandings. Цвета команд для карточек пилотов
   подтягиваются из уже загруженной таблицы teams (по названию команды)

При ошибке сети refresh* бросает исключение, ViewModel ловит его и передает в UI флаг
isNetworkError. observe* продолжает отдавать кэш из Room — это офлайн-режим.

## Настройки

Настройки хранятся в DataStore Preferences и доступны через вкладку «Настройки» в нижнем меню:

- Тема: Системная / Светлая / Тёмная.
- Язык: Системный / English / Русский.
- Сезон по умолчанию: выбор года (от 2020 до 2026), который будет открываться при запуске.

## Тесты

Есть unit-тесты на RaceRepositoryImpl (JUnit + kotlinx-coroutines-test):

- refreshRaces writes mapped entities to dao — успешный парсинг ScheduleResponse и запись в DAO.
- refreshRaces fails but dao retains cached data — при ошибке сети кэш не стирается.
- refreshResults writes mapped results to dao — успешный парсинг ResultsResponse и запись в DAO.
- refreshResults fails but dao retains cached results — при ошибке сети кэш результатов не
  стирается.

## Известные ограничения

- При смене языка Activity пересоздаётся (поведение AppCompatDelegate). На некоторых устройствах
  может быть заметно кратковременное мигание фона.
- Некоторые схемы трасс отсутствуют в res/drawable и отображаются как заглушка.
- Ergast-эндпоинт не отдает эмодзи-флаги, а брать флаг команды для пилота было бы некорректно,
  поэтому флаг для пилотов не отображается.
- Данные о длине трассы и количестве поворотов не предоставляются Jolpica API (показываются только
  круги).

## Лицензия

Проект создан в учебных целях. Данные предоставлены [Jolpica F1](https://api.jolpi.ca/).