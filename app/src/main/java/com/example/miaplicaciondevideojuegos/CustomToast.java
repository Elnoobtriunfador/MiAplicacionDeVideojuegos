package com.example.miaplicaciondevideojuegos;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class CustomToast {

    public static void showToastShorter(Context context, String message, int durationInMillis) {
        final Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

        toast.show();

        new Handler().postDelayed(toast::cancel, durationInMillis);
    }
}
