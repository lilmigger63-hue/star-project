package dk.starswordmaster.client;
import dk.starswordmaster.progression.*;
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
 public SkillTreeScreen(){super(Text.literal("Star Swordmaster"));}
 private ButtonWidget button(String text,int x,int y,int w,Runnable action){return addDrawableChild(ButtonWidget.builder(Text.literal(text),b->action.run()).dimensions(x,y,w,18).build());}
 @Override protected void init(){
  panelX=width-158;view.fit(panelX,height-100);actionY=Math.min(137,height-107);assignButtons.clear();classButtons.clear();styleButtons.clear();
  PlayerProgress.PlayerClass[] classes={PlayerProgress.PlayerClass.SWORDSMAN,PlayerProgress.PlayerClass.MAGE,PlayerProgress.PlayerClass.SPELLBLADE};
  String[] labels={"Sværdkæmper","Magiker","Magisk sværd"};
  for(int i=0;i<3;i++){
   var cls=classes[i];var name=labels[i];int buttonWidth=Math.max(66,(width-16)/3);
   var b=ButtonWidget.builder(Text.literal(name),w->client.setScreen(new ConfirmScreen(confirm->{if(confirm)StarSwordmasterClient.send("class",cls.name(),0);client.setScreen(this);},Text.literal("Permanent klassevalg"),Text.literal("Vælg "+name+"? Du kan ikke skifte klasse bagefter.")))).dimensions(8+i*buttonWidth,28,buttonWidth-4,18).build();classButtons.add(addDrawableChild(b));
  }
  unlock=addDrawableChild(ButtonWidget.builder(Text.translatable("screen.starswordmaster.learn_perk"),b->StarSwordmasterClient.send("unlock",selected,0)).dimensions(panelX+8,actionY,142,18).build());
  for(int i=0;i<5;i++){final int slot=i;assignButtons.add(addDrawableChild(ButtonWidget.builder(Text.literal(""+(i+1)),b->StarSwordmasterClient.send("assign",selected,slot)).dimensions(panelX+8+i*28,actionY+35,25,18).build()));}
  var styles=PlayerProgress.SwordStyle.values();String[] styleNames={"Hurtig stil","Tung stil","Defensiv stil"};int styleWidth=Math.max(65,(width-76)/3);
  for(int i=0;i<3;i++){var style=styles[i];styleButtons.add(button(styleNames[i],8+i*styleWidth,height-22,styleWidth-4,()->StarSwordmasterClient.send("style",style.name(),0)));}
  button("Luk",width-62,height-22,54,this::close);
  updateButtons();
 }
 private void updateButtons(){
  var p=StarSwordmasterClient.data;var a=Ability.ALL.get(selected);
  String[] styles={"Hurtig stil","Tung stil","Defensiv stil"};for(int i=0;i<styleButtons.size();i++)styleButtons.get(i).setMessage(Text.literal((p.style()==PlayerProgress.SwordStyle.values()[i]?"[ ":"")+styles[i]+(p.style()==PlayerProgress.SwordStyle.values()[i]?" ]":"")));
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
  c.fill(0,0,width,50,0xDD07101E);c.fill(0,height-49,width,height,0xEE07101E);c.fill(panelX,50,width,height-49,0xED081120);
  var p=StarSwordmasterClient.data;
  c.drawTextWithShadow(textRenderer,"STAR SWORDMASTER · Lv "+p.level()+" · Rang "+p.rank(),8,7,0xFFE8CB88);
  c.drawTextWithShadow(textRenderer,"Perkpoint: "+p.points()+"  "+p.playerClass(),8,17,0xFFABBED0);
  c.enableScissor(0,51,panelX,height-50);
  for(Node n:nodes){Ability a=Ability.ALL.get(n.id());Node parent=find(a.parent());int px=parent==null?(int)view.screenX(0,panelX/2.0):nx(parent),py=parent==null?(int)view.screenY(0,height/2.0):ny(parent);line(c,px,py,nx(n),ny(n),connectionColor(a,p.learned().contains(n.id())));}
  int size=dk.starswordmaster.ui.SkillTreeLayout.nodeSize(view.zoom());
  for(Node n:nodes){int x=nx(n)-size/2,y=ny(n)-size/2;if(n.id().equals(selected))c.fill(x-3,y-3,x+size+3,y+size+3,0xFFE8CE8B);Ability ability=Ability.ALL.get(n.id());boolean known=p.learned().contains(n.id());boolean classLocked=!ability.classAllowed(p.playerClass());boolean available=p.canUnlock(n.id());
   int frame=known?0xFFFFCF68:classLocked?0xFF874254:available?0xFF6CDCCD:0xFF49586E;int edge=dk.starswordmaster.ui.SkillTreeLayout.frameWidth(view.zoom());c.fill(x-edge,y-edge,x+size+edge,y+size+edge,frame);SkillHud.icon(c,n.id(),x,y,size,known||available);}
  c.disableScissor();
  Ability a=Ability.ALL.get(selected);
  c.drawTextWithShadow(textRenderer,SkillHud.name(selected),panelX+8,57,SkillHud.color(a));
  if(actionY>110){
   c.drawTextWithShadow(textRenderer,textRenderer.trimToWidth(Text.translatable("description.starswordmaster."+selected).getString(),142),panelX+8,70,0xFFBBCDDD);
   c.drawTextWithShadow(textRenderer,Text.translatable("screen.starswordmaster.skill_requirement",skillName(a),a.requirement(),p.skillLevel(a.skill())),panelX+8,85,0xFF99ADC3);
   c.drawTextWithShadow(textRenderer,"Mana "+String.format(java.util.Locale.ROOT,"%.1f",p.manaCost(a))+" · Stamina "+String.format(java.util.Locale.ROOT,"%.1f",p.staminaCost(a)),panelX+8,98,0xFF77BFEA);
   c.drawTextWithShadow(textRenderer,a.active()?Text.literal("Cooldown: "+p.cooldownTicks(a)/20.0+" sek."):Text.translatable("screen.starswordmaster.passive_level",a.characterRequirement()),panelX+8,111,0xFFE4C990);
   if(a.exclusiveClass()!=null)c.drawTextWithShadow(textRenderer,a.exclusiveClass().name(),panelX+8,123,0xFFC8A5EF);
  }
  c.drawTextWithShadow(textRenderer,"Tildel til evneplads:",panelX+8,actionY+24,0xFFBBCDDD);
  c.drawTextWithShadow(textRenderer,"Træk: flyt · Scroll: zoom",8,height-59,0xFFA3B3C8);
  super.render(c,mouseX,mouseY,delta);
  if(mouseX<panelX&&mouseY>50&&mouseY<height-50){var node=dk.starswordmaster.ui.SkillTreeLayout.hitNode(view,mouseX,mouseY,panelX/2.0,height/2.0);if(node!=null)c.drawTooltip(textRenderer,details(Ability.ALL.get(node.id())),mouseX,mouseY);}
 }
 private static Text skillName(Ability a){return Text.translatable("skill.starswordmaster."+a.skill().name().toLowerCase(Locale.ROOT));}
 private static int connectionColor(Ability a,boolean learned){return switch(a.skill()){case LIGHTNING,WATER,EARTH,DARK->learned?SkillHud.color(a):0xFF000000|((SkillHud.color(a)&0xFEFEFE)>>>1);default->learned?0xFFB29150:0xFF3C4D61;};}
 private List<Text> details(Ability a){
  var p=StarSwordmasterClient.data;var lines=new ArrayList<Text>();lines.add(SkillHud.name(a.id()));lines.add(Text.translatable("description.starswordmaster."+a.id()));lines.add(Text.translatable("screen.starswordmaster.skill_requirement",skillName(a),a.requirement(),p.skillLevel(a.skill())));
  if(!a.parent().isEmpty())lines.add(Text.literal("Tidligere evne: "+SkillHud.name(a.parent()).getString()));
  if(a.characterRequirement()>1)lines.add(Text.translatable("screen.starswordmaster.character_requirement",a.characterRequirement(),p.level().toString()));
  if(a.exclusiveClass()!=null)lines.add(Text.literal("Kræver klasse: "+a.exclusiveClass()));
  lines.add(Text.literal("Mana: "+String.format(java.util.Locale.ROOT,"%.1f",p.manaCost(a))+" · Stamina: "+String.format(java.util.Locale.ROOT,"%.1f",p.staminaCost(a))+" · Cooldown: "+p.cooldownTicks(a)/20.0+" s"));lines.add(Text.literal(p.learned().contains(a.id())?"Lært": "Pris: 1 perkpoint"));return lines;
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
