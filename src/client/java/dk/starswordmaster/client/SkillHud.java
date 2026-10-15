package dk.starswordmaster.client;
import dk.starswordmaster.progression.*;
import dk.starswordmaster.ui.SkillHudLayout;
import net.fabricmc.fabric.api.client.rendering.v1.hud.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;
public final class SkillHud {
 public static void register(){
  HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR,Identifier.of("starswordmaster","skills"),(context,tick)->render(context));
  // Reserve one row without moving the actual hotbar or changing vanilla status values.
  for(var id:new Identifier[]{VanillaHudElements.ARMOR_BAR,VanillaHudElements.HEALTH_BAR,VanillaHudElements.FOOD_BAR,VanillaHudElements.AIR_BAR,VanillaHudElements.MOUNT_HEALTH,VanillaHudElements.INFO_BAR,VanillaHudElements.EXPERIENCE_LEVEL,VanillaHudElements.HELD_ITEM_TOOLTIP}){
   HudElementRegistry.replaceElement(id,original->(context,tick)->{
    renderVanillaIndicator(context,()->original.render(context,tick));
   });
  }
 }
 public static void renderVanillaIndicator(DrawContext context,Runnable draw){
  if(!visible(MinecraftClient.getInstance())){draw.run();return;}
  var matrices=context.getMatrices();matrices.pushMatrix();
  try{matrices.translate(0,-SkillHudLayout.VANILLA_LIFT);draw.run();}finally{matrices.popMatrix();}
 }
 private static boolean visible(MinecraftClient mc){return StarSwordmasterClient.ready&&StarSwordmasterClient.data!=null&&mc.player!=null&&!mc.player.isSpectator()&&!mc.options.hudHidden;}
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
  var mc=MinecraftClient.getInstance();if(!visible(mc))return;
  var p=StarSwordmasterClient.data;var layout=SkillHudLayout.forScreen(c.getScaledWindowWidth(),c.getScaledWindowHeight());int x=layout.x(),y=layout.y();
  c.fill(x,y,x+SkillHudLayout.WIDTH,y+SkillHudLayout.HEIGHT,0xFF536273);
  c.fill(x+1,y+1,x+SkillHudLayout.WIDTH-1,y+SkillHudLayout.HEIGHT-1,0xE80C1424);
  bar(c,x+38,layout.manaY(),SkillHudLayout.WIDTH-38,p.mana()/p.maxMana(),0xFF4B9EE8);
  bar(c,x+38,layout.staminaY(),SkillHudLayout.WIDTH-38,p.stamina()/p.maxStamina(),0xFFE0AD43);
  c.drawTextWithShadow(mc.textRenderer,"Lv "+p.level(),x+1,layout.manaY()+1,0xFFE3C987);
  for(int i=0;i<SkillHudLayout.SLOTS;i++){
   int sx=layout.iconX(i),sy=layout.iconY(),size=SkillHudLayout.ICON_SIZE;String id=p.slot(i);
   c.fill(layout.slotX(i),y+1,layout.slotX(i)+SkillHudLayout.SLOT_PITCH,y+SkillHudLayout.HEIGHT-1,0xFF354153);
   icon(c,id,sx,sy,size,true);
   if(!id.isEmpty()){
    int cooldown=p.cooldown(id);
    if(cooldown>0){int h=Math.min(size,(int)Math.ceil((double)size*cooldown/Math.max(1,p.cooldownDuration(id))));c.fill(sx,sy+size-h,sx+size,sy+size,0xBA080B16);String seconds=String.valueOf((cooldown+19)/20);c.drawTextWithShadow(mc.textRenderer,seconds,sx+(size-mc.textRenderer.getWidth(seconds))/2,y+2,0xFFFFFFFF);}
    else if(!p.canCast(i))c.fill(sx,sy,sx+size,sy+size,0x99AD2538);
   }
   String key=mc.textRenderer.trimToWidth(StarSwordmasterClient.keys[i].getBoundKeyLocalizedText().getString(),SkillHudLayout.SLOT_PITCH-2);
   c.drawTextWithShadow(mc.textRenderer,key,sx+(size-mc.textRenderer.getWidth(key))/2,y+11,0xFFFFFFFF);
  }
 }
 private static void bar(DrawContext c,int x,int y,int w,double percent,int color){c.fill(x,y,x+w,y+4,0xFF253144);c.fill(x,y,x+(int)(w*Math.max(0,Math.min(1,percent))),y+4,color);}
}
