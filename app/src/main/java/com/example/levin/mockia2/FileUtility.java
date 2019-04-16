package com.example.levin.mockia2;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

/*
    This object deals with retrieving information from the raw resource files, and converting this
    information to useful forms (e.g reading strings from the files, and then constructing different
    objects from them).

    The raw resource files store all of the different "data objects" - discrete songs, skills, or
    tasks. From now on, descriptions will use use the term "data object" to refer to any of these.
     */
public class FileUtility {
    Context appContext;
    InputStream skillsIS;

    InputStream tasksIS;

    InputStream songsIS;

    FileInputStream progressFIS;
    FileOutputStream progressFOS;

    public FileUtility(Context c) throws FileNotFoundException {
        appContext = c;
        skillsIS = appContext.getResources().openRawResource(R.raw.skills);
        tasksIS = appContext.getResources().openRawResource(R.raw.tasks);
        songsIS = appContext.getResources().openRawResource(R.raw.songs);

        //progressFIS = appContext.openFileInput("progress.txt");
        //progressFOS= appContext.openFileOutput("progress.txt", Context.MODE_APPEND);
    }

    //Gets the whole chunk of string describing a single data object
    public String getObjectString(String id) throws IOException {

        InputStream inputStream;

        if (id.substring(0, 1).equals("S"))
            inputStream = skillsIS;
        else if (id.substring(0, 1).equals("T"))
            inputStream = tasksIS;
        else if (id.substring(0, 1).equals("P"))
            inputStream = songsIS;
        else {
            Log.e("myErrorTag", "Invalid data object type!");
            throw new FileNotFoundException();
        }

        BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));

        String lineToFind = "{" + id;
        String line;
        String result = "";
        boolean lineFound = false;
        while ((line = bReader.readLine()) != null) {
            if (line.equals(lineToFind)) {
                lineFound = true;
            }
            else if (line.equals("}") && lineFound) {
                break;
            }

            if (lineFound) {
                result = result + line + "\n";
            }
        }
        //Log.d("myInfoTag", "RESULT: " + result);
        inputStream.reset(); //SUPER IMPORTANT!!!
        return result;
    }

    /*
    Returns all the information on a single data object, split into different array elements by
    category (e.g name, id, content...)
    */
    public ArrayList<String> getObjectContentsArray(String id) throws IOException {
        String skillString = getObjectString(id);
        if (skillString.equals(""))
            return null;

        BufferedReader strReader = new BufferedReader(new StringReader(skillString));

        String line;
        String currentContents = "";
        ArrayList<String> contents = new ArrayList<>();
        boolean contentsFound = false;

        contents.add(id);

        strReader.readLine(); strReader.readLine();

        while ((line = strReader.readLine()) != null) {
            if (line.substring(0, 1).equals("-")){
                contents.add(currentContents);
                currentContents = "";
            }
            else {
                if (currentContents.equals(""))
                    currentContents = line;
                else
                    currentContents = currentContents + "\n" + line;
            }
        }
        contents.add(currentContents);

        //Log.d("myInfoTag", "id: " + contents.get(0) + ", tasks: " + contents.get(3));
        return contents;
    }

    /*
    Returns all IDs for a certain data type (e.g for all skills, all songs, or all tasks).
    Every data object has a string id associated with it.
     */
    public ArrayList<String> getAllIDs(String type) throws IOException {
        InputStream inputStream;

        if (type.equals("skills"))
            inputStream = skillsIS;
        else if (type.equals("tasks"))
            inputStream = tasksIS;
        else if (type.equals("songs"))
            inputStream = songsIS;
        else {
            Log.e("myErrorTag", "Invalid data object type!");
            throw new java.io.FileNotFoundException();
        }

        //collect all ids
        BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));

        ArrayList<String> allIds = new ArrayList<>();

        String line;

        while ((line = bReader.readLine()) != null) {
            if ((line.length() > 0) && (line.substring(0, 1).equals("{")))
                allIds.add(line.substring(1));
        }

        //reset
        inputStream.reset();

        return allIds;
    }

    /*
    Returns a string data object in the form of a SkillNode instance. This is for the skillTree
    object.
     */
    public SkillNode getSkillNode(String id) throws IOException {
        ArrayList<String> skillContents = getObjectContentsArray(id);
        if (skillContents == null)
            return null;

        BufferedReader strReader = new BufferedReader(new StringReader(skillContents.get(3)));
        ArrayList<String> tasks = new ArrayList<>();

        //Log.d("myInfoTag", "in getSkillNode. id: " + id + ", skillContents.get(3): " + skillContents.get(3));

        String line = null;
        while ((line = strReader.readLine()) != null) {
            //Log.d("myInfoTag", "task line length: " + line.length());
            //Log.d("myInfoTag", "task line: " + line);
            tasks.add(line.substring(0, 7));
        }

        String prevSkillId = skillContents.get(4);
        if (!prevSkillId.equals("")) {
            prevSkillId = prevSkillId.substring(0, 7);
        }

        SkillNode skill = new SkillNode(
                id,
                skillContents.get(1),
                skillContents.get(2),
                tasks,
                prevSkillId,
                Integer.parseInt(skillContents.get(5))
        );

        return skill;
    }

    /*
    Returns every single skill object as an array of skillNodes.
     */
    public ArrayList<SkillNode> getAllSkillNodes() throws IOException {
        ArrayList<SkillNode> allSkillNodes = new ArrayList<>();

        ArrayList<String> allSkillIds = getAllIDs("skills");

        for (String id : allSkillIds) {
            allSkillNodes.add(getSkillNode(id));
        }

        return allSkillNodes;
    }

    /*
    Returns a song in the form of a SongItem instance.
     */
    public SongItem getSongItem(String id) throws IOException {
        ArrayList<String> songContents = getObjectContentsArray(id);
        if (songContents == null)
            return null;

        BufferedReader strReader = new BufferedReader(new StringReader(songContents.get(2)));
        ArrayList<String> genres = new ArrayList<>();

        //Log.d("myInfoTag", "in getSkillNode. id: " + id + ", skillContents.get(3): " + skillContents.get(3));

        String line = null;
        while ((line = strReader.readLine()) != null) {
            genres.add(line);
        }

        strReader = new BufferedReader(new StringReader(songContents.get(3)));
        ArrayList<String> topSkillIds = new ArrayList<>();

        while ((line = strReader.readLine()) != null) {
            topSkillIds.add(line.substring(0, 7));
        }

        SongItem song = new SongItem(
                songContents.get(0),
                songContents.get(1),
                songContents.get(4)
        );
        song.setGenres(genres);
        song.setTopSkillIds(topSkillIds);

        return song;
    }

    /*
    Returns every song in an array of songItems.
     */
    public ArrayList<SongItem> getAllSongItems() throws IOException {
        ArrayList<SongItem> allSongs = new ArrayList<>();

        ArrayList<String> allSongIds = getAllIDs("songs");

        for (String id : allSongIds) {
            allSongs.add(getSongItem(id));
        }

        return allSongs;
    }

    /*
    Returns the id of the skill associated with a given task (lesson).
     */
    public String getSkillIdForTaskId(String taskId) throws IOException {
        ArrayList<SkillNode> allSkills = getAllSkillNodes();
        for (SkillNode skill : allSkills) {
            if (skill.getTasks().contains(taskId)) {
                return skill.getId();
            }
        }
        return null;
    }
}
