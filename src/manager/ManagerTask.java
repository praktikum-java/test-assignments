package manager;

import finalAppSprint3.tasks.Epic;
import finalAppSprint3.tasks.SubTask;
import finalAppSprint3.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public interface ManagerTask {

    Task createTask(Task task);

    Task getTaskByID(Integer ID);

    void update(Epic epic);
    void update(SubTask subTask);
    void update(Task task);
    void deleteTasks();

    void deleteTask(Integer ID);

    ArrayList<Task> getTasks();

    Epic createEpic(Epic epic);

    Epic getEpicByID(Integer ID);

    void deleteEpics();

    void deleteEpic(Integer ID);

    ArrayList<SubTask> getEpicSubtasks(Integer ID);

    HashMap<Integer, Epic> getEpics();

    ArrayList<SubTask> getSubTasks() ;

    SubTask createSubTask(SubTask subTask);

    SubTask getSubTaskByID(Integer ID);

    void deleteSubTasks() ;

    void deleteSubTask(Integer ID, Integer epicID);

    LinkedList<Object> getHistory();
}