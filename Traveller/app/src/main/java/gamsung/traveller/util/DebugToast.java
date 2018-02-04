package gamsung.traveller.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import gamsung.traveller.BuildConfig;

/**
 * Created by shin on 2018. 1. 25..
 */

public class DebugToast {

    public static void show(Context context, String msg){

        if(BuildConfig.DEBUG){
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showOnThread(final Context context, final String msg){

        if(BuildConfig.DEBUG){

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
