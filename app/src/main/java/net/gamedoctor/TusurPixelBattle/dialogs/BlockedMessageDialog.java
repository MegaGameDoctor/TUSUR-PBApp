package net.gamedoctor.TusurPixelBattle.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import net.gamedoctor.TusurPixelBattle.R;

public class BlockedMessageDialog extends Dialog {
    private final String text;

    public BlockedMessageDialog(Context context, String text) {
        super(context);
        this.text = text;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.blocked_text_dialog);
        setCancelable(false);
        TextView t = findViewById(R.id.blocked_text_dialog_text);
        t.setText(this.text);
    }
}