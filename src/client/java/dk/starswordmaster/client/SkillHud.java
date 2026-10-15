package dk.starswordmaster.client;
import dk.starswordmaster.progression.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;
public final class SkillHud {
 public static Text name(String id){return Text.translatable("ability.starswordmaster."+id);}
 public static int color(Ability a){return switch(a.skill()){case FIRE->0xFFFF873E;case FROST->0xFF54CBFF;case SUPPORT->0xFF77E98B;case AURA->0xFFB784FF;case SWORD->a.exclusiveClass()==null?0xFFFFD27B:0xFFC698FF;};}
 public static void icon(DrawContext c,String id,int x,int y,int size,boolean learned){
  int border=id.isEmpty()?0xFF536273:learned?0xFFE1BD70:0xFF49586E;
  c.fill(x-1,y-1,x+size+1,y+size+1,border);c.fill(x,y,x+size,y+size,0xFF0C1424);
  if(!id.isEmpty()){
   c.drawTexture(RenderPipelines.GUI_TEXTURED,Identifier.of("starswordmaster","textures/skills/"+id+".png"),x,y,0,0,size,size,32,32,32,32);
   if(!learned)c.fill(x,y,x+size,y+size,0x99071321);
  }
 }
 public static void render(DrawContext c){
  var mc=MinecraftClient.getInstance();if(!StarSwordmasterClient.ready||mc.player==null||mc.player.isSpectator()||mc.options.hudHidden)return;
  var p=StarSwordmasterClient.data;int x=c.getScaledWindowWidth()/2-72,y=c.getScaledWindowHeight()-89;
  c.fill(x-5,y-15,x+149,y+29,0xB008101E);
  bar(c,x,y-12,144,p.mana()/p.maxMana(),0xFF4B9EE8);
  bar(c,x,y-6,144,p.stamina()/p.maxStamina(),0xFFE0AD43);
  for(int i=0;i<5;i++){
   int sx=x+i*30;String id=p.slot(i);icon(c,id,sx,y,24,true);
   if(!id.isEmpty()){
    Ability a=Ability.ALL.get(id);int cooldown=p.cooldown(id);
    if(cooldown>0){int h=Math.min(24,(int)Math.ceil(24.0*cooldown/Math.max(1,p.cooldownDuration(id))));c.fill(sx,y+24-h,sx+24,y+24,0xBA080B16);c.drawTextWithShadow(mc.textRenderer,String.valueOf((cooldown+19)/20),sx+9,y+8,0xFFFFFFFF);}
    else if(!p.canCast(i))c.fill(sx,y,sx+24,y+24,0x99AD2538);
   }
   String key=StarSwordmasterClient.keys[i].getBoundKeyLocalizedText().getString();if(key.length()>4)key=key.substring(0,4);
   c.drawTextWithShadow(mc.textRenderer,key,sx+2,y+18,0xFFFFFFFF);
  }
  c.drawTextWithShadow(mc.textRenderer,"Lv "+p.level(),x-38,y+8,0xFFE3C987);
 }
 private static void bar(DrawContext c,int x,int y,int w,double percent,int color){c.fill(x,y,x+w,y+4,0xFF253144);c.fill(x,y,x+(int)(w*Math.max(0,Math.min(1,percent))),y+4,color);}
}
