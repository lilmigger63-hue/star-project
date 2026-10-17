package dk.starswordmaster.presentation;

import java.util.OptionalDouble;
import java.util.Optional;

/** Finite downward collision-column search. Implementations must read loaded chunks only. */
public final class EarthTerrainSampler {
 public static final int MAX_COLUMN_QUERIES=16;
 public interface Column {
  boolean loaded(int x,int z);
  int bottom();int topExclusive();
  boolean fluid(int x,int y,int z);
  /** Highest collision surface covering the exact local X/Z, in [0,1]; NaN for no collision. */
  double top(int x,int y,int z,double localX,double localZ);
  /** Free distance above a sampled surface, with a finite requested maximum. */
  double clearance(double x,double y,double z,double limit);
 }
 public record Ground(double base,double height){}
 private EarthTerrainSampler(){}
 public static OptionalDouble surface(Column column,double x,double referenceY,double z){
  if(!Double.isFinite(x)||!Double.isFinite(referenceY)||!Double.isFinite(z)||Math.abs(x)>29_999_980||Math.abs(z)>29_999_980||referenceY<column.bottom()-2||referenceY>column.topExclusive()+2)return OptionalDouble.empty();
  int bx=(int)Math.floor(x),bz=(int)Math.floor(z);
  if(!column.loaded(bx,bz))return OptionalDouble.empty();
  int start=Math.min(column.topExclusive()-1,(int)Math.floor(referenceY+1));
  for(int count=0,y=start;count<MAX_COLUMN_QUERIES&&y>=column.bottom();count++,y--){
   if(column.fluid(bx,y,bz))return OptionalDouble.empty();
   double top=column.top(bx,y,bz,x-bx,z-bz);
   if(Double.isFinite(top)&&top>0&&top<=1&&y+top<=referenceY+1.5)return OptionalDouble.of(y+top);
  }
  return OptionalDouble.empty();
 }
 /** Five once-only support columns keep the entire footprint grounded and out of low ceilings. */
 public static Optional<Ground> support(Column column,double x,double referenceY,double z,double width,double depth,double requestedHeight){
  if(!Double.isFinite(width+depth+requestedHeight)||width<=0||depth<=0||requestedHeight<1||width>3||depth>3||requestedHeight>9)return Optional.empty();
  double min=Double.POSITIVE_INFINITY,max=Double.NEGATIVE_INFINITY;
  for(int i=0;i<5;i++){
   double sx=x+(i==1?-1:i==2?1:0)*width*.7,sz=z+(i==3?-1:i==4?1:0)*depth*.7;
   var surface=surface(column,sx,referenceY,sz);if(surface.isEmpty())return Optional.empty();
   min=Math.min(min,surface.getAsDouble());max=Math.max(max,surface.getAsDouble());
  }
  if(max-min>1.25)return Optional.empty();
  double base=min-.12,height=Math.min(requestedHeight,column.topExclusive()-base-.05);
  for(int i=0;i<5;i++){
   double sx=x+(i==1?-1:i==2?1:0)*width*.7,sz=z+(i==3?-1:i==4?1:0)*depth*.7;
   double clearance=column.clearance(sx,max+.05,sz,Math.max(0,height+base-max));
   if(!Double.isFinite(clearance)||clearance<0)return Optional.empty();
   height=Math.min(height,clearance+max-base);
  }
  return height<1?Optional.empty():Optional.of(new Ground(base,height));
 }
}
