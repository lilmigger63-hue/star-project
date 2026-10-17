package dk.starswordmaster.client;
import dk.starswordmaster.progression.*;
import dk.starswordmaster.ui.SkillHudLayout;
import dk.starswordmaster.ui.DarkFantasyPalette;
import dk.starswordmaster.ui.ResourceHudLayout;
import dk.starswordmaster.client.mixin.HudHealthAccessor;
import net.fabricmc.fabric.api.client.rendering.v1.hud.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;
import net.minecraft.entity.LivingEntity;
public final class SkillHud {
 public static void register(){
  HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR,Identifier.of("starswordmaster","skills"),(context,tick)->render(context));
  // Replace hearts only while our synchronized health display is available.
  HudElementRegistry.replaceElement(VanillaHudElements.HEALTH_BAR,original->(context,tick)->{
   if(!visible(MinecraftClient.getInstance()))original.render(context,tick);
  });
  for(var id:new Identifier[]{VanillaHudElements.HELD_ITEM_TOOLTIP,VanillaHudElements.OVERLAY_MESSAGE})HudElementRegistry.replaceElement(id,original->(context,tick)->{
   var layout=resourceLayout(context.getScaledWindowWidth(),context.getScaledWindowHeight());
   var mc=MinecraftClient.getInstance();boolean nativeStatus=!id.equals(VanillaHudElements.HELD_ITEM_TOOLTIP)||mc.interactionManager==null||mc.interactionManager.hasStatusBars();
   renderVanilla(context,()->original.render(context,tick),layout.tooltipLift(context.getScaledWindowHeight(),indicatorExtra(),nativeStatus));
  });
  // Only XP, its level and food meet the hotbar. Other native indicators keep their existing lift.
  for(var id:new Identifier[]{VanillaHudElements.ARMOR_BAR,VanillaHudElements.FOOD_BAR,VanillaHudElements.AIR_BAR,VanillaHudElements.MOUNT_HEALTH,VanillaHudElements.INFO_BAR,VanillaHudElements.EXPERIENCE_LEVEL}){
   HudElementRegistry.replaceElement(id,original->(context,tick)->{
    int lift=id.equals(VanillaHudElements.FOOD_BAR)?SkillHudLayout.EXPERIENCE_LIFT:
     id.equals(VanillaHudElements.INFO_BAR)||id.equals(VanillaHudElements.EXPERIENCE_LEVEL)?SkillHudLayout.infoBarLift(experienceBar()):SkillHudLayout.VANILLA_LIFT;
    renderVanilla(context,()->original.render(context,tick),lift);
   });
  }
 }
 private static boolean experienceBar(){return ((HudHealthAccessor)MinecraftClient.getInstance().inGameHud).starswordmaster$getCurrentBar().getValue() instanceof net.minecraft.client.gui.hud.bar.ExperienceBar;}
 public static ResourceHudLayout resourceLayout(int width,int height){return ResourceHudLayout.forScreen(width,height,indicatorExtra());}
 private static int indicatorExtra(){
  var mc=MinecraftClient.getInstance();var p=mc.player;if(p==null)return 0;
  int armor=ResourceHudLayout.tooltipExtraLift(p.getMaxHealth(),p.getHealth(),((HudHealthAccessor)mc.inGameHud).starswordmaster$getRenderHealthValue(),p.getAbsorptionAmount());
  int hearts=p.getVehicle() instanceof LivingEntity mount&&mount.isLiving()?Math.min(30,(int)(mount.getMaxHealth()+0.5F)/2):0;
  int air=Math.max(0,(hearts+9)/10-2)*10;
  return Math.max(armor,air);
 }
 public static int chatLift(){
  var mc=MinecraftClient.getInstance();if(!visible(mc))return 0;
  int h=mc.getWindow().getScaledHeight();return resourceLayout(mc.getWindow().getScaledWidth(),h).chatLift(h,indicatorExtra());
 }
 public static int chatHeight(int requested,double scale){
  var mc=MinecraftClient.getInstance();if(!visible(mc))return requested;
  int h=mc.getWindow().getScaledHeight();return resourceLayout(mc.getWindow().getScaledWidth(),h).chatHeight(h,requested,scale,indicatorExtra());
 }
 public static void renderVanillaIndicator(DrawContext context,Runnable draw){renderVanilla(context,draw,SkillHudLayout.VANILLA_LIFT);}
 private static void renderVanilla(DrawContext context,Runnable draw,int lift){
  if(!visible(MinecraftClient.getInstance())){draw.run();return;}
  var matrices=context.getMatrices();matrices.pushMatrix();
  try{matrices.translate(0,-lift);draw.run();}finally{matrices.popMatrix();}
 }
 static boolean visible(MinecraftClient mc){return StarSwordmasterClient.ready&&StarSwordmasterClient.data!=null&&mc.player!=null&&!mc.player.isSpectator()&&!mc.options.hudHidden;}
 public static Text name(String id){return Text.translatable("ability.starswordmaster."+id);}
 public static int color(Ability a){return switch(a.skill()){case LIGHTNING->0xFF9AABFF;case WATER->0xFF3FDCDD;case EARTH->0xFFC4A163;case DARK->0xFFB366D4;case FIRE->0xFFFF873E;case FROST->0xFF54CBFF;case SUPPORT->0xFF77E98B;case AURA->0xFFB784FF;case SWORD->a.exclusiveClass()==null?0xFFFFD27B:0xFFC698FF;};}
 public static void icon(DrawContext c,String id,int x,int y,int size,boolean learned){
  int border=id.isEmpty()?DarkFantasyPalette.IRON:learned?DarkFantasyPalette.LEARNED:DarkFantasyPalette.LOCKED;
  c.fill(x-1,y-1,x+size+1,y+size+1,border);c.fill(x,y,x+size,y+size,DarkFantasyPalette.CHARCOAL);
  if(!id.isEmpty()){
   c.drawTexture(RenderPipelines.GUI_TEXTURED,Identifier.of("starswordmaster","textures/skills/"+id+".png"),x,y,0,0,size,size,32,32,32,32);
   if(!learned)c.fill(x,y,x+size,y+size,0xAA121416);
  }
 }
 public static void render(DrawContext c){
  var mc=MinecraftClient.getInstance();if(!visible(mc))return;
  ResourceHud.render(c);
  var layout=SkillHudLayout.forResources(resourceLayout(c.getScaledWindowWidth(),c.getScaledWindowHeight()));
  var matrices=c.getMatrices();matrices.pushMatrix();
  try{matrices.translate(layout.x(),layout.y());matrices.scale(SkillHudLayout.SCALE,SkillHudLayout.SCALE);renderBar(c,mc,StarSwordmasterClient.data);}finally{matrices.popMatrix();}
 }
 private static void renderBar(DrawContext c,MinecraftClient mc,PlayerProgress p){
  // Retain every original design coordinate; one transform scales frames, icons, cooldowns and labels together.
  var layout=new SkillHudLayout(0,0);int x=0,y=0;
  c.fill(x,y,x+SkillHudLayout.WIDTH,y+SkillHudLayout.HEIGHT,DarkFantasyPalette.IRON);
  c.fill(x+1,y+1,x+SkillHudLayout.WIDTH-1,y+SkillHudLayout.HEIGHT-1,0xEB121416);
  for(int i=0;i<SkillHudLayout.SLOTS;i++){
   int sx=layout.iconX(i),sy=layout.iconY(),size=SkillHudLayout.ICON_SIZE;String id=p.slot(i);
   c.fill(layout.slotX(i),y+1,layout.slotX(i)+SkillHudLayout.SLOT_PITCH,y+SkillHudLayout.HEIGHT-1,DarkFantasyPalette.SLATE);
   icon(c,id,sx,sy,size,true);
   if(!id.isEmpty()){
    int cooldown=p.cooldown(id);
    if(cooldown>0){int h=Math.min(size,(int)Math.ceil((double)size*cooldown/Math.max(1,p.cooldownDuration(id))));c.fill(sx,sy+size-h,sx+size,sy+size,0xD0121416);String seconds=String.valueOf((cooldown+19)/20);c.drawTextWithShadow(mc.textRenderer,seconds,sx+(size-mc.textRenderer.getWidth(seconds))/2,y+2,0xFFFFFFFF);}
    else if(!p.canCast(i))c.fill(sx,sy,sx+size,sy+size,0xAA7D414B);
   }
   String key=mc.textRenderer.trimToWidth(StarSwordmasterClient.keys[i].getBoundKeyLocalizedText().getString(),SkillHudLayout.SLOT_PITCH-2);
   c.drawTextWithShadow(mc.textRenderer,key,sx+(size-mc.textRenderer.getWidth(key))/2,y+11,0xFFFFFFFF);
  }
 }
}
