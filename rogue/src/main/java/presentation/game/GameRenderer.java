package presentation.game;

import backend.Controller;
import backend.domain.game_object.creature.Creature;
import backend.domain.game_object.creature.player.Player;
import backend.domain.map.Constants;
import backend.domain.map.FieldTypes;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import presentation.common.PresentationConstants;

import java.util.ArrayList;

public class GameRenderer {
  private final TerminalPosition pos;
  private final TextGraphics painter;
  private final Controller controller;

  public GameRenderer(Screen screen, Controller controller) {
    TerminalSize size = screen.getTerminalSize();

    this.pos = new TerminalPosition((size.getColumns() - PresentationConstants.SCREEN_WIDTH) / 2,
            (size.getRows() - PresentationConstants.SCREEN_HEIGHT) / 2);
    this.painter = screen.newTextGraphics();
    this.controller = controller;
  }

  public void print() {
    printLogs(controller.getMessages());
    printMap();
    printInfo();
  }

  private void printLogs(ArrayList<String> messages) {
    painter.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);

    int maxLines = PresentationConstants.MSG_FIELD_HEIGHT;
    String emptyStr = " ".repeat(PresentationConstants.SCREEN_WIDTH - 2);

    int last = messages.size() - 1;
    for (int i = 0; i < maxLines; ++i) {
      String msg = emptyStr;

      if (i <= last) {
        painter.putString(pos.withRelative(1, i), emptyStr);
        msg = messages.get(last - i);
      }

      painter.putString(pos.withRelative(1, i), msg);
    }
  }

  private void printMap() {
    var field = controller.getFieldToPrint();
    printDungeon(field);
    printPlayer();
  }

  private void printDungeon(FieldTypes[][] field) {
    for (int y = 0; y < Constants.HEIGHT; ++y) {
      for (int x = 0; x < Constants.WIDTH; ++x) {
        FieldTypes cellType = field[y][x];

        painter.setForegroundColor(getColorForCell(cellType));
        painter.setCharacter(
                pos.withRelative(x, y + PresentationConstants.MAP_FIELD_START_POSITION_Y),
                getCharacterForCell(cellType));
      }
    }
  }

  private TextColor getColorForCell(FieldTypes cellType) {
    return switch (cellType) {
      case HORIZONTAL_WALL, VERTICAL_WALL, LEFT_UP_CORNER, RIGHT_UP_CORNER, LEFT_DOWN_CORNER,
           RIGHT_DOWN_CORNER, DOOR, OGRE    -> TextColor.ANSI.YELLOW;

      case ROOM_FLOOR, ZOMBIE               -> TextColor.ANSI.GREEN;
      case WEAPON                           -> TextColor.ANSI.MAGENTA;
      case ELIXIR                           -> TextColor.ANSI.CYAN;
      case FOOD                             -> TextColor.ANSI.RED_BRIGHT;
      case SCROLL                           -> TextColor.ANSI.CYAN_BRIGHT;
      case VAMPIRE                          -> TextColor.ANSI.RED;
      case GHOST, MIMIC                     -> TextColor.ANSI.WHITE_BRIGHT;
      case SNAKE, STAIRS                    -> TextColor.ANSI.GREEN_BRIGHT;
      default                               -> TextColor.ANSI.WHITE;
    };
  }

  private char getCharacterForCell(FieldTypes cellType) {
    return switch (cellType) {
      case HORIZONTAL_WALL      -> '═';
      case VERTICAL_WALL        -> '║';
      case LEFT_UP_CORNER       -> '╔';
      case RIGHT_UP_CORNER      -> '╗';
      case LEFT_DOWN_CORNER     -> '╚';
      case RIGHT_DOWN_CORNER    -> '╝';
      case DOOR                 -> 'I';
      case ROAD                 -> '░';
      case ROOM_FLOOR           -> '.';
      case WEAPON               -> 'w';
      case ELIXIR               -> 'e';
      case FOOD                 -> 'f';
      case SCROLL               -> '&';
      case ZOMBIE               -> 'z';
      case VAMPIRE              -> 'v';
      case GHOST                -> 'g';
      case OGRE                 -> 'O';
      case SNAKE                -> 'S';
      case MIMIC                -> 'm';
      case STAIRS               -> 'E';
      default                   -> ' ';
    };
  }

  private void printPlayer() {
    var player = controller.getPlayer();
    painter.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
    painter.setCharacter(pos.withRelative(
                    player.getPosition().getX(),
                    player.getPosition().getY() + PresentationConstants.MAP_FIELD_START_POSITION_Y),
            '@');
  }

  private void printInfo() {
    var player = controller.getPlayer();

    painter.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);

    int columnWidth = PresentationConstants.SCREEN_WIDTH / 3;
    printPlayerStats(1,
            PresentationConstants.INFO_FIELD_START_POSITION_Y,
            player);

    printPlayerProgress(columnWidth + 1,
            PresentationConstants.INFO_FIELD_START_POSITION_Y,
            player);

    painter.putString(pos.withRelative(2 * columnWidth + 1,
            PresentationConstants.INFO_FIELD_START_POSITION_Y + 1),
            String.format("Dungeon level - %d", controller.getDungeonLevel()));
  }

  private void printPlayerStats(int x, int y, Creature player) {
    painter.putString(
            pos.withRelative(x, y++),
            String.format("Health:  %d/%d", player.getStats().getHP(), player.getStats().getMaxHealth()));

    painter.putString(
            pos.withRelative(x, y++),
            String.format(
                    "Agility: %d (%+d)", player.getBaseStats().getAgility(), player.getBonusStats().getAgility()));

    painter.putString(
            pos.withRelative(x, y),
            String.format("Power:   %d (%+d)", player.getBaseStats().getPower(), player.getBonusStats().getPower()));
  }

  private void printPlayerProgress(int x, int y, Player player) {
    painter.putString(
            pos.withRelative(x, y++),
            String.format("EXP: %d", player.getXp()));

    painter.putString(
            pos.withRelative(x, y++),
            String.format("LVL: %d", player.getCurrentLevel()));

    painter.putString(
            pos.withRelative(x, y),
            String.format("Treasures: %d", player.getTreasures()));
  }

}
