package fr.polytech.marecal;

import com.sun.istack.internal.NotNull;
import fr.polytech.marecal.validator.FormValidator;
import fr.polytech.marecal.validator.FormValidatorCssClass;
import fr.polytech.marecal.validator.InvalidationReason;
import javafx.scene.control.*;
import javafx.scene.web.HTMLEditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Robin on 23/05/2017. <br>
 * This class represent a form with different fields
 */
public class FormMap extends HashMap<String, FormMap.Field>
{
    /** The submit button of the form */
    private Button submitButton;

    /** the number of unvalidated fields */
    private int nbOfUnvalidatedFields = 0;

    /**
     * Add a field to the form.<br>
     * If it's a {@link TextInputControl}, the is tested on every text changes
     *
     * @param key       the HashMap key
     * @param valueType the type of the value (e.g. firstname, date, datetime...)
     * @param field     the field
     */
    public void add (@NotNull String key, @NotNull FieldValueType valueType, @NotNull Control field)
    {
        Field formFieldObject = new Field(field, valueType);
        super.put(key, formFieldObject);

        if (field instanceof TextInputControl)
        {
            ((TextInputControl) field).textProperty()
                                      .addListener((obs, oldV, newV) -> FormValidator.validateField(formFieldObject));
        }
    }

    /**
     * Add a field to the form.<br>
     * If it's a {@link TextInputControl}, the is tested on every text changes
     *
     * @param key        the HashMap key
     * @param valueType  the type of the value (e.g. firstname, date, datetime...)
     * @param field      the field
     * @param isRequired true if the field should not be empty, false otherwise
     */
    public void add (@NotNull String key, @NotNull FieldValueType valueType, @NotNull Control field, boolean isRequired)
    {
        add(key, valueType, field);
        get(key).setRequired(isRequired);
    }

    /**
     * Set the submit button of the form <br>
     * Doing this allows the form validator to disable the button
     * if at least one field is unvalidated.
     *
     * @param submitButton the submit button of the form
     */
    public void setSubmitButton (@NotNull Button submitButton)
    {
        this.submitButton = submitButton;
    }


    /**
     * Update the state (enabled or disabled) of the submit button
     */
    private void updateSubmitButtonState ()
    {
        if (submitButton != null)
        {
            submitButton.setDisable(nbOfUnvalidatedFields != 0);
        }
    }

    /**
     * Get the submit button of the form
     *
     * @return the submit button of the form
     */
    public Button getSubmitButton ()
    {
        return submitButton;
    }

    /**
     * Reset every fields at it's default content
     */
    public void clearAll ()
    {
        forEach((s, field) ->
        {
            Control control = field.getField();

            if (control instanceof TextInputControl)
            {
                ((TextInputControl) control).clear();
            }
            else if (control instanceof HTMLEditor)
            {
                ((HTMLEditor) control).setHtmlText("");
            }
            else if (control instanceof ToggleButton)
            {
                ((ToggleButton) control).setSelected(false);
            }
            else if (control instanceof CheckBox)
            {
                ((CheckBox) control).setSelected(false);
            }
            else if (control instanceof ComboBox)
            {
                ((ComboBox<Object>) control).getSelectionModel()
                                            .clearSelection();
            }
        });
    }

    /**
     * Inner class representing a field of a form
     */
    public class Field
    {
        /** The field */
        private Control field;

        /** The type of the value (e.g. Name, Date, Datetime...) */
        private FieldValueType valueTypes = FieldValueType.UNDEFINED;

        /** Has the field been validated or not */
        private boolean validated = false;

        /** Is field required or not */
        private boolean required = false;

        /** The list of invalidation states */
        private List<InvalidationReason> invalidationReasonList = new ArrayList<>();

        /** Default constructor */
        public Field ()
        {
        }

        /**
         * 3 parameters constructor
         *
         * @param field     the field extending {@link Control}
         * @param valueType the type of the value (e.g. Name, Date, Datetime...)
         */
        public Field (Control field, FieldValueType valueType)
        {
            this();
            this.field = field;
            this.valueTypes = valueType;
        }


        /**
         * Reload the tooltip of the field based on the invalidation reasons
         */
        private void reloadTooltip ()
        {
            if (invalidationReasonList.isEmpty())
            {
                field.setTooltip(null);
            }
            else
            {
                String text = "";
                for (InvalidationReason reason : invalidationReasonList)
                {
                    text += System.lineSeparator();
                    text += "- " + FormValidator.getInvalidationReasonMessageMap()
                                                .getMessage(reason);
                }

                text = text.substring(System.lineSeparator()
                                            .length());
                field.setTooltip(new Tooltip(text));
            }
        }

        /**
         * 1 parameter constructor
         *
         * @param field the field extending {@link Control}
         */
        public Field (Control field)
        {
            this.field = field;
        }

        /**
         * Get the field as {@link Control} instance
         *
         * @return the field as {@link Control} instance
         */
        public Control getField ()
        {
            return field;
        }

        /**
         * Set the field
         *
         * @param field the field
         */
        public void setField (@NotNull Control field)
        {
            this.field = field;
        }

        /**
         * Get the type of the value (e.g. Name, Date, Datetime...)
         *
         * @return the type of the value (e.g. Name, Date, Datetime...)
         */
        public FieldValueType getValueTypes ()
        {
            return valueTypes;
        }

        /**
         * Set the type of the value (e.g. Name, Date, Datetime...)
         *
         * @param valueTypes the type of the value (e.g. Name, Date, Datetime...)
         */
        public void setValueTypes (@NotNull FieldValueType valueTypes)
        {
            this.valueTypes = valueTypes;
        }

        /**
         * Mark the field as validated <br>
         * If the field was previously unvalidated, the unvalidation CSS class is removed
         */
        public void validate ()
        {
            if (!validated)
            {
                nbOfUnvalidatedFields--;
            }

            field.getStyleClass()
                 .remove(FormValidatorCssClass.INPUT_INVALID);
            validated = true;


            updateSubmitButtonState();
        }

        /**
         * Mark the field as unvalidated <br>
         * The field style is changed using CSS class
         */
        public void unValidate ()
        {
            if (validated)
            {
                nbOfUnvalidatedFields++;
            }

            field.getStyleClass()
                 .add(FormValidatorCssClass.INPUT_INVALID);
            validated = false;
            updateSubmitButtonState();
        }

        /**
         * Know if the field has been validated or not
         *
         * @return true if it was validated, false otherwise
         */
        public boolean isValidated ()
        {
            return validated;
        }

        /**
         * Know if the field is required or not
         *
         * @return true if the field is required, false otherwise
         */
        public boolean isRequired ()
        {
            return required;
        }

        /**
         * Set the field as required or not
         *
         * @param required true if required, false otherwise
         */
        public void setRequired (boolean required)
        {
            this.required = required;
        }

        /**
         * Get the list of invalidation reasons
         *
         * @return the list of invalidation reasons
         */
        public List<InvalidationReason> getInvalidationReasonList ()
        {
            return new ArrayList<>(invalidationReasonList);
        }

        /**
         * Add an invalidation reason <br>
         * This reloads the field's tooltip
         *
         * @param reason the invalidation reason to add
         */
        public void addInvalidationReason (InvalidationReason reason)
        {
            if (!invalidationReasonList.contains(reason))
            {
                invalidationReasonList.add(reason);
                reloadTooltip();
            }
        }


        /**
         * Remove an invalidation reason <br>
         * This reloads the field's tooltip
         *
         * @param reason the invalidation reason to remove
         */
        public void removeInvalidationReason (InvalidationReason reason)
        {
            invalidationReasonList.remove(reason);
            reloadTooltip();
        }
    }
}
