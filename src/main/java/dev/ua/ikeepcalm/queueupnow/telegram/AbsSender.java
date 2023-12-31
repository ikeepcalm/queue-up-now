package dev.ua.ikeepcalm.queueupnow.telegram;

import dev.ua.ikeepcalm.queueupnow.telegram.wrappers.EditMessage;
import dev.ua.ikeepcalm.queueupnow.telegram.wrappers.RemoveMessage;
import dev.ua.ikeepcalm.queueupnow.telegram.wrappers.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.*;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AbsSender extends DefaultAbsSender{

    private static final Logger LOGGER = LoggerFactory.getLogger(AbsSender.class);

    public AbsSender(@Value(value="${telegram.bot.token}") String botToken) {
        super(new DefaultBotOptions(), botToken);
    }

    private Object executeCommand(BotApiMethod<?> command, String errorMessage) {
        try {
            return execute(command);
        } catch (TelegramApiException e) {
            LOGGER.error(errorMessage, e);
            throw new RuntimeException(e);
        }
    }

    public List<ChatMember> getChatAdministrators(String chatId) {
        GetChatAdministrators getChatAdministrators = new GetChatAdministrators(chatId);
        return (List<ChatMember>) executeCommand(getChatAdministrators, "Failed to get chat administrators!");
    }

    public void sendAnswerCallbackQuery(String text, String callbackQueryId) {
        AnswerCallbackQuery acq = new AnswerCallbackQuery();
        acq.setText(text);
        acq.setShowAlert(true);
        acq.setCallbackQueryId(callbackQueryId);
        executeCommand(acq, "Failed to send AnswerCallbackQuery");
    }

    public Message sendEditMessage(EditMessage editMessage) {
        try {
            if (editMessage.getFilePath() == null && editMessage.getText() == null) {
                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
                editMessageReplyMarkup.setMessageId(editMessage.getMessageId());
                editMessageReplyMarkup.setReplyMarkup((InlineKeyboardMarkup) editMessage.getReplyKeyboard());
                editMessageReplyMarkup.setChatId(editMessage.getChatId());
                return (Message) execute(editMessageReplyMarkup);
            } else if (editMessage.getFilePath() == null) {
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setText(editMessage.getText());
                editMessageText.setMessageId(editMessage.getMessageId());
                editMessageText.setChatId(editMessage.getChatId());
                editMessageText.setReplyMarkup((InlineKeyboardMarkup) editMessage.getReplyKeyboard());
                return (Message) execute(editMessageText);
            } else {
                EditMessageCaption editMessageCaption = new EditMessageCaption();
                editMessageCaption.setMessageId(editMessage.getMessageId());
                editMessageCaption.setCaption(editMessage.getText());
                editMessageCaption.setParseMode(editMessage.getParseMode());
                editMessageCaption.setReplyMarkup((InlineKeyboardMarkup) editMessage.getReplyKeyboard());
                editMessageCaption.setChatId(editMessage.getChatId());
                EditMessageMedia editMessageMedia = new EditMessageMedia();
                editMessageMedia.setMessageId(editMessage.getMessageId());
                editMessageMedia.setChatId(editMessage.getChatId());
                editMessageMedia.setMedia(new InputMediaPhoto(editMessage.getFilePath()));
                execute(editMessageMedia);
                return (Message) execute(editMessageCaption);
            }
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send EditMessage: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void pinChatMessage(long chatId, long messageId) {
        PinChatMessage pinChatMessage = new PinChatMessage();
        pinChatMessage.setChatId(chatId);
        pinChatMessage.setMessageId((int) messageId);
        executeCommand(pinChatMessage, "Failed to pin chat message");
    }

    public InlineKeyboardMarkup createMarkup(String[] values, String prefix) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (String name : values) {
            String callbackOfButton = prefix + name;
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData(callbackOfButton);
            button.setText(name + " кредитів / " + name + "0 (✧)");
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public Message sendTextMessage(TextMessage textMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(textMessage.getText());
        sendMessage.setChatId(textMessage.getChatId());
        if (textMessage.getParseMode() != null){
            sendMessage.setParseMode(textMessage.getParseMode());
        }
        sendMessage.setReplyMarkup(textMessage.getReplyKeyboard());
        if (textMessage.getMessageId() != 0) {
            sendMessage.setReplyToMessageId(textMessage.getMessageId());
        } return (Message) executeCommand(sendMessage, "Failed to send multi-message");
    }

    public void sendRemoveMessage(RemoveMessage removeMessage) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(removeMessage.getChatId());
        deleteMessage.setMessageId(removeMessage.getMessageId());
        executeCommand(deleteMessage, "Failed to purge message");
    }

}

