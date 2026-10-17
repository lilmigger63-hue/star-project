package dk.starswordmaster.client.aura;

/** Metadata belongs to the native item render state and is erased whenever vanilla clears it. */
public interface AuraItemState {
 void starswordmaster$setAura(SwordAuraSnapshot snapshot);
 SwordAuraSnapshot starswordmaster$takeAura();
}
