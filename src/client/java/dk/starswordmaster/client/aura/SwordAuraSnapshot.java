package dk.starswordmaster.client.aura;

import dk.starswordmaster.presentation.VisualState;
import java.util.UUID;
import net.minecraft.item.ItemDisplayContext;

/** Immutable extraction result; no entity or mutable matrix is captured by deferred draw commands. */
public record SwordAuraSnapshot(UUID player,int entityId,int hand,ItemDisplayContext context,Object world,VisualState visual,float age,boolean swinging){}
