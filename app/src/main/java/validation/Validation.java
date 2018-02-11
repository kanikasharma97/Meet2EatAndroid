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
    public static boolean handleSufficientLength (Editable value, EditText field, int length) {
        if (value.length() < length){
            field.setError("Password should be greater than or equal to " + length);
            return false;
        }
        return true;
    }


}
