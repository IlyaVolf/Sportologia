# Sportologia

Проект в рамках ВКР (2022-2023)

## Проект неактуален!
Проект, представленный в этом репозитории, не завершен, и работы по завершению этого проекта в этом репозитории вестись не будут.

Тем не менее, этот репозиторий может быть полезен для ознакомления с функционалом приложения и используемыми технологиями.

На данный момент я переписываю проект с нуля (чисто, аккуратно и грамотно), и этот проект будет уже завершён в другом репозитории (как минимум до окончания работ он будет закрыт).

Немного о структуре нового проекта:

<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/236a9ed2-1c47-4840-9aa3-8c8cf155b190" width="580" height="380">


<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/9b8aacef-db08-40b2-bc52-8bed4af48ba4" width="580" height="124">


## Описание приложения

Социальная сеть для любителей спорта, позволяющая делиться своими спортивными достижениями с другими пользователями, анонсировать или находить спортивные мероприятия, находить готовые или создавать свои тренировочные программы.

Реализованный функционал первой итерации:
-  профили пользователей: спортсмены и спортивные организации;
-  текстовые публикации с фотографиями, мероприятия и услуги в виде тренировочных программ;
-  лента публикаций и предстоящих мероприятий состоящих в подписке пользователей;
-  поиск пользователей, тренировочных программ и мероприятий;
-  сохранение публикаций, тренировочных программ и мероприятий в избранное;
-  тёмная и светлая темы.

## Тех. стек
Kotlin, MVVM, Jetpack DataStore, Kotlin Coroutines, Kotlin Flow, LiveData, Hilt, Jetpack Navigation Component, Jetpack Paging 3, Google Cloud Firestore, Google Cloud Storage, Google Firebase Authentication, Yandex MapKit SDK, Glide.

## Демонстрация приложения

### Дизайн
[Дизайн в Figma](https://www.figma.com/file/QG7eM8nOX1fpbAiPGnrop1/Sportologia-Share)

### Скриншоты

-  Вход в учётную запись, регистрация

<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/736a5810-7111-45ae-9938-20501c197aaa" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/587f8881-898a-45c0-9bb3-f5fa2779054a" width="216" height="384">

-  Главная страница
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/791f1705-d196-4d6e-8bab-4c9a3f93fcee" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/cf1dd7b4-2096-475e-aec8-7ca4df6c0dfe" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/58f3f0cf-99a6-4ccf-85e6-2c3ad9f15815" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/985d2d9b-6828-4b40-9eac-8c49a9221c2c" width="216" height="384">

-  Страница пользователя
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/5f1fdf5e-c321-4165-bf35-7e64ecf41357" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/fe656788-7e2d-4aa9-a4f8-9d91e4de2cd6" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/65f8c513-7d53-4ead-9fdb-2dde2bef8309" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/5ff61b4c-d270-49d8-be24-78fa51217945" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/aedbcbd5-fc80-4111-964f-35b3a997c7a5" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/64bf5a53-8ade-4993-b135-58d3effbf3da" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/aa75c2c8-ad91-445c-9c4b-a2f50f430711" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/3a535ce5-5233-4a44-9387-d118770dfc8f" width="216" height="384">

-  Страница с услугами
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/00c07ead-25fd-4ef1-8199-37e4b20bceae" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/4f125da1-b091-4162-82bf-c58ff4924373" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/13e28446-cca9-44a0-8ba8-412ce248c0a9" width="216" height="384">

-   Страница поиска
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/7a5c47dc-9009-4d6b-99df-3f74dd71e24d" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/dd73c0de-29e3-4fb1-8736-2bfd51a2be11" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/5b34a7d1-bc0b-4a9f-a396-cf69da79bf5c" width="216" height="384">
<img src="https://github.com/IlyaVolf/Sportologia/assets/70796651/45567037-2f4c-4a31-b1ff-ab6c6c7744fe" width="216" height="384">

### Видео
-  Продолжительность - 1:15

![video_2023-08-16_21-39-25 (online-video-cutter com)](https://github.com/IlyaVolf/LimaApp/assets/70796651/1ef504e4-80e9-4417-8e28-754ba40b7aa8)

-  Продолжительность - 1:13

![video_2023-08-16_22-28-30 (online-video-cutter com)](https://github.com/IlyaVolf/LimaApp/assets/70796651/5a5a92f2-442d-42f3-a78d-7f581d359230)

