# Apex — F1 Stats

Android-приложение для просмотра календаря и результатов гонок Формулы-1. Дипломный проект.

## О проекте

Приложение получает данные о сезонах, этапах и результатах гонок через Jolpica F1 API
(https://api.jolpi.ca/). Данные кэшируются локально, чтобы календарь и результаты были доступны без
подключения к сети.

Тема диплома: «Проектирование и разработка Android-приложения “Apex” для отображения календаря и
результатов гонок Формулы-1».

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
├── ApexApplication.kt
├── MainActivity.kt
├── data/
│   ├── local/
│   │   ├── ApexDatabase.kt
│   │   ├── dao/RaceDao.kt
│   │   └── entity/
│   │       ├── RaceEntity.kt
│   │       └── ResultEntity.kt
│   ├── remote/
│   │   ├── JolpicaApi.kt
│   │   ├── UserAgentInterceptor.kt
│   │   └── dto/
│   │       ├── ScheduleDto.kt
│   │       └── ResultsDto.kt
│   ├── repository/
│   │   ├── RaceRepository.kt
│   │   └── RaceRepositoryImpl.kt
│   └── settings/
│       └── SettingsRepository.kt
├── di/
│   ├── NetworkModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
└── ui/
    ├── races/
    │   ├── RacesUiState.kt
    │   ├── RacesViewModel.kt
    │   ├── RacesScreen.kt
    │   ├── RaceDetailViewModel.kt
    │   └── RaceDetailScreen.kt
    ├── settings/
    │   ├── SettingsViewModel.kt
    │   └── SettingsScreen.kt
    └── theme/
        ├── Color.kt
        └── Theme.kt
```

По мере развития проекта будут внесены изменения в структуру.

## API

Используется Alpha-версия Jolpica: `https://api.jolpi.ca/f1/alpha/...`.

Текущие эндпоинты:

- `GET /f1/alpha/schedules/{year}/` — календарь сезона с трассами и сессиями.
- `GET /f1/alpha/results/{round_id}/{session_filter}/` — результаты гонки (по умолчанию
  session_filter = R).

Jolpica требует передавать кастомный заголовок `User-Agent`. В приложении это делает
`UserAgentInterceptor`.

## Как работает загрузка данных

1. RacesViewModel при смене года или Pull-to-Refresh вызывает RaceRepository.refreshRaces (year).
2. RaceRepositoryImpl запрашивает JolpicaApi.getSchedule (year).
3. Полученные ScheduleEvent маппятся в RaceEntity и записываются в Room через RaceDao.insertRaces.
4. RaceDao.observeRaces (year) возвращает Flow<List<RaceEntity>>, который UI собирает через
   collectAsStateWithLifecycle.

При тапе по карточке открывается RaceDetailScreen, который через RaceDetailViewModel запрашивает
refreshResults (roundId), маппит ResultItem → ResultEntity и отображает таблицу из observeResults
(roundId).

При ошибке сети refresh* бросает исключение, ViewModel ловит его и ставит флаг isOffline (или Error
в RaceDetailScreen). observe* продолжает отдавать кэш из Room — это и есть офлайн-режим. Если кэш
пуст, UI показывает сообщение «Нет сети и нет сохранённых данных».

## Настройки

Настройки хранятся в DataStore Preferences и доступны через иконку шестеренки на главном экране:

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

## Лицензия

Проект создан в учебных целях. Данные предоставлены [Jolpica F1](https://api.jolpi.ca/).