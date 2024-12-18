package net.gamedoctor.TusurPixelBattle.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import net.gamedoctor.TusurPixelBattle.BuildConfig;
import net.gamedoctor.TusurPixelBattle.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Year;

public class AboutDialog extends Dialog {
    private static final String TAG = AboutDialog.class.getSimpleName();
    private final Context mContext;

    public AboutDialog(Context context) {
        super(context);
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("StringFormatInvalid")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.about);

        PackageInfo p;
        String version = "???";
        int vCode = 0;
        TextView tv = findViewById(R.id.info_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv.setText(Html.fromHtml(readRawTextFile(R.raw.about_info).replaceAll("%year%", String.valueOf(Year.now().getValue())), Html.FROM_HTML_MODE_LEGACY));
        } else {
            tv.setText(Html.fromHtml(readRawTextFile(R.raw.about_info).replaceAll("%year%", String.valueOf(Year.now().getValue()))));
        }
        tv.setLinkTextColor(Color.BLUE);
        Linkify.addLinks(tv, Linkify.ALL);
        TextView ver = findViewById(R.id.version_string);
        Context aContext = mContext.getApplicationContext();
        try {
            p = aContext.getPackageManager().getPackageInfo(aContext.getPackageName(), 0);
            version = p.versionName;
            vCode = p.versionCode;
        } catch (NameNotFoundException e) {
            Log.i(TAG, "Failed to retrieve package name");
            e.printStackTrace();
        }
        ver.setText(aContext.getResources().getString(R.string.about_version, version, vCode).replace("!ver!", BuildConfig.VERSION_NAME));
        Button button = findViewById(R.id.about_ok_button);
        button.setOnClickListener(v -> dismiss());

    }

    public String readRawTextFile(int id) {

        InputStream inputStream = mContext.getResources().openRawResource(id);

        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader buf = new BufferedReader(in);

        String line;

        StringBuilder text = new StringBuilder();
        try {
            while ((line = buf.readLine()) != null)
                text.append(line);
        } catch (IOException e) {
            return null;
        }

        return text.toString();
    }

}
