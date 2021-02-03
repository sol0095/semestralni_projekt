package cz.vsb.semestralni_projekt.grammar_tool;

import org.apache.commons.lang3.StringEscapeUtils;

public class DataPreparator {

    public String encodeXML(String str){
        return encodeString(replaceXml(str));
    }

    private String replaceXml(String line) {
        return StringEscapeUtils.unescapeXml(line);
    }

    private String encodeString(String line){
        while(line.contains("&amp;"))
            line = line.replaceAll("&amp;", "&");

        while(line.contains("&amp"))
            line = line.replaceAll("&amp", "&");

        line = line.replaceAll("&lt;","<").replaceAll("&gt;",">");
        line = line.replaceAll("&lt","<").replaceAll("&gt",">")
                .replaceAll("\n", " ").replaceAll("\r", " ").toUpperCase();
        return line;
    }

    public Boolean containsSelect(String str){
        if(str.indexOf("select") != -1 && str.indexOf("from") != -1) {
            return true;
        }
        return false;
    }
}
