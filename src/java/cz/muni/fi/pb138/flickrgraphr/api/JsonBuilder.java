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
}
