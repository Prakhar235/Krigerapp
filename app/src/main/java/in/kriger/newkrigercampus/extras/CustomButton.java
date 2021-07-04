package in.kriger.newkrigercampus.extras;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * Created by poojanrathod on 7/26/17.
 */

public class CustomButton extends AppCompatButton {

    public CustomButton(Context context) {
        super(context);
        init();
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
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
}
