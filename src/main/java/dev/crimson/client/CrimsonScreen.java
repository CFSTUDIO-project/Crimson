package dev.crimson.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import java.util.List;

public final class CrimsonScreen extends Screen {
    private CrimsonModule.Category selected=CrimsonModule.Category.COMBAT;
    private long opened=System.currentTimeMillis();
    private long closingAt=-1;
    private int scroll=0;
    private static final int[] ACCENTS={0xFFFF334D,0xFFFF526B,0xFFB13CFF,0xFF36B9FF};
    private static final String[] ACCENT_NAMES={"CRIMSON","RUBY","VIOLET","ICE"};
    public CrimsonScreen(){super(Component.literal("Crimson"));}
    public static int accentColor(){return ACCENTS[CrimsonConfig.accentTheme%ACCENTS.length];}
    public void beginClose(){if(closingAt<0)closingAt=System.currentTimeMillis();}
    @Override public void onClose(){beginClose();}
    @Override public boolean isPauseScreen(){return false;}
    private float progress(){
        long now=System.currentTimeMillis();
        if(closingAt>=0){float p=1f-(now-closingAt)/210f;if(p<=0){Minecraft.getInstance().setScreen(null);return 0;}return Math.max(0,p);}
        return Math.min(1f,(now-opened)/260f);
    }
    @Override public void render(GuiGraphics g,int mx,int my,float partial){
        float p=progress();if(p<=0)return;
        int w=Math.min(850,width-24),h=Math.min(510,height-24),x=(width-w)/2,y=(height-h)/2;
        int accent=accentColor();
        g.fill(0,0,width,height,((int)(0xA0*p)<<24));
        g.fill(x-3,y-3,x+w+3,y+h+3,0x553E0711);
        g.fill(x-1,y-1,x+w+1,y+h+1,accent);
        g.fill(x,y,x+w,y+h,CrimsonConfig.backgroundTheme==1?0xF51B1C25:CrimsonConfig.backgroundTheme==2?0xF51A101F:0xF50A0B11);
        g.fillGradient(x+1,y+1,x+w-1,y+48,0xFF171018,0xFF090A10);
        g.fillGradient(x+1,y+46,x+w-1,y+48,accent,0xFF722D77);
        g.drawString(font,"CRIMSON",x+18,y+15,accent,true);
        g.drawString(font,"CLIENT  /  1.0.0",x+126,y+19,0xFFB9ADB4,false);
        g.drawString(font,"RIGHT SHIFT  ·  CLOSE",x+w-160,y+19,0xFF9C929A,false);
        int side=Math.min(164,w/3),bodyX=x+side;
        g.fill(bodyX,y+49,bodyX+1,y+h-1,0xFF51232E);
        int tabY=y+62;
        for(CrimsonModule.Category c:CrimsonModule.Category.values()){
            boolean active=c==selected;
            int th=34;
            if(active){g.fill(x+10,tabY-5,bodyX-10,tabY+th-5,0xB32B1119);g.fill(bodyX-13,tabY-2,bodyX-10,tabY+th-8,accent);}
            g.drawString(font,c.title(),x+23,tabY+7,active?0xFFFFE9EF:0xFFB6ADB2,active);
            tabY+=39;
        }
        int cx=bodyX+15,contentW=w-side-30;
        g.drawString(font,selected.title().toUpperCase()+"  /  MODULE CONFIGURATION",cx,y+62,0xFFF1E9ED,true);
        g.drawString(font,"Toggle modules · profiles save locally",cx,y+78,0xFF958B93,false);
        List<CrimsonModule> modules=CrimsonModule.in(selected);
        int cols=contentW<410?1:2,cardW=(contentW-(cols-1)*12)/cols;
        int startY=y+102;
        for(int i=scroll;i<modules.size();i++){
            int col=(i-scroll)%cols,row=(i-scroll)/cols,cy=startY+row*62;
            if(cy+53>y+h-53)break;
            int bx=cx+col*(cardW+12);CrimsonModule m=modules.get(i);boolean on=CrimsonConfig.enabled(m);
            g.fill(bx,cy,bx+cardW,cy+52,0xFF34313A);g.fill(bx+1,cy+1,bx+cardW-1,cy+51,0xF5111219);
            g.fill(bx+1,cy+1,bx+3,cy+51,on?accent:0xFF45414A);
            g.drawString(font,m.title(),bx+11,cy+9,0xFFF1EAEE,true);
            g.drawString(font,on?"ON":"OFF",bx+cardW-37,cy+9,on?accent:0xFF8B8188,true);
            g.drawString(font,m.description(),bx+11,cy+29,0xFF9C929A,false);
            if(m.permissionGated())g.drawString(font,"PERMISSION GATED",bx+11,cy+41,0xFFD39A5D,false);
        }
        int fy=y+h-40,third=(contentW-22)/3;
        button(g,cx,fy,third,27,"SAVE PROFILE",accent,mx,my);
        button(g,cx+third+11,fy,third,27,"LOAD CONFIG",accent,mx,my);
        button(g,cx+(third+11)*2,fy,contentW-2*(third+11),27,"RESET",accent,mx,my);
        super.render(g,mx,my,partial);
    }
    private void button(GuiGraphics g,int x,int y,int w,int h,String label,int accent,int mx,int my){
        boolean hover=mx>=x&&mx<=x+w&&my>=y&&my<=y+h;
        g.fill(x,y,x+w,y+h,hover?accent:0xFF6B2734);g.fill(x+1,y+1,x+w-1,y+h-1,0xFF101116);
        g.drawCenteredString(font,label,x+w/2,y+9,0xFFEDE5E9);
    }
    @Override public boolean mouseClicked(double mx,double my,int button){
        if(button!=0)return super.mouseClicked(mx,my,button);
        int w=Math.min(850,width-24),h=Math.min(510,height-24),x=(width-w)/2,y=(height-h)/2;
        int side=Math.min(164,w/3),bodyX=x+side;
        int tabY=y+62;
        for(CrimsonModule.Category c:CrimsonModule.Category.values()){
            if(mx>=x+8&&mx<=bodyX-8&&my>=tabY-5&&my<=tabY+29){selected=c;scroll=0;CrimsonClient.playClick(Minecraft.getInstance());return true;}
            tabY+=39;
        }
        int contentW=w-side-30,cx=bodyX+15,cols=contentW<410?1:2,cardW=(contentW-(cols-1)*12)/cols;
        List<CrimsonModule> modules=CrimsonModule.in(selected);
        for(int i=scroll;i<modules.size();i++){
            int col=(i-scroll)%cols,row=(i-scroll)/cols,cy=y+102+row*62,bx=cx+col*(cardW+12);
            if(cy+53>y+h-53)break;
            if(mx>=bx&&mx<=bx+cardW&&my>=cy&&my<=cy+52){CrimsonConfig.toggle(modules.get(i));CrimsonConfig.save();CrimsonClient.playClick(Minecraft.getInstance());return true;}
        }
        int fy=y+h-40,third=(contentW-22)/3;
        if(my>=fy&&my<=fy+27){
            if(mx>=cx&&mx<=cx+third)CrimsonConfig.save();
            else if(mx>=cx+third+11&&mx<=cx+2*third+11)CrimsonConfig.reload();
            else if(mx>=cx+2*(third+11)&&mx<=cx+contentW)CrimsonConfig.reset();
            CrimsonClient.playClick(Minecraft.getInstance());return true;
        }
        return super.mouseClicked(mx,my,button);
    }
    @Override public boolean mouseScrolled(double mx,double my,double hx,double vy){
        scroll=Math.max(0,Math.min(CrimsonModule.in(selected).size()-1,scroll+(vy<0?1:-1)));return true;
    }
    @Override public boolean keyPressed(net.minecraft.client.input.KeyEvent event){
        if(event.key()==GLFW.GLFW_KEY_ESCAPE||event.key()==GLFW.GLFW_KEY_RIGHT_SHIFT){beginClose();return true;}
        return super.keyPressed(event);
    }
}