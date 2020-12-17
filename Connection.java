package com.javarush.task.task30.task3008;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Клиент и сервер будут общаться через сокетное соединение.
 * Одна сторона будет записывать данные в сокет, а другая читать.
 * Их общение представляет собой обмен сообщениями Message.
 * Класс Connection будет выполнять роль обертки над классом java.net.Socket,
 * которая должна будет уметь сериализовать и десериализовать объекты типа Message в сокет.
 * Методы этого класса должны быть готовы к вызову из разных потоков.
 * */

// класс соединения между клиентом и сервером
public class Connection implements Closeable {

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    // метод записывает (сериализоввывет) сообщение message в ObjectOutputStream.
    public void send(Message message) throws IOException {
        synchronized (out) {
            out.writeObject(message);
        }
    }

    // читает (десериализовывает) данные из ObjectInputStream
    public Message receive() throws IOException, ClassNotFoundException {
        Message message;
        synchronized (in) {
            message = (Message) in.readObject();
        }
        return message;
    }

    // возвращает удаленный адрес сокетного соединения
    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    // закрывает все ресурсы класса
    @Override
    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
