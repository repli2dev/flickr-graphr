package cz.muni.fi.pb138.flickrgraphr.api;

/**
 * Helper class which builds JSON objects to be used in Graphr API
 *
 * @author mantaexx
 */
public class JsonBuilder {

	/**
	 * Returns JSON object for error
	 * @param errCode Error code
	 * @param errMessage Message of error
	 * @return JSON object with stat=fail and details of error
	 */
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

	/**
	 * Enum of error types
	 */
	public static enum errorType {

		DataNotExist,
		UserNotExist,
		IncorrectParameters
	}

	/**
	 * Return code of error from given error type
	 * @param type Type of error
	 * @return Code of error (GUI depends on this)
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
	 * Returns error message for given error type
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

	/**
	 * Returns JSON object with error from given error type
	 * @param type Error type
	 * @return String with JSON object
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
}
