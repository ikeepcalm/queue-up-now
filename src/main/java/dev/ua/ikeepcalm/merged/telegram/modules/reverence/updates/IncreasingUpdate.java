package dev.ua.ikeepcalm.merged.telegram.modules.reverence.updates;

import dev.ua.ikeepcalm.merged.database.entities.reverence.ReverenceUser;
import dev.ua.ikeepcalm.merged.telegram.modules.CommandParent;
import dev.ua.ikeepcalm.merged.telegram.modules.reverence.patterns.ReverencePatterns;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class IncreasingUpdate extends CommandParent {

    @Override
    @SneakyThrows
    public void processUpdate(Message message) {
        instantiateUpdate(message);
        if (ReverencePatterns.isIncreasingUpdate(message) && userService.checkIfUserExists(message.getReplyToMessage().getFrom().getId(), reverenceChat)) {
            ReverenceUser foundRepliedUser = userService.findById(message.getReplyToMessage().getFrom().getId(), reverenceChat);
            int eventValue = Math.abs(Integer.parseInt(message.getText()));
            if (reverenceUser.getCredits() >= eventValue) {
                foundRepliedUser.setReverence(foundRepliedUser.getReverence() + eventValue);
                reverenceUser.setCredits(reverenceUser.getCredits() - eventValue);
                userService.save(foundRepliedUser);
                userService.save(reverenceUser);
                sendMessage("✔");
            } else {
                sendMessage("✖️");
            }
        } else {
            sendMessage("✖️");
        }
    }
}
