package dev.crimson.client;

import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public final class CrimsonConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("crimson.properties");
    private static final Path DIR = FabricLoader.getInstance().getConfigDir().resolve("crimson");
    private static final Path HOSTS = DIR.resolve("allowed-servers.txt");
    private static final Properties VALUES = new Properties();
    private static final Set<String> ALLOWED = new TreeSet<>();
    public static int accentTheme = 0, backgroundTheme = 0;
    public static boolean clickSounds = true, compactHud = true;
    private static boolean loaded;
    private CrimsonConfig() {}
    public static void load() {
        if (loaded) return;
        loaded = true;
        try {
            Files.createDirectories(DIR);
            if (Files.exists(FILE)) try (Reader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) { VALUES.load(r); }
            for (CrimsonModule m : CrimsonModule.values()) VALUES.putIfAbsent("module."+m.name().toLowerCase(Locale.ROOT), (m==CrimsonModule.COORDINATES||m==CrimsonModule.FPS||m==CrimsonModule.DIRECTION||m==CrimsonModule.HUD_PANEL)?"true":"false");
            accentTheme = clamp(parse("accentTheme",0),0,3);
            backgroundTheme = clamp(parse("backgroundTheme",0),0,2);
            clickSounds = Boolean.parseBoolean(VALUES.getProperty("clickSounds","true"));
            compactHud = Boolean.parseBoolean(VALUES.getProperty("compactHud","true"));
            if (!Files.exists(HOSTS)) Files.writeString(HOSTS,"# Add one exact server hostname per line only when its rules permit client-side radar.\n",StandardCharsets.UTF_8);
            ALLOWED.clear();
            for(String line:Files.readAllLines(HOSTS,StandardCharsets.UTF_8)){String h=line.trim().toLowerCase(Locale.ROOT);if(!h.isEmpty()&&!h.startsWith("#"))ALLOWED.add(h);}
        } catch(IOException e){CrimsonClient.LOGGER.error("Could not load Crimson configuration",e);}
    }
    private static int parse(String k,int d){try{return Integer.parseInt(VALUES.getProperty(k,Integer.toString(d)));}catch(NumberFormatException e){return d;}}
    private static int clamp(int n,int min,int max){return Math.max(min,Math.min(max,n));}
    public static boolean enabled(CrimsonModule m){load();return Boolean.parseBoolean(VALUES.getProperty("module."+m.name().toLowerCase(Locale.ROOT),"false"));}
    public static void setEnabled(CrimsonModule m,boolean v){load();VALUES.setProperty("module."+m.name().toLowerCase(Locale.ROOT),Boolean.toString(v));}
    public static void toggle(CrimsonModule m){setEnabled(m,!enabled(m));}
    public static boolean isHostAllowed(String address){
        load();if(address==null)return false;String host=address.trim().toLowerCase(Locale.ROOT);
        if(host.startsWith("[")){int i=host.indexOf(']');if(i>0)host=host.substring(1,i);}
        else if(host.indexOf(':')>=0)host=host.substring(0,host.lastIndexOf(':'));
        return ALLOWED.contains(host);
    }
    public static String allowedHostSummary(){load();return ALLOWED.isEmpty()?"No multiplayer hosts approved":ALLOWED.size()+" host(s) approved locally";}
    public static void cycleAccent(){accentTheme=(accentTheme+1)%4;}
    public static void cycleBackground(){backgroundTheme=(backgroundTheme+1)%3;}
    public static void save(){
        load();VALUES.setProperty("accentTheme",Integer.toString(accentTheme));VALUES.setProperty("backgroundTheme",Integer.toString(backgroundTheme));
        VALUES.setProperty("clickSounds",Boolean.toString(clickSounds));VALUES.setProperty("compactHud",Boolean.toString(compactHud));
        try(Writer w=Files.newBufferedWriter(FILE,StandardCharsets.UTF_8)){VALUES.store(w,"Crimson client configuration");}
        catch(IOException e){CrimsonClient.LOGGER.error("Could not save Crimson configuration",e);}
    }
    public static void reload(){ loaded=false; VALUES.clear(); ALLOWED.clear(); load(); }
    public static void reset(){
        load();for(CrimsonModule m:CrimsonModule.values())setEnabled(m,m==CrimsonModule.COORDINATES||m==CrimsonModule.FPS||m==CrimsonModule.DIRECTION||m==CrimsonModule.HUD_PANEL);
        accentTheme=0;backgroundTheme=0;clickSounds=true;compactHud=true;save();
    }
}