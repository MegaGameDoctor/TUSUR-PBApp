package net.gamedoctor.TusurPixelBattle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import net.gamedoctor.TusurPixelBattle.dialogs.AboutDialog;
import net.gamedoctor.TusurPixelBattle.dialogs.BlockedMessageDialog;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static net.gamedoctor.TusurPixelBattle.Storage.*;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<DrawerMenuItem> listMenuItem = new ArrayList<>();
    private int currentColor;
    private Button[] colorButtons;
    private int[] colors;
    private ActionBarDrawerToggle drawerToggle;
    private MenuItem pixel_timer;

    public void sendAuthRequest() {
        View promptsView = LayoutInflater.from(this).inflate(
                R.layout.set_name, null);

        final EditText nameColumn = promptsView
                .findViewById(R.id.edit_name);

        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle(getResources().getString(R.string.auth_window_title))
                .setView(promptsView)
                .setPositiveButton(getResources().getString(R.string.auth_window_save), null)
                .setNegativeButton(getResources().getString(R.string.auth_window_close), null)
                .show();
        Button positiveButton = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = nameColumn.getText().toString();
                if (name == null) {
                    if (nameStr.equals("") || nameStr.startsWith(" ") || nameStr.length() < 3) {
                        Toast.makeText(MainActivity.this, "Информация не сохранена", Toast.LENGTH_SHORT).show();
                    } else {
                        database.createAccountData(nameStr);
                        Storage.name = nameStr;
                        coreManager.requestAllPixels();
                        Toast.makeText(MainActivity.this, "Информация сохранена", Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
            }
        });

        Button negativeButton = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        coreManager.setActivity(this);
        hh = new Handler();

        if (!database.isAccountExist()) {
            sendAuthRequest();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateDrawerHeader();
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);


        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        ListView leftDrawer = findViewById(R.id.left_drawer);

        addDrawerItems();

        DrawerMenuItemAdapter adapter = new DrawerMenuItemAdapter(this, listMenuItem);
        leftDrawer.setAdapter(adapter);

        leftDrawer.setOnItemClickListener((adapterView, view, i, l) -> listMenuItem.get(i).execute());

        initPalette();
        initPixels();
        fillScreen(ContextCompat.getColor(MainActivity.this, R.color.white));

        LinearLayout paper = findViewById(R.id.paper_linear_layout);

        for (int y = 0; y < paper.getChildCount(); y++) {
            LinearLayout l = (LinearLayout) paper.getChildAt(y);

            for (int x = 0; x < l.getChildCount(); x++) {
                View pixel = l.getChildAt(x);
                pixelsX.put(pixel, x);
                pixelsY.put(pixel, y);
                pixels.put(getFormatedXY(x, y), pixel);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        coreManager.requestAllPixels();
                    }
                }, 2000L);
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Button b = findViewById(data.getIntExtra("id", 0));
                GradientDrawable gd = (GradientDrawable) b.getBackground();
                int c = data.getIntExtra("color", 0);
                gd.setColor(c);

                colors[data.getIntExtra("position", 0)] = c;

                if (data.getBooleanExtra("currentColor", false)) {
                    currentColor = c;
                    findViewById(R.id.palette_linear_layout).setBackgroundColor(currentColor);
                }
            }
        }
    }

    private void addDrawerItems() {
        /*
        DrawerMenuItem drawerExport = new DrawerMenuItem(R.drawable.ic_menu_export, R.string.menu_export) {
            @Override
            public void execute() {
                String filename;

                Calendar calendar = Calendar.getInstance();

                long unixTime = System.currentTimeMillis() / 1000;
                unixTime %= 1000000;

                filename = "KRPB_IMG_"
                        + calendar.get(Calendar.YEAR)
                        + calendar.get(Calendar.MONTH)
                        + calendar.get(Calendar.DAY_OF_MONTH) + "_" + unixTime + ".jpg";

                screenShot(findViewById(R.id.paper_linear_layout), filename);
            }
        };

         */

        DrawerMenuItem drawerAbout = new DrawerMenuItem(R.drawable.ic_menu_about, R.string.menu_about) {
            @Override
            public void execute() {
                AboutDialog about = new AboutDialog(MainActivity.this);
                about.setTitle(getResources().getString(R.string.action_about));
                about.show();
            }
        };

        DrawerMenuItem drawerChangeAuth = new DrawerMenuItem(R.drawable.ic_menu_auth, R.string.menu_auth) {
            @Override
            public void execute() {
                View promptsView = LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.set_name, null);

                final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(MainActivity.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle(getResources().getString(R.string.auth_window_title))
                        .setView(promptsView)
                        .setPositiveButton(getResources().getString(R.string.auth_window_change), null)
                        .setNegativeButton(getResources().getString(R.string.auth_window_close), null)
                        .show();

                final EditText nameColumn = promptsView
                        .findViewById(R.id.edit_name);
                if (name != null)
                    nameColumn.setText(Storage.name);


                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nameStr = nameColumn.getText().toString();
                        if (nameStr.equals("") || nameStr.startsWith(" ") || nameStr.length() < 3) {
                            Toast.makeText(MainActivity.this, "Информация не обновлена: '" + nameStr + "'", Toast.LENGTH_SHORT).show();
                        } else {
                            coreManager.tryAuth(nameStr);
                            Toast.makeText(MainActivity.this, "Информация обновлена", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });

                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        };

        DrawerMenuItem drawerStats = new DrawerMenuItem(R.drawable.ic_menu_stats, R.string.menu_stats) {
            @Override
            public void execute() {
                // getResources().getString(R.string.action_privacy)
                coreManager.requestStats();
            }
        };

        //listMenuItem.add(drawerExport);
        listMenuItem.add(drawerChangeAuth);
        listMenuItem.add(drawerAbout);
        listMenuItem.add(drawerStats);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        pixel_timer = menu.findItem(R.id.pixel_timer);
        pixel_timer.setTitle("...");

        hh.post(pixel_timer_updater);
        //coreManager.requestAllPixels();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.open_chat:
                if (name == null) {
                    sendAuthRequest();
                    return true;
                }
                View promptsView = LayoutInflater.from(this).inflate(
                        R.layout.chat, null);

                final EditText message = promptsView
                        .findViewById(R.id.edit_chat);

                chatLog = promptsView.findViewById(R.id.chat_list);
                chatLog.setTextColor(Color.BLACK);
                chatLog.setMovementMethod(new ScrollingMovementMethod());

                StringBuilder b = new StringBuilder();

                for (String s : chatInfo) {
                    b.append(s);
                    b.append("\n");
                }
                chatLog.setText(b.toString());


                chatLog.scrollTo(0, 0);

                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Чат")
                        .setView(promptsView)
                        .setPositiveButton("Отправить", null)
                        .setNegativeButton("Выйти", null)
                        .show();
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (name != null && !message.getText().toString().equals("")) {
                            coreManager.sendChatMessage(message.getText().toString());
                            StringBuilder b = new StringBuilder();

                            for (String s : chatInfo) {
                                b.append(s);
                                b.append("\n");
                            }
                            chatLog.setText(b.toString());
                            message.setText("");
                            Toast.makeText(MainActivity.this, "Сообщение отправлено", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Сообщение не отправлено", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                return true;
                /*
            case R.id.update_grid:
                if (name != null) {
                    alertDialog.setTitle("Обновление данных");
                    alertDialog.setMessage("Вы уверены, что хотите запросить актуальную информацию о поле?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                            (dialog, which) -> {
                                dialog.dismiss();
                                fillScreen(-1);
                                coreManager.requestAllPixels();
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                } else {
                    sendAuthRequest();
                }
                return true;

                 */
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void pixelGrid(boolean visible) {
        LinearLayout paper = findViewById(R.id.paper_linear_layout);

        int x;
        int y;

        if (!visible) {
            x = 0;
            y = 0;
        } else {
            x = 2;
            y = 2;
        }

        for (int i = 0; i < paper.getChildCount(); i++) {
            LinearLayout l = (LinearLayout) paper.getChildAt(i);

            for (int j = 0; j < l.getChildCount(); j++) {
                View pixel = l.getChildAt(j);

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) pixel.getLayoutParams();

                layoutParams.setMargins(x, y, 0, 0);
                pixel.setLayoutParams(layoutParams);
            }
        }
    }

    private void updateDrawerHeader() {
        View view = findViewById(R.id.paper_linear_layout);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        ImageView header = findViewById(R.id.drawer_header);
        header.setImageBitmap(bitmap);
    }

    private void initPalette() {
        colorButtons = new Button[]{
                findViewById(R.id.color_button_black),
                findViewById(R.id.color_button_eclipse),
                findViewById(R.id.color_button_grey),
                findViewById(R.id.color_button_white),
                findViewById(R.id.color_button_red),
                findViewById(R.id.color_button_orange),
                findViewById(R.id.color_button_yellow),
                findViewById(R.id.color_button_lime),
                findViewById(R.id.color_button_malachite),
                findViewById(R.id.color_button_cyan),
                findViewById(R.id.color_button_sapphire),
                findViewById(R.id.color_button_purple),
                findViewById(R.id.color_button_lt_purple),
                findViewById(R.id.color_button_magenta)
        };

        colors = new int[]{
                ContextCompat.getColor(this, R.color.black),
                ContextCompat.getColor(this, R.color.eclipse),
                ContextCompat.getColor(this, R.color.grey),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.red),
                ContextCompat.getColor(this, R.color.orange),
                ContextCompat.getColor(this, R.color.yellow),
                ContextCompat.getColor(this, R.color.lime),
                ContextCompat.getColor(this, R.color.malachite),
                ContextCompat.getColor(this, R.color.cyan),
                ContextCompat.getColor(this, R.color.sapphire),
                ContextCompat.getColor(this, R.color.purple),
                ContextCompat.getColor(this, R.color.lt_purple),
                ContextCompat.getColor(this, R.color.magenta),
        };

        for (int i = 0; i < colorButtons.length; i++) {
            GradientDrawable cd = (GradientDrawable) colorButtons[i].getBackground();
            cd.setColor(colors[i]);
        }

        selectColor(colorButtons[0]);
    }

    private void initPixels() {
        LinearLayout paper = findViewById(R.id.paper_linear_layout);

        for (int i = 0; i < paper.getChildCount(); i++) {
            LinearLayout l = (LinearLayout) paper.getChildAt(i);

            for (int j = 0; j < l.getChildCount(); j++) {
                View pixel = l.getChildAt(j);

                pixel.setOnLongClickListener(view -> {
                    selectColor(((ColorDrawable) view.getBackground()).getColor());
                    Toast.makeText(MainActivity.this, "Выбран цвет с зажатого пикселя", Toast.LENGTH_SHORT).show();
                    return false;
                });
            }
        }
    }

    public void fillScreen(int color) {
        LinearLayout paper = findViewById(R.id.paper_linear_layout);

        for (int i = 0; i < paper.getChildCount(); i++) {
            LinearLayout l = (LinearLayout) paper.getChildAt(i);

            for (int j = 0; j < l.getChildCount(); j++) {
                View pixel = l.getChildAt(j);

                pixel.setBackgroundColor(color);
            }
        }
    }

    public void selectColor(View v) {
        int i = 0;

        for (Button b : colorButtons) {
            if (v.getId() == b.getId()) {
                break;
            }

            i += 1;
        }

        selectColor(colors[i]);
    }

    public void selectColor(int color) {
        currentColor = color;

        findViewById(R.id.palette_linear_layout).setBackgroundColor(currentColor);
    }

    public View getPixelByPosition(int x, int y) {
        return pixels.get(getFormatedXY(x, y));
    }

    public String getFormatedXY(int x, int y) {
        return x + "@" + y;
    }

    public void changeColor(View v) {
        if (name != null) {
            if (System.currentTimeMillis() > nextPixelTime) {
                coreManager.paintPixel(pixelsX.get(v), pixelsY.get(v), currentColor);
            }
        } else {
            sendAuthRequest();
        }
    }

    public void displayLoading(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeLoadingSync();
                nowLoading = new BlockedMessageDialog(MainActivity.this, text);
                nowLoading.show();
            }
        });
    }

    public void removeLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeLoadingSync();
            }
        });
    }

    private void removeLoadingSync() {
        if (nowLoading != null) {
            nowLoading.dismiss();
            nowLoading = null;
        }
    }

    private String secondsToFormatedString(long millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    Runnable pixel_timer_updater = new Runnable() {
        @Override
        public void run() {
            try {
                String resultLogo;
                if (System.currentTimeMillis() > nextPixelTime) {
                    resultLogo = "✅";
                } else {
                    resultLogo = secondsToFormatedString(nextPixelTime - System.currentTimeMillis());
                }

                pixel_timer.setTitle(resultLogo);
            } finally {
                hh.postDelayed(pixel_timer_updater, 900L);
            }
        }
    };


}
