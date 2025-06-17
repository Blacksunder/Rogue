package backend.domain.util;

import backend.domain.map.Constants;
import backend.domain.map.MapKeeper;

public class CreatureConstants {
    public static final int PlayerHealthMultiplier = 30;
    public static final int EnemyHealthMultiplier = 15;
    public static final int PlayerXPRate = 30;
    public static final int PlayerLevelModifier = 2;
    public static final int PlayerTreasuresModifier = 2;
    public static final int SessionLevelModifier = 5;
    public static final int EnemyMaxPossibleHealth = 300;
    public static final int MaxPossibleSumOfParams = 20;
    public static int getBonusStats(MapKeeper map) {
        return Constants.random(0, map.getSessionRecord().getMaxLevel())*CreatureConstants.SessionLevelModifier +
                Constants.random(0, map.getPlayer().getCurrentLevel())* CreatureConstants.PlayerLevelModifier +
                Constants.random(0, map.getPlayer().getTreasures() / 100) * CreatureConstants.PlayerLevelModifier +
                Constants.random(0, map.getSessionRecord().getKills() / 8) * 5 +
                Constants.random(0, map.getMonsters().size()) / 3;
    }
}
