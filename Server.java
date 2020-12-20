package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
 * - создал и реализовал String serverHandshake(Connection connection), это этап рукопожатия (знакомства сервера с клиентом)
 * Метод в качестве параметра принимает соединение connection, а возвращает имя нового клиента.
 * 9. В классе Handler:
 * - создал и реализовал notifyUsers(Connection connection, String userName)
 * Метод будет отправлять клиенту (новому участнику) информации об остальных клиентах (участниках) чата
 * 10. В классе Handler:
 * - создал и реализовал serverMainLoop(Connection connection, String userName)
 * Метод проверяет тип сообщения и отправляет отформатированное сообщение всем участникам чата
 * 11. В классе Handler:
 * - реализовал метод run()
 * 12. Создал пакет client, где будут храниться все классы, отвечающие за реализацию клиентов
 * - в пакете, создал класс Client
 *   в классе Client:
 * - создал класс SocketThread унаследовал его от Thread
 * Он будет отвечать за поток, устанавливающий сокетное соединение и читающий сообщения сервера.
 * - создал поле типа Connection
 * - добавил поле boolean clientConnected, сразу проинициализировал его значением false
 * если клиент подсоединен к серверу поле вернет значение true, в противном случае false
 * 13. В классе Client:
 *  - создал и реализовал getServerAddress(),
 *  Метод зпрашивает ввод адреса сервера у пользователя и возвращает введенное значение
 *  - создал и реализовал getServerPort(),
 *  Метод запрашивает ввод порта сервера и возвращает его.
 *  - создал и реализовал getUserName(),
 *  Метод запрашивает и возвращает имя пользователя
 *  - создал и реализовал shouldSendTextFromConsole(),
 *  Метод проверяет отправлен ли текст из консоли
 *  - создал и реализовал getSocketThread(),
 *  Метод создает и возвращает новый объект класса SocketThread
 *  - создал и реализовал sendTextMessage(String text),
 *  Метод создает новое текстовое сообщение, используя переданный текст и отправляет его серверу
 * 14.
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

    // реализовывает протокол общения с клиентом
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
            for (String name : connectionMap.keySet()) {
                if (!userName.equals(name)) {
                    connection.send(new Message(MessageType.USER_ADDED, name));
                }
            }
        }

        // проверяет тип сообщения и отправляет отформатированное сообщение всем участникам чата
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + message.getData()));
                } else {
                    ConsoleHelper.writeMessage("Ошибка! Сообщение не является текстом");
                }
            }
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage("Установлено новое соединение с удаленным адресом " + socket.getRemoteSocketAddress());

            try (Connection connection = new Connection(socket)) {
                String userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);

                if (userName != null) {
                    connectionMap.remove(userName);
                    sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
                }
                ConsoleHelper.writeMessage("Соединение с удаленным адресом закрыто");
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом");
            }
        }
    }
}
