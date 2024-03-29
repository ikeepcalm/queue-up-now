package dev.ua.ikeepcalm.queueupnow.telegram.modules.impl.queues.utils;

import dev.ua.ikeepcalm.queueupnow.database.entities.queue.MixedQueue;
import dev.ua.ikeepcalm.queueupnow.database.entities.queue.SimpleQueue;
import dev.ua.ikeepcalm.queueupnow.database.entities.queue.SimpleUser;
import dev.ua.ikeepcalm.queueupnow.telegram.wrappers.TextMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class QueueMarkupUtil {

    public static InlineKeyboardMarkup createMarkup(SimpleQueue simpleQueue) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        ArrayList<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        ArrayList<InlineKeyboardButton> firstRow = new ArrayList<>();
        ArrayList<InlineKeyboardButton> secondRow = new ArrayList<>();

        InlineKeyboardButton queueUp = new InlineKeyboardButton();
        queueUp.setText("Join \uD83D\uDD30");
        queueUp.setCallbackData(simpleQueue.getId() + "-simple-join");

        InlineKeyboardButton exit = new InlineKeyboardButton();
        exit.setText("Leave \ud83d\udd04");
        exit.setCallbackData(simpleQueue.getId() + "-simple-exit");

        InlineKeyboardButton delete = new InlineKeyboardButton();
        delete.setText("Delete ❌");
        delete.setCallbackData(simpleQueue.getId() + "-simple-delete");

        InlineKeyboardButton notify = new InlineKeyboardButton();
        notify.setText("Notify ⚠");
        notify.setCallbackData(simpleQueue.getId() + "-simple-notify");
        firstRow.add(queueUp);
        firstRow.add(exit);

        secondRow.add(delete);
        secondRow.add(notify);

        keyboard.add(firstRow);
        keyboard.add(secondRow);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup createMarkup(MixedQueue mixedQueue) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        ArrayList<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        ArrayList<InlineKeyboardButton> firstRow = new ArrayList<>();
        ArrayList<InlineKeyboardButton> secondRow = new ArrayList<>();

        InlineKeyboardButton join = new InlineKeyboardButton();
        join.setText("Join \uD83D\uDD30");
        join.setCallbackData(mixedQueue.getId() + "-mixed-join");

        InlineKeyboardButton shuffle = new InlineKeyboardButton();
        shuffle.setText("Shuffle \uD83D\uDD00");
        shuffle.setCallbackData(mixedQueue.getId() + "-mixed-shuffle");
        firstRow.add(join);
        secondRow.add(shuffle);

        keyboard.add(firstRow);
        keyboard.add(secondRow);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public static TextMessage createNotification(long chatId, SimpleQueue simpleQueue) {
        SimpleUser simpleUser = simpleQueue.getContents().get(0);
        TextMessage textMessage = new TextMessage();
        textMessage.setChatId(chatId);
        textMessage.setMessageId(simpleQueue.getMessageId());

        if (simpleUser != null) {
            textMessage.setText(simpleUser.getName() + " (@" + simpleUser.getUsername() + "), твоя черга відповідати у " + simpleQueue.getAlias() + "!");
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        ArrayList<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        ArrayList<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton flush = new InlineKeyboardButton();
        flush.setText("I'm done ✅");
        flush.setCallbackData(simpleQueue.getId() + "-simple-exit");

        row.add(flush);

        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);

        textMessage.setReplyKeyboard(inlineKeyboardMarkup);

        return textMessage;
    }

}
