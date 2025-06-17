package backend.domain.game_object.creature.monsters;

import backend.domain.map.Constants;
import backend.domain.map.FieldTypes;
import backend.domain.map.MapKeeper;
import backend.domain.util.CreatureConstants;
import backend.domain.util.ObjectPosition;
import backend.domain.util.CreatureStats;
import backend.domain.util.Modifiers;

import java.util.ArrayList;

public class Ghost extends DefaultEnemy {
    private final int roomIndex;
    private static final int ghostHostility = Modifiers.LOW;
    private static final int ghostAgility = Modifiers.HIGH;
    private static final int ghostPower = Modifiers.LOW;
    private static final int ghostMaxHealth = CreatureConstants.EnemyHealthMultiplier * Modifiers.LOW;
    private final int hideProbability = 20;

    public Ghost(ObjectPosition position, MapKeeper map, int roomIndex) {
        super(new CreatureStats(ghostMaxHealth, ghostMaxHealth, ghostPower, ghostAgility),
                position, map, ghostHostility);
        this.roomIndex = roomIndex;
        setName("Ghost");
    }

    @Override
    public void specificMove() {
        if (super.isInvisible()) {
            super.changeVisibility();
        }
        int startX = super.getMap().getRooms().get(roomIndex).getXAngle() + 1;
        int startY = super.getMap().getRooms().get(roomIndex).getYAngle() + 1;
        int height = super.getMap().getRooms().get(roomIndex).getHeight() - 2;
        int width = super.getMap().getRooms().get(roomIndex).getWidth() - 2;
        ArrayList<int[]> coordsToSpawn = new ArrayList<>();
        for (int i = startY; i < startY + height; i++) {
            for (int j = startX; j < startX + width; j++) {
                if (super.getMap().getField()[i][j] == FieldTypes.ROOM_FLOOR) {
                    coordsToSpawn.add(new int[]{i, j});
                }
            }
        }
        int[] point = coordsToSpawn.get(Constants.random(0, coordsToSpawn.size() - 1));
        specificMove(point[0], point[1]);
        if (Constants.random(0, 100) < hideProbability) {
            hide();
        }
    }

    private void hide() {
        super.changeVisibility();
    }
}
