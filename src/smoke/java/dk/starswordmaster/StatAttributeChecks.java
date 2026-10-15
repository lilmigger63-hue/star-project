package dk.starswordmaster;
import net.minecraft.entity.attribute.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
/** Attribute registries need Fabric's transformed runtime; execute these checks after game bootstrap. */
public final class StatAttributeChecks {
 private static void check(boolean value,String message){if(!value)throw new AssertionError("STAT_ATTRIBUTES FAIL: "+message);}
 private static void equal(double expected,double actual){check(Math.abs(expected-actual)<1e-9,expected+" != "+actual);}
 private static EntityAttributeInstance attribute(double base,java.util.function.Consumer<EntityAttributeInstance> changed){return new EntityAttributeInstance(RegistryEntry.of(new ClampedEntityAttribute("test",base,0,1024)),changed);}
 public static void run(){
  int[] changes={0};var a=attribute(1,value->changes[0]++);StatAttributes.update(a,StatAttributes.MINING_ID,0.5,EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);int first=changes[0];for(int i=0;i<100;i++)StatAttributes.update(a,StatAttributes.MINING_ID,0.5,EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);equal(1.5,a.getValue());check(a.getModifiers().size()==1&&changes[0]==first&&a.getPersistentModifiers().isEmpty(),"idempotent temporary modifier");System.out.println("STAT_ATTRIBUTES PASS: repeated synchronization is idempotent and temporary");
  a=attribute(0.1,value->{});var other=new EntityAttributeModifier(Identifier.of("other","speed"),0.5,EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);a.addPersistentModifier(other);StatAttributes.update(a,StatAttributes.SPEED_ID,0.2,EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);equal(0.18,a.getValue());equal(0.1,a.getBaseValue());check(other.equals(a.getModifier(other.id()))&&a.getPersistentModifiers().size()==1,"other speed modifier retained");System.out.println("STAT_ATTRIBUTES PASS: movement combines with other modifiers");
  a=attribute(20,value->{});other=new EntityAttributeModifier(Identifier.of("other","health"),4,EntityAttributeModifier.Operation.ADD_VALUE);a.addPersistentModifier(other);StatAttributes.update(a,StatAttributes.HEALTH_ID,1,EntityAttributeModifier.Operation.ADD_VALUE);equal(25,a.getValue());StatAttributes.update(a,StatAttributes.HEALTH_ID,10,EntityAttributeModifier.Operation.ADD_VALUE);equal(34,a.getValue());check(a.getModifiers().size()==2&&a.getPersistentModifiers().size()==2,"health is persistent and replaced instead of duplicated");StatAttributes.update(a,StatAttributes.HEALTH_ID,0,EntityAttributeModifier.Operation.ADD_VALUE);equal(24,a.getValue());check(a.getModifiers().size()==1&&other.equals(a.getModifier(other.id())),"removed only owned modifier");System.out.println("STAT_ATTRIBUTES PASS: changed and zero stats affect only owned modifier");
  StatAttributes.update(null,Identifier.of("starswordmaster","test"),1,EntityAttributeModifier.Operation.ADD_VALUE);System.out.println("STAT_ATTRIBUTES PASS: unavailable attribute is safe");
 }
}
