package in.kriger.newkrigercampus.extras;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by poojanrathod on 6/29/17.
 */

public class TypefaceUtil {



    public Typeface getfont(Context context){
        return Typeface.createFromAsset(context.getAssets(), "fonts/open-sans-light.ttf");
    }

    public Typeface getfontbold(Context context){
        return Typeface.createFromAsset(context.getAssets(), "fonts/open-sans-regular.ttf");
    }
}
