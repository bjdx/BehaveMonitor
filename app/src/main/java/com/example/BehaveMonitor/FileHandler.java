package com.example.BehaveMonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class FileHandler {

    private static String rootDir = null;

    public static void setRootDirectory() {
        rootDir = new File(Environment.getExternalStorageDirectory(), "Behaviour Monitor").getAbsolutePath();
    }

    public static String getSessionsDirectory() {
        return new File(rootDir, "Sessions").getAbsolutePath();
    }

    public static String getTemplateDirectory() {
        return new File(rootDir, "Templates").getAbsolutePath();
    }

    /**
     * Checks the required folders have been created.
     */
    public static void checkFoldersExist() {
        if (rootDir == null) {
            setRootDirectory();
        }

        File file = new File(rootDir, "Templates");
        file.mkdirs(); // Creates the specified folder if it doesn't already exist. Will also create any missing directories.

        file = new File(rootDir, "Sessions" + File.separator + "Default");
        file.mkdirs();
    }

    public static void createNewFolder(String folderName) {
        File projDir = new File(getSessionsDirectory(), folderName);
        if (!projDir.mkdirs()) {
            Log.e("ERROR", "Failed to create directory!");
        }
    }

    public static void deleteFolder(String folder) {
        File projDir = new File(getSessionsDirectory(), folder);
        if (projDir.exists()) {
            if (!projDir.delete()) {
                Log.e("Behave", "Failed to delete session folder!");
            }
        }
    }

    public static boolean saveSession(String folder, Session session) {
        String name = session.getName() + ".txt";
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

    public static void saveTemplate(Context context, final Template newTemp) {
        final File file = new File(getTemplateDirectory(), newTemp.name + ".template");

        //Check if the template already exists.
        if(file.exists()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);

            alert.setTitle("Overwrite Template");
            alert.setMessage("Warning a template with this name already exists, do you want to overwrite it?");

            //If yes overwrite
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    saveTemplateFile(file, newTemp);
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();

        } else {
            saveTemplateFile(file, newTemp);
        }
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
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
