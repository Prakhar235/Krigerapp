package in.kriger.newkrigercampus.classes;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class UserDetailSpan extends ClickableSpan {
    private final UserDetails recipient;

    public UserDetailSpan(UserDetails recipient) {
        super();
        this.recipient = recipient;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(true);
    }

    @Override
    public void onClick(View view) {
    }
}