package backend.domain.game_object.item.elixir;

import backend.domain.map.Constants;
import backend.domain.map.MapKeeper;
import backend.domain.util.CreatureStats;
import backend.domain.util.ObjectPosition;

public class HealthElixir extends Elixir{
    public HealthElixir(ObjectPosition position, MapKeeper map, int current_level) {
        super(position, map, current_level);
        setStats(new CreatureStats(Constants.random(1, 10), Constants.random(1+getQuantity(), 4+getQuantity()) , 0, 0));
        getStats().setMaxHealth(0);
        setName("Health Elixir");
    }
}
