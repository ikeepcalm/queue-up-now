package dev.ua.ikeepcalm.lumios.web.endpoints;

import dev.ua.ikeepcalm.lumios.database.dal.interfaces.ChatService;
import dev.ua.ikeepcalm.lumios.database.dal.interfaces.RecordService;
import dev.ua.ikeepcalm.lumios.database.dal.interfaces.ShotService;
import dev.ua.ikeepcalm.lumios.database.entities.records.MessageRecord;
import dev.ua.ikeepcalm.lumios.database.entities.records.wrappers.MessageWrapper;
import dev.ua.ikeepcalm.lumios.database.entities.reverence.ReverenceChat;
import dev.ua.ikeepcalm.lumios.database.entities.reverence.shots.ChatShot;
import dev.ua.ikeepcalm.lumios.database.entities.reverence.shots.UserShot;
import dev.ua.ikeepcalm.lumios.database.entities.reverence.shots.wrappers.DifferenceWrapper;
import dev.ua.ikeepcalm.lumios.database.exceptions.NoSuchEntityException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/records")
public class RecordsController {

    private final RecordService recordService;
    private final ShotService shotService;
    private final ChatService chatService;

    public RecordsController(RecordService recordService, ShotService shotService, ChatService chatService) {
        this.recordService = recordService;
        this.shotService = shotService;
        this.chatService = chatService;
    }

    @GetMapping("/messages")
    public ResponseEntity<List<MessageWrapper>> getMessages(
            @RequestParam("chatId") Long chatId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        ReverenceChat chat;
        try {
            chat = chatService.findByChatId(chatId);
        } catch (NoSuchEntityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<MessageRecord> messageRecords = recordService.findAllByChatAndDateBetween(chat, startDate.atStartOfDay(), endDate.atStartOfDay());
        messageRecords.removeIf(messageRecord -> !messageRecord.getChatId().equals(chatId));
        List<MessageWrapper> messageWrappers = MessageWrapper.wrapperList(messageRecords);

        return ResponseEntity.status(HttpStatus.OK).body(messageWrappers);
    }

    @GetMapping("/ratings")
    public ResponseEntity<List<DifferenceWrapper>> getShots(
            @RequestParam("chatId") Long chatId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        ChatShot startShot;
        ChatShot endShot;
        try {
            startShot = shotService.findByChatIdAndDate(chatId, startDate);
            endShot = shotService.findByChatIdAndDate(chatId, endDate);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<DifferenceWrapper> wrappers = new ArrayList<>();

        for (UserShot shot : startShot.getUserShots()) {
            UserShot endUserShot = endShot.getUserShots().stream().filter(userShot -> userShot.getId().equals(shot.getId())).findFirst().orElse(shot);
            DifferenceWrapper wrapper = new DifferenceWrapper(shot, endUserShot);
            wrappers.add(wrapper);
        }

        return ResponseEntity.status(HttpStatus.OK).body(wrappers);
    }

    @GetMapping("/rating")
    public ResponseEntity<List<UserShot>> getShot(
            @RequestParam("chatId") Long chatId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        ChatShot chatShot;
        try {
            chatShot = shotService.findByChatIdAndDate(chatId, startDate);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        System.out.println(chatShot.getUserShots().size());
        return ResponseEntity.status(HttpStatus.OK).body(chatShot.getUserShots());
    }

}
