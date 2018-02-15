package gamsung.traveller.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by shin on 2018. 2. 15..
 */

public class ImageSupporter {


    public static ArrayList<String> getPathOfAllImages(Context context)
    {
        ArrayList<String> result = new ArrayList<>();
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int columnDisplayName = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);

        if(cursor != null) {
            while (cursor.moveToNext()) {
                String pathOfImage = cursor.getString(columnIndex);
//                String nameOfFile = cursor.getString(columnDisplayName);

                if (!TextUtils.isEmpty(pathOfImage))
                    result.add(pathOfImage);
            }
            cursor.close();
        }

        return result;
    }
}
