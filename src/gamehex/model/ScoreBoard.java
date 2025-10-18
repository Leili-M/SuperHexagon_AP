package gamehex.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ScoreBoard {
    private final List<ScoreEntry> entries = new ArrayList<>();

    /* ───── API ───── */
    public void add(ScoreEntry e){ entries.add(e); }

    public List<ScoreEntry> allSortedDesc(){
        return entries.stream()
                .sorted(Comparator.comparingLong(ScoreEntry::millis).reversed())
                .toList();
    }

    public long bestMillis(){
        return entries.stream().mapToLong(ScoreEntry::millis).max().orElse(0L);
    }

    /* ───── JSON Persistence (بدون لایبرری) ───── */

    public void saveToJson(Path path){
        try {
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            String json = toJson();
            Files.writeString(path, json, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("[ScoreBoard] saveToJson failed: " + e);
        }
    }

    public void loadFromJson(Path path){
        entries.clear();
        if (!Files.exists(path)) return;
        try {
            String json = Files.readString(path, StandardCharsets.UTF_8);
            fromJson(json);
        } catch (IOException e) {
            System.err.println("[ScoreBoard] loadFromJson failed: " + e);
        }
    }

    private String toJson(){
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i=0;i<entries.size();i++){
            ScoreEntry e = entries.get(i);
            if (i>0) sb.append(',');
            sb.append('{')
                    .append("\"player\":\"").append(escape(e.player())).append("\",")
                    .append("\"millis\":").append(e.millis()).append(',')
                    .append("\"epochMs\":").append(e.epochMs())
                    .append('}');
        }
        sb.append(']');
        return sb.toString();
    }

    private static String escape(String s){
        return s.replace("\\","\\\\").replace("\"","\\\"");
    }

    private void fromJson(String json){
        Pattern obj = Pattern.compile("\\{[^}]*}");
        Matcher m = obj.matcher(json);
        Pattern pPlayer = Pattern.compile("\"player\"\\s*:\\s*\"(.*?)\"");
        Pattern pMillis = Pattern.compile("\"millis\"\\s*:\\s*(\\d+)");
        Pattern pEpoch  = Pattern.compile("\"epochMs\"\\s*:\\s*(\\d+)");

        while (m.find()){
            String o = m.group();
            String player = matchStr(pPlayer, o, "Player");
            long millis   = matchLong(pMillis, o, 0L);
            long epoch    = matchLong(pEpoch,  o, 0L);
            entries.add(new ScoreEntry(player, millis, epoch));
        }
    }

    private static String matchStr(Pattern p, String s, String def){
        Matcher m = p.matcher(s);
        return m.find() ? unescape(m.group(1)) : def;
    }
    private static long matchLong(Pattern p, String s, long def){
        Matcher m = p.matcher(s);
        return m.find() ? Long.parseLong(m.group(1)) : def;
    }
    private static String unescape(String s){
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
