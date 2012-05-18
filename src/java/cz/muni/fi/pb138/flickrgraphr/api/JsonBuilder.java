/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.flickrgraphr.api;

/**
 *
 * @author mantaexx
 */
public class JsonBuilder {

    public static String getErrorJson(int errCode, String errMessage) {

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"stat\" : \"fail\",");
        sb.append("\"error\": {");
        sb.append("\"code\" : ").append(errCode).append(",");
        sb.append("\"message\" : \"").append(errMessage).append("\"");
        sb.append("}");
        sb.append("}");

        return sb.toString();
    }

    public static enum errorType {

        DataNotExist,
        UserNotExist,
        IncorrectParameters
    }

    public static int getCodeForError(errorType type) {
        switch (type) {
            case DataNotExist:
                return 1;
            case UserNotExist:
                return 2;
            case IncorrectParameters:
                return 3;
            default:
                return 0;
        }
    }
    
    public static String getMessageForError(errorType type){
        switch (type) {
            case DataNotExist:
                return "Data not existing";
            case UserNotExist:
                return "User not existing";
            case IncorrectParameters:
                return "Incorrect parameters.";
            default:
                return "Undefined error";
        }
    }
    
    public static String getErrorJsonForError(errorType type){
        String result = String.format(
                "{\"stat\" : \"fail\","
                + "\"error\" : {"
                + "\"code\" : %d,"
                + "\"message\" : \"%s\""
                + "}",
                getCodeForError(type),
                getMessageForError(type));
        return result;
    }
}
