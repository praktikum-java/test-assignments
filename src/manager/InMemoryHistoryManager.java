package manager;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private LinkedList<Object> taskHistory = new LinkedList<>();

    @Override
    public void add(Object obj) {
        if(taskHistory.size() > 10) {
            taskHistory.remove(0);
        } else {
            taskHistory.add(obj);
        }
    }

    @Override
    public LinkedList<Object> getHistory() {
        return this.taskHistory;
    }
}
