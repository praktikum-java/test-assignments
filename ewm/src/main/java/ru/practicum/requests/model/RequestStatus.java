package ru.practicum.requests.model;

public enum RequestStatus {
    PENDING,                    // рассматриваемый
    CONFIRMED,                  // подтвержденный
    REJECTED,                    // отклоненный(для прохождения Postman тестов)
    CANCELED                    // отклоненный
}
