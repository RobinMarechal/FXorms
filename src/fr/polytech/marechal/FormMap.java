package fr.polytech.marechal;

import com.sun.istack.internal.NotNull;
import fr.polytech.marechal.validator.FormValidator;
import fr.polytech.marechal.validator.FormValidatorCssClass;
import fr.polytech.marechal.validator.InvalidationReason;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
     * @param <T>       a class extending {@link Control}
     */
    public <T extends Control> void add (@NotNull String key, @NotNull FieldValueType valueType, @NotNull T field)
    {
        Field<T> formFieldObject = new Field<>(field, valueType);
        super.put(key, formFieldObject);

        if (field instanceof TextInputControl) {
            ((TextInputControl) field).textProperty().addListener((obs, oldV, newV) -> FormValidator.validateField(formFieldObject));
        }
        else if (field instanceof ComboBoxBase) {
            ((ComboBoxBase) field).valueProperty().addListener((observable, oldValue, newValue) -> FormValidator.validateField(formFieldObject));
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
     * @param <T>        a class extending {@link Control}
     */

    public <T extends Control> void add (@NotNull String key, @NotNull FieldValueType valueType, @NotNull T field, boolean isRequired)
    {
        add(key, valueType, field);
        get(key).setRequired(isRequired);

        if (isRequired) {
            nbOfUnvalidatedFields++;
        }

        updateSubmitButtonState();
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
        updateSubmitButtonState();
    }


    /**
     * Update the state (enabled or disabled) of the submit button
     */
    private void updateSubmitButtonState ()
    {
        if (submitButton != null) {
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
        forEach((s, field) -> {
            Control control = field.getField();

            if (control instanceof TextInputControl) {
                ((TextInputControl) control).clear();
            }
            else if (control instanceof HTMLEditor) {
                ((HTMLEditor) control).setHtmlText("");
            }
            else if (control instanceof ToggleButton) {
                ((ToggleButton) control).setSelected(false);
            }
            else if (control instanceof CheckBox) {
                ((CheckBox) control).setSelected(false);
            }
            else if (control instanceof ComboBox) {
                ((ComboBox<Object>) control).getSelectionModel().clearSelection();
            }
        });
    }

    /**
     * Define the action to trigger when the form is submitted
     *
     * @param eventHandler the event handler
     */
    public void setOnSubmit (EventHandler<ActionEvent> eventHandler)
    {
        submitButton.setOnAction(eventHandler);
    }

    /**
     * Console debugging String
     *
     * @return the debugging String
     */
    @Override
    public String toString ()
    {
        String linesep = System.lineSeparator();
        String format  = "FormMap: {%ls%s%ls%s%ls%s%ls}";

        format = format.replaceAll("%ls", linesep);

        String fields = "";
        String btn    = "";

        String infosFormat = "\tInfo: {nbFields=%d, unvalidatedFields=%d, button=%b}";
        String infos       = String.format(infosFormat, this.size(), nbOfUnvalidatedFields, submitButton != null);

        for (Entry<String, Field> entry : this.entrySet()) {
            String key   = entry.getKey();
            Field  field = entry.getValue();


            String  className = field.getField().getClass().getSimpleName();
            boolean validated = field.isValidated();
            boolean required  = field.isRequired();
            int     nbReasons = field.getInvalidationReasonList().size();
            String  value     = field.getValue().toString();

            String fieldFormat = "\tField: {key=%s, type=%s, validated=%b, required=%b, invalidationReasons=%d, value=%s}" + linesep;

            fields += String.format(fieldFormat, key, className, validated, required, nbReasons, value);
        }

        if (!fields.isEmpty()) {
            fields = fields.substring(0, fields.length() - 1);
        }

        if (submitButton != null) {
            String btnFormat = "\tSubmit: {type=%s, disabled=%b, text=%s}";
            btn += String.format(btnFormat, submitButton.getClass().getSimpleName(), submitButton.isDisabled(), submitButton.getText());
        }

        return String.format(format, infos, fields, btn);
    }

    /**
     * Checks if the map contains a set of keys
     * @param keys the keys that the map should contains
     * @return true if the map contains all the keys, false otherwise
     */
    public boolean hasKeys (String... keys)
    {
        for (String key : keys) {
            if (!this.containsKey(key)) {
                return false;
            }
        }
    }


    // ------------------------------------------------------
    // Inner class Field
    // ------------------------------------------------------

    /**
     * Inner class representing a field of a form
     *
     * @param <T> a class extending {@link Control}
     */
    public class Field<T extends Control>
    {
        /** The field */
        private T field;

        /** The type of the value (e.g. Name, Date, Datetime...) */
        private FieldValueType valueTypes = FieldValueType.UNDEFINED;

        /** Has the field been validated or not */
        private boolean validated = false;

        /** Is field required or not */
        private boolean required = false;

        /** The list of invalidation states */
        private List<InvalidationReason> invalidationReasonList = new ArrayList<>();

        /** Default constructor */
        Field ()
        {
        }

        /**
         * 3 parameters constructor
         *
         * @param field     the field extending {@link Control}
         * @param valueType the type of the value (e.g. Name, Date, Datetime...)
         */
        Field (T field, FieldValueType valueType)
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
            if (invalidationReasonList.isEmpty()) {
                field.setTooltip(null);
            }
            else {
                StringBuilder text = new StringBuilder();
                for (InvalidationReason reason : invalidationReasonList) {
                    text.append(System.lineSeparator());
                    text.append("- ").append(FormValidator.getInvalidationReasonMessageMap().getMessage(reason));
                }

                text = new StringBuilder(text.substring(System.lineSeparator().length()));
                field.setTooltip(new Tooltip(text.toString()));
            }
        }

        /**
         * Get the field as {@link Control} child instance
         *
         * @return the field as {@link Control} child instance
         */
        public T getField ()
        {
            return field;
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
         * Mark the field as validated <br>
         * If the field was previously unvalidated, the unvalidation CSS class is removed
         */
        public void validate ()
        {
            if (!validated) {
                nbOfUnvalidatedFields--;
            }

            field.getStyleClass().remove(FormValidatorCssClass.INPUT_INVALID);
            validated = true;


            updateSubmitButtonState();
        }

        /**
         * Mark the field as unvalidated <br>
         * The field style is changed using CSS class
         */
        public void unValidate ()
        {
            if (validated) {
                nbOfUnvalidatedFields++;
            }

            field.getStyleClass().add(FormValidatorCssClass.INPUT_INVALID);
            validated = false;
            updateSubmitButtonState();
        }

        /**
         * Know if the field has been validated or not
         *
         * @return true if it was validated, false otherwise
         */
        boolean isValidated ()
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
        void setRequired (boolean required)
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
            if (!invalidationReasonList.contains(reason)) {
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

        /**
         * Get item value
         *
         * @return the value of the field
         */
        public Object getValue ()
        {
            if (field instanceof TextInputControl) {
                return ((TextInputControl) field).getText();
            }
            else if (field instanceof HTMLEditor) {
                return ((HTMLEditor) field).getHtmlText();
            }
            else if (field instanceof ToggleButton) {
                return ((ToggleButton) field).isSelected();
            }
            else if (field instanceof CheckBox) {
                return ((CheckBox) field).isSelected();
            }
            else if (field instanceof ComboBox) {
                return ((ComboBox<Object>) field).getValue();
            }

            return "";
        }
    }
}
