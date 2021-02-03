package cz.vsb.semestralni_projekt.grammar_tool;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLFileReader implements PropertyLoader, ConsolePrinter {
    private BufferedReader reader = null;
    private boolean expression = false;
    private String tag;
    private int rowID;
    private boolean endOfFile = false;
    private boolean canTakeCodeElements = false;
    private String tagElement = "Tags=\"&lt;";
    private String rowIdElement = "<row Id=\"";
    private String codeElementStart = "&lt;code&gt;", getCodeElementEnd = "&lt;/code&gt;";
    private String parentIDElement = "ParentId=\"";
    private String[] tags;
    private String line = "";
    private HashSet<Integer> ids = new HashSet<>();
    private ArrayList<String> codeElemets = new ArrayList<>();

    public boolean startReading(){
        tags = loadProperty("grammar.tags").split(" ");
        try {
            writeToConsole(Colors.WHITE, "Starting to read...");
            reader = new BufferedReader(new FileReader(loadProperty("xml.input")));
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writeToConsole(Colors.RED, "Input file was not found, program will be stopped!");
        return false;
    }

    public void stopReading(){
        try {
            if(reader != null)
                reader.close();
            writeToConsole(Colors.GREEN, "Reading finished! ...file writing continue...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Boolean searchedTag(String tag){
        for(String t : tags){
            if(tag.contains(t))
                return true;
        }
        return false;
    }

    private void checkForRow(){
        canTakeCodeElements = false;

        if(line.indexOf(rowIdElement) < 0)
            return;

        tag = getOccurence(tagElement +"(.*?)\"", line);
        boolean hasTag = hasOccurence(tag);

        if(hasTag && searchedTag(tag))
            getRowID(line);
        else if(!hasTag){
            String parID = getOccurence(parentIDElement +"(.*?)\"", line);
            if(parID == null)
                return;
            int parentID = Integer.valueOf(parID.replaceAll("\\D+",""));

            if(ids.contains(parentID))
              getRowID(line);
            else
                return;
        }

        if(canTakeCodeElements)
           getCodeElements(line);
    }

    private void getRowID(String row){
        rowID = Integer.valueOf(getOccurence(rowIdElement +"(.*?)\"", row).replaceAll("\\D+",""));
        ids.add(rowID);
        canTakeCodeElements = true;
    }

    private void getCodeElements(String row){
        getAllCodeElements(codeElementStart + "(.*?)" + getCodeElementEnd, row);

        if(codeElemets.size() > 0)
            expression = true;
    }

    private boolean hasOccurence(String str){
        if(str != null)
            return true;

        return false;
    }

    private String getOccurence(String regex, String str){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if(matcher.find())
            return matcher.group();

        return null;
    }

    private void getAllCodeElements(String regex, String row){
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(row);

        while (m.find()) {
            String code = m.group();
            codeElemets.add(code.substring(codeElementStart.length(), code.length() - getCodeElementEnd.length()));
        }
    }

    public boolean readFile(){
        if(codeElemets.size() == 0)
            expression = false;

        try{
            while (!expression && (line = reader.readLine()) != null) {
                checkForRow();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(line == null && !expression){
            endOfFile = true;
            stopReading();
            ids = null;
            return false;
        }
        return true;
    }

    public boolean getEndOfFile(){
        return endOfFile;
    }

    public String getTag() {
        return tag;
    }

    public String getCode() {
        return codeElemets.remove(0);
    }

    public int getRowID() {
        return rowID;
    }
}
