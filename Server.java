package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 1. Создал классы ConsoleHelper, Connection, Message, MessageType, Server.
 * В последнем вызвал метод main
 * 2. Реализлвал класс ConsoleHelper.
 * 3. Добавил значения в enum MessageType.
 * 4. Реализовал класс Message
 * 5. Реализовал класс Connection.
 */

/*
* Сервер должен поддерживать множество соединений с разными клиентами одновременно.
Это можно реализовать с помощью алгоритма:
- Сервер создает серверное сокетное соединение.
- В цикле ожидает, когда какой-то клиент подключится к сокету.
- Создает новый поток обработчик Handler, в котором будет происходить обмен сообщениями с клиентом.
- Ожидает следующее соединение.
* */

// основной класс сервера
public class Server {
    public static void main(String[] args) {

        // запрашиваем порт сервера
        int port = ConsoleHelper.readInt();
        try // создем серверный сокет на основе полученного порта
            (ServerSocket serverSocket = new ServerSocket(port))
        {
            ConsoleHelper.writeMessage("Server is running...");
            while (true) {
                // слушаем и принимаем входящие сокетные соединения
                Socket client = serverSocket.accept();
                // создаем и запускаем новый поток Handler
                new Handler(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }
    }
}
