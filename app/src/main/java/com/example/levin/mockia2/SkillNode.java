package com.example.levin.mockia2;

import java.util.ArrayList;

/*
This is a node object for the skill tree, representing a single "skill". It contains all the data
to do with that skill, including its ID, its name, associated tasks, maximum progress level.
 */
public class SkillNode {
    private String id;
    private String name;
    private String description;
    private ArrayList<String> tasks;
    private String prevSkillId; //the String id of the previous skill

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    private int maxProgress;

    private SkillNode prevNode; //the actuall SkillNode of the previous skill
    private ArrayList<SkillNode> nextNodes;

    public SkillNode getPrevNode() {
        return prevNode;
    }

    public void setPrevNode(SkillNode prevNode) {
        this.prevNode = prevNode;
    }

    public ArrayList<SkillNode> getNextNodes() {
        return nextNodes;
    }

    public void setNextNodes(ArrayList<SkillNode> nextNodes) {
        this.nextNodes = nextNodes;
    }

    public void addNextNodes(SkillNode newNode) {
        this.nextNodes.add(newNode);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<String> tasks) {
        this.tasks = tasks;
    }

    public String getPrevSkillId() {
        return prevSkillId;
    }

    public void setPrevSkillId(String prevSkillId) {
        this.prevSkillId = prevSkillId;
    }

    public SkillNode(String i, String n, String d, ArrayList<String> t, String p, int m)
    {
        id = i;
        name = n;
        description = d;
        tasks = t;
        prevSkillId = p;
        maxProgress = m;

        prevNode = null;
        nextNodes = new ArrayList<>();
    }


}
