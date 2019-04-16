package com.example.levin.mockia2;

/*
Child class of queueItem, specifically for songs - so it contains song-specific data.
 */
public class TaskItem extends QueueItem {

    private String name;
    private String id;

    private String successCondition;


    private String skillName;

    public TaskItem(String i, String n, String c, String s) {
        super(i, n, c);
        successCondition = s;
        skillName = "none";
    }

    //The skill which this task teaches.
    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSuccessCondition() {
        return successCondition;
    }

    public void setSuccessCondition(String successCondition) {
        this.successCondition = successCondition;
    }
}
