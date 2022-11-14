package manager;

import java.util.LinkedList;

public interface HistoryManager {
    void add(Object obj);

    LinkedList<Object> getHistory();
}
