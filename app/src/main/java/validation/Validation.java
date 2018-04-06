package validation;
import android.text.Editable;
import android.widget.EditText;

/**
 * Created by kanikasharma on 29/01/18.
 */

public class Validation {
    public static boolean handleEmptyField (Editable value, EditText field) {
        if (value.length() == 0) {
            field.setError("Can't be empty");
            return false;
        }

        return true;

    }
    public static boolean handleSufficientLength (Editable value, EditText field, int length, String fieldName) {
        if (value.length() < length){
            field.setError(fieldName + " should be greater than or equal to " + length);
            return false;
        }
        return true;
    }

    public static boolean handleExactLength(Editable value, EditText field, int length, String fieldName) {
        if (value.length() != length) {
            field.setError(fieldName + " should be equal to " + length);
            return false;
        }
        return true;
    }
public static boolean handleRange(Editable value,EditText field, int start, int end, String fieldName){
    if (value.length()<start || value.length()>end){
        field.setError(fieldName + " should be between " + start+ " and " + end );
        return false;
    }

     return true;
}

}
