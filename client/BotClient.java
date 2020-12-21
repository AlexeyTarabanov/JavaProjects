package com.javarush.task.task30.task3008.client;

// бот автоматически будет отвечать на некоторые команды
public class BotClient extends Client{

    public static void main(String[] args) {
        BotClient bot = new BotClient();
        bot.run();
    }

    // создает и возвращает объект класса BotSocketThread
    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    // проверяем отправлен ли тескт из консоли
    @Override
    protected boolean shouldSendTextFromConsole() {
        // так как мы не хотим, чтобы бот отправлял текст введенный в консоль, устанавливаем значение false
        return false;
    }

    // генерируем новое имя бота
    @Override
    protected String getUserName() {
        return "date_bot_" + (int) (Math.random() * 100);
    }

    public class BotSocketThread extends SocketThread {

    }
}
