package com.winkytech.bdclean;

public class PositionList {

    String id, name,dept_level;

    public PositionList(String id, String name,String dept_level) {
        this.id = id;
        this.name = name;
        this.dept_level = dept_level;
    }

    public String getDept_level() {
        return dept_level;
    }

    public void setDept_level(String dept_level) {
        this.dept_level = dept_level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
