package manager;

import finalAppSprint3.tasks.Epic;
import finalAppSprint3.tasks.SubTask;
import finalAppSprint3.tasks.Task;
import finalAppSprint3.tasks.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements ManagerTask {

    private Integer ID = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager managerHistory = Managers.getDefaultHistory();

    private final ArrayList<Integer> subTasksIDs = new ArrayList<>();

    @Override
    public LinkedList<Object> getHistory() {
        return managerHistory.getHistory();
    }

    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    @Override
    public Task getTaskByID(Integer ID) {
        managerHistory.add(tasks.get(ID));
        return tasks.get(ID);
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public Epic getEpicByID(Integer ID) {
        managerHistory.add(epics.get(ID));
        return epics.get(ID);
    }

    @Override
    public ArrayList<SubTask> getEpicSubtasks(Integer ID) {
        ArrayList<SubTask> result = new ArrayList<>();
        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            if (entry.getValue().getEpicID().equals(ID)) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        ArrayList<SubTask> result = new ArrayList<>();

        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            result.add(entry.getValue());
        }

        return result;
    }

    @Override
    public SubTask getSubTaskByID(Integer ID) {
        managerHistory.add(subTasks.get(ID));
        return subTasks.get(ID);
    }

    @Override
    public Task createTask(Task task) {
        task = new Task(ID++, task.getName(), task.getDescription(), TaskStatus.NEW);
        tasks.put(task.getID(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic = new Epic(ID++, epic.getName(), epic.getDescription(), TaskStatus.NEW);
        epics.put(epic.getID(), epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        subTask.setID(ID++);
        subTask.setStatus(TaskStatus.NEW);
        subTasks.put(subTask.getID(), subTask);
        Epic epic = epics.get(subTask.getEpicID());
        epic.getSubTaskIDs().add(subTask.getID());
        update(epic);
        return subTask;
    }

    @Override
    public void update(Task task) {
        tasks.put(ID, task);
    }


    @Override
    public void update(SubTask subTask) {
        subTasks.put(ID, subTask);
        update(epics.get(subTask.getEpicID()));
    }
    @Override
    public void update(Epic epic) {
        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            if (entry.getValue().getEpicID().equals(ID)) {
                subTasksIDs.add(entry.getKey());
            }
        }

    }

    public TaskStatus updateStatus(Epic epic) {
        if (Objects.nonNull(epic)) {
            ArrayList<Integer> epicSubTaskIDs;
            if (epic.getSubTaskIDs().isEmpty()) epicSubTaskIDs = new ArrayList<>();
            else epicSubTaskIDs = epic.getSubTaskIDs();
            Integer doneCounter = 0;
            Integer newCounter = 0;

            TaskStatus epicStatus = TaskStatus.NEW;

            if (!epicSubTaskIDs.isEmpty()) {
                for (Integer object : epicSubTaskIDs)
                    if (subTasks.containsKey(object)) {
                        SubTask task = subTasks.get(object);
                        if (task.getStatus().equals(TaskStatus.NEW)) {
                            newCounter++;
                        } else if (task.getStatus().equals(TaskStatus.DONE)) {
                            doneCounter++;
                        }
                    }

                if (doneCounter == epicSubTaskIDs.size()) {
                    epicStatus = TaskStatus.DONE;
                } else if (newCounter == epicSubTaskIDs.size()) {
                    epicStatus = TaskStatus.NEW;
                } else {
                    epicStatus = TaskStatus.IN_PROGRESS;
                }
            }
            epic.setStatus(epicStatus);
            return epicStatus;
        } else {
            return null;
        }
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteTask(Integer ID) {
        tasks.remove(ID);
    }

    @Override
    public void deleteEpics() {
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void deleteEpic(Integer ID) {
        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            if (entry.getValue().getEpicID().equals(ID)) {
                subTasks.remove(entry.getKey());
            }
        }
        epics.remove(ID);
    }

    @Override
    public void deleteSubTasks() {
        subTasks.clear();
        for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
            update(entry.getValue());
        }
    }

    @Override
    public void deleteSubTask(Integer ID, Integer epicID) {
        subTasks.remove(ID);
        update(epics.get(epicID));
    }

}