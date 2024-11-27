package net.gamedoctor.TusurPixelBattle.core;

import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
public class CoreMessage {
    private String action;
    private String data;

    public Map<String, String> toRByteMap() {
        if (this.action == null) this.action = "noAction";
        if (this.data == null) this.data = "noData";
        Map<String, String> map = new HashMap<>();
        map.put("action", this.action);
        map.put("data", this.data);
        return map;
    }
}