package dk.starswordmaster.client;
import dk.starswordmaster.progression.*;
import dk.starswordmaster.ui.SkillHudLayout;
import dk.starswordmaster.ui.ResourceHudLayout;
import dk.starswordmaster.client.mixin.HudHealthAccessor;
import net.fabricmc.fabric.api.client.rendering.v1.hud.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;
public final class SkillHud {
 public static void register(){
  HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR,Identifier.of("starswordmaster","skills"),(context,tick)->render(context));
  // Replace hearts only while our synchronized health display is available.
  HudElementRegistry.replaceElement(VanillaHudElements.HEALTH_BAR,original->(context,tick)->{
   if(!visible(MinecraftClient.getInstance()))original.render(context,tick);
  });
  for(var id:new Identifier[]{VanillaHudElements.HELD_ITEM_TOOLTIP,VanillaHudElements.OVERLAY_MESSAGE})HudElementRegistry.replaceElement(id,original->(context,tick)->{
   var mc=MinecraftClient.getInstance();var player=mc.player;
   int extra=player==null?0:ResourceHudLayout.tooltipExtraLift(player.getMaxHealth(),player.getHealth(),((HudHealthAccessor)mc.inGameHud).starswordmaster$getRenderHealthValue(),player.getAbsorptionAmount());
   renderVanillaIndicator(context,()->original.render(context,tick),extra);
  });
  // Reserve room above the hotbar; resource layout chooses the offset at the current GUI size.
  for(var id:new Identifier[]{VanillaHudElements.ARMOR_BAR,VanillaHudElements.FOOD_BAR,VanillaHudElements.AIR_BAR,VanillaHudElements.MOUNT_HEALTH,VanillaHudElements.INFO_BAR,VanillaHudElements.EXPERIENCE_LEVEL}){
   HudElementRegistry.replaceElement(id,original->(context,tick)->{
    renderVanillaIndicator(context,()->original.render(context,tick));
   });
  }
 }
 public static void renderVanillaIndicator(DrawContext context,Runnable draw){renderVanillaIndicator(context,draw,0);}
 private static void renderVanillaIndicator(DrawContext context,Runnable draw,int extra){
  if(!visible(MinecraftClient.getInstance())){draw.run();return;}
  var matrices=context.getMatrices();matrices.pushMatrix();
  try{matrices.translate(0,-ResourceHudLayout.forScreen(context.getScaledWindowWidth(),context.getScaledWindowHeight()).vanillaLift()-extra);draw.run();}finally{matrices.popMatrix();}
 }
 static boolean visible(MinecraftClient mc){return StarSwordmasterClient.ready&&StarSwordmasterClient.data!=null&&mc.player!=null&&!mc.player.isSpectator()&&!mc.options.hudHidden;}
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
  ResourceHud.render(c);
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
}
