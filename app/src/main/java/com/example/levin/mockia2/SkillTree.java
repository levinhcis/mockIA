package com.example.levin.mockia2;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/*
This is the skillTree object, which stores skillNode objects in a tree for the sake of finding the
relationships between different skills.
 */
public class SkillTree {
    Context appContext;
    FileUtility fileUtility;
    SkillNode root;

    public SkillTree(FileUtility f) throws IOException {
        fileUtility = f;

        ArrayList<SkillNode> skillsArray = fileUtility.getAllSkillNodes();

        //Set the root node as the very first skill in the skill file.
        root = skillsArray.get(0);

        SkillNode parentNode;

        for (SkillNode skillNode : skillsArray.subList(1, skillsArray.size())) {
            parentNode = getNodeWithID(skillNode.getPrevSkillId(), root);
            if (parentNode != null) {
                parentNode.addNextNodes(skillNode);
                skillNode.setPrevNode(parentNode);
            }
        }
    }

    //External "starter" method for getNodeWithID (because root node is private)
    public SkillNode externalGetNodeWithID(String id) { //external test of getNodeWithID
        return getNodeWithID(id, root);
    }

    //Find and return the skillNode with the associated id
    private SkillNode getNodeWithID(String id, SkillNode currentNode) {
        SkillNode result;

        //If the current node matches the id, return
        if (currentNode.getId().equals(id))
            return currentNode;

        //Otherwise, go through every node that follows from the current node, and call this
        //function for each (recursion)
        for (SkillNode nextNode : currentNode.getNextNodes()) {
            result = getNodeWithID(id, nextNode);
            if (result != null) {
                return result;
            }
        }

        //Return null if neither this node, nor any of the nodes leading on from this node,
        //match the given id.
        return null;
    }
    /*
    Getters for data from specific skillNodes
     */

    public String getNodeName(String id) {
        SkillNode node = getNodeWithID(id, root);
        return node.getName();
    }

    public String getNodeDescription(String id) {
        SkillNode node = getNodeWithID(id, root);
        return node.getDescription();
    }

    public ArrayList<String> getNodeTasksID(String id) {
        SkillNode node = getNodeWithID(id, root);
        return node.getTasks();
    }

    public String getPrevNodeID(String id) {
        //Log.d("myInfoTag", "getting prevNode id of: " + id);
        SkillNode node = getNodeWithID(id, root); //returning null when getNodeWithID("S000007", root)
        if (node == null)
            Log.e("myErrorTag", "Could not find node!");
        return node.getPrevSkillId();
    }

    public ArrayList<String> getRelevantTasks(String id) {
        SkillNode node = getNodeWithID(id, root);
        return node.getTasks();
    }

    public int getNodeMaxProgress(String id) {
        SkillNode node = getNodeWithID(id, root);
        if (node == null) {
            //Log.d("myInfoTag", "node is null: " + id);
            return 0;
        }
        else
            return node.getMaxProgress();
    }

    /*
    Returns the skill IDs for all the skills to do with a given song, ordered from most basic
    to most advanced.
     */
    public ArrayList<String> getAllSkillsForSong(String id) throws IOException {

        ArrayList<ArrayList<String>> skillLines = new ArrayList<>();

        ArrayList<String> songTopSkills = fileUtility.getSongItem(id).getTopSkillIds();

        ArrayList<String> topSkill;

        for (String skillId : songTopSkills) {
            topSkill = new ArrayList<>();
            topSkill.add(skillId);
            skillLines.add(topSkill);
        }

        SkillNode currentNode;

        for (ArrayList<String> skillLine : skillLines) {
            currentNode = this.getNodeWithID(skillLine.get(0), root);

            while (true) {
                currentNode = currentNode.getPrevNode();
                if (currentNode == null) {
                    break;
                }
                skillLine.add(0, currentNode.getId());
            }
        }

        ArrayList<String> curatedSkillsForSong = new ArrayList<>();

        while (skillLines.size() > 0) {
            for (int lineIndex = 0; lineIndex < skillLines.size(); lineIndex++) {
                ArrayList<String> skillLine = skillLines.get(lineIndex);

                if (!curatedSkillsForSong.contains(skillLine.get(0))) {
                    curatedSkillsForSong.add(0, skillLine.get(0));
                }
                skillLine.remove(0);
                if (skillLine.size() == 0)
                    skillLines.remove(skillLine);
            }
        }
        return curatedSkillsForSong;
    }
}
