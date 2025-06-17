package backend.domain.game_object.item.scroll;

import backend.domain.map.Constants;
import backend.domain.map.MapKeeper;
import backend.domain.util.CreatureStats;
import backend.domain.util.ObjectPosition;

public class PowerScroll extends Scroll{
    public PowerScroll(ObjectPosition position, MapKeeper map, int current_level) {
        super(position, map, current_level);
        setStats(new CreatureStats(0, 0, Constants.random(1+getQuantity(), 3+getQuantity()), 0));
        setName("Power Scroll");
    }
}
