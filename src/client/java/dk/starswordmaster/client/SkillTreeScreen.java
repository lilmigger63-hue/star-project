package dk.starswordmaster.client;
import dk.starswordmaster.progression.*;
import dk.starswordmaster.ui.*;
import dk.starswordmaster.ui.SkillTreeLayout.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.math.*;
import java.util.*;
/** Eight constellation arms share their layout with hit testing and the camera. */
public final class SkillTreeScreen extends Screen {
 private final TreeViewport view=new TreeViewport();
 private final List<ButtonWidget> classButtons=new ArrayList<>(),assignButtons=new ArrayList<>();
 private final List<ButtonWidget> detailButtons=new ArrayList<>();
 private TreeScreenLayout layout;
 private String selected="spark";
 private ButtonWidget unlock,reset,styleButton,detailsButton,focusButton;
 private boolean detailsPage;
 private int focusIndex=-1;
 public SkillTreeScreen(){super(text("title"));}
 private static Text text(String key,Object... args){return Text.translatable("screen.starswordmaster."+key,args);}
 private static Text className(PlayerProgress.PlayerClass cls){return Text.translatable("status.starswordmaster.class."+cls.name().toLowerCase(Locale.ROOT));}
 private static Text styleName(PlayerProgress.SwordStyle style){return Text.translatable("status.starswordmaster.style."+style.name().toLowerCase(Locale.ROOT));}
 private static String compact(BigInteger n){return ProgressNumbers.compact(n);}
 private static String number(double n){return String.format(Locale.ROOT,"%.1f",n);}
 private ButtonWidget button(Text label,int x,int y,int w,Runnable action){return addDrawableChild(FantasyUi.button(label,x,y,w,18,b->action.run()));}
 private void fit(){view.fit(layout.treeWidth(),layout.bodyBottom()-layout.bodyTop());focusIndex=-1;}
 @Override protected void init(){
  layout=TreeScreenLayout.of(width,height);fit();classButtons.clear();assignButtons.clear();detailButtons.clear();
  var classes=new PlayerProgress.PlayerClass[]{PlayerProgress.PlayerClass.SWORDSMAN,PlayerProgress.PlayerClass.MAGE,PlayerProgress.PlayerClass.SPELLBLADE};
  int classWidth=(width-16)/3;
  for(int i=0;i<classes.length;i++){var cls=classes[i];var name=className(cls);
   classButtons.add(button(name,8+i*classWidth,32,classWidth-4,()->client.setScreen(new ConfirmScreen(yes->{if(yes)StarSwordmasterClient.send("class",cls.name(),0);client.setScreen(this);},text("confirm_class"),text("class_warning",name),text("confirm"),text("cancel")))));
  }
  int dx=layout.detailX()+8,dw=layout.detailWidth()-16,actionY=layout.bodyBottom()-53;
  unlock=button(text("learn_perk"),dx,actionY,dw,()->StarSwordmasterClient.send("unlock",selected,0));detailButtons.add(unlock);
  int slotWidth=dw/5;
  for(int i=0;i<5;i++){int slot=i;var b=button(Text.literal(""+(i+1)),dx+i*slotWidth,actionY+32,slotWidth-3,()->StarSwordmasterClient.send("assign",selected,slot));assignButtons.add(b);detailButtons.add(b);}
  int third=(width-16)/3;
  button(text("fit"),8,height-45,third-4,this::fit);
  focusButton=button(text("all_branches"),8+third,height-45,third-4,()->{focusIndex=(focusIndex+1)%(Sector.values().length+1);if(focusIndex==8){fit();}else view.focus(Sector.values()[focusIndex],layout.treeWidth(),layout.bodyBottom()-layout.bodyTop());});
  detailsButton=button(text("details"),8+third*2,height-45,third-4,()->{detailsPage=!detailsPage;updateButtons();});
  styleButton=button(text("style"),8,height-23,third-4,()->{var values=PlayerProgress.SwordStyle.values();StarSwordmasterClient.send("style",values[(StarSwordmasterClient.data.style().ordinal()+1)%values.length].name(),0);});
  reset=button(text("reset_skills"),8+third,height-23,third-4,this::confirmReset);
  button(text("close"),8+third*2,height-23,third-4,this::close);updateButtons();
 }
 private void confirmReset(){client.setScreen(new ConfirmScreen(yes->{if(yes)StarSwordmasterClient.send("reset_skills","",0);client.setScreen(this);},text("reset_title"),text("reset_warning"),text("confirm_reset"),text("cancel")));}
 private boolean showingDetails(){return layout.sidePanel()||detailsPage;}
 private boolean showingTree(){return layout.sidePanel()||!detailsPage;}
 private void updateButtons(){
  var p=StarSwordmasterClient.data;var a=Ability.ALL.get(selected);
  for(var b:classButtons)b.active=p.level().compareTo(BigInteger.TEN)>=0&&p.playerClass()==PlayerProgress.PlayerClass.UNINITIATED;
  for(var b:detailButtons)b.visible=showingDetails();
  unlock.active=p.canUnlock(selected);for(var b:assignButtons)b.active=a!=null&&a.active()&&p.learned().contains(selected);
  styleButton.setMessage(text("current_style",styleName(p.style())));reset.active=!p.learned().isEmpty();
  detailsButton.visible=!layout.sidePanel();detailsButton.setMessage(text(detailsPage?"back_tree":"details"));
  focusButton.setMessage(focusIndex>=0&&focusIndex<8?Text.translatable(Sector.values()[focusIndex].translationKey()):text("all_branches"));
 }
 @Override public void tick(){updateButtons();}
 private int sx(double x){return (int)view.screenX(x,layout.centerX());}
 private int sy(double y){return (int)view.screenY(y,layout.centerY());}
 private boolean inTree(double x,double y){return showingTree()&&layout.inTree(x,y);}
 private void line(DrawContext c,int x1,int y1,int x2,int y2,int color){
  if(Math.max(x1,x2)<0||Math.min(x1,x2)>layout.treeWidth()||Math.max(y1,y2)<layout.bodyTop()||Math.min(y1,y2)>layout.bodyBottom())return;
  int steps=Math.max(Math.abs(x2-x1),Math.abs(y2-y1));for(int i=0;i<=steps;i++){double t=steps==0?0:(double)i/steps;int x=(int)(x1+(x2-x1)*t),y=(int)(y1+(y2-y1)*t);if(layout.inTree(x,y))c.fill(x,y,x+1,y+1,color);}
 }
 private void path(DrawContext c,List<Point> points,int color){for(int i=1;i<points.size();i++){var a=points.get(i-1);var b=points.get(i);line(c,sx(a.x()),sy(a.y()),sx(b.x()),sy(b.y()),color);}}
 private void circle(DrawContext c,int x,int y,int radius,int color){
  for(int row=-radius;row<=radius;row++){int half=(int)Math.sqrt(radius*radius-row*row);c.fill(x-half,y+row,x+half+1,y+row+1,color);}
 }
 private void tree(DrawContext c){
  var p=StarSwordmasterClient.data;c.enableScissor(0,layout.bodyTop(),layout.treeWidth(),layout.bodyBottom());
  for(var branch:SkillTreeLayout.BRANCHES)path(c,branch.points(),branch.spine()?0xFF49413A:0xFF363532);
  for(var edge:SkillTreeLayout.EDGES){boolean known=p.learned().contains(edge.child().id());int color=known?SkillTreeLayout.sector(edge.child()).color():edge.crossSector()?0xFF685579:0xFF55524B;if(edge.child().id().equals(selected))color=DarkFantasyPalette.GOLD;path(c,edge.points(),color);}
  int hub=Math.max(8,(int)(42*view.zoom())),cx=sx(0),cy=sy(0);circle(c,cx,cy,hub,DarkFantasyPalette.BRONZE);circle(c,cx,cy,hub-2,DarkFantasyPalette.SLATE);
  if(view.zoom()>=0.19){c.drawCenteredTextWithShadow(textRenderer,text("hub_level",compact(p.level())),cx,cy-9,DarkFantasyPalette.GOLD);c.drawCenteredTextWithShadow(textRenderer,text("hub_points",compact(p.points())),cx,cy+2,DarkFantasyPalette.TEXT);}
  int size=SkillTreeLayout.nodeSize(view.zoom());
  for(var n:SkillTreeLayout.NODES){int x=sx(n.x())-size/2,y=sy(n.y())-size/2;if(x+size+4<0||x-4>=layout.treeWidth()||y+size+4<layout.bodyTop()||y-4>=layout.bodyBottom())continue;
   Ability a=Ability.ALL.get(n.id());boolean known=p.learned().contains(n.id()),locked=!a.classAllowed(p.playerClass()),available=p.canUnlock(n.id());
   if(n.id().equals(selected))c.fill(x-3,y-3,x+size+3,y+size+3,DarkFantasyPalette.GOLD);
   int frame=known?DarkFantasyPalette.LEARNED:locked?DarkFantasyPalette.CLASS_LOCKED:available?DarkFantasyPalette.AVAILABLE:DarkFantasyPalette.LOCKED,edge=SkillTreeLayout.frameWidth(view.zoom());c.fill(x-edge,y-edge,x+size+edge,y+size+edge,frame);SkillHud.icon(c,n.id(),x,y,size,known||available);
   if(known)c.fill(x+size-3,y+size-3,x+size,y+size,DarkFantasyPalette.LEARNED);else if(available)c.fill(x,y,x+2,y+2,DarkFantasyPalette.AVAILABLE);
  }
  for(var sector:Sector.values()){double angle=Math.toRadians(sector.angle());int x=sx(Math.cos(angle)*130),y=sy(Math.sin(angle)*130);if(view.zoom()>=0.28)c.drawCenteredTextWithShadow(textRenderer,Text.translatable(sector.translationKey()),x,y-4,sector.color());}
  c.disableScissor();
 }
 private void detail(DrawContext c,Text label,int y,int color){c.drawTextWithShadow(textRenderer,textRenderer.trimToWidth(label.getString(),layout.detailWidth()-16),layout.detailX()+8,y,color);}
 private void details(DrawContext c){
  var a=Ability.ALL.get(selected);var p=StarSwordmasterClient.data;int x=layout.detailX(),top=layout.bodyTop(),bottom=layout.bodyBottom(),limit=bottom-58;
  c.fill(x,top,width,bottom,DarkFantasyPalette.SLATE);FantasyUi.corners(c,x,top,layout.detailWidth(),bottom-top);
  var lines=details(a);int y=top+5;for(int i=0;i<lines.size()&&y+9<=limit;i++){detail(c,lines.get(i),y,i==0?SkillHud.color(a):DarkFantasyPalette.TEXT);y+=12;}
  detail(c,text("assign_slot"),bottom-32,DarkFantasyPalette.MUTED);
 }
 @Override public void render(DrawContext c,int mouseX,int mouseY,float delta){
  c.drawTexture(RenderPipelines.GUI_TEXTURED,Identifier.of("starswordmaster","textures/gui/constellation.png"),0,0,0,0,width,height,1024,768,1024,768);c.fill(0,0,width,height,0xB0121416);c.fill(0,0,width,54,0xF0121416);c.fill(0,height-50,width,height,0xF0121416);
  var p=StarSwordmasterClient.data;
  c.drawTextWithShadow(textRenderer,textRenderer.trimToWidth(text("overview",compact(p.level()),compact(p.points()),compact(p.statPoints())).getString(),width-16),8,5,DarkFantasyPalette.GOLD);
  c.drawTextWithShadow(textRenderer,textRenderer.trimToWidth(text("xp_summary",compact(p.xpInLevel()),compact(p.xpForNextLevel())).getString(),width-16),8,16,DarkFantasyPalette.TEXT);
  double progress=new BigDecimal(p.xpInLevel()).divide(new BigDecimal(p.xpForNextLevel()),MathContext.DECIMAL64).doubleValue();c.fill(8,27,width-8,29,DarkFantasyPalette.IRON);c.fill(8,27,8+(int)((width-16)*Math.clamp(progress,0,1)),29,DarkFantasyPalette.GOLD);
  if(showingTree())tree(c);if(showingDetails())details(c);super.render(c,mouseX,mouseY,delta);
  if(mouseY<30)c.drawTooltip(textRenderer,List.of(text("overview",p.level().toString(),p.points().toString(),p.statPoints().toString()),text("xp_summary",p.xpInLevel().toString(),p.xpForNextLevel().toString())),mouseX,mouseY);
  else if(inTree(mouseX,mouseY)){var n=SkillTreeLayout.hitNode(view,mouseX,mouseY,layout.centerX(),layout.centerY());if(n!=null)c.drawTooltip(textRenderer,details(Ability.ALL.get(n.id())),mouseX,mouseY);}
  else if(showingDetails()&&mouseX>=layout.detailX()&&mouseY>=layout.bodyTop()&&mouseY<layout.bodyBottom()-56)c.drawTooltip(textRenderer,details(Ability.ALL.get(selected)),mouseX,mouseY);
 }
 private List<Text> details(Ability a){
  var p=StarSwordmasterClient.data;var lines=new ArrayList<Text>();lines.add(SkillHud.name(a.id()));lines.add(Text.translatable("description.starswordmaster."+a.id()));lines.add(text("skill_requirement",Text.translatable("skill.starswordmaster."+a.skill().name().toLowerCase(Locale.ROOT)),a.requirement(),p.skillLevel(a.skill())));
  if(!a.parent().isEmpty())lines.add(text("previous_ability",SkillHud.name(a.parent())));
  if(a.characterRequirement()>1)lines.add(text("character_requirement",a.characterRequirement(),compact(p.level())));
  if(a.exclusiveClass()!=null)lines.add(text("required_class",className(a.exclusiveClass())));
  if(a.active())lines.add(text("cost_details",number(p.manaCost(a)),number(p.staminaCost(a)),number(p.cooldownTicks(a)/20.0)));
  lines.add(text(p.learned().contains(a.id())?"learned":"price"));if(!p.learned().contains(a.id()))lines.add(text(!a.classAllowed(p.playerClass())?"state.class_locked":p.canUnlock(a.id())?"state.available":"state.locked"));return lines;
 }
 @Override public boolean mouseClicked(Click click,boolean doubleClick){
  if(super.mouseClicked(click,doubleClick))return true;
  if(click.button()==0&&inTree(click.x(),click.y())){var node=SkillTreeLayout.hitNode(view,click.x(),click.y(),layout.centerX(),layout.centerY());if(node!=null){selected=node.id();if(!layout.sidePanel())detailsPage=true;updateButtons();}return true;}return false;
 }
 @Override public boolean mouseDragged(Click click,double dx,double dy){if(click.button()==0&&inTree(click.x(),click.y())){view.pan(dx,dy);return true;}return super.mouseDragged(click,dx,dy);}
 @Override public boolean mouseScrolled(double x,double y,double horizontal,double vertical){if(inTree(x,y)){view.zoomAt(x,y,layout.centerX(),layout.centerY(),vertical);return true;}return super.mouseScrolled(x,y,horizontal,vertical);}
 @Override public boolean shouldPause(){return false;}
}
