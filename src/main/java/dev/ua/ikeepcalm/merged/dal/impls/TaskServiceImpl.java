/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Service
 */
package dev.ua.ikeepcalm.merged.dal.impls;

import dev.ua.ikeepcalm.merged.dal.interfaces.TaskService;
import dev.ua.ikeepcalm.merged.dal.repositories.TaskRepository;
import dev.ua.ikeepcalm.merged.entities.reverence.ReverenceChat;
import dev.ua.ikeepcalm.merged.entities.tasks.DueTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl
implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public void save(DueTask dueTask) {
        taskRepository.save(dueTask);
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

