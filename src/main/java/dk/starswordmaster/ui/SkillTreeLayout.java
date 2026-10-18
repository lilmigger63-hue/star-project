package dk.starswordmaster.ui;

import dk.starswordmaster.progression.Ability;
import dk.starswordmaster.progression.Skill;
import java.util.*;

/** Immutable world-space geometry. Decorative branches never imply an unlock requirement. */
public final class SkillTreeLayout {
 public record Node(String id,double x,double y) {}
 public record Point(double x,double y) {}
 public record Bounds(double left,double top,double right,double bottom) {
  public double width(){return right-left;}
  public double height(){return bottom-top;}
 }
 public enum Sector {
  SWORD(225,0xffc6a66b), FIRE(270,0xffc77951), FROST(315,0xff9bc9d9),
  LIGHTNING(0,0xffc6b96e), WATER(45,0xff559eae), EARTH(90,0xff8eaa78),
  DARK(135,0xff9b81b2), SUPPORT(180,0xff8ebbae);
  private final double angle;
  private final int color;
  Sector(double angle,int color){this.angle=angle;this.color=color;}
  /** Clockwise degrees in screen coordinates, with zero pointing right. */
  public double angle(){return angle;}
  public int color(){return color;}
  public String translationKey(){return "gui.starswordmaster.branch."+name().toLowerCase(Locale.ROOT);}
 }
 public record Edge(Node parent,Node child,List<Point> points,boolean crossSector) {
  public Edge {points=List.copyOf(points);}
 }
 public record Branch(Sector sector,List<Point> points,boolean spine) {
  public Branch {points=List.copyOf(points);}
 }
 public static final List<Node> NODES;
 public static final List<Edge> EDGES;
 public static final List<Branch> BRANCHES;
 public static final Bounds BOUNDS;
 private static final Map<String,Node> BY_ID;
 private static final Map<String,Integer> DEPTH;
 private static final Map<Sector,Bounds> SECTOR_BOUNDS;
 public static final double SPACING=1.7;
 private static final double NODE_SEPARATION=64;
 static {
  Map<String,Ability> catalogue=Ability.ALL;
  Map<String,Integer> depths=new HashMap<>();
  for(var ability:catalogue.values())depth(ability.id(),catalogue,depths,new HashSet<>());
  DEPTH=Map.copyOf(depths);
  Comparator<Ability> order=Comparator.comparingInt(Ability::requirement).thenComparing(Ability::parent).thenComparing(Ability::id);
  Map<String,List<Ability>> children=new HashMap<>();
  for(var ability:catalogue.values())if(!ability.parent().isEmpty()&&sector(ability.id())==sector(ability.parent()))
   children.computeIfAbsent(ability.parent(),ignored->new ArrayList<>()).add(ability);
  children.values().forEach(list->list.sort(order));
  Map<String,Double> angles=new HashMap<>();
  for(var sector:Sector.values()){
   var roots=catalogue.values().stream().filter(a->sector(a.id())==sector)
    .filter(a->a.parent().isEmpty()||sector(a.parent())!=sector).sorted(order).toList();
   Map<String,Double> lanes=new HashMap<>();
   int leaves=0;
   for(var root:roots)leaves=assignLanes(root,children,lanes,leaves);
   for(var entry:lanes.entrySet()){
    double offset=leaves<=1?0:(entry.getValue()/(leaves-1)-0.5)*38;
    angles.put(entry.getKey(),Math.toRadians(sector.angle()+offset));
   }
  }
  var sorted=catalogue.values().stream().sorted(Comparator.comparingInt((Ability a)->depths.get(a.id()))
   .thenComparingInt(a->sector(a.id()).ordinal()).thenComparing(order)).toList();
  Map<String,Node> nodes=new LinkedHashMap<>();
  for(var ability:sorted){
   double radius=180+depths.get(ability.id())*150+ability.requirement()*4+Math.max(0,ability.characterRequirement()-1)*2;
   Node parent=nodes.get(ability.parent());
   if(parent!=null)radius=Math.max(radius,Math.hypot(parent.x(),parent.y())+(ability.characterRequirement()==25?230:145));
   double angle=angles.get(ability.id());
   Node node=polarNode(ability.id(),radius,angle);
   while(overlaps(node,nodes.values())){radius+=NODE_SEPARATION;node=polarNode(ability.id(),radius,angle);}
   nodes.put(node.id(),node);
  }
  // Preserve lane ordering/topology, then expand the completed geometry uniformly.
  nodes.replaceAll((id,n)->new Node(id,n.x()*SPACING,n.y()*SPACING));
  BY_ID=Map.copyOf(nodes);
  NODES=List.copyOf(nodes.values());
  List<Edge> edges=new ArrayList<>();
  for(var child:NODES){
   var parent=nodes.get(catalogue.get(child.id()).parent());
   if(parent!=null)edges.add(route(parent,child));
  }
  EDGES=List.copyOf(edges);
  List<Branch> branches=new ArrayList<>();
  Map<Sector,Bounds> sectorBounds=new EnumMap<>(Sector.class);
  for(var sector:Sector.values()){
   var group=NODES.stream().filter(n->sector(n)==sector).toList();
   double end=group.stream().mapToDouble(n->Math.hypot(n.x(),n.y())).max().orElse(300*SPACING)+65*SPACING;
   double angle=Math.toRadians(sector.angle());
   branches.add(new Branch(sector,List.of(polar(75*SPACING,angle),polar(end,angle)),true));
   // Repeated paired forks give sparse sectors the drawn twig silhouette without invented abilities.
   for(double radius=220*SPACING;radius<end-60*SPACING;radius+=155*SPACING){
    for(int side:new int[]{-1,1})branches.add(new Branch(sector,
     List.of(polar(radius,angle),polar(radius+55*SPACING,angle+side*0.09),polar(radius+110*SPACING,angle+side*0.17)),false));
   }
   sectorBounds.put(sector,bounds(group.stream().map(n->new Point(n.x(),n.y())).toList(),48));
  }
  BRANCHES=List.copyOf(branches);
  SECTOR_BOUNDS=Map.copyOf(sectorBounds);
  List<Point> extent=new ArrayList<>();
  extent.add(new Point(-90,-90));extent.add(new Point(90,90));
  for(var node:NODES)extent.add(new Point(node.x(),node.y()));
  for(var branch:BRANCHES)extent.addAll(branch.points());
  BOUNDS=bounds(extent,48);
 }
 private SkillTreeLayout() {}
 public static Sector sector(Node node){return sector(node.id());}
 public static Sector sector(String id){
  Ability ability=Ability.ALL.get(id);
  if(ability==null)throw new IllegalArgumentException("Unknown ability: "+id);
  return ability.skill()==Skill.AURA?Sector.SWORD:Sector.valueOf(ability.skill().name());
 }
 public static Node node(String id){return BY_ID.get(id);}
 public static int depth(String id){
  Integer result=DEPTH.get(id);
  if(result==null)throw new IllegalArgumentException("Unknown ability: "+id);
  return result;
 }
 public static Bounds bounds(Sector sector){return SECTOR_BOUNDS.get(sector);}
 private static int depth(String id,Map<String,Ability> catalogue,Map<String,Integer> depths,Set<String> visiting){
  if(depths.containsKey(id))return depths.get(id);
  Ability ability=catalogue.get(id);
  if(ability==null)throw new IllegalStateException("Missing prerequisite: "+id);
  if(!visiting.add(id))throw new IllegalStateException("Cyclic prerequisite: "+id);
  int result=ability.parent().isEmpty()?0:depth(ability.parent(),catalogue,depths,visiting)+1;
  visiting.remove(id);depths.put(id,result);return result;
 }
 private static int assignLanes(Ability root,Map<String,List<Ability>> children,Map<String,Double> lanes,int next){
  var descendants=children.getOrDefault(root.id(),List.of());
  if(descendants.isEmpty()){lanes.put(root.id(),(double)next);return next+1;}
  int start=next;
  for(var child:descendants)next=assignLanes(child,children,lanes,next);
  lanes.put(root.id(),(start+next-1)/2.0);return next;
 }
 private static Node polarNode(String id,double radius,double angle){var p=polar(radius,angle);return new Node(id,p.x(),p.y());}
 private static Point polar(double radius,double angle){return new Point(Math.cos(angle)*radius,Math.sin(angle)*radius);}
 private static boolean overlaps(Node node,Collection<Node> others){
  for(var other:others)if(Math.hypot(node.x()-other.x(),node.y()-other.y())<NODE_SEPARATION)return true;
  return false;
 }
 private static Edge route(Node parent,Node child){
  boolean cross=sector(parent)!=sector(child);
  List<Point> points=new ArrayList<>();points.add(new Point(parent.x(),parent.y()));
  double parentRadius=Math.hypot(parent.x(),parent.y()),childRadius=Math.hypot(child.x(),child.y());
  double from=Math.atan2(parent.y(),parent.x()),to=Math.atan2(child.y(),child.x());
  double turn=Math.IEEEremainder(to-from,Math.PI*2);
  double radius=cross?Math.max(125*SPACING,parentRadius-45*SPACING):(parentRadius+childRadius)/2;
  points.add(polar(radius,from));
  int segments=cross?Math.max(2,(int)Math.ceil(Math.abs(turn)/0.15)):1;
  for(int i=1;i<=segments;i++)points.add(polar(radius,from+turn*i/segments));
  points.add(new Point(child.x(),child.y()));
  return new Edge(parent,child,points,cross);
 }
 private static Bounds bounds(List<Point> points,double padding){
  return new Bounds(points.stream().mapToDouble(Point::x).min().orElse(0)-padding,
   points.stream().mapToDouble(Point::y).min().orElse(0)-padding,
   points.stream().mapToDouble(Point::x).max().orElse(0)+padding,
   points.stream().mapToDouble(Point::y).max().orElse(0)+padding);
 }
 public static Node hitNode(TreeViewport view,double x,double y,double centerX,double centerY){
  int radius=nodeSize(view.zoom())/2+frameWidth(view.zoom());Node closest=null;double best=Double.POSITIVE_INFINITY;
  for(var node:NODES){
   double dx=x-(int)view.screenX(node.x(),centerX),dy=y-(int)view.screenY(node.y(),centerY),distance=dx*dx+dy*dy;
   if(Math.abs(dx)<=radius&&Math.abs(dy)<=radius&&distance<best){closest=node;best=distance;}
  }
  return closest;
 }
 public static int nodeSize(double zoom){return Math.max(5,(int)(26*zoom));}
 public static int frameWidth(double zoom){return Math.max(1,(int)Math.round(2*zoom));}
}
