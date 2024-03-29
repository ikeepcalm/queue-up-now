package dev.ua.ikeepcalm.queueupnow.database.dal.impls;

import dev.ua.ikeepcalm.queueupnow.database.dal.interfaces.TaskService;
import dev.ua.ikeepcalm.queueupnow.database.dal.repositories.timetable.TaskRepository;
import dev.ua.ikeepcalm.queueupnow.database.entities.reverence.ReverenceChat;
import dev.ua.ikeepcalm.queueupnow.database.entities.tasks.DueTask;
import org.springframework.stereotype.Service;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl
implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void save(DueTask dueTask) {
        taskRepository.save(dueTask);
    }

    @Override
    public void delete(DueTask dueTask) {
        taskRepository.delete(dueTask);
    }

    @Override
    public boolean existsByChatAndTaskName(ReverenceChat chat, String taskName) {
        return taskRepository.existsByChatAndTaskName(chat, taskName);
    }

    @Override
    public List<DueTask> getTasksForCurrentChat(ReverenceChat chatId) {
        return taskRepository.findByChat(chatId);
    }

    @Override
    public DueTask findTaskById(Long id) throws InputMismatchException{
        Optional<DueTask> dueTask = taskRepository.findById(id);
        if (dueTask.isEmpty()){
            throw new InputMismatchException("Couldn't find DueTask by id " + id);
        } else {
            return dueTask.get();
        }
    }

}

