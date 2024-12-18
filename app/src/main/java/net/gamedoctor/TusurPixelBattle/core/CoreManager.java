package net.gamedoctor.TusurPixelBattle.core;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;
import com.mayakplay.aclf.cloud.infrastructure.NettyGatewayClient;
import com.mayakplay.aclf.cloud.stereotype.Nugget;
import lombok.Getter;
import lombok.Setter;
import net.gamedoctor.TusurPixelBattle.MainActivity;
import net.gamedoctor.TusurPixelBattle.Storage;
import net.gamedoctor.TusurPixelBattle.dialogs.SimpleDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.gamedoctor.TusurPixelBattle.Storage.*;

public class CoreManager extends AsyncTask<String, String, CoreManager> {
    private final String ip;
    private NettyGatewayClient client;
    @SuppressLint("StaticFieldLeak")
    @Setter
    @Getter
    private MainActivity activity;

    public CoreManager(String ip) {
        this.ip = ip;
    }

    @Override
    protected CoreManager doInBackground(String... strings) {
        client = new NettyGatewayClient(this, ip, 8292, "app_main", new HashMap<>());
        client.addReceiveCallback(this::onMessage);
        return null;
    }

    public void onMessage(Nugget nugget) {
        try {
            Map<String, String> map = nugget.getParameters();
            String action = map.get("action");
            String data = map.get("data");
            if (action.equals("loadAllPixelsAnswer")) {
                String[] pixels = data.split("!!!");
                for (String pixelStr : pixels) {
                    String[] pixelData = pixelStr.split("@");
                    int x = Integer.parseInt(pixelData[0]);
                    int y = Integer.parseInt(pixelData[1]);
                    int color = Integer.parseInt(pixelData[2]);
                    boolean paintedByPlayer = Boolean.parseBoolean(pixelData[3]);

                    TextView pixel = (TextView) activity.getPixelByPosition(x, y);
                    pixel.setBackgroundColor(color);

                    if (!paintedByPlayer) {
                        pixel.setText("B");
                    } else {
                        pixel.setText("");
                    }
                }

                this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.removeLoading();
                        Toast.makeText(activity, "Полотно загружено", Toast.LENGTH_LONG).show();
                    }
                });
            } else if (action.equals("updatePixel")) {
                String[] dd = data.split("@");
                boolean paintedByPlayer = Boolean.parseBoolean(dd[4]);
                TextView pixel = (TextView) activity.getPixelByPosition(Integer.parseInt(dd[0]), Integer.parseInt(dd[1]));
                pixel.setBackgroundColor(Integer.parseInt(dd[2]));

                if (!paintedByPlayer) {
                    pixel.setText("B");
                } else {
                    pixel.setText("");
                }
            } else if (action.equals("paintPixelAnswer")) {
                if (data.equals("NO_AUTH")) {
                    name = null;
                    this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.sendAuthRequest();
                        }
                    });
                } else if (data.startsWith("SUCCESS:")) {
                    String[] pixelData = data.split(":");
                    Storage.nextPixelTime = System.currentTimeMillis() + Integer.parseInt(pixelData[4]) * 1000L + 2000L; // 2 sec тк не успевает
                    activity.getPixelByPosition(Integer.parseInt(pixelData[1]), Integer.parseInt(pixelData[2])).setBackgroundColor(Integer.parseInt(pixelData[3]));
                } else {
                    Storage.nextPixelTime = System.currentTimeMillis() + Integer.parseInt(data) * 1000L + 2000L; // 2 sec тк не успевает
                }
            } else if (action.equals("chatMessage")) {
                Storage.chatInfo.add(data);

                StringBuilder b = new StringBuilder();
                for (String s : chatInfo) {
                    b.append(s);
                    b.append("\n");
                }
                this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatLog.setText(b.toString());
                        chatLog.scrollTo(0, 0);
                    }
                });
            } else if (action.equals("statsAnswer")) {
                this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.removeLoading();
                        SimpleDialog stats = new SimpleDialog(activity, data);
                        stats.setTitle("Статистика");
                        stats.show();
                    }
                });
            } else if (action.equals("authAnswer")) {
                activity.removeLoading();
                if (data.startsWith("SUCCESS:")) {
                    String[] dd = data.replaceFirst("SUCCESS:", "").split("@!@");
                    database.updateAccountData(dd[0], dd[1]);
                    Storage.name = dd[0];
                    Storage.hashedPassword = dd[1];

                    requestAllPixels();
                } else {
                    this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.removeLoading();
                            SimpleDialog answer = new SimpleDialog(activity, data);
                            answer.setTitle("Ошибка");
                            answer.show();
                        }
                    });
                }
            } else if (action.equals("pixelInfoAnswer")) {
                String[] dd = data.split("@");
                String text = dd[3].replace("%lastRepaintedDate%", getTimeString(System.currentTimeMillis() - Long.parseLong(dd[2])));
                this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.removeLoading();
                        SimpleDialog answer = new SimpleDialog(activity, text);
                        answer.setTitle("Пиксель (" + dd[0] + ";" + dd[1] + ")");
                        answer.show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void paintPixel(int x, int y, int color) {
        if (Storage.name != null) {
            CoreMessage msg = new CoreMessage();
            msg.setAction("paintPixel");
            msg.setData(Storage.name + "@!@" + hashedPassword + "@!@" + x + "@!@" + y + "@!@" + color);
            send(msg);
        }
    }

    public void sendChatMessage(String message) {
        if (Storage.name != null) {
            CoreMessage msg = new CoreMessage();
            msg.setAction("sendChatMessage");
            msg.setData(Storage.name + "@!@" + hashedPassword + "@!@" + message);
            send(msg);
        }
    }

    public void requestAllPixels() {
        if (Storage.name != null) {
            activity.displayLoading("Получение данных...");
            CoreMessage msg = new CoreMessage();
            msg.setAction("loadAllPixels");
            send(msg);
        }
    }

    public void tryAuth(String name, String password) {
        activity.displayLoading("Авторизация...");
        CoreMessage msg = new CoreMessage();
        msg.setAction("tryAuth");
        msg.setData(name + "@!@" + password);
        send(msg);
    }

    public void requestStats() {
        if (Storage.name != null) {
            activity.displayLoading("Получение данных...");
            CoreMessage msg = new CoreMessage();
            msg.setAction("getStats");
            msg.setData(Storage.name + "@!@" + hashedPassword);
            send(msg);
        }
    }

    public void requestPixelInfo(int x, int y) {
        if (Storage.name != null) {
            activity.displayLoading("Получение данных...");
            CoreMessage msg = new CoreMessage();
            msg.setAction("getPixelInfo");
            msg.setData(Storage.name + "@!@" + hashedPassword + "@!@" + x + "@!@" + y);
            send(msg);
        }
    }

    public String secondsToFormatedString(long millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private String getTimeString(long time) {
        int seconds = (int) ((time /= 1000L) % 60L);
        int minutes = (int) ((time /= 60L) % 60L);
        int hours = (int) ((time /= 60L) % 24L);
        int days = (int) (time / 24L);

        if (seconds == 0) seconds = 1;

        StringBuilder sb = new StringBuilder();
        if (days != 0) {
            sb.append(days).append(" ").append("дн.");
        } else if (hours != 0) {
            sb.append(hours).append(" ").append("ч.");
        } else if (minutes != 0) {
            sb.append(minutes).append(" ").append("мин.");
        } else if (seconds >= 0) {
            sb.append(seconds).append(" ").append("сек.");
        }

        return sb.toString().trim();
    }

    public void send(CoreMessage msg) {
        new Thread() {
            public void run() {
                try {
                    if (client != null) client.sendNugget("fromApp", msg.toRByteMap());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}