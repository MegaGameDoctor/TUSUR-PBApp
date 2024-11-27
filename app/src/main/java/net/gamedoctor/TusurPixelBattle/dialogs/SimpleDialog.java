package net.gamedoctor.TusurPixelBattle.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;
import net.gamedoctor.TusurPixelBattle.R;

public class SimpleDialog extends Dialog {
    private final String text;

    public SimpleDialog(Context context, String text) {
        super(context);
        this.text = text;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.generic_dialog);
        TextView tv = findViewById(R.id.info_text);
        tv.setText(this.text);
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setTextColor(Color.BLACK);
        Button button = findViewById(R.id.about_ok_button);
        button.setOnClickListener(v -> dismiss());
    }
}