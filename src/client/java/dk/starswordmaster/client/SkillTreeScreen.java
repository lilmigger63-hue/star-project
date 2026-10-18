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
import net.minecraft.util.Language;
import java.math.*;
import java.util.*;
/** Eight constellation arms share their layout with hit testing and the camera. */
public final class SkillTreeScreen extends Screen {
 private static final Identifier BACKGROUND=Identifier.of("starswordmaster","textures/gui/constellation.png");
 private static final Map<String,Identifier> ICONS=new HashMap<>();
 static {for(var n:SkillTreeLayout.NODES)ICONS.put(n.id(),Identifier.of("starswordmaster","textures/skills/"+n.id()+".png"));}
 private Text hubEvolution=Text.empty();
 private List<Text> evolutionTooltip=List.of();
 private int hubEvolutionWidth;
 private final TreeViewport view=new TreeViewport();
 private final TreeRenderProjection projection=new TreeRenderProjection();
 private final TreeProgressCache progression=new TreeProgressCache();
 private final TreeLineRenderer treeLines=new TreeLineRenderer();
 private final Map<String,Integer> edgeColors=new HashMap<>();
 private final Map<String,List<Text>> tooltipCache=new HashMap<>();
 private final EnumMap<Sector,Text> branchNames=new EnumMap<>(Sector.class);
 private final EnumMap<Sector,Integer> branchWidths=new EnumMap<>(Sector.class);
 private final List<ButtonWidget> classButtons=new ArrayList<>(),assignButtons=new ArrayList<>();
 private final List<ButtonWidget> detailButtons=new ArrayList<>();
 private TreeScreenLayout layout;
 private String selected="spark",cachedSelection;
 private ButtonWidget unlock,reset,styleButton,detailsButton,focusButton;
 private boolean detailsPage,buttonsDirty=true;
 private int focusIndex=-1,cachedFocus=-2,cachedWidth=-1,hubLevelWidth,hubPointsWidth;
 private boolean cachedDetailsPage;
 private PlayerProgress cachedSnapshot;
 private Language cachedLanguage;
 private BigInteger cachedXp;
 private String overview="",xpSummary="";
 private List<Text> overviewTooltip=List.of();
 private List<String> detailLines=List.of();
 private Text hubLevel=Text.empty(),hubPoints=Text.empty(),assignLabel=Text.empty();
 private double xpProgress;
 public SkillTreeScreen(){super(text("title"));}
 private static Text text(String key,Object... args){return Text.translatable("screen.starswordmaster."+key,args);}
 private static Text className(PlayerProgress.PlayerClass cls){return Text.translatable("status.starswordmaster.class."+cls.name().toLowerCase(Locale.ROOT));}
 private static Text styleName(PlayerProgress.SwordStyle style){return Text.translatable("status.starswordmaster.style."+style.name().toLowerCase(Locale.ROOT));}
 private static String compact(BigInteger n){return ProgressNumbers.compact(n);}
 private static String number(double n){return String.format(Locale.ROOT,"%.1f",n);}
 private ButtonWidget button(Text label,int x,int y,int w,Runnable action){return addDrawableChild(FantasyUi.button(label,x,y,w,18,b->action.run()));}
 private void fit(){view.fit(layout.treeWidth(),layout.bodyBottom()-layout.bodyTop());focusIndex=-1;buttonsDirty=true;}
 @Override protected void init(){
  // SplashOverlay reinitializes the current screen after every resource/font reload.
  cachedLanguage=null;cachedWidth=-1;
  layout=TreeScreenLayout.of(width,height);fit();classButtons.clear();assignButtons.clear();detailButtons.clear();buttonsDirty=true;
  var classes=new PlayerProgress.PlayerClass[]{PlayerProgress.PlayerClass.SWORDSMAN,PlayerProgress.PlayerClass.MAGE,PlayerProgress.PlayerClass.SPELLBLADE};
  int classWidth=(width-16)/3;
  for(int i=0;i<classes.length;i++){var cls=classes[i];var name=className(cls);
   classButtons.add(button(name,8+i*classWidth,32,classWidth-4,()->client.setScreen(new ConfirmScreen(yes->{if(yes)StarSwordmasterClient.send("class",cls.name(),0);client.setScreen(this);},text("confirm_class"),text("class_warning",name),text("confirm"),text("cancel")))));
  }
  for(var b:classButtons)b.setTooltip(net.minecraft.client.gui.tooltip.Tooltip.of(text("class_requirement",PlayerProgress.CLASS_SELECTION_LEVEL)));
  int dx=layout.detailX()+8,dw=layout.detailWidth()-16,actionY=layout.bodyBottom()-53;
  unlock=button(text("learn_perk"),dx,actionY,dw,()->StarSwordmasterClient.send("unlock",selected,0));detailButtons.add(unlock);
  int slotWidth=dw/5;
  for(int i=0;i<5;i++){int slot=i;var b=button(Text.literal(""+(i+1)),dx+i*slotWidth,actionY+32,slotWidth-3,()->StarSwordmasterClient.send("assign",selected,slot));assignButtons.add(b);detailButtons.add(b);}
  int third=(width-16)/3;
  button(text("fit"),8,height-45,third-4,this::fit);
  focusButton=button(text("all_branches"),8+third,height-45,third-4,()->{focusIndex=(focusIndex+1)%(Sector.values().length+1);if(focusIndex==8){fit();}else view.focus(Sector.values()[focusIndex],layout.treeWidth(),layout.bodyBottom()-layout.bodyTop());buttonsDirty=true;});
  detailsButton=button(text("details"),8+third*2,height-45,third-4,()->{detailsPage=!detailsPage;updateButtons();});
  styleButton=button(text("style"),8,height-23,third-4,()->{var values=PlayerProgress.SwordStyle.values();StarSwordmasterClient.send("style",values[(StarSwordmasterClient.data.style().ordinal()+1)%values.length].name(),0);});
  reset=button(text("reset_skills"),8+third,height-23,third-4,this::confirmReset);
  button(text("close"),8+third*2,height-23,third-4,this::close);updateButtons();
 }
 private void confirmReset(){client.setScreen(new ConfirmScreen(yes->{if(yes)StarSwordmasterClient.send("reset_skills","",0);client.setScreen(this);},text("reset_title"),text("reset_warning"),text("confirm_reset"),text("cancel")));}
 private boolean showingDetails(){return layout.sidePanel()||detailsPage;}
 private boolean showingTree(){return layout.sidePanel()||!detailsPage;}
 private void updateButtons(){buttonsDirty=true;refresh(true);}
 /** Snapshots may change resources each tick without changing a single tree label or unlock state. */
 private void refresh(boolean poll){
  var p=StarSwordmasterClient.data;var language=Language.getInstance();boolean translated=language!=cachedLanguage,resized=cachedWidth!=width;
  boolean changed=false,xpChanged=false;
  if(poll||p!=cachedSnapshot||cachedSnapshot==null){
   changed=progression.update(p);xpChanged=!p.totalXp().equals(cachedXp);cachedSnapshot=p;
  }
  boolean selectionChanged=!selected.equals(cachedSelection);
  if(translated){
   branchNames.clear();branchWidths.clear();
   for(var sector:Sector.values()){var name=Text.translatable(sector.translationKey());branchNames.put(sector,name);branchWidths.put(sector,textRenderer.getWidth(name));}
   assignLabel=text("assign_slot");cachedLanguage=language;
  }
  if(changed||translated)tooltipCache.clear();
  if(changed||translated||resized||xpChanged){
   var level=p.level();var points=p.points();var statPoints=p.statPoints();var xp=p.xpInLevel();var next=p.xpForNextLevel();
   overview=textRenderer.trimToWidth(text("overview",compact(level),compact(points),compact(statPoints)).getString(),width-16);
   xpSummary=textRenderer.trimToWidth(text("xp_summary",compact(xp),compact(next)).getString(),width-16);
   overviewTooltip=List.of(text("overview",level.toString(),points.toString(),statPoints.toString()),text("xp_summary",xp.toString(),next.toString()));
   xpProgress=new BigDecimal(xp).divide(new BigDecimal(next),MathContext.DECIMAL64).doubleValue();cachedXp=p.totalXp();
   hubLevel=text("hub_level",compact(level));hubPoints=text("hub_points",compact(points));
   hubEvolution=p.evolution().stage()>0?EvolutionText.title(p.evolution()):className(p.playerClass());hubEvolutionWidth=textRenderer.getWidth(hubEvolution);
   var evolutionLines=new ArrayList<Text>();evolutionLines.add(Text.translatable("status.starswordmaster.evolution"));evolutionLines.add(hubEvolution);evolutionLines.addAll(EvolutionText.effects(p.evolution()));evolutionLines.add(Text.translatable("status.starswordmaster.evolution.separate"));evolutionTooltip=List.copyOf(evolutionLines);
   hubLevelWidth=textRenderer.getWidth(hubLevel);hubPointsWidth=textRenderer.getWidth(hubPoints);
  }
  if(changed||translated||selectionChanged){
   Set<String> path=new HashSet<>();String id=selected;
   while(!id.isEmpty()&&path.add(id)){var ability=Ability.ALL.get(id);if(ability==null)break;id=ability.parent();}
   for(var edge:SkillTreeLayout.EDGES){var state=progression.node(edge.child().id());
    edgeColors.put(edge.child().id(),path.contains(edge.child().id())?DarkFantasyPalette.GOLD:state.known()?SkillTreeLayout.sector(edge.child()).color():edge.crossSector()?0xFF685579:0xFF55524B);
   }
  }
  if(changed||translated||resized||selectionChanged){
   var lines=details(Ability.ALL.get(selected));var trimmed=new ArrayList<String>(lines.size());
   for(var line:lines)trimmed.add(textRenderer.trimToWidth(line.getString(),layout.detailWidth()-16));detailLines=List.copyOf(trimmed);
  }
  if(buttonsDirty||changed||translated||selectionChanged||cachedDetailsPage!=detailsPage||cachedFocus!=focusIndex){
   var a=Ability.ALL.get(selected);var state=progression.node(selected);
   for(var b:classButtons)b.active=p.level().compareTo(BigInteger.valueOf(PlayerProgress.CLASS_SELECTION_LEVEL))>=0&&p.playerClass()==PlayerProgress.PlayerClass.UNINITIATED;
   for(var b:detailButtons)b.visible=showingDetails();
   unlock.active=state.available();for(var b:assignButtons)b.active=a!=null&&a.active()&&state.known();
   styleButton.setMessage(text("current_style",styleName(p.style())));reset.active=!p.learned().isEmpty();
   detailsButton.visible=!layout.sidePanel();detailsButton.setMessage(text(detailsPage?"back_tree":"details"));
   focusButton.setMessage(focusIndex>=0&&focusIndex<8?branchNames.get(Sector.values()[focusIndex]):text("all_branches"));
   cachedDetailsPage=detailsPage;cachedFocus=focusIndex;buttonsDirty=false;
  }
  cachedSelection=selected;cachedWidth=width;
 }
 @Override public void tick(){refresh(true);}
 private boolean inTree(double x,double y){return showingTree()&&layout.inTree(x,y);}
 private void circle(DrawContext c,int x,int y,int radius,int color){
  if(!projection.intersects(x-radius,y-radius,x+radius+1,y+radius+1))return;
  for(int row=Math.max(-radius,layout.bodyTop()-y);row<=Math.min(radius,layout.bodyBottom()-1-y);row++){
   int half=(int)Math.sqrt(radius*radius-row*row),left=Math.max(0,x-half),right=Math.min(layout.treeWidth(),x+half+1);
   if(right>left)c.fill(left,y+row,right,y+row+1,color);
  }
 }
 private void tree(DrawContext c){
  if(projection.update(view,layout))treeLines.project(projection.lines());
  c.enableScissor(0,layout.bodyTop(),layout.treeWidth(),layout.bodyBottom());
  try{
   treeLines.draw(c,edgeColors);
   int cx=projection.hubX(),cy=projection.hubY();
   if(projection.hubVisible()){
    circle(c,cx,cy,projection.hubRadius(),DarkFantasyPalette.BRONZE);circle(c,cx,cy,projection.hubRadius()-2,DarkFantasyPalette.SLATE);
   }
   if(projection.fullDetail()){
    if(projection.intersects(cx-hubLevelWidth/2,cy-9,cx+hubLevelWidth/2+1,cy+1))c.drawCenteredTextWithShadow(textRenderer,hubLevel,cx,cy-9,DarkFantasyPalette.GOLD);
    if(projection.intersects(cx-hubPointsWidth/2,cy+2,cx+hubPointsWidth/2+1,cy+12))c.drawCenteredTextWithShadow(textRenderer,hubPoints,cx,cy+2,DarkFantasyPalette.TEXT);
   }
   if(projection.fullDetail()&&projection.intersects(cx-hubEvolutionWidth/2.0,cy+13,cx+hubEvolutionWidth/2.0,cy+23))c.drawCenteredTextWithShadow(textRenderer,hubEvolution,cx,cy+13,DarkFantasyPalette.GOLD);
   int size=projection.nodeSize(),edge=SkillTreeLayout.frameWidth(view.zoom());
   for(var n:projection.nodes()){
    int x=n.x(),y=n.y();var state=progression.node(n.node().id());
    if(n.node().id().equals(selected))c.fill(x-3,y-3,x+size+3,y+size+3,DarkFantasyPalette.GOLD);
    int frame=state.known()?DarkFantasyPalette.LEARNED:state.classLocked()?DarkFantasyPalette.CLASS_LOCKED:state.available()?DarkFantasyPalette.AVAILABLE:DarkFantasyPalette.LOCKED;
    c.fill(x-edge,y-edge,x+size+edge,y+size+edge,frame);
    if(projection.fullDetail())icon(c,n.node().id(),x,y,size,state.known()||state.available());
    else c.fill(x,y,x+size,y+size,state.known()||state.available()?SkillTreeLayout.sector(n.node()).color():DarkFantasyPalette.CHARCOAL);
    if(state.known())c.fill(x+size-3,y+size-3,x+size,y+size,DarkFantasyPalette.LEARNED);else if(state.available())c.fill(x,y,x+2,y+2,DarkFantasyPalette.AVAILABLE);
   }
   for(var label:projection.labels()){
    int half=branchWidths.get(label.sector())/2;
    if(projection.intersects(label.x()-half,label.y(),label.x()+half+1,label.y()+10))c.drawCenteredTextWithShadow(textRenderer,branchNames.get(label.sector()),label.x(),label.y(),label.sector().color());
   }
  }finally{c.disableScissor();}
 }
 private void icon(DrawContext c,String id,int x,int y,int size,boolean learned){
  c.fill(x-1,y-1,x+size+1,y+size+1,learned?DarkFantasyPalette.LEARNED:DarkFantasyPalette.LOCKED);c.fill(x,y,x+size,y+size,DarkFantasyPalette.CHARCOAL);
  c.drawTexture(RenderPipelines.GUI_TEXTURED,ICONS.get(id),x,y,0,0,size,size,32,32,32,32);if(!learned)c.fill(x,y,x+size,y+size,0xAA121416);
 }
 private void details(DrawContext c){
  var a=Ability.ALL.get(selected);int x=layout.detailX(),top=layout.bodyTop(),bottom=layout.bodyBottom(),limit=bottom-58;
  c.fill(x,top,width,bottom,DarkFantasyPalette.SLATE);FantasyUi.corners(c,x,top,layout.detailWidth(),bottom-top);
  int y=top+5;for(int i=0;i<detailLines.size()&&y+9<=limit;i++){c.drawTextWithShadow(textRenderer,detailLines.get(i),x+8,y,i==0?SkillHud.color(a):DarkFantasyPalette.TEXT);y+=12;}
  c.drawTextWithShadow(textRenderer,assignLabel,x+8,bottom-32,DarkFantasyPalette.MUTED);
 }
 @Override public void render(DrawContext c,int mouseX,int mouseY,float delta){
  refresh(false);
  c.drawTexture(RenderPipelines.GUI_TEXTURED,BACKGROUND,0,0,0,0,width,height,1024,768,1024,768);c.fill(0,0,width,height,0xB0121416);c.fill(0,0,width,54,0xF0121416);c.fill(0,height-50,width,height,0xF0121416);
  c.drawTextWithShadow(textRenderer,overview,8,5,DarkFantasyPalette.GOLD);c.drawTextWithShadow(textRenderer,xpSummary,8,16,DarkFantasyPalette.TEXT);
  c.fill(8,27,width-8,29,DarkFantasyPalette.IRON);c.fill(8,27,8+(int)((width-16)*Math.clamp(xpProgress,0,1)),29,DarkFantasyPalette.GOLD);
  if(showingTree())tree(c);if(showingDetails())details(c);super.render(c,mouseX,mouseY,delta);
  if(mouseY<30)c.drawTooltip(textRenderer,overviewTooltip,mouseX,mouseY);
  else if(inTree(mouseX,mouseY)){
   var n=SkillTreeLayout.hitNode(view,mouseX,mouseY,layout.centerX(),layout.centerY());
   if(n!=null)c.drawTooltip(textRenderer,details(Ability.ALL.get(n.id())),mouseX,mouseY);
   else if(Math.abs(mouseX-projection.hubX())<=Math.max(projection.hubRadius(),projection.fullDetail()?hubEvolutionWidth/2:0)&&mouseY>=projection.hubY()-projection.hubRadius()&&mouseY<=projection.hubY()+Math.max(projection.fullDetail()?23:0,projection.hubRadius()))c.drawTooltip(textRenderer,evolutionTooltip,mouseX,mouseY);
  }
  else if(showingDetails()&&mouseX>=layout.detailX()&&mouseY>=layout.bodyTop()&&mouseY<layout.bodyBottom()-56)c.drawTooltip(textRenderer,details(Ability.ALL.get(selected)),mouseX,mouseY);
 }
 private List<Text> details(Ability a){
  var cached=tooltipCache.get(a.id());if(cached!=null)return cached;
  var p=StarSwordmasterClient.data;var lines=new ArrayList<Text>();lines.add(SkillHud.name(a.id()));lines.add(Text.translatable("description.starswordmaster."+a.id()));lines.add(text("skill_requirement",Text.translatable("skill.starswordmaster."+a.skill().name().toLowerCase(Locale.ROOT)),a.requirement(),p.skillLevel(a.skill())));
  if(!a.parent().isEmpty())lines.add(text("previous_ability",SkillHud.name(a.parent())));
  if(a.characterRequirement()>1)lines.add(text("character_requirement",a.characterRequirement(),compact(p.level())));
  if(a.exclusiveClass()!=null)lines.add(text("required_class",className(a.exclusiveClass())));
  if(a.active())lines.add(text("cost_details",number(p.manaCost(a)),number(p.staminaCost(a)),number(p.cooldownTicks(a)/20.0)));
  var state=progression.node(a.id());lines.add(text(state.known()?"learned":"price"));if(!state.known())lines.add(text(state.classLocked()?"state.class_locked":state.available()?"state.available":"state.locked"));
  var result=List.copyOf(lines);tooltipCache.put(a.id(),result);return result;
 }
 @Override public boolean mouseClicked(Click click,boolean doubleClick){
  if(super.mouseClicked(click,doubleClick))return true;
  if(click.button()==0&&inTree(click.x(),click.y())){var node=SkillTreeLayout.hitNode(view,click.x(),click.y(),layout.centerX(),layout.centerY());if(node!=null){selected=node.id();if(!layout.sidePanel())detailsPage=true;updateButtons();}return true;}return false;
 }
 @Override public boolean mouseDragged(Click click,double dx,double dy){if(click.button()==0&&inTree(click.x(),click.y())){view.pan(dx,dy);return true;}return super.mouseDragged(click,dx,dy);}
 @Override public boolean mouseScrolled(double x,double y,double horizontal,double vertical){if(inTree(x,y)){view.zoomAt(x,y,layout.centerX(),layout.centerY(),vertical);return true;}return super.mouseScrolled(x,y,horizontal,vertical);}
 @Override public boolean shouldPause(){return false;}
}
