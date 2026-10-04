package org.betterx.wover.potions.api;

import org.betterx.wover.events.api.Subscriber;

public interface OnBootstrapPotions extends Subscriber {
    void bootstrap(BrewingBuilder builder);
}
