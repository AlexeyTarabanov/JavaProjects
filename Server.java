package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 1. Создал классы ConsoleHelper, Connection, Message, MessageType, Server.
 * - в последнем вызвал метод main
 * 2. В класс ConsoleHelper добавил:
 * - поле reader и сразу инициализировал его
 * - метод writeMessage(String message), который выводит сообщение message в консоль.
 * реализовал его.
 * - методы int readInt() / String readString(), которые возвращают число / строку, считанные с консоли
 * 3. Добавил значения в enum MessageType.
 * 4. В класс Message добавил:
 * - поддержку Serializable
 * - поля MessageType type, String data, геттеры для них и контструкторы.
 * 5. В класс Connection добавил:
 * - поля Socket socket, ObjectOutputStream out, ObjectInputStream in и конструктор
 * - метод send(Message message), который будет отправлять сообщения (сериализовать)
 * - метод receive(), который читает сообщение (десериализовывает)
 * - метод SocketAddress getRemoteSocketAddress(), который возвращает удаленный адрес сокетного соединения
 * - метод close(), который закрывает все ресурсы класса
 * 6. В класс Server добавил:
 * - статический вложенный класс Handler, унаследовал его от Thread
 * В класс Handler добавил:
 * - поле Socket socket и конструктор
 * в методе main:
 * - считываю с клавиатуры порт сервера
 * - создал серверный сокет на основе полученного порта
 * - слушаем и принимаем входящие сокетные соединения
 * - создаю и сразу запускаю новый поток Handler
 * 7. В класс Server добавил:
 * - поле connectionMap, где ключом будет имя клиента, а значением - соединение с ним
 * - метод sendBroadcastMessage, который должен отправлять сообщение всем соединениям из connectionMap
 * 8. В классе Handler:
 * - создал и реализовал String serverHandshake(Connection connection)
 * Метод в качестве параметра принимает соединение connection, а возвращает имя нового клиента.
 * 9. В классе Handler:
 * - создал и реализовал notifyUsers(Connection connection, String userName)
 * Метод будет отправлять клиенту (новому участнику) информации об остальных клиентах (участниках) чата
 * 10. В классе Handler:
 * - создал и реализовал serverMainLoop(Connection connection, String userName)
 * Метод проверяет тип сообщения и отправляет отформатированное сообщение всем участникам чата
 * 11.
 *
 */


// основной класс сервера
public class Server {

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    // отправляет сообщение всем участникам чата
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

        // возвращает имя клиента
        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {

            connection.send(new Message(MessageType.NAME_REQUEST, "Введите имя пользователя"));
            Message answer = connection.receive();

            if (answer.getType() != MessageType.USER_NAME || answer.getData().isEmpty()
                    || connectionMap.containsKey(answer.getData()))
                return serverHandshake(connection);

            connectionMap.put(answer.getData(), connection);
            connection.send(new Message(MessageType.NAME_ACCEPTED, "Имя принято"));

            return answer.getData();
        }

        // отправляет новому участнику информации об остальных клиентах (участниках) чата
        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (String name: connectionMap.keySet()) {
                if (!userName.equals(name)) {
                    connection.send(new Message(MessageType.USER_ADDED, name));
                }
            }
        }

        // проверяет тип сообщения и отправляет отформатированное сообщение всем участникам чата
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                // получаем сообщение
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    // отправляем сообщение всем участникам чата
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + message.getData()));
                } else {
                    ConsoleHelper.writeMessage("Ошибка! Сообщение не является текстом");
                }
            }
        }
    }
}
