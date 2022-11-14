package finalAppSprint3.tasks;

import java.util.Objects;

public class SubTask extends Task {
    private final Integer epicID;

    public SubTask(String name, String description, Integer epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    public SubTask(Integer ID, String name, String description, TaskStatus status, Integer epicId) {
        super(ID, name, description, status);
        this.epicID = epicId;
    }

    public Integer getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "ID=" + getID() +
                ", epicID=" + epicID +
                ", status=" + this.getStatus() +
                ", description=" + getDescription() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        SubTask subTask = (SubTask) o;
        return Objects.equals(getEpicID(), subTask.getEpicID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEpicID());
    }
}
