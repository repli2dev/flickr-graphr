package cz.muni.fi.pb138.flickrgraphr.api;

/**
 * Helper class which builds JSON objects to be used in Graphr API
 *
 * @author mantaexx
 */
public class JsonBuilder {

//    public static String getErrorJson(int errCode, String errMessage) {
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("{");
//        sb.append("\"stat\" : \"fail\",");
//        sb.append("\"error\": {");
//        sb.append("\"code\" : ").append(errCode).append(",");
//        sb.append("\"message\" : \"").append(errMessage).append("\"");
//        sb.append("}");
//        sb.append("}");
//
//        return sb.toString();
//    }
    /**
     * Enum of error types
     */
    public static enum errorType {

        DataNotExist,
        UserNotExist,
        IncorrectParameters
    }

    /**
     * Int representation of error
     *
     * @param type enum with error type
     * @return int value 1 - Data not exist 2 - User not exist 3 - Incorrect
     * parameters 0 - undefined error
     */
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



    /**
     * This method creates JSON representation of errror
     *
     * @param type error type
     * @return string containing JSON
     */
    public static String getErrorJsonForError(errorType type) {
        String result = String.format(
                "{\"stat\" : \"fail\","
                + "\"error\" : {"
                + "\"code\" : %d,"
                + "\"message\" : \"%s\"}"
                + "}",
                getCodeForError(type),
                getMessageForError(type));
        return result;
    }


    /**
     * Returns error message for given error type
     *
     * @param type Error type
     * @return Description of error
     */
    public static String getMessageForError(errorType type) {
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


}
