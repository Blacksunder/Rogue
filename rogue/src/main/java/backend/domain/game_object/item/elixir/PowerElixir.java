package backend.domain.game_object.item.elixir;

import backend.domain.map.Constants;
import backend.domain.map.MapKeeper;
import backend.domain.util.CreatureStats;
import backend.domain.util.ObjectPosition;

public class PowerElixir extends Elixir {
    public PowerElixir(ObjectPosition position, MapKeeper map, int current_level) {
        super(position, map, current_level);
        setStats(new CreatureStats(0, 0, Constants.random(1+getQuantity(), 6+getQuantity()), 0));
        setName("Power Elixir");
    }
}
