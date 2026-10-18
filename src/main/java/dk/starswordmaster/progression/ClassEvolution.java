package dk.starswordmaster.progression;

import java.math.BigInteger;
import java.util.Locale;
import static dk.starswordmaster.progression.PlayerProgress.PlayerClass;

/** Fixed total bonuses, derived from class and exact character level; never saved or awarded. */
public record ClassEvolution(PlayerClass playerClass,int stage,int minimumLevel,String titleKey,
 double damageBonus,int agilityBonus,int manaBonus,int staminaBonus,
 double manaRegenerationBonus,double manaDiscount,double staminaDiscount) {
 private static final BigInteger TEN=BigInteger.TEN,TWENTY=BigInteger.valueOf(20),THIRTY=BigInteger.valueOf(30);
 private static final ClassEvolution[][] PRESETS=new ClassEvolution[PlayerClass.values().length][4];
 static {
  for(var cls:PlayerClass.values())for(int stage=0;stage<=3;stage++) {
   int s=cls==PlayerClass.UNINITIATED?0:stage;
   String name=cls.name().toLowerCase(Locale.ROOT);
   String title=s==0?"status.starswordmaster.class."+name:"status.starswordmaster.evolution."+name+"."+s;
   int minimum=cls==PlayerClass.UNINITIATED?1:s==0?PlayerProgress.CLASS_SELECTION_LEVEL:s*10;
   PRESETS[cls.ordinal()][stage]=switch(cls) {
    case SWORDSMAN->new ClassEvolution(cls,s,minimum,title,.04*s,s,0,5*s,0,0,s==0?0:.05*(s-1));
    case MAGE->new ClassEvolution(cls,s,minimum,title,.04*s,0,10*s,0,.05*s,s==0?0:.05*(s-1),0);
    case SPELLBLADE->new ClassEvolution(cls,s,minimum,title,new double[]{0,.03,.06,.10}[s],0,5*s,5*s,0,new double[]{0,.03,.06,.08}[s],new double[]{0,.03,.06,.08}[s]);
    case UNINITIATED->new ClassEvolution(cls,0,1,title,0,0,0,0,0,0,0);
   };
  }
 }
 public static ClassEvolution from(PlayerClass cls,BigInteger level) {
  if(cls==null||cls==PlayerClass.UNINITIATED||level==null||level.compareTo(BigInteger.valueOf(PlayerProgress.CLASS_SELECTION_LEVEL))<0)
   return PRESETS[PlayerClass.UNINITIATED.ordinal()][0];
  int stage=level.compareTo(THIRTY)>=0?3:level.compareTo(TWENTY)>=0?2:level.compareTo(TEN)>=0?1:0;
  return PRESETS[cls.ordinal()][stage];
 }
 /** Damage categories also keep support, healing and defensive abilities out of damage bonuses. */
 public double damageBonus(Ability ability) {
  if(ability==null)return 0;
  boolean matches=switch(playerClass) {
   case SWORDSMAN->ability.damageKind()==Ability.DamageKind.SWORD||ability.damageKind()==Ability.DamageKind.ENCHANTED_SWORD;
   case MAGE->ability.damageKind()==Ability.DamageKind.MAGIC;
   case SPELLBLADE->ability.damageKind()==Ability.DamageKind.ENCHANTED_SWORD;
   case UNINITIATED->false;
  };
  return matches?damageBonus:0;
 }
}
