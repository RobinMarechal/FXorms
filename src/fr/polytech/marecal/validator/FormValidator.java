package fr.polytech.marecal.validator;

import com.sun.istack.internal.NotNull;
import fr.polytech.marecal.FieldTypes;
import fr.polytech.marecal.FieldValueType;
import fr.polytech.marecal.FormMap;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;
import javafx.scene.web.HTMLEditor;

/**
 * Created by Robin on 21/05/2017.<br>
 * A form validator
 */
public class FormValidator
{
    /** The form to validate */
    private FormMap form;

    /** The invalidation messages map */
    private static InvalidationReasonMessageMap invalidationReasonMessageMap = new InvalidationReasonMessageMap();

    /** Default constructor */
    public FormValidator ()
    {
    }

    /**
     * Get the invalidation messages map
     * @return the invalidation messages map
     */
    public static InvalidationReasonMessageMap getInvalidationReasonMessageMap ()
    {
        return invalidationReasonMessageMap;
    }

    /**
     * 1 parameter constructor
     *
     * @param form the form to validate
     */
    public FormValidator (FormMap form)
    {
        this.form = form;
    }

    /**
     * Get the form
     *
     * @return the form
     */
    public FormMap getForm ()
    {
        return form;
    }

    /**
     * Set the form
     *
     * @param form the form
     */
    public void setForm (@NotNull FormMap form)
    {
        this.form = form;
    }

    /**
     * Performs the validation of the form <br>
     * Each field is tested based on the fields' {@link FieldTypes} and their {@link FieldValueType} with regexp test
     *
     * @return true if every fields were validated, false otherwise
     */
    public boolean validateForm ()
    {
        boolean result = true;

        for (FormMap.Entry<String, FormMap.Field> entry : form.entrySet())
        {
            if (!validateField(entry.getValue()))
            {
                result = false;
            }
        }

        return result;
    }

    /**
     * Test if a field is valid or not <br>
     * Each field is tested based on it's {@link FieldTypes} and it's {@link FieldValueType} with regexp  <br>
     * If a field is unvalidated, a CSS style is applied to it.
     *
     * @param formField the form field instance
     * @return true if the content is valid, false otherwise
     */
    public static boolean validateField (@NotNull FormMap.Field formField)
    {
        final Control        control    = formField.getField();
        final FieldValueType valueTypes = formField.getValueTypes();

        if (control instanceof TextInputControl || control instanceof HTMLEditor)
        {
            try
            {
                formField.getInvalidationReasonList().remove(InvalidationReason.UNKNOWN);

                String content;

                if (control instanceof TextInputControl)
                {
                    content = ((TextInputControl) control).getText();
                }
                else
                {
                    content = ((HTMLEditor) control).getHtmlText();
                }

                if(content.isEmpty() && formField.isRequired())
                {
                    formField.unValidate();
                    formField.addInvalidationReason(InvalidationReason.REQUIRED_FIELD);
                    return false;
                }
                else
                {
                    formField.validate();
                    formField.removeInvalidationReason(InvalidationReason.REQUIRED_FIELD);
                }

                if (content.matches(valueTypes.getRegexp()))
                {
                    formField.validate();
                    formField.removeInvalidationReason(InvalidationReason.INCORRECT_VALUE);
                    return true;
                }
                else
                {
                    formField.unValidate();
                    formField.addInvalidationReason(InvalidationReason.INCORRECT_VALUE);
                    return false;
                }
            }
            catch (ClassCastException e)
            {
                formField.unValidate();
                formField.addInvalidationReason(InvalidationReason.UNKNOWN);
                return false;
            }
        }

        return true;
    }
}
