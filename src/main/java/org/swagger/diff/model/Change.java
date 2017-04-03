package org.swagger.diff.model;

public class Change {
    public ChangeType type;
    public boolean breaking;
    public String path;
    public String oldValue;
    public String newValue;

    public Change() {
    }

    public Change(ChangeType type, boolean breaking, String path, String oldValue, String newValue) {
        this.type = type;
        this.breaking = breaking;
        this.path = path;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
