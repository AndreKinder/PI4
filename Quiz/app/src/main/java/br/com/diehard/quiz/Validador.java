package br.com.diehard.quiz;

import android.text.TextUtils;

/**
 * Created by Andre on 04/05/2016.
 */
public class Validador {
    public final static boolean validateEmail(String txtemail) {
        if (TextUtils.isEmpty(txtemail)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(txtemail).matches();
        }
    }
}
