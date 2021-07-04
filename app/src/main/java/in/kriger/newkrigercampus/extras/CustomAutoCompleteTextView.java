package in.kriger.newkrigercampus.extras;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;

/**
 * Created by poojanrathod on 12/24/17.
 */

public class CustomAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    public CustomAutoCompleteTextView(Context context) {
        super(context);
        init();
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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
