package dev.ua.ikeepcalm.queueupnow.telegram.modules;

import dev.ua.ikeepcalm.queueupnow.database.dal.interfaces.*;
import dev.ua.ikeepcalm.queueupnow.database.entities.queue.SimpleQueue;
import dev.ua.ikeepcalm.queueupnow.database.entities.reverence.ReverenceChat;
import dev.ua.ikeepcalm.queueupnow.database.entities.reverence.ReverenceUser;
import dev.ua.ikeepcalm.queueupnow.database.exceptions.NoSuchEntityException;
import dev.ua.ikeepcalm.queueupnow.telegram.AbsSender;
import dev.ua.ikeepcalm.queueupnow.telegram.wrappers.EditMessage;
import dev.ua.ikeepcalm.queueupnow.telegram.wrappers.RemoveMessage;
import dev.ua.ikeepcalm.queueupnow.telegram.wrappers.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Timer;
import java.util.UUID;

@Component
public abstract class CallbackParent {

    protected Message message;
    protected AbsSender absSender;

    protected ReverenceChat reverenceChat;
    protected ReverenceUser reverenceUser;

    protected ChatService chatService;
    protected UserService userService;
    protected TaskService taskService;
    protected ShopService shopService;
    protected TimetableService timetableService;
    protected QueueService queueService;
    private Logger logger;

    @Autowired
    private void setupDependencies(AbsSender absSender,
                                   ChatService chatService,
                                   UserService userService,
                                   TaskService taskService,
                                   ShopService shopService,
                                   TimetableService timetableService,
                                   QueueService queueService) {
        this.absSender = absSender;
        this.chatService = chatService;
        this.userService = userService;
        this.taskService = taskService;
        this.shopService = shopService;
        this.timetableService = timetableService;
        this.queueService = queueService;
        this.logger = LoggerFactory.getLogger(CallbackParent.class);
    }

    protected void instantiateUpdate(CallbackQuery message) {
        this.message = message.getMessage();

        try {
            this.reverenceChat = chatService.findByChatId(message.getMessage().getChatId());
        } catch (NoSuchEntityException e) {
            ReverenceChat newChat = new ReverenceChat();
            newChat.setChatId(message.getMessage().getChatId());
            this.chatService.save(newChat);
            this.reverenceChat = newChat;
            sendMessage("""
                    Привіт!

                    Дякую за те, що додали мене сюди! Коротенький список того, що я вмію:
                                        
                    - створювати черги;
                    - записувати завдання;
                    - виводити розклад по команді;
                    - нагадувати про пару за декілька хвилин до початку
                    - керувати мікро-економікою всередині чату і ще багато чого!
                                        
                    Щоб дізнатися більше, натисніть /help@queueupnow_bot!
                    """);
            return;
        }

        if (!message.getFrom().getIsBot()){
            try {
                this.reverenceUser = this.userService.findById(message.getFrom().getId(), reverenceChat);
            } catch (NoSuchEntityException e) {
                ReverenceUser newUser = new ReverenceUser();
                newUser.setUserId(message.getFrom().getId());
                newUser.setUsername(message.getFrom().getUserName());
                newUser.setCredits(100);
                newUser.setSustainable(100);
                newUser.setChannel(reverenceChat);
                userService.save(newUser);
                this.userService.save(newUser);
                this.reverenceUser = newUser;
                sendMessage("@" + message.getFrom().getUserName() + """
                                            
                        Давай знайомитись! Мене звуть КуєуєАпБот, а тебе?
                                            
                        ...зроблю вигляд, що запам'ятав. Ще побачимося!
                        """);
            }
        } logInteraction(message);
    }

    public abstract void processUpdate(CallbackQuery message);

    protected void sendMessage(String text) {
        TextMessage textMessage = new TextMessage();
        textMessage.setChatId(message.getChatId());
        textMessage.setMessageId(message.getMessageId());
        textMessage.setText(text);
        Message sent = absSender.sendTextMessage(textMessage);
        scheduleMessageToDelete(message);
        scheduleMessageToDelete(sent);
    }


    protected void sendMessage(String text, String parseMode) {
        TextMessage textMessage = new TextMessage();
        textMessage.setChatId(message.getChatId());
        textMessage.setMessageId(message.getMessageId());
        textMessage.setParseMode(parseMode);
        textMessage.setText(text);
        Message sent = absSender.sendTextMessage(textMessage);
        scheduleMessageToDelete(message);
        scheduleMessageToDelete(sent);
    }

    protected void sendMessage(TextMessage textMessage) {
        Message sent = absSender.sendTextMessage(textMessage);
        scheduleMessageToDelete(message);
        scheduleMessageToDelete(sent);
    }

    protected void editMessage(EditMessage message) {
        absSender.sendEditMessage(message);
    }

    private void logInteraction(CallbackQuery message) {
        if (message == null || message.getData() == null || message.getData().startsWith("shop")){
            return;
        }

        String receivedCallback = message.getData();
        String action = getActionFromCallback(receivedCallback);
        String queueUUID = removeCallbackPrefixes(receivedCallback);

        SimpleQueue simpleQueue = null;
        try {
            simpleQueue = queueService.findSimpleById(UUID.fromString(queueUUID));
        } catch (NoSuchEntityException e) {
            logger.error("Callback: [{}]: {}",
                    message.getFrom().getUserName(),
                    "No such queue");
        }
        String username = message.getFrom().getUserName();

        if (simpleQueue != null) {
            logger.info("Callback: [{}] - [{}]: {}",
                    username,
                    simpleQueue.getAlias(),
                    action);
        } else {
            logger.info("Callback: [{}]: {}",
                    username,
                    action);
        }
    }

    private String getActionFromCallback(String data) {
        String[] suffixes = {"-simple-delete", "-simple-exit", "-simple-flush", "-simple-join", "-simple-notify",
                "-mixed-join", "-mixed-shuffle"};
        for (String suffix : suffixes) {
            if (data.endsWith(suffix)) {
                return suffix;
            }
        }
        return "";
    }

    private String removeCallbackPrefixes(String data) {
        if (data != null) {
            String[] suffixes = {"-simple-delete", "-simple-exit", "-simple-flush", "-simple-join", "-simple-notify",
                    "-mixed-join", "-mixed-shuffle"};

            for (String suffix : suffixes) {
                data = data.replace(suffix, "");
            }
        }

        return data;
    }

    private void scheduleMessageToDelete(Message message) {
        new Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        RemoveMessage removeMessage = new RemoveMessage(message.getMessageId(), message.getChatId());
                        absSender.sendRemoveMessage(removeMessage);
                    }
                }, 300000);
    }


}

