package in.kriger.newkrigercampus.extras;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.hendraanggrian.appcompat.widget.SocialTextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class SpecialTextView extends SocialTextView {

    public SpecialTextView(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public SpecialTextView(@NotNull Context context) {
        super(context);
        init();
    }

    public SpecialTextView(@NotNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
