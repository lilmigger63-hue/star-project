package dk.starswordmaster.client.circles;

import dk.starswordmaster.circles.MagicCircleMesh;
import dk.starswordmaster.presentation.AbilityPresentation;
import java.util.UUID;

/** Immutable extraction state for the native deferred command; never retains a mutable entity. */
public record CircleSnapshot(UUID player,int entityId,int hand,boolean firstPerson,boolean slim,AbilityPresentation.Element element,MagicCircleMesh.Profile profile,float age){}
