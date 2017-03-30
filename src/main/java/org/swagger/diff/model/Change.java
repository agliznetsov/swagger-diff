package org.swagger.diff.model;

public class Change {
    public ChangeType type;
    public boolean breaking;
    public String group;
    public String name;
    public String oldValue;
    public String newValue;

    public Change() {
    }

    public Change(ChangeType type, boolean breaking, String group, String name, String oldValue, String newValue) {
        this.type = type;
        this.breaking = breaking;
        this.group = group;
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
