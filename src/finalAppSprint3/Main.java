package finalAppSprint3;

import finalAppSprint3.tasks.Epic;
import finalAppSprint3.tasks.SubTask;
import finalAppSprint3.tasks.Task;
import finalAppSprint3.tasks.TaskStatus;
import manager.InMemoryTaskManager;
import manager.ManagerTask;
import manager.Managers;

public class Main {

    public static void main(String[] args) {
        System.out.println("Начало работы");
        outputTests();
    }

    private static void outputTests() {
        ManagerTask manager = Managers.getDefault();
        InMemoryTaskManager managerMemory = new InMemoryTaskManager();

        //Создание задач
        Task task1 = manager.createTask(new Task(1, "Задача №1: устроить день СПА", "Пора поухаживать за лицом и телом при помощи миллиарда баночек и скляночек", TaskStatus.NEW));
        Task task2 = manager.createTask(new Task(2, "Задача №2: уход за котом", "Что-то котяра оброс. Пора бы его вычесать и подстричь когти", TaskStatus.NEW));

        //Создание эпиков
        Epic epic1 = manager.createEpic(new Epic(3, "Эпик1","Эпик №1: начать жить осознанно", TaskStatus.NEW));
        Epic epic2 = manager.createEpic(new Epic(4, "Эпик2","Эпик №2: нейтив англ", TaskStatus.NEW));

        //Создание подзадач
        SubTask subTask1 = manager.createSubTask(new SubTask("подзадача №1", "записаться к психологу", epic1.getID()));
        SubTask subTask2 = manager.createSubTask(new SubTask("подзадача №2", "пройти тест на определение нынешнего уровня владения английским", epic2.getID()));
        SubTask subTask3 = manager.createSubTask(new SubTask("подзадача №1", "купить красивую тетрадочку для занятий", epic2.getID()));

        System.out.println("История созданных задач");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpicSubtasks(epic1.getID()));
        System.out.println(manager.getEpicSubtasks(epic2.getID()));
        System.out.println();

        System.out.println("Проверка обновления статуса Эпика");
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.update(subTask2);
        subTask3.setStatus(TaskStatus.DONE);
        manager.update(subTask3);
        manager.update(epic2);
        managerMemory.updateStatus(epic2);
        System.out.println(manager.getEpicByID(epic2.getID()));
        System.out.println();

        System.out.println("Проверка удаления подзадачи из Эпика");
        manager.deleteSubTask(6, 3);
        System.out.println(manager.getEpicByID(3));
        System.out.println();

        System.out.println("Проверка удаления задач");
        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubTasks();
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getEpicSubtasks(epic2.getID()));
        System.out.println();

        System.out.println("Проверка удаления подзадач после удаления эпика");
        manager.deleteEpic(4);
        System.out.println(manager.getEpicByID(4));
        System.out.println("Подзадачи для данного эпика отсутствуют");
        System.out.println();
        System.out.println(manager.getHistory());

    }
}
