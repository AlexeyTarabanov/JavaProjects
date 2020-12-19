package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 1. Создал классы ConsoleHelper, Connection, Message, MessageType, Server.
 *    - в последнем вызвал метод main
 * 2. В класс ConsoleHelper добавил:
 *    - поле reader и сразу инициализировал его
 *    - метод writeMessage(String message), который выводит сообщение message в консоль.
 *      реализовал его.
 *    - методы int readInt() / String readString(), которые возвращают число / строку, считанные с консоли
 * 3. Добавил значения в enum MessageType.
 * 4. В класс Message добавил:
 *    - добавил поддержку Serializable
 *    - поля MessageType type, String data, геттеры для них и контструкторы.
 * 5. В класс Connection добавил:
 *    - поля Socket socket, ObjectOutputStream out, ObjectInputStream in и конструктор
 *    - метод send(Message message), который будет отправлять сообщения (сериализовать)
 *    - метод receive(), который читает сообщение (десериализовывает)
 *    - метод SocketAddress getRemoteSocketAddress(), который возвращает удаленный адрес сокетного соединения
 *    - метод close(), который закрывает все ресурсы класса
 * 6. В класс Server добавил:
 *    - статический вложенный класс Handler, унаследовал его от Thread
 *      В класс Handler добавил:
 *        - поле Socket socket и конструктор
 *    в методе main:
 *    - считываю с клавиатуры порт сервера
 *    - создал серверный сокет на основе полученного порта
 *    - слушаем и принимаем входящие сокетные соединения
 *    - создаю и сразу запускаю новый поток Handler
 * 7. В класс Server добавил:
 *    - поле connectionMap, где ключом будет имя клиента, а значением - соединение с ним
 *    - метод sendBroadcastMessage, который должен отправлять сообщение всем соединениям из connectionMap
 */


// основной класс сервера
public class Server {

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message) {
        try {
            for (Connection connection : connectionMap.values()) {
                connection.send(message);
            }
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Сообщение не отправлено");
        }
    }

    public static void main(String[] args) {

        int port = ConsoleHelper.readInt();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("Server is running...");
            while (true) {
                Socket client = serverSocket.accept();
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
