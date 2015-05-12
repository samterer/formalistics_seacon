package ph.com.gs3.formalistics.global.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Ervinne on 5/6/2015.
 */
public class EncryptionUtility {

    public static String encrypt(String rawString) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(rawString.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return rawString;
        }
    }

}
