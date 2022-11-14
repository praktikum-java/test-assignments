package finalAppSprint3.tasks;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIDs;
    public Epic(Integer ID, String name, String description, TaskStatus epicStatus, ArrayList<Integer> subTasksIDs) {
        super(name, description);
        this.subTaskIDs = new ArrayList<>();
    }
    public Epic(Integer ID, String name, String description, TaskStatus status) {
        super(ID, name, description, status);
        this.subTaskIDs = new ArrayList<>();
    }

    public Epic(Integer ID, String name, String description, String status, ArrayList<Integer> subTaskIDs) {
        super(ID, name, description, TaskStatus.valueOf(status));
        this.subTaskIDs = subTaskIDs;
    }

    public ArrayList<Integer> getSubTaskIDs() {
        return this.subTaskIDs;
    }

    public void setSubTaskIDs(ArrayList<Integer> subTaskIDs) {
        this.subTaskIDs = subTaskIDs;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskIDs=" + subTaskIDs +
                ", ID=" + getID() + ", status=" + getStatus() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(getSubTaskIDs(), epic.getSubTaskIDs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubTaskIDs());
    }
}
