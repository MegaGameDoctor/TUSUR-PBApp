# TUSUR-PBApp

## Описание

Мобильное приложение разработано под платформу Android на языке Java. Пользовательский интерфейс приложения состоит из
одной игровой сцены. На ней изначально пользователю будет предложено авторизоваться, чтобы начать сохранять свою
статистику. Для этого нужно будет просто ввести предпочитаемый игровой ник. На игровой сцене игрока встретит полотно
определённого размера. Ниже полотна будет находиться список доступных цветов, а сверху - таймер до следующего
закрашивания. Пользователь выбирает цвет, нажимает на пиксель и перекрашивает его - это изменение отображается на всех
активных устройствах, которые подключены через приложение к этому серверу. Также в приложении есть небольшой онлайн чат.
Для организации соединения с сервером задействована библиотека Netty.

## Запуск

Чтобы подготовить среду разработки с этим приложением - достаточно копировать все файлы и открыть их как проект в
IntelliJ IDEA. Далее нужно будет дождаться, пока фреймворк Gradle завершит установку требуемых библиотек и утилит, а
затем запустить процесс app, который откроет приложение на установленном эмуляторе.

## Краткая демонстрация

![Демонстрация](https://github.com/user-attachments/assets/44d93746-31f3-4324-93ee-05fb5a72cda0)
