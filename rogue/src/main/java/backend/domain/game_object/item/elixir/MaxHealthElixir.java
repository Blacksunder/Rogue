package backend.domain.game_object.item.elixir;

import backend.domain.map.Constants;
import backend.domain.map.MapKeeper;
import backend.domain.util.CreatureStats;
import backend.domain.util.ObjectPosition;

public class MaxHealthElixir extends Elixir{
    public MaxHealthElixir(ObjectPosition position, MapKeeper map, int current_level) {
        super(position, map, current_level);
        setStats(new CreatureStats(Constants.random(2+getQuantity(), 10+getQuantity()), 0, 0, 0));
        setName("Max Health Elixir");
    }
}
