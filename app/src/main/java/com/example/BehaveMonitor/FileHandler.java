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

    public static void setRootDirectory() {
        rootDir = new File(Environment.getExternalStorageDirectory(), "Behaviour Monitor").getAbsolutePath();
    }

    public static File getSessionsDirectory() {
        return new File(rootDir, "Sessions");
    }

    public static File getTemplateDirectory() {
        return new File(rootDir, "Templates");
    }

//    public static String getTemplateDirectoryPath() {
//        return new File(rootDir, "Templates").getAbsolutePath();
//    }

    /**
     * Checks the required folders have been created.
     */
    public static void checkFoldersExist() {
        if (rootDir == null) {
            setRootDirectory();
        }

        File file = getTemplateDirectory();
        file.mkdirs(); // Creates the specified folder if it doesn't already exist. Will also create any missing directories.

        file = new File(getSessionsDirectory(), "Default");
        file.mkdirs();
    }

    public static String[] getFolders() {
        File projDir = getSessionsDirectory();
        String[] folders = projDir.list();
        if (folders.length == 0) {
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
        }
    }

    public static void deleteFolder(String folder) {
        File projDir = new File(getSessionsDirectory(), folder);
        deleteDirectory(projDir);

//        if (projDir.exists()) {
//            if (!projDir.delete()) {
//                Log.e("Behave", "Failed to delete session folder!");
//            }
//        }

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
    public static boolean checkSessionName(String activeFolder, String session) {
        File file = new File(getSessionsDirectory(), activeFolder + File.separator + session + ".txt");
        return !file.exists();
    }

    public static boolean saveSession(String folder, Session session) {
        String name = session.getName() + ".csv";
        File file = new File(getSessionsDirectory(), folder + File.separator + name);

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
            String[] tmpNames = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                String[] parts = files[i].split("\\.");
                if (parts.length > 1) {
                    if (parts[1].equals("template")) {
                        tmpNames[i] = parts[0];
                    }
                }
            }

            return tmpNames;
        }
    }

    public static boolean templateExists(String template) {
        final File file = new File(getTemplateDirectory(), template + ".template");
        return file.exists();
    }

    public static void saveTemplate(Context context, final Template newTemp) {
        final File file = new File(getTemplateDirectory(), newTemp.name + ".template");

        //Check if the template already exists.
//        if(file.exists()) {
//            AlertDialog.Builder alert = new AlertDialog.Builder(context);
//
//            alert.setTitle("Overwrite Template");
//            alert.setMessage("Warning a template with this name already exists, do you want to overwrite it?");
//
//            //If yes overwrite
//            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    saveTemplateFile(file, newTemp);
//                }
//            });
//
//            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    // Canceled.
//                }
//            });
//
//            alert.show();
//
//        } else {
            saveTemplateFile(file, newTemp);
//        }
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
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
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
            }
        }
    }


}
