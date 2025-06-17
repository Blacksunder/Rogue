package backend.domain.game_object.item.scroll;

import backend.domain.map.Constants;
import backend.domain.map.MapKeeper;
import backend.domain.util.CreatureStats;
import backend.domain.util.ObjectPosition;

public class MaxHealthScroll extends Scroll{
    public MaxHealthScroll(ObjectPosition position, MapKeeper map, int current_level) {
        super(position, map, current_level);
        setStats(new CreatureStats(Constants.random(getQuantity(), 10+getQuantity()), 0, 0, 0));
        setName("Max Health Scroll");
    }
}
