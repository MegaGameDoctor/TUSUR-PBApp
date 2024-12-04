package net.gamedoctor.TusurPixelBattle;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import net.gamedoctor.TusurPixelBattle.core.CoreManager;
import net.gamedoctor.TusurPixelBattle.db.ServerDB;
import net.gamedoctor.TusurPixelBattle.dialogs.BlockedMessageDialog;

import java.util.ArrayList;
import java.util.HashMap;

public class Storage {
    public static String name;
    public static String hashedPassword;
    public static CoreManager coreManager;
    public static ServerDB database;
    public static Handler hh;
    public static HashMap<View, Integer> pixelsX = new HashMap<>();
    public static HashMap<View, Integer> pixelsY = new HashMap<>();
    public static HashMap<String, View> pixels = new HashMap<>();
    public static long nextPixelTime = 0L;
    public static ArrayList<String> chatInfo = new ArrayList<>();
    public static TextView chatLog;
    public static BlockedMessageDialog nowLoading;
}
