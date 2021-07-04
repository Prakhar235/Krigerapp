package in.kriger.newkrigercampus.extras;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * Created by poojanrathod on 7/26/17.
 */

public class CustomEditText extends AppCompatEditText {
    public CustomEditText(Context context) {
        super(context);
        init();

    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {


        try {

            Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto.light.ttf");
            setTypeface(myTypeface);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }



}
