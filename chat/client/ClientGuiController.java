package com.javarush.task.task30.task3008.chat.client;

public class ClientGuiController extends Client {

    public static void main(String[] args) {
        ClientGuiController controller = new ClientGuiController();
        controller.run();
    }

    // поле, отвечающее за модель ClientGuiModel
    private ClientGuiModel model = new ClientGuiModel();
    // поле, отвечающее за представление ClientGuiView
    private ClientGuiView view = new ClientGuiView(this);

    // создает и возвращает объект типа GuiSocketThread
    @Override
    protected SocketThread getSocketThread() {
        return new GuiSocketThread();
    }

    // получает объект SocketThread через метод getSocketThread() и вызывает у него метод run()
    @Override
    public void run() {
        getSocketThread().run();
    }

    @Override
    protected String getServerAddress() {
        return view.getServerAddress();
    }

    @Override
    protected int getServerPort() {
        return view.getServerPort();
    }

    @Override
    protected String getUserName() {
        return view.getUserName();
    }

    public ClientGuiModel getModel() {
        return model;
    }

    public class GuiSocketThread extends SocketThread {

        // устанавливает новое сообщение у модели и вызывает обновление вывода сообщений у представления
        @Override
        protected void processIncomingMessage(String message) {
            //super.processIncomingMessage(message);
            model.setNewMessage(message);
            view.refreshMessages();
        }

        // добавляет нового пользователя в модель и вызывает обновление вывода пользователей у отображения
        @Override
        protected void informAboutAddingNewUser(String userName) {
            //super.informAboutAddingNewUser(userName);
            model.addUser(userName);
            view.refreshUsers();
        }

        // удаляет пользователя из модели и вызывает обновление вывода пользователей у отображения
        @Override
        protected void informAboutDeletingNewUser(String userName) {
            //super.informAboutDeletingNewUser(userName);
            model.deleteUser(userName);
            view.refreshUsers();
        }

        // вызывает аналогичный метод у представления
        @Override
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            view.notifyConnectionStatusChanged(clientConnected);
        }
    }
}
