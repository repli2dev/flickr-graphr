/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.flickrgraphr.api;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.commons.validator.routines.EmailValidator;




/**
 * This class can recognize type of user identification
 * @author mantaexx
 */
public class Validator {

    
  
    
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * This method recognizes id type from string
     * @param id string with user identification - eg. display name or e-mail
     * @return id type, or invalidId
     */
    public static IdType getIdType(String id) {

        if (id == null && id.equals("")) {
            return IdType.invalidId;
        }

        if (isEmail(id)) {
            return IdType.email;
        }

        if (isUserId(id)) {
            return IdType.flickrId;
        }
        
        if(id.length() > 3)
            return IdType.name;

        return IdType.invalidId;
    }

    /**
     * Determines if given string is e-mail address
     * @param id string with e-mail
     * @return true if given string is e-mail
     */
    public static boolean isEmail(String id) {
        EmailValidator eVal = EmailValidator.getInstance();
        return eVal.isValid(id);
    }
    
    /**
     * Determines if given string is flickr user ID
     * @param id string with id
     * @return returns true if given string is sth. like 12345678@N12
     */
    public static boolean isUserId(String id) {
        Pattern uIdPattern = Pattern.compile("\\d{8}[@][A-Z]\\d{2}");
        Matcher matcher = uIdPattern.matcher(id);

        return matcher.matches();
    }
    
    public static boolean isDate(String date){
        DateValidator dv = DateValidator.getInstance();
        return dv.isValid(date, DATE_FORMAT);
    }
    
    public static Date getDate(String date){
        DateValidator dv = DateValidator.getInstance();
        return dv.validate(date, DATE_FORMAT);
        
    }
}
