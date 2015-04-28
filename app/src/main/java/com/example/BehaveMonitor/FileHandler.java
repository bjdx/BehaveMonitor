//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static String rootDir = null;

    private static Context context;

    public static void setRootDirectory(Context c) {
        rootDir = new File(Environment.getExternalStorageDirectory(), "Chicken Scratch").getAbsolutePath();
        context = c;
    }

    public static File getSessionsDirectory() {
        return new File(rootDir, "Sessions");
    }

    public static File getTemplateDirectory() {
        return new File(rootDir, "Templates");
    }

    /**
     * Checks the required folders have been created.
     */
    public static void checkFoldersExist(Context context) {
        if (rootDir == null) {
            setRootDirectory(context);
        }

        File file = getTemplateDirectory();
        file.mkdirs(); // Creates the specified folder if it doesn't already exist. Will also create any missing directories.

        file = new File(getSessionsDirectory(), "Default");
        if(!file.mkdirs()){
            Log.e("Behave", "Failed to create Default session folder.");
        }
    }

    public static String[] getFolders() {
        File projDir = getSessionsDirectory();
        String[] folders = projDir.list();
        if (folders == null || folders.length == 0) {
            Log.e("Behave", "No folders found!");
        }

        return folders;
    }

    public static List<Integer> getSessionCounts() {
        String[] folderNames = getFolders();

        List<Integer> sessionCounts = new ArrayList<>();
        for (String name : folderNames) {
            File folder = new File(getSessionsDirectory(), name);
            String[] sessions = folder.list();
            sessionCounts.add(sessions.length);
        }

        return sessionCounts;
    }

    public static void createNewFolder(String folderName) {
        File projDir = new File(getSessionsDirectory(), folderName);
        if (!projDir.mkdirs()) {
            Log.e("ERROR", "Failed to create directory!");
        } else {
            DBHelper db = DBHelper.getInstance(context);
            db.setFolder(folderName);
        }
    }

    public static List<File> getSessions() {
        List<File> sessions = new ArrayList<>();
        File sessionsDirectory = getSessionsDirectory();
        return findSessions(sessions, sessionsDirectory);
    }

    public static List<File> findSessions(List<File> sessions, File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] contents = file.listFiles();
                for (File content : contents) {
                    if (content.isDirectory()) {
                        sessions = findSessions(sessions, content);
                    } else {
                        String[] parts = content.getName().split("\\.(?=[^\\.]+$)");
                        if (parts.length > 1) {
                            if (parts[1].equals("csv")) {
                                sessions.add(content);
                            }
                        }
                    }
                }
            } else {
                sessions.add(file);
            }
        }

        return sessions;
    }

    public static void deleteSession(File session) {
        if (!session.delete()) {
            Log.e("Behave", "Failed to delete session: " + session.getName());
        }
    }

    public static void deleteFolder(String folder) {
        File projDir = new File(getSessionsDirectory(), folder);
        deleteDirectory(projDir);

        DBHelper db = DBHelper.getInstance(context);
        db.removeFolder(folder);

        if ("Default".equals(folder)) { // If default folder, remake folder
            File file = new File(getSessionsDirectory(), "Default");
            file.mkdirs();
        }
    }

    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] contents = directory.listFiles();
            for (File content : contents) {
                if (content.isDirectory()) {
                    deleteDirectory(content);
                } else {
                    if (!content.delete()) {
                        Log.e("Behave", "Failed to delete file: " + content.getName());
                    }
                }
            }

            if (!directory.delete()) {
                Log.e("Behave", "Failed to delete file: " + directory.getName());
            }
        }
    }

    /**
     * Checks to see if a session with the given name already exists within the given folder.
     * @param activeFolder the folder to check in
     * @param session the session name to check for
     * @return true if the session name is unique within the folder, false otherwise.
     */
    public static boolean checkSessionName(String activeFolder, String session, String location) {
        File file = new File(getSessionsDirectory(), activeFolder + File.separator + session + "_" + location + ".csv");
        return !file.exists();
    }

    public static String getVersionName(String folder, String name) {
        File file = new File(getSessionsDirectory(), folder + File.separator + name + ".csv");
        if (!file.exists()) {
            return name;
        }

        int i = 1;
        while (true) {
            String number = i < 10 ? "0" + i : "" + i;
            file = new File(getSessionsDirectory(), folder + File.separator + name + " (v" + number + ").csv");
            if (file.exists()) {
                i++;
            } else {
                return file.getName();
            }
        }
    }

    public static boolean saveSession(String folder, Session session, String name) {
        File file = new File(getSessionsDirectory(), folder + File.separator + name + ".csv");

        try {
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.write(session.toString());
            printWriter.close();
            return true;
        } catch (FileNotFoundException e) {
            Log.e("Behave", "Failed to save session, couldn't find file");
        }
        return false;
    }

    public static String[] getTemplateNames() {
        File projDir = getTemplateDirectory();
        String[] files = projDir.list();
        if (files.length == 0) {
            Log.e("Behave", "No templates exist.");
            return null;
        } else {
            List<String> fileNames = new ArrayList<>();
            for (String file : files) {
                String[] parts = file.split("\\.(?=[^\\.]+$)");
                if (parts.length > 1) {
                    if (parts[1].equals("template")) {
                        fileNames.add(parts[0]);
                    }
                }
            }

            return fileNames.toArray(new String[fileNames.size()]);
        }
    }

    public static boolean templateExists(String template) {
        final File file = new File(getTemplateDirectory(), template + ".template");
        return file.exists();
    }

    public static void saveTemplate(Context context, final Template newTemp) {
        final File file = new File(getTemplateDirectory(), newTemp.name + ".template");
        if (file.exists()) {
            file.delete();
        }

        saveTemplateFile(file, newTemp);
    }

    private static void saveTemplateFile(File file, Template newTemp) {
        //Convert template to string the write it and read it back to check.
        String string = newTemp.toString();

        //Save File
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(string.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Read back file and check against original.
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int)file.length()];

            fis.read(data);
            String tmpIN = new String(data,"UTF-8");
            Log.d("Behave","Read Back Template: "+ tmpIN);
            Log.d("Behave","Template Saved Correctly: "+tmpIN.equals(string));

            DBHelper db = DBHelper.getInstance(context);
            db.setTemplate(newTemp.toString());

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // Separate catch block for IOException because other exceptions are encapsulated by it.
            Log.e("Behave", "IOException not otherwise caught");
            e.printStackTrace();
        }
    }

    public static String readTemplate(String name) {
        File file = new File(getTemplateDirectory(), name + ".template");
        final StringBuilder storedString = new StringBuilder();
        try {
            Reader dataIO = new FileReader(file);
            BufferedReader br = new BufferedReader(dataIO);
            String strLine;
            if ((strLine = br.readLine()) != null) {
                storedString.append(strLine);
            }

            br.close();
            dataIO.close();

            return storedString.toString();
        } catch  (Exception e) {
            Log.e("Behave", "Error trying to read template");
        }

        return "";
    }

    public static void deleteTemplate(String template) {
        File projDir = new File(getTemplateDirectory(), template + ".template");
        if (projDir.exists()) {
            if (!projDir.delete()) {
                Log.e("ERROR", "Failed to delete template!");
            } else {
                DBHelper db = DBHelper.getInstance(context);
                db.removeTemplate(template);
            }
        }
    }
}
