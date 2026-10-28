# Apex — F1 Stats

Android-приложение для просмотра календаря и результатов гонок Формулы-1. Дипломный проект.

## О проекте

Приложение получает данные о сезонах, этапах и результатах гонок через Jolpica F1 API (https://api.jolpi.ca/). Данные кэшируются локально, чтобы календарь и результаты были доступны без подключения к сети.

Тема диплома: «Проектирование и разработка Android-приложения “Apex” для отображения календаря и результатов гонок Формулы-1».

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
├── ApexApplication.kt          # @HiltAndroidApp
├── MainActivity.kt             # Single Activity, точка входа
├── data/
│   ├── remote/
│   │   ├── JolpicaApi.kt       # Retrofit-интерфейс
│   │   ├── UserAgentInterceptor.kt
│   │   └── dto/                # DTO под ответы Jolpica Alpha API
│   └── ...
└── di/
    └── NetworkModule.kt        # OkHttpClient, Retrofit, JolpicaApi
```

По мере развития проекта будут внесены изменения в структуру.

## API

Используется Alpha-версия Jolpica: `https://api.jolpi.ca/f1/alpha/...`.

Текущий эндпоинт:

- `GET /f1/alpha/schedules/{year}/` — календарь сезона с трассами и сессиями.

Jolpica требует передавать кастомный заголовок `User-Agent`. В приложении это делает `UserAgentInterceptor`.

## Лицензия

Проект создан в учебных целях. Данные предоставлены [Jolpica F1](https://api.jolpi.ca/).