package backend.domain.game_object.item.scroll;

import backend.domain.game_object.item.Item;
import backend.domain.map.MapKeeper;
import backend.domain.util.CreatureStats;
import backend.domain.util.ObjectPosition;

public class Scroll extends Item {
    private CreatureStats stats;

    public Scroll(ObjectPosition position, MapKeeper map, int current_level) {
        super(position, map, current_level, null);
        setName("Scroll");
    }

    public void setStats(CreatureStats stats) {
        this.stats = stats;
    }

    @Override
    public void onUse() {
        if (getParent() != null) {
            getParent().getBaseStats().plus(stats);
        }
        setParent(null);
        setDead(true);
        getLogger().addMessage("You have read " + getName());
    }

    @Override
    public String toString() {
        return super.toString() + " " + stats;
    }
}
