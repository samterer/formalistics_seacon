package ph.com.gs3.formalistics.global.utilities;

import android.text.TextUtils;

public class Validator {

    public static final int VALID = 1;
    public static final int INVALID = 2;
    public static final int TOO_SHORT = 3;
    public static final int EMPTY = 4;

    public static int validateNonEmpty(CharSequence target) {

        int result = EMPTY;

        if (target != null && target.toString().trim().length() > 0) {
            result = VALID;
        }

        return result;
    }

    public static int validateEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return EMPTY;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches() ? VALID : INVALID;
        }
    }

    public static int validatePassword(CharSequence target) {

        if (TextUtils.isEmpty(target)) {
            return EMPTY;
        } else {
//			return target.length() > 4 ? VALID : TOO_SHORT;
            return VALID;
        }

    }

}
