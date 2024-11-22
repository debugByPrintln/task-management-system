# Task management system

## Оглавление

- [Автор](#автор)
- [Описание](#описание)
- [Технологии](#технологии)
- [Запуск](#запуск)
- [API-документация](#API-документация)
- [Авторизация](#авторизация)
- [Ролевая система](#ролевая-система)
- [Примеры запросов](#примеры-запросов)
- [Тестирование](#тестирование)

## Автор
Мельников Никита Сергеевич
- Выполнение тестового задания: [https://docs.google.com/document/d/1wW70asO6XmOLXWlnTDraFwZLavGuoIAHvppBAD6Pke4/edit?tab=t.0](https://docs.google.com/document/d/1AEn47oothRkYktTSu40Yo7gBlCiuV1ezvV0aSurHOCQ/edit?tab=t.0)

## Описание
Task Management System — это RESTful API, разработанное на Java с использованием Spring Boot, для управления задачами. Система позволяет создавать, редактировать, удалять и просматривать задачи, а также управлять пользователями и комментариями к задачам.

## Технологии
- **Java 17**
- **Spring Boot 3.3.5**
- **Spring Security**
- **JWT**
- **PostgreSQL**
- **Maven**
- **Docker**
- **Swagger**

## Запуск
Приложение разворачивается на порте 8080.

Для запуска приложения используйте комманды:
```
mvn clean install

docker-compose up --build
```

## API-документация
API-документация доступна через Swagger UI:

Swagger UI: http://localhost:8080/swagger-ui.html

OpenAPI JSON: http://localhost:8080/v3/api-docs

## Авторизация
Для доступа к API требуется аутентификация с использованием JWT токена. Вы можете получить токен, выполнив POST запрос на /auth/signin с email и паролем или на auth/signup с email, паролем и ролью нового пользователя.

Пример запроса на auth/signin
```
{
  "email": "example@example.com",
  "password": "string"
}
```

Пример запроса на auth/signup
```
{
  "email": "example@example.com",
  "password": "string",
  "role": "ROLE_USER"
}
```
Пример ответа:
```
{
    "token": "your_jwt_token"
}
```

Используйте полученный токен в заголовке Authorization для доступа к защищенным ресурсам:
```
Authorization: Bearer your_jwt_token
```

Удобнее всего тестировать приложение через Postman. Там можно удобно настроить авторизацию: во вкладке Authorization выбрать Bearer Token и вставить в поле полученный token.

По умолчанию, в приложении изначально создаются тестовые пользователи - admin@example.com с паролем admin и ролью ROLE_ADMIN и user@example.com с паролем user и ролью ROLE_USER.

## Ролевая система
Система поддерживает две роли:
- ADMIN: Полный доступ ко всем ресурсам.
- USER: Доступ к собственным задачам и комментариям.

## Примеры запросов
Создание задачи:
POST api/tasks
```
{
  "title": "New Task",
  "description": "Task description",
  "status": "PENDING",
  "priority": "MEDIUM",
  "authorId": 1,
  "assigneeId": 2
}
```

Получение всех задач:
GET /api/tasks

Обновление задачи:
PUT /api/tasks/{id}
```
{
  "title": "Updated Task",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "authorId": 1,
  "assigneeId": 2
}
```

Удаление задачи:
DELETE /api/tasks/{id}

## Тестирование
Для запуска тестов используйте команду:
```
mvn test
```
