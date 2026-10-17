package dk.starswordmaster.client;
import dk.starswordmaster.ui.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
/** Compact resource portrait; reads synchronized values and never sends actions or changes player state. */
public final class ResourceHud {
 private ResourceHud(){}
 public static void render(DrawContext c){
  var mc=MinecraftClient.getInstance();if(!SkillHud.visible(mc))return;
  var d=ResourceHudData.from(StarSwordmasterClient.data,StarSwordmasterClient.ready,mc.player.getHealth(),mc.player.getMaxHealth(),mc.player.getAbsorptionAmount()).orElseThrow();
  var l=SkillHud.resourceLayout(c.getScaledWindowWidth(),c.getScaledWindowHeight());
  portrait(c,mc,l,d.level());
  meter(c,l,0,d.mana(),d.mana().text(),0xFF2675BF,0xFF5CBAEF,0);
  meter(c,l,1,d.health(),d.healthText(),0xFFB52F3F,0xFFF06069,d.absorption());
  meter(c,l,2,d.stamina(),d.stamina().text(),0xFF258348,0xFF60CC77,0);

 }
 private static void meter(DrawContext c,ResourceHudLayout l,int row,ResourceHudData.Meter value,String text,int color,int highlight,double absorption){
  var mc=MinecraftClient.getInstance();int x=l.barX(),y=l.barY(row),w=ResourceHudLayout.BAR_WIDTH,h=ResourceHudLayout.BAR_HEIGHT;
  c.fill(x,y,x+w,y+h,0xFF060A10);c.fill(x+1,y+1,x+w-1,y+h-1,DarkFantasyPalette.BRONZE);
  c.fill(x+2,y+2,x+w-2,y+h-2,DarkFantasyPalette.SLATE);int fill=(int)((w-4)*value.fraction());
  c.fill(x+2,y+2,x+2+fill,y+h-2,color);c.fill(x+2,y+2,x+2+fill,y+3,highlight);
  c.fill(x+1,y+h-2,x+w-1,y+h-1,DarkFantasyPalette.CHARCOAL);
  if(absorption>0&&Double.isFinite(absorption)&&value.available()){
   int gold=(int)((w-4)*Math.min(1,absorption/(value.maximum()+absorption)));
   c.fill(x+2,y+h-3,x+2+gold,y+h-2,0xFFFFD36B);
  }
  icon(c,mc,row,x+3,y+2);
  int textX=x+15+(w-18-mc.textRenderer.getWidth(text))/2;
  c.drawTextWithShadow(mc.textRenderer,text,textX,y+1,0xFFFFFFFF);
 }
 private static void icon(DrawContext c,MinecraftClient mc,int row,int x,int y){
  if(row==1){
   String heart=mc.player.hasStatusEffect(StatusEffects.POISON)?"poisoned_full":mc.player.hasStatusEffect(StatusEffects.WITHER)?"withered_full":mc.player.isFrozen()?"frozen_full":"full";
   c.drawGuiTexture(RenderPipelines.GUI_TEXTURED,Identifier.ofVanilla("hud/heart/"+heart),x,y,9,9);return;
  }
  if(row==0){ // Pixel drop, with a pale reflection against the blue meter.
   c.fill(x+4,y,x+5,y+1,0xFFD7F2FF);c.fill(x+3,y+1,x+6,y+3,0xFF91D6FF);c.fill(x+2,y+3,x+7,y+7,0xFF8FD8FF);c.fill(x+3,y+7,x+6,y+8,0xFF8FD8FF);c.fill(x+3,y+3,x+4,y+5,0xFFF3FCFF);
  }else{ // Stamina bolt.
   c.fill(x+4,y,x+7,y+2,0xFFC7FFAF);c.fill(x+3,y+2,x+6,y+4,0xFFC7FFAF);c.fill(x+2,y+4,x+7,y+5,0xFFC7FFAF);c.fill(x+4,y+5,x+6,y+7,0xFFC7FFAF);c.fill(x+3,y+7,x+5,y+8,0xFFC7FFAF);
  }
 }
 private static int inset(int diameter,int row){double radius=diameter/2.0,dy=row+0.5-radius;return (int)Math.ceil(radius-Math.sqrt(Math.max(0,radius*radius-dy*dy)));}
 private static void disc(DrawContext c,int x,int y,int diameter,int top,int bottom){for(int row=0;row<diameter;row++){int left=inset(diameter,row);c.fill(x+left,y+row,x+diameter-left,y+row+1,row<diameter/2?top:bottom);}}
 private static void portrait(DrawContext c,MinecraftClient mc,ResourceHudLayout l,java.math.BigInteger level){
  int x=l.x(),y=l.y();disc(c,x,y,40,DarkFantasyPalette.CHARCOAL,DarkFantasyPalette.CHARCOAL);disc(c,x+1,y+1,38,DarkFantasyPalette.GOLD,DarkFantasyPalette.BRONZE);disc(c,x+3,y+3,34,DarkFantasyPalette.SLATE,DarkFantasyPalette.SLATE);
  // Vanilla provides a default skin while remote textures load. Clip its normal face/hat draw to a circle.
  var skin=mc.player.getSkin();
  for(int row=0;row<32;row++){
   int left=inset(32,row);c.enableScissor(x+4+left,y+4+row,x+36-left,y+5+row);
   try{PlayerSkinDrawer.draw(c,skin,x+4,y+4,32);}finally{c.disableScissor();}
  }
  String full=net.minecraft.text.Text.translatable("hud.starswordmaster.level",level.toString()).getString();String text=mc.textRenderer.getWidth(full)<=36?full:mc.textRenderer.trimToWidth(full,30)+"…";int w=Math.max(30,mc.textRenderer.getWidth(text)+4),bx=x+(40-w)/2;
  c.fill(bx,y+30,bx+w,y+40,DarkFantasyPalette.CHARCOAL);c.fill(bx+1,y+31,bx+w-1,y+39,DarkFantasyPalette.SLATE);
  c.drawTextWithShadow(mc.textRenderer,text,x+(40-mc.textRenderer.getWidth(text))/2,y+31,DarkFantasyPalette.GOLD);
 }
}
