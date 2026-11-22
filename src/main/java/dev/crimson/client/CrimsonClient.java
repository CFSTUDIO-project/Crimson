package dev.crimson.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class CrimsonClient implements ClientModInitializer {
    public static final String MOD_ID="crimson";
    public static final Logger LOGGER=LoggerFactory.getLogger("Crimson");
    private static KeyMapping menuKey,zoomKey;
    private static boolean zooming;
    private static int oldFov=70;
    private static double oldGamma=Double.NaN;
    private static boolean brightApplied;

    @Override public void onInitializeClient(){
        CrimsonConfig.load();
        KeyMapping.Category cat=KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID,"controls"));
        menuKey=KeyBindingHelper.registerKeyBinding(new KeyMapping("key.crimson.menu",InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_RIGHT_SHIFT,cat));
        zoomKey=KeyBindingHelper.registerKeyBinding(new KeyMapping("key.crimson.zoom",InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_C,cat));
        ClientTickEvents.END_CLIENT_TICK.register(client->{
            while(menuKey.consumeClick()){
                if(client.screen==null)client.setScreen(new CrimsonScreen());
                else if(client.screen instanceof CrimsonScreen s)s.beginClose();
            }
            updateBright(client);updateZoom(client);
        });
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID,"main_hud"),(g,delta)->renderHud(Minecraft.getInstance(),g));
        LOGGER.info("Crimson client initialized");
    }
    private static void updateBright(Minecraft c){
        if(c.options==null)return;
        boolean on=CrimsonConfig.enabled(CrimsonModule.FULLBRIGHT);
        if(on&&!brightApplied){oldGamma=c.options.gamma().get();brightApplied=true;}
        if(on)c.options.gamma().set(1.0);
        else if(brightApplied){if(!Double.isNaN(oldGamma))c.options.gamma().set(oldGamma);brightApplied=false;oldGamma=Double.NaN;}
    }
    private static void updateZoom(Minecraft c){
        if(c.player==null||c.options==null||c.screen!=null){restoreZoom(c);return;}
        boolean down=zoomKey.isDown()&&CrimsonConfig.enabled(CrimsonModule.ZOOM);
        if(down&&!zooming){oldFov=c.options.fov().get();zooming=true;}
        if(down)c.options.fov().set(35);else restoreZoom(c);
    }
    private static void restoreZoom(Minecraft c){if(zooming&&c.options!=null)c.options.fov().set(oldFov);zooming=false;}
    private static void renderHud(Minecraft c,net.minecraft.client.gui.GuiGraphics g){
        if(c.player==null||c.level==null||c.options.hideGui)return;
        List<String> lines=new ArrayList<>();
        if(CrimsonConfig.enabled(CrimsonModule.COORDINATES))lines.add(String.format("XYZ  %.1f  %.1f  %.1f",c.player.getX(),c.player.getY(),c.player.getZ()));
        if(CrimsonConfig.enabled(CrimsonModule.FPS))lines.add("FPS  "+c.getFps());
        if(CrimsonConfig.enabled(CrimsonModule.DIRECTION))lines.add("FACING  "+c.player.getDirection().getName().toUpperCase());
        if(CrimsonConfig.enabled(CrimsonModule.HEALTH))lines.add(String.format("HEALTH  %.1f / %.1f",c.player.getHealth(),c.player.getMaxHealth()));
        if(CrimsonConfig.enabled(CrimsonModule.HUNGER))lines.add("HUNGER  "+c.player.getFoodData().getFoodLevel()+"/20");
        if(CrimsonConfig.enabled(CrimsonModule.BIOME)){
            String biome=c.level.getBiome(BlockPos.containing(c.player.getX(),c.player.getY(),c.player.getZ())).unwrapKey().map(k->k.toString().replaceAll(".* / ","").replaceAll(".*:","").replace('_',' ')).orElse("unknown");
            lines.add("BIOME  "+biome.toUpperCase());
        }
        if(CrimsonConfig.enabled(CrimsonModule.ATTACK_COOLDOWN))lines.add("ATTACK  "+Math.round(c.player.getAttackStrengthScale(0)*100)+"%");
        if(CrimsonConfig.enabled(CrimsonModule.SPRINT_STATUS))lines.add("SPRINT  "+(c.player.isSprinting()?"ACTIVE":"IDLE"));
        if(CrimsonConfig.enabled(CrimsonModule.WORLD_CLOCK)){long t=Math.floorMod(c.level.getDayTime(),24000L);lines.add(String.format("WORLD TIME  %02d:%02d",(t/1000+6)%24,(t%1000)*60/1000));}
        if(CrimsonConfig.enabled(CrimsonModule.REAL_CLOCK))lines.add("LOCAL TIME  "+LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        if(CrimsonConfig.enabled(CrimsonModule.KEYSTROKES))lines.add("KEYS  "+key(c.options.keyUp.isDown(),"W")+" "+key(c.options.keyLeft.isDown(),"A")+" "+key(c.options.keyDown.isDown(),"S")+" "+key(c.options.keyRight.isDown(),"D"));
        if(lines.isEmpty())return;
        int x=10,y=10,w=0;for(String s:lines)w=Math.max(w,c.font.width(s));
        if(CrimsonConfig.compactHud&&CrimsonConfig.enabled(CrimsonModule.HUD_PANEL)){g.fill(x-5,y-5,x+w+11,y+lines.size()*13+5,0xA90A0C12);g.fill(x-5,y-5,x+w+11,y-3,CrimsonScreen.accentColor());}
        for(String s:lines){g.drawString(c.font,Component.literal(s),x,y,0xFFF1E9EA,true);y+=13;}
    }
    private static String key(boolean down,String s){return down?"["+s+"]":s;}
    public static void playClick(Minecraft c){if(CrimsonConfig.clickSounds)c.getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,0.85F));}
}