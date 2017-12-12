package fr.polytech.marechal.validator;

/**
 * Created by Robin on 07/07/2017.<br>
 * The reasons of a field's invalidation
 */
public enum InvalidationReason
{
    /** The field is required */
    REQUIRED_FIELD,
    /** The field's value is incorrect */
    INCORRECT_VALUE,
    /** Unknown invalidation reason */
    UNKNOWN
    ;
}
