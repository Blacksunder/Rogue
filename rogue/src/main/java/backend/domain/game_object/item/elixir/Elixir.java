package backend.domain.game_object.item.elixir;

import backend.domain.game_object.item.Item;
import backend.domain.map.Constants;
import backend.domain.map.MapKeeper;
import backend.domain.util.CreatureStats;
import backend.domain.util.ObjectPosition;
import backend.domain.util.*;

public class Elixir extends Item {
    private CreatureStats stats;

    public Elixir(ObjectPosition position, MapKeeper map, int current_level) {
        super(position, map, current_level, null);
        setName("Elixir");
    }

    public void setStats(CreatureStats stats) {
        this.stats = stats;
    }

    public CreatureStats getStats() {
        return stats;
    }

    @Override
    public void onUse() {
        if (getParent() != null) {
            getParent().addTempStats(new Pair<Integer, CreatureStats>(10, stats));
        }
        setParent(null);
        setDead(true);
        getLogger().addMessage("You have drunk " + getName());
    }

    @Override
    public String toString() {
        return super.toString() + " " + stats;
    }
}

