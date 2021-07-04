package in.kriger.newkrigercampus.classes;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;

import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.activities.LoginActivity;

public class DialogBuilder {

    String text;
    int type;
    Boolean isNegative;
    String posText;
    String negText;
    ArrayList<UserDetails> arrayList;
    Context context;


    public DialogBuilder(Context context, String text, int type, Boolean isNegative, String posText, String negText, ArrayList<UserDetails> arrayList) {
        this.text = text;
        this.type = type;
        this.isNegative = isNegative;
        this.posText = posText;
        this.negText = negText;
        this.arrayList = arrayList;
        this.context = context;
    }


    public void show(){

        final Dialog dialog = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.layout_dialog);

        TextView textView = (TextView) dialog.findViewById(R.id.dialogue_text);
        textView.setText(text);

        if (!(type == 2)) {
            dialog.setCancelable(false);
        }

        Button posButton = (Button) dialog.findViewById(R.id.button_positive);
        posButton.setText(posText);
        posButton.setVisibility(View.VISIBLE);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type == 10){
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }

            }
        });

        if (isNegative){

            Button negButton = (Button) dialog.findViewById(R.id.button_negative);
            negButton.setText(negText);
            negButton.setVisibility(View.VISIBLE);
            negButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        try {
            dialog.show();
        }catch(WindowManager.BadTokenException e){

        }

    }
}
