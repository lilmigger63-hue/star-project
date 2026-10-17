package dk.starswordmaster.client;

import dk.starswordmaster.ui.DarkFantasyPalette;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Restrained iron/bronze frames, shared without changing widget input or layout behavior. */
final class FantasyUi extends ButtonWidget {
 private FantasyUi(Text label,int x,int y,int width,int height,PressAction action,NarrationSupplier narration){super(x,y,width,height,label,action,narration);}
 static ButtonWidget button(Text label,int x,int y,int width,int height,PressAction action){return new FantasyUi(label,x,y,width,height,action,DEFAULT_NARRATION_SUPPLIER);}
 static ButtonWidget statButton(Text label,PressAction action,Text narration){return new FantasyUi(label,0,0,18,18,action,ignored->narration.copy());}
 @Override protected void renderWidget(DrawContext c,int mouseX,int mouseY,float delta){
  boolean focus=isHovered()||isFocused();
  int edge=active?(focus?DarkFantasyPalette.GOLD:DarkFantasyPalette.BRONZE):DarkFantasyPalette.IRON;
  frame(c,getX(),getY(),getWidth(),getHeight(),edge,focus&&active?0xFF303338:DarkFantasyPalette.SLATE);
  drawScrollableText(c,MinecraftClient.getInstance().textRenderer,2,active?DarkFantasyPalette.TEXT:DarkFantasyPalette.MUTED);
 }
 static void frame(DrawContext c,int x,int y,int w,int h,int edge,int fill){
  c.fill(x,y,x+w,y+h,edge);c.fill(x+1,y+1,x+w-1,y+h-1,fill);
  c.fill(x+2,y+2,x+w-2,y+3,0x255F686D);c.fill(x+2,y+h-2,x+w-2,y+h-1,0x66000000);
 }
 /** Tiny original pixel runes at panel corners; no atlas allocations or obscuring glow. */
 static void corners(DrawContext c,int x,int y,int w,int h){
  for(int side=0;side<2;side++)for(int row=0;row<2;row++){
   int px=side==0?x+3:x+w-4,py=row==0?y+3:y+h-4;
   int dx=side==0?1:-1,dy=row==0?1:-1;
   for(int i=0;i<7;i++){c.fill(px+dx*i,py,px+dx*i+1,py+1,DarkFantasyPalette.BRONZE);c.fill(px,py+dy*i,px+1,py+dy*i+1,DarkFantasyPalette.BRONZE);}
   c.fill(px+dx*3,py+dy*3,px+dx*3+1,py+dy*3+1,DarkFantasyPalette.GOLD);
  }
 }
}
