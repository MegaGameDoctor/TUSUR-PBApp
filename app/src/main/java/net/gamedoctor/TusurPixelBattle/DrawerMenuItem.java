package net.gamedoctor.TusurPixelBattle;

public abstract class DrawerMenuItem {
    private final int iconId;
    private final int stringId;

    DrawerMenuItem(int id, int string) {
        iconId = id;
        stringId = string;
    }

    public abstract void execute();

    int getIconId() {
        return iconId;
    }

    int getStringId() {
        return stringId;
    }
}
