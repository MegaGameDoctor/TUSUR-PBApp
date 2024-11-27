package net.gamedoctor.TusurPixelBattle;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import net.gamedoctor.TusurPixelBattle.core.CoreManager;
import net.gamedoctor.TusurPixelBattle.db.ServerDB;

import static net.gamedoctor.TusurPixelBattle.Storage.coreManager;
import static net.gamedoctor.TusurPixelBattle.Storage.database;

public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        new Thread() {
            @Override
            public void run() {
                try {
                    load();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.start();
    }

    public void load() {
        database = new ServerDB(this);
        database.open();

        coreManager = new CoreManager("server.pbtusur.ru");

        if (database.isAccountExist()) {
            Storage.name = database.getName();
        } else {
            Storage.name = null;
        }

        coreManager.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}