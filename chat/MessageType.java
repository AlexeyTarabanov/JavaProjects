package com.javarush.task.task30.task3008.chat;

/**
 * 1) NAME_REQUEST - запрос имени.
 * 2) USER_NAME - имя пользователя.
 * 3) NAME_ACCEPTED - имя принято.
 * 4) TEXT - текстовое сообщение.
 * 5) USER_ADDED - пользователь добавлен.
 * 6) USER_REMOVED - пользователь удален.
 * */

// отвечает за тип сообщений пересылаемых между клиентом и сервером
public enum MessageType {
    NAME_REQUEST, USER_NAME, NAME_ACCEPTED, TEXT, USER_ADDED, USER_REMOVED
}
