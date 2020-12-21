package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.Connection;
import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;

/**
 * - Клиент, в начале своей работы, должен запросить у пользователя адрес и порт сервера,
 * подсоединиться к указанному адресу, получить запрос имени от сервера, спросить имя у пользователя,
 * отправить имя пользователя серверу, дождаться принятия имени сервером.
 * - После этого клиент может обмениваться текстовыми сообщениями с сервером.
 * - Обмен сообщениями будет происходить в двух параллельно работающих потоках.
 * - Один будет заниматься чтением из консоли и отправкой прочитанного серверу,
 * а второй поток будет получать данные от сервера и выводить их в консоль.
 * */

public class Client {

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    // protected позволит обращаться к этому полю из класса потомков, но запретит обращение из других классов вне пакета.
    protected Connection connection;
    // volatile позволит гарантировать что каждый поток, использующий поле clientConnected, работает с актуальным, а не кэшированным его значением
    private volatile boolean clientConnected = false;

    // зпрашивает ввод адреса сервера у пользователя и возвращает введенное значение
    protected String getServerAddress() {
        return ConsoleHelper.readString();
    }

    // запрашивает ввод порта сервера и возвращает его.
    protected int getServerPort() {
        return ConsoleHelper.readInt();
    }

    // запрашивает и возвращает имя пользователя
    protected String getUserName() {
        return ConsoleHelper.readString();
    }

    // проверяем отправлен ли текст из консоли
    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    // создает и возвращает новый объект класса SocketThread
    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    // создает новое текстовое сообщение, используя переданный текст и отправляет его серверу
    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Вы ввели некорректное значение");
            clientConnected = false;
        }
    }

    public void run() {

        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();

        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            ConsoleHelper.writeMessage("Ошибка ожидания");
        }

        if (clientConnected) {
            ConsoleHelper.writeMessage("Соединение установлено.\n" + "Для выхода наберите команду 'exit'.");
            while (clientConnected) {
                String string = ConsoleHelper.readString();
                if (string.equals("exit"))
                    break;
                if (shouldSendTextFromConsole()) {
                    sendTextMessage(string);
                }
            }
        } else {
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        }
    }

    public class SocketThread extends Thread {

        // выводит текст message в консоль
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        // выводит в консоль информацию о том, что участник с именем userName присоединился к чату
        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage("Пользовтель " + userName + " присоединился к чату");
        }

        // выводит в консоль, что участник с именем userName покинул чат
        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage("Пользовтель " + userName + " покинул чат");
        }

        // устанавливает значение полю clientConnected внешнего объекта Client в соответствии с переданным параметром
        // оповещает (пробуждает ожидающий) основной поток класса Client
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notifyAll();
            }
        }
    }
}
