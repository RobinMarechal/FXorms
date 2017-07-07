package fr.polytech.marecal.validator;

import java.util.HashMap;

/**
 * Created by Robin on 07/07/2017.
 * A map of invalidation reason's messages
 */
public class InvalidationReasonMessageMap extends HashMap<InvalidationReason, String>
{
    /**
     * Default constructor
     */
    InvalidationReasonMessageMap ()
    {
        put(InvalidationReason.INCORRECT_VALUE, "The value is incorrect");
        put(InvalidationReason.REQUIRED_FIELD, "The field is required");
        put(InvalidationReason.UNKNOWN, "Unknown error");
    }

    /**
     * Set the message for an invalidation reason
     *
     * @param reason  the reason
     * @param message the message for this reason
     * @return the old message
     */
    public String setMessage (InvalidationReason reason, String message)
    {
        return put(reason, message);
    }

    /**
     * Get the invalidation message for a reason
     *
     * @param reason the reason
     * @return The invalidation message for a reason
     */
    public String getMessage (InvalidationReason reason)
    {
        return get(reason);
    }
}
