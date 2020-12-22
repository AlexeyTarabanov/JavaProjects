package com.javarush.task.task30.task3008.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClientGuiModel {

    // в нем будет храниться список всех участников чата
    private final Set<String> allUserNames = new HashSet<>();
    // будет храниться новое сообщение, которое получил клиент
    private String newMessage;

    public Set<String> getAllUserNames() {
        // запретили модифицировать возвращенное множество
        return Collections.unmodifiableSet(allUserNames);
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    // добавляет имя участника во множество, хранящее всех участников
    public void addUser(String newUserName) {
        allUserNames.add(newUserName);
    }

    // удаляет имя участника из множества
    public void deleteUser(String userName) {
        allUserNames.remove(userName);
    }
}
