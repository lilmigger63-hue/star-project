package dk.starswordmaster.client;
import dk.starswordmaster.progression.*;
import dk.starswordmaster.ui.DarkFantasyPalette;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.util.*;
import dk.starswordmaster.ui.SkillTreeLayout.Node;
public final class SkillTreeScreen extends Screen {
 private final List<Node> nodes=dk.starswordmaster.ui.SkillTreeLayout.NODES;
 private final List<ButtonWidget> styleButtons=new ArrayList<>();
 private final List<ButtonWidget> assignButtons=new ArrayList<>(),classButtons=new ArrayList<>();
 private final dk.starswordmaster.ui.TreeViewport view=new dk.starswordmaster.ui.TreeViewport();
 private String selected="spark";
 private ButtonWidget unlock;
 private int panelX,actionY;
 public SkillTreeScreen(){super(treeText("title"));}
 private static Text treeText(String key,Object... args){return Text.translatable("screen.starswordmaster."+key,args);}
 private static Text className(PlayerProgress.PlayerClass cls){return Text.translatable("status.starswordmaster.class."+cls.name().toLowerCase(Locale.ROOT));}
 private static Text styleName(PlayerProgress.SwordStyle style){return Text.translatable("status.starswordmaster.style."+style.name().toLowerCase(Locale.ROOT));}
 private void detail(DrawContext c,Text text,int y,int color){c.drawTextWithShadow(textRenderer,textRenderer.trimToWidth(text.getString(),142),panelX+8,y,color);}
 private static String number(double value){return String.format(Locale.ROOT,"%.1f",value);}
 private ButtonWidget button(Text text,int x,int y,int w,Runnable action){return addDrawableChild(FantasyUi.button(text,x,y,w,18,b->action.run()));}
 @Override protected void init(){
  panelX=width-158;view.fit(panelX,height-100);actionY=Math.min(137,height-107);assignButtons.clear();classButtons.clear();styleButtons.clear();
  PlayerProgress.PlayerClass[] classes={PlayerProgress.PlayerClass.SWORDSMAN,PlayerProgress.PlayerClass.MAGE,PlayerProgress.PlayerClass.SPELLBLADE};
  for(int i=0;i<3;i++){
   var cls=classes[i];var name=className(cls);int buttonWidth=Math.max(66,(width-16)/3);
   var b=FantasyUi.button(name,8+i*buttonWidth,28,buttonWidth-4,18,w->client.setScreen(new ConfirmScreen(confirm->{if(confirm)StarSwordmasterClient.send("class",cls.name(),0);client.setScreen(this);},treeText("confirm_class"),treeText("class_warning",name),treeText("confirm"),treeText("cancel"))));classButtons.add(addDrawableChild(b));
  }
  unlock=addDrawableChild(FantasyUi.button(treeText("learn_perk"),panelX+8,actionY,142,18,b->StarSwordmasterClient.send("unlock",selected,0)));
  for(int i=0;i<5;i++){final int slot=i;assignButtons.add(addDrawableChild(FantasyUi.button(Text.literal(""+(i+1)),panelX+8+i*28,actionY+35,25,18,b->StarSwordmasterClient.send("assign",selected,slot))));}
  var styles=PlayerProgress.SwordStyle.values();int styleWidth=Math.max(65,(width-76)/3);
  for(int i=0;i<3;i++){var style=styles[i];styleButtons.add(button(styleName(style),8+i*styleWidth,height-22,styleWidth-4,()->StarSwordmasterClient.send("style",style.name(),0)));}
  button(treeText("close"),width-62,height-22,54,this::close);
  updateButtons();
 }
 private void updateButtons(){
  var p=StarSwordmasterClient.data;var a=Ability.ALL.get(selected);
  for(int i=0;i<styleButtons.size();i++){var style=PlayerProgress.SwordStyle.values()[i];styleButtons.get(i).setMessage(p.style()==style?treeText("selected_style",styleName(style)):styleName(style));}
  for(var b:classButtons)b.active=p.level().compareTo(java.math.BigInteger.TEN)>=0&&p.playerClass()==PlayerProgress.PlayerClass.UNINITIATED;
  unlock.active=p.canUnlock(selected);
  for(var b:assignButtons)b.active=a!=null&&a.active()&&p.learned().contains(selected);
 }
 @Override public void tick(){updateButtons();}
 private int nx(Node n){return (int)view.screenX(n.x(),panelX/2.0);}
 private int ny(Node n){return (int)view.screenY(n.y(),height/2.0);}
 private Node find(String id){return nodes.stream().filter(n->n.id().equals(id)).findFirst().orElse(null);}
 private void line(DrawContext c,int x1,int y1,int x2,int y2,int color){int steps=Math.max(Math.abs(x2-x1),Math.abs(y2-y1));for(int i=0;i<=steps;i++){double t=steps==0?0:(double)i/steps;int x=(int)(x1+(x2-x1)*t),y=(int)(y1+(y2-y1)*t);c.fill(x,y,x+2,y+2,color);}}
 @Override public void render(DrawContext c,int mouseX,int mouseY,float delta){
  c.drawTexture(RenderPipelines.GUI_TEXTURED,Identifier.of("starswordmaster","textures/gui/constellation.png"),0,0,0,0,width,height,1024,768,1024,768);
  c.fill(0,0,width,height,0x80121416);c.fill(0,0,width,50,0xF0121416);c.fill(0,height-49,width,height,0xF0121416);c.fill(panelX,50,width,height-49,0xF0202529);
  c.fill(panelX,50,panelX+1,height-49,DarkFantasyPalette.BRONZE);c.fill(0,49,width,50,DarkFantasyPalette.BRONZE);FantasyUi.corners(c,panelX,50,width-panelX,height-99);
  var p=StarSwordmasterClient.data;
  c.drawTextWithShadow(textRenderer,textRenderer.trimToWidth(treeText("header",p.level().toString(),p.rank()).getString(),width-16),8,7,DarkFantasyPalette.GOLD);
  c.drawTextWithShadow(textRenderer,textRenderer.trimToWidth(treeText("points_class",p.points().toString(),className(p.playerClass())).getString(),width-16),8,17,DarkFantasyPalette.MUTED);
  c.enableScissor(0,51,panelX,height-50);
  for(Node n:nodes){Ability a=Ability.ALL.get(n.id());Node parent=find(a.parent());int px=parent==null?(int)view.screenX(0,panelX/2.0):nx(parent),py=parent==null?(int)view.screenY(0,height/2.0):ny(parent);line(c,px,py,nx(n),ny(n),connectionColor(a,p.learned().contains(n.id())));}
  int size=dk.starswordmaster.ui.SkillTreeLayout.nodeSize(view.zoom());
  for(Node n:nodes){int x=nx(n)-size/2,y=ny(n)-size/2;if(n.id().equals(selected))c.fill(x-3,y-3,x+size+3,y+size+3,DarkFantasyPalette.GOLD);Ability ability=Ability.ALL.get(n.id());boolean known=p.learned().contains(n.id());boolean classLocked=!ability.classAllowed(p.playerClass());boolean available=p.canUnlock(n.id());
   int frame=known?DarkFantasyPalette.LEARNED:classLocked?DarkFantasyPalette.CLASS_LOCKED:available?DarkFantasyPalette.AVAILABLE:DarkFantasyPalette.LOCKED;int edge=dk.starswordmaster.ui.SkillTreeLayout.frameWidth(view.zoom());c.fill(x-edge,y-edge,x+size+edge,y+size+edge,frame);SkillHud.icon(c,n.id(),x,y,size,known||available);
   if(known){c.fill(x+size-4,y+size-4,x+size,y+size,DarkFantasyPalette.LEARNED);}else if(classLocked){c.fill(x+size-4,y,x+size,y+2,DarkFantasyPalette.CLASS_LOCKED);}else if(available){c.fill(x,y,x+3,y+3,DarkFantasyPalette.AVAILABLE);}}
  c.disableScissor();
  Ability a=Ability.ALL.get(selected);
  detail(c,SkillHud.name(selected),57,SkillHud.color(a));
  if(actionY>110){
   c.drawTextWithShadow(textRenderer,textRenderer.trimToWidth(Text.translatable("description.starswordmaster."+selected).getString(),142),panelX+8,70,DarkFantasyPalette.TEXT);
   detail(c,Text.translatable("screen.starswordmaster.skill_requirement",skillName(a),a.requirement(),p.skillLevel(a.skill())),85,DarkFantasyPalette.MUTED);
   detail(c,treeText("resource_cost",number(p.manaCost(a)),number(p.staminaCost(a))),98,DarkFantasyPalette.MUTED);
   detail(c,a.active()?treeText("cooldown",number(p.cooldownTicks(a)/20.0)):Text.translatable("screen.starswordmaster.passive_level",a.characterRequirement()),111,DarkFantasyPalette.GOLD);
   if(a.exclusiveClass()!=null)detail(c,className(a.exclusiveClass()),123,DarkFantasyPalette.GOLD);
  }
  detail(c,treeText("assign_slot"),actionY+24,DarkFantasyPalette.TEXT);
  c.drawTextWithShadow(textRenderer,treeText("tree_hint"),8,height-59,DarkFantasyPalette.MUTED);
  super.render(c,mouseX,mouseY,delta);
  if(mouseX>=panelX&&mouseY>=54&&mouseY<Math.min(actionY,133))c.drawTooltip(textRenderer,details(a),mouseX,mouseY);
  if(mouseX<panelX&&mouseY>50&&mouseY<height-50){var node=dk.starswordmaster.ui.SkillTreeLayout.hitNode(view,mouseX,mouseY,panelX/2.0,height/2.0);if(node!=null)c.drawTooltip(textRenderer,details(Ability.ALL.get(node.id())),mouseX,mouseY);}
 }
 private static Text skillName(Ability a){return Text.translatable("skill.starswordmaster."+a.skill().name().toLowerCase(Locale.ROOT));}
 private static int connectionColor(Ability a,boolean learned){return switch(a.skill()){case LIGHTNING,WATER,EARTH,DARK->learned?SkillHud.color(a):0xFF000000|((SkillHud.color(a)&0xFEFEFE)>>>1);default->learned?DarkFantasyPalette.BRONZE:DarkFantasyPalette.IRON;};}
 private List<Text> details(Ability a){
  var p=StarSwordmasterClient.data;var lines=new ArrayList<Text>();lines.add(SkillHud.name(a.id()));lines.add(Text.translatable("description.starswordmaster."+a.id()));lines.add(Text.translatable("screen.starswordmaster.skill_requirement",skillName(a),a.requirement(),p.skillLevel(a.skill())));
  if(!a.parent().isEmpty())lines.add(treeText("previous_ability",SkillHud.name(a.parent())));
  if(a.characterRequirement()>1)lines.add(Text.translatable("screen.starswordmaster.character_requirement",a.characterRequirement(),p.level().toString()));
  if(a.exclusiveClass()!=null)lines.add(treeText("required_class",className(a.exclusiveClass())));
  lines.add(treeText("cost_details",number(p.manaCost(a)),number(p.staminaCost(a)),number(p.cooldownTicks(a)/20.0)));lines.add(treeText(p.learned().contains(a.id())?"learned":"price"));if(!p.learned().contains(a.id()))lines.add(treeText(!a.classAllowed(p.playerClass())?"state.class_locked":p.canUnlock(a.id())?"state.available":"state.locked"));return lines;
 }
 @Override public boolean mouseClicked(Click click,boolean doubleClick){
  if(super.mouseClicked(click,doubleClick))return true;
  if(click.button()==0&&click.x()<panelX&&click.y()>50&&click.y()<height-50){var node=dk.starswordmaster.ui.SkillTreeLayout.hitNode(view,click.x(),click.y(),panelX/2.0,height/2.0);if(node!=null){selected=node.id();updateButtons();}return true;}
  return false;
 }
 @Override public boolean mouseDragged(Click click,double dx,double dy){if(click.button()==0&&click.x()<panelX&&click.y()>50&&click.y()<height-50){view.pan(dx,dy);return true;}return super.mouseDragged(click,dx,dy);}
 @Override public boolean mouseScrolled(double x,double y,double horizontal,double vertical){if(x<panelX&&y>50&&y<height-50){view.zoomAt(x,y,panelX/2.0,height/2.0,vertical);return true;}return super.mouseScrolled(x,y,horizontal,vertical);}
 @Override public boolean shouldPause(){return false;}
}
