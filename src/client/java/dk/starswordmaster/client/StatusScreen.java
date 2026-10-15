package dk.starswordmaster.client;
import dk.starswordmaster.progression.*;
import dk.starswordmaster.ui.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import java.util.*;
/** Displays authoritative snapshots; explicit stat purchases are validated and saved by the server. */
public final class StatusScreen extends Screen {
 private StatusLayout layout;
 private double scroll;
 private int contentHeight;
 private int pointerX,pointerY;
 private boolean draggingScroll;
 private double scrollGrab;
 private List<Text> tooltip;
 private final List<ButtonWidget> statButtons=new ArrayList<>();
 private PlayerProgress pendingStatSnapshot;
 private static final int GOLD=0xFFE8CB88,WHITE=0xFFDAE5EF,MUTED=0xFF99AEC3;
 public StatusScreen(){super(label("title"));}
 private static Text label(String key,Object... args){return Text.translatable("status.starswordmaster."+key,args);}
 private static String number(double value){return String.format(Locale.ROOT,"%.1f",value).replaceFirst("\\.0$","");}
 private static Text effects(PlayerProgress.Stat stat,StatBonuses b){
  return switch(stat){
   case STRENGTH->label("effect.strength",number(b.abilityDamage()*100),number(b.miningSpeed()*100));
   case AGILITY->label("effect.agility",number(b.cooldownReduction()*100),number(b.movementSpeed()*100));
   case ENDURANCE->label("effect.endurance",b.stamina(),number(b.health()));
   case SPIRIT->label("effect.spirit",b.mana(),number(b.manaRegeneration()*100));
  };
 }
 @Override protected void init(){
  layout=StatusLayout.forScreen(width,height);
  statButtons.clear();
  for(var stat:PlayerProgress.Stat.values()){
   Text description=label("increase_stat",label("stat."+stat.name().toLowerCase(Locale.ROOT)));
   var button=ButtonWidget.builder(Text.literal("+"),b->spendStat(stat)).dimensions(0,0,18,18).tooltip(Tooltip.of(description)).narrationSupplier(defaultText->description.copy()).build();
   statButtons.add(addSelectableChild(button));
  }
  addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"),b->close()).dimensions(layout.x()+layout.width()-70,layout.y()+layout.height()-23,60,18).build());
  updateStatButtons();
 }
 private void spendStat(PlayerProgress.Stat stat){
  var data=StarSwordmasterClient.data;
  if(!StarSwordmasterClient.ready||client==null||client.player==null||data==null||data.statPoints()==0||pendingStatSnapshot==data)return;
  pendingStatSnapshot=data;StarSwordmasterClient.send("stat",stat.name(),0);updateStatButtons();
 }
 private void updateStatButtons(){
  var data=StarSwordmasterClient.data;
  if(pendingStatSnapshot!=data)pendingStatSnapshot=null;
  boolean ready=StarSwordmasterClient.ready&&client!=null&&client.player!=null&&data!=null;
  for(int i=0;i<statButtons.size();i++){
   var button=statButtons.get(i);int y=layout.statButtonY(i,scroll);
   button.setX(layout.statButtonX());button.setY(y);button.visible=ready&&layout.statButtonInView(y);
   button.active=ready&&data.statPoints()>0&&pendingStatSnapshot==null;
  }
 }
 @Override public void tick(){updateStatButtons();}
 private void text(DrawContext c,Text value,int x,int y,int maxWidth,int color){
  String full=value.getString(),visible=textRenderer.trimToWidth(full,Math.max(1,maxWidth));
  c.drawTextWithShadow(textRenderer,visible,x,y,color);
  if(!visible.equals(full)&&pointerX>=x&&pointerX<x+maxWidth&&pointerY>=y&&pointerY<y+textRenderer.fontHeight&&pointerY>=layout.bodyTop()&&pointerY<layout.bodyBottom())tooltip=List.of(value);
 }
 private void card(DrawContext c,String title,int x,int y,int w,int h){
  c.fill(x,y,x+w,y+h,0xE80B1829);c.fill(x,y,x+w,y+1,0xFF354962);
  text(c,label(title),x+8,y+6,w-16,GOLD);
 }
 private void row(DrawContext c,String key,Text value,int x,int y,int w){
  Text name=label(key);int labelWidth=textRenderer.getWidth(name)+8;
  text(c,name,x+8,y,w-16,MUTED);text(c,value,x+8+labelWidth,y,w-16-labelWidth,WHITE);
 }
 private void bar(DrawContext c,int x,int y,int w,double fraction,int color){
  c.fill(x,y,x+w,y+4,0xFF25354B);c.fill(x,y,x+(int)(w*Math.max(0,Math.min(1,fraction))),y+4,color);
 }
 private void character(DrawContext c,StatusData d,int x,int y,int w){
  card(c,"character",x,y,w,StatusLayout.CHARACTER_HEIGHT);
  row(c,"level",Text.literal(Integer.toString(d.level())),x,y+20,w);
  row(c,"class",label("class."+d.playerClass().name().toLowerCase(Locale.ROOT)),x,y+31,w);
  row(c,"rank",Text.literal(Integer.toString(d.rank())),x,y+42,w);
  row(c,"style",label("style."+d.style().name().toLowerCase(Locale.ROOT)),x,y+53,w);
  row(c,"total_xp",Text.literal(Integer.toString(d.totalXp())),x,y+64,w);
  text(c,d.xpForNextLevel()==0?label("max_level"):label("next_xp",d.xpInLevel(),d.xpForNextLevel()),x+8,y+77,w-16,MUTED);
  bar(c,x+8,y+90,w-16,d.xpProgress(),0xFFBDA268);
  row(c,"perk_points",Text.literal(Integer.toString(d.perkPoints())),x,y+103,w);
 }
 private void stats(DrawContext c,StatusData d,int x,int y,int w){
  card(c,"stats",x,y,w,StatusLayout.STATS_HEIGHT);row(c,"stat_points",Text.literal(Integer.toString(d.statPoints())),x,y+20,w);int i=0;
  for(var stat:PlayerProgress.Stat.values()){
   String key=stat.name().toLowerCase(Locale.ROOT);int rowY=y+38+26*i++;
   row(c,"stat."+key,Text.literal(Integer.toString(d.stats().get(stat))),x,rowY,w-28);
   Text effect=effects(stat,d.bonuses());text(c,effect,x+8,rowY+11,w-42,MUTED);
   if(pointerX>=x+8&&pointerX<x+w-28&&pointerY>=rowY&&pointerY<rowY+22&&pointerY>=layout.bodyTop()&&pointerY<layout.bodyBottom())tooltip=List.of(label("stat."+key),effect,label("rule.old."+key),label("rule.new."+key));
  }
 }
 private void resources(DrawContext c,StatusData d,int x,int y,int w){
  card(c,"resources",x,y,w,66);
  row(c,"mana",Text.literal(String.format(Locale.ROOT,"%.1f / %.1f",d.mana(),d.maxMana())),x,y+20,w);
  bar(c,x+8,y+32,w-16,d.maxMana()>0?d.mana()/d.maxMana():0,0xFF4B9EE8);
  row(c,"stamina",Text.literal(String.format(Locale.ROOT,"%.1f / %.1f",d.stamina(),d.maxStamina())),x,y+43,w);
  bar(c,x+8,y+55,w-16,d.maxStamina()>0?d.stamina()/d.maxStamina():0,0xFFE0AD43);
 }
 private int abilities(DrawContext c,List<String> ids,String heading,int x,int y,int w,int mouseX,int mouseY){
  int h=26+Math.max(1,ids.size())*16;card(c,heading,x,y,w,h);
  text(c,Text.literal(Integer.toString(ids.size())),x+w-26,y+6,20,MUTED);
  if(ids.isEmpty())text(c,label("none"),x+8,y+23,w-16,MUTED);
  for(int i=0;i<ids.size();i++){
   String id=ids.get(i);int rowY=y+22+16*i;
   SkillHud.icon(c,id,x+8,rowY,12,true);text(c,SkillHud.name(id),x+26,rowY+2,w-34,WHITE);
   if(mouseX>=x&&mouseX<x+w&&mouseY>=rowY&&mouseY<rowY+16&&mouseY>=layout.bodyTop()&&mouseY<layout.bodyBottom())tooltip=List.of(SkillHud.name(id),Text.translatable("description.starswordmaster."+id));
  }
  return h;
 }
 @Override public void render(DrawContext c,int mouseX,int mouseY,float delta){
  tooltip=null;pointerX=mouseX;pointerY=mouseY;c.fill(0,0,width,height,0x99020811);
  c.fill(layout.x()-1,layout.y()-1,layout.x()+layout.width()+1,layout.y()+layout.height()+1,0xFF52627B);
  c.drawTexture(RenderPipelines.GUI_TEXTURED,Identifier.of("starswordmaster","textures/gui/constellation.png"),layout.x(),layout.y(),0,0,layout.width(),layout.height(),1024,768,1024,768);
  c.fill(layout.x(),layout.y(),layout.x()+layout.width(),layout.y()+layout.height(),0xCF07101E);
  text(c,title,layout.x()+12,layout.y()+11,layout.width()-24,GOLD);
  var snapshot=StatusData.from(StarSwordmasterClient.data,StarSwordmasterClient.ready&&client!=null&&client.player!=null);
  if(snapshot.isEmpty()){
   contentHeight=0;scroll=0;text(c,label("loading"),layout.bodyX(),layout.bodyTop()+8,layout.bodyWidth(),MUTED);
  }else{
   var d=snapshot.get();int w=layout.bodyWidth(),column=layout.columnWidth();
   int top=layout.summaryHeight();
   int ah=26+Math.max(1,d.active().size())*16,ph=26+Math.max(1,d.passive().size())*16;
   contentHeight=top+74+(layout.twoColumns()?Math.max(ah,ph):ah+8+ph);
   scroll=layout.clampScroll(scroll,contentHeight);int x=layout.bodyX(),y=layout.bodyTop()-(int)scroll;
   c.enableScissor(x,layout.bodyTop(),x+w,layout.bodyBottom());
   character(c,d,x,y,column);stats(c,d,layout.statsX(),layout.statsTop()-(int)scroll,column);
   y+=top;resources(c,d,x,y,w);y+=74;
   int activeHeight=abilities(c,d.active(),"active",x,y,column,mouseX,mouseY);
   abilities(c,d.passive(),"passive",layout.twoColumns()?x+column+8:x,layout.twoColumns()?y:y+activeHeight+8,column,mouseX,mouseY);
   updateStatButtons();for(var button:statButtons)button.render(c,mouseX,mouseY,delta);
   c.disableScissor();
   if(contentHeight>layout.bodyHeight()){
    int trackX=layout.x()+layout.width()-7,h=layout.thumbHeight(contentHeight);
    int thumbY=layout.thumbTop(scroll,contentHeight);
    c.fill(trackX,layout.bodyTop(),trackX+2,layout.bodyBottom(),0xFF25354B);c.fill(trackX,thumbY,trackX+2,thumbY+h,0xFFBDA268);
   }
  }
  updateStatButtons();
  super.render(c,mouseX,mouseY,delta);
  if(tooltip!=null)c.drawTooltip(textRenderer,tooltip,mouseX,mouseY);
 }
 @Override public boolean mouseClicked(Click click,boolean doubleClick){
  updateStatButtons();
  int trackX=layout.x()+layout.width()-7;
  if(click.button()==0&&contentHeight>layout.bodyHeight()&&click.x()>=trackX-3&&click.x()<trackX+5&&click.y()>=layout.bodyTop()&&click.y()<layout.bodyBottom()){
   int top=layout.thumbTop(scroll,contentHeight),h=layout.thumbHeight(contentHeight);
   scrollGrab=click.y()>=top&&click.y()<top+h?click.y()-top:h/2.0;draggingScroll=true;
   scroll=layout.scrollForThumb(click.y(),contentHeight,scrollGrab);return true;
  }
  return super.mouseClicked(click,doubleClick);
 }
 @Override public boolean mouseDragged(Click click,double dx,double dy){
  if(draggingScroll&&click.button()==0){scroll=layout.scrollForThumb(click.y(),contentHeight,scrollGrab);return true;}
  return super.mouseDragged(click,dx,dy);
 }
 @Override public boolean mouseReleased(Click click){
  if(draggingScroll&&click.button()==0){draggingScroll=false;return true;}
  return super.mouseReleased(click);
 }
 @Override public boolean mouseScrolled(double x,double y,double horizontal,double vertical){
  if(x>=layout.x()&&x<layout.x()+layout.width()&&y>=layout.bodyTop()&&y<layout.bodyBottom()){
   scroll=layout.clampScroll(scroll-vertical*24,contentHeight);return true;
  }
  return super.mouseScrolled(x,y,horizontal,vertical);
 }
 @Override public boolean keyPressed(KeyInput input){
  updateStatButtons();
  switch(input.key()){
   case GLFW.GLFW_KEY_PAGE_DOWN->scroll=layout.clampScroll(scroll+layout.bodyHeight(),contentHeight);
   case GLFW.GLFW_KEY_PAGE_UP->scroll=layout.clampScroll(scroll-layout.bodyHeight(),contentHeight);
   case GLFW.GLFW_KEY_HOME->scroll=0;
   case GLFW.GLFW_KEY_END->scroll=layout.clampScroll(contentHeight,contentHeight);
   default->{return super.keyPressed(input);}
  }
  return true;
 }
 @Override public boolean shouldPause(){return false;}
}
