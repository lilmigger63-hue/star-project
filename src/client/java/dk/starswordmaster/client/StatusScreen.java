package dk.starswordmaster.client;
import dk.starswordmaster.progression.*;
import dk.starswordmaster.ui.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import java.util.*;
/** A read-only view: never sends actions or mutates player progression. */
public final class StatusScreen extends Screen {
 private StatusLayout layout;
 private double scroll;
 private int contentHeight;
 private int pointerX,pointerY;
 private boolean draggingScroll;
 private double scrollGrab;
 private List<Text> tooltip;
 private static final int GOLD=0xFFE8CB88,WHITE=0xFFDAE5EF,MUTED=0xFF99AEC3;
 public StatusScreen(){super(label("title"));}
 private static Text label(String key,Object... args){return Text.translatable("status.starswordmaster."+key,args);}
 @Override protected void init(){
  layout=StatusLayout.forScreen(width,height);
  addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"),b->close()).dimensions(layout.x()+layout.width()-70,layout.y()+layout.height()-23,60,18).build());
 }
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
  card(c,"character",x,y,w,100);
  row(c,"level",Text.literal(Integer.toString(d.level())),x,y+20,w);
  row(c,"class",label("class."+d.playerClass().name().toLowerCase(Locale.ROOT)),x,y+31,w);
  row(c,"rank",Text.literal(Integer.toString(d.rank())),x,y+42,w);
  row(c,"style",label("style."+d.style().name().toLowerCase(Locale.ROOT)),x,y+53,w);
  row(c,"total_xp",Text.literal(Integer.toString(d.totalXp())),x,y+64,w);
  text(c,d.xpForNextLevel()==0?label("max_level"):label("next_xp",d.xpInLevel(),d.xpForNextLevel()),x+8,y+77,w-16,MUTED);
  bar(c,x+8,y+90,w-16,d.xpProgress(),0xFFBDA268);
 }
 private void stats(DrawContext c,StatusData d,int x,int y,int w){
  card(c,"stats",x,y,w,100);int i=0;
  for(var stat:PlayerProgress.Stat.values())row(c,"stat."+stat.name().toLowerCase(Locale.ROOT),Text.literal(Integer.toString(d.stats().get(stat))),x,y+20+11*i++,w);
  row(c,"stat_points",Text.literal(Integer.toString(d.statPoints())),x,y+64,w);
  row(c,"perk_points",Text.literal(Integer.toString(d.perkPoints())),x,y+77,w);
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
   var d=snapshot.get();int w=layout.bodyWidth(),column=layout.twoColumns()?(w-8)/2:w;
   int top=layout.twoColumns()?108:216;
   int ah=26+Math.max(1,d.active().size())*16,ph=26+Math.max(1,d.passive().size())*16;
   contentHeight=top+74+(layout.twoColumns()?Math.max(ah,ph):ah+8+ph);
   scroll=layout.clampScroll(scroll,contentHeight);int x=layout.bodyX(),y=layout.bodyTop()-(int)scroll;
   c.enableScissor(x,layout.bodyTop(),x+w,layout.bodyBottom());
   character(c,d,x,y,column);stats(c,d,layout.twoColumns()?x+column+8:x,layout.twoColumns()?y:y+108,column);
   y+=top;resources(c,d,x,y,w);y+=74;
   int activeHeight=abilities(c,d.active(),"active",x,y,column,mouseX,mouseY);
   abilities(c,d.passive(),"passive",layout.twoColumns()?x+column+8:x,layout.twoColumns()?y:y+activeHeight+8,column,mouseX,mouseY);
   c.disableScissor();
   if(contentHeight>layout.bodyHeight()){
    int trackX=layout.x()+layout.width()-7,h=layout.thumbHeight(contentHeight);
    int thumbY=layout.thumbTop(scroll,contentHeight);
    c.fill(trackX,layout.bodyTop(),trackX+2,layout.bodyBottom(),0xFF25354B);c.fill(trackX,thumbY,trackX+2,thumbY+h,0xFFBDA268);
   }
  }
  super.render(c,mouseX,mouseY,delta);
  if(tooltip!=null)c.drawTooltip(textRenderer,tooltip,mouseX,mouseY);
 }
 @Override public boolean mouseClicked(Click click,boolean doubleClick){
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
