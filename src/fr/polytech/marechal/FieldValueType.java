package fr.polytech.marechal;

import com.sun.istack.internal.NotNull;
import fr.polytech.marechal.validator.FormValidator;

import java.util.HashMap;

/**
 * Created by Robin on 23/05/2017. <br>
 * Form.Field value type. <br>
 * the type allow the {@link FormValidator} to validated (or not) a field with a regular expression
 */
public enum FieldValueType
{
    FIRSTNAME("^([A-Z]?[a-z]+)(([ -][A-Z]?[a-z]+))*$"),
    LASTNAME("^([A-Z]?[a-z]+)(([ -][A-Z]?[a-z]+))*$"),
    NAME("^\\w[\\w\\s]*$"),
    HOURS("^[0-9]{0,2}$"),
    MINUTES("^[0-9]{0,2}$"),
    SECONDS("^[0-9]{0,2}$"),
    DATE("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$"),
    TIME("^[0-9]{1,2}:[0-9]{1,2}$"),
    DATETIME("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}$"),
    EMAIL("^([\\w\\.\\-_]+)?\\w+@([\\w-_]|(\\w|-|_|(\\.[\\w-_]+)))+(\\.\\w+){1,}$"),
    URL("^https?\\:\\/\\/[-a-zA-Z0-9@:%._\\+~#=]{2,256}(\\.[a-z]{2,6})?\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)$"),
    PRICE("^-?\\d+(\\.\\d+)?$"),
    TEXT("^.*$"),
    VARCHAR("^.{0,255}$"),
    NUMBERS_INT("^-?\\d+$"),
    NUMBERS_INT_UNSIGNED("^\\d+$"),
    NUMBERS_DOUBLE("^-?\\d+(\\.\\d+)?$"),
    NUMBERS_DOUBLE_UNSIGNED("^\\d+(\\.\\d+)?$"),
    CHARACTER_LETTER("^[a-zA-Z]{1}$"),
    CHARACTER_LETTER_NUMBER("^[a-zA-Z0-9]{1}$"),
    CHARACTER_NUMBER("^\\d{1}$"),
    CHARACTER("^.{1}$"),
    UNDEFINED("^.*$");

    /** The regexp */
    private final String regexp;

    /**
     * Default constructor
     * @param regexp the regular expression
     */
    FieldValueType (@NotNull String regexp)
    {
        this.regexp = regexp;
    }

    /**
     * Get the associate regular expression
     * @return the associate regular expression
     */
    public String getRegexp ()
    {
        return regexp;
    }

    /**
     * Get the String associated to the enum value
     * @return the String associated to the enum value
     */
    @Override
    public String toString ()
    {
        return super.toString();
    }

    public void func (HashMap<String, String> map){

    }
}
