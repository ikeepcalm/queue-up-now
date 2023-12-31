package dev.ua.ikeepcalm.queueupnow.telegram.modules.queues.callbacks.mixed;

import dev.ua.ikeepcalm.queueupnow.database.entities.queue.MixedQueue;
import dev.ua.ikeepcalm.queueupnow.database.entities.queue.SimpleQueue;
import dev.ua.ikeepcalm.queueupnow.database.entities.queue.SimpleUser;
import dev.ua.ikeepcalm.queueupnow.database.exceptions.NoSuchEntityException;
import dev.ua.ikeepcalm.queueupnow.telegram.modules.CallbackParent;
import dev.ua.ikeepcalm.queueupnow.telegram.modules.queues.utils.QueueUpdateUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import java.util.UUID;

@Component
public class ShuffleCallback extends CallbackParent {

    @Override
    @Transactional
    public void processUpdate(CallbackQuery message) {
        String receivedCallback = message.getData().replace("-mixed-shuffle", "");
        String callbackQueryId = message.getId();
        instantiateUpdate(message);
        MixedQueue mixedQueue = null;
        try {
            mixedQueue = queueService.findMixedById(UUID.fromString(receivedCallback));
            for (ChatMember chatMember : absSender.getChatAdministrators(String.valueOf(message.getMessage().getChatId()))) {
                if (chatMember.getUser().getId().equals(message.getFrom().getId()) || message.getFrom().getUserName().equals("ikeepcalm")) {
                    if (!mixedQueue.isShuffled()) {
                        mixedQueue.shuffleContents();

                        SimpleQueue simpleQueue = new SimpleQueue();
                        simpleQueue.setId(mixedQueue.getId());
                        simpleQueue.setMessageId(mixedQueue.getMessageId());
                        simpleQueue.setAlias(mixedQueue.getAlias());
                        for (int i = 0; i < mixedQueue.getContents().size(); i++) {
                            SimpleUser simpleUser = new SimpleUser();
                            simpleUser.setName(mixedQueue.getContents().get(i).getName());
                            simpleUser.setAccountId(mixedQueue.getContents().get(i).getAccountId());
                            simpleUser.setUsername(mixedQueue.getContents().get(i).getUsername());
                            simpleQueue.getContents().add(simpleUser);
                        }

                        queueService.save(simpleQueue);
                        queueService.deleteMixedQueue(mixedQueue);

                        simpleQueue.setMessageId(absSender.sendEditMessage
                                        (QueueUpdateUtil.updateMessage(message.getMessage(), simpleQueue))
                                .getMessageId());

                        queueService.save(simpleQueue);
                        this.absSender.sendAnswerCallbackQuery("Успішно перемішано цю чергу!", callbackQueryId);
                    } else {
                        this.absSender.sendAnswerCallbackQuery("Ця черга вже перемішана!", callbackQueryId);
                    }
                    break;
                }
            }
        } catch (NoSuchEntityException e) {
            absSender.sendAnswerCallbackQuery("Помилка! Не знайдено чергу з таким ID!", callbackQueryId);
        }
    }
}
