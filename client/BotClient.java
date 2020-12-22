package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        // выводит текст message в консоль
        @Override
        protected void processIncomingMessage(String message) {
            // выводим в консоль текст полученного сообщения message
            ConsoleHelper.writeMessage(message);
            // проверяем, что message не пустая строка и что содержит ":"
            if (!message.isEmpty() && message.contains(":")) {
                // получаем имя отправителя
                String name = message.split(":")[0];
                // текст сообщения
                String textMessage = message.substring(message.indexOf(":") + 2);
                // отправляем ответ в зависимости от текста принятого сообщения
                SimpleDateFormat dateFormat;
                switch (textMessage) {
                    case "дата":
                        dateFormat = new SimpleDateFormat("d.MM.YYYY");
                        break;
                    case "день":
                        dateFormat = new SimpleDateFormat("d");
                        break;
                    case "месяц":
                        dateFormat = new SimpleDateFormat("MMMM");
                        break;
                    case "год":
                        dateFormat = new SimpleDateFormat("YYYY");
                        break;
                    case "время":
                        dateFormat = new SimpleDateFormat("H:mm:ss");
                        break;
                    case "час":
                        dateFormat = new SimpleDateFormat("H");
                        break;
                    case "минуты":
                        dateFormat = new SimpleDateFormat("m");
                        break;
                    case "секунды":
                        dateFormat = new SimpleDateFormat("s");
                        break;
                    default:
                        dateFormat = null;
                }
                if (dateFormat != null) {
                    // для получения текущей даты используем класс Calendar и метод getTime()
                    //sendTextMessage("Информация для " + name + ": " + dateFormat.format(Calendar.getInstance().getTime()));
                    sendTextMessage(String.format("Информация для %s: %s", name, dateFormat.format(Calendar.getInstance().getTime())));
                }
            }
        }
    }
}

