package presentation;

import backend.Controller;
import backend.domain.game_object.item.Item;
import backend.domain.map.FieldTypes;
import backend.domain.util.Direction;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import presentation.common.PresentationConstants;
import presentation.game.GameRenderer;
import presentation.menu.GameMenu;

import java.io.IOException;
import java.util.ArrayList;

public class View {
  private Screen screen;
  private final Controller controller;

  private TextGraphics painter;
  private GameMenu menu;
  private GameRenderer drawer;

  public View(Controller controller) {
    this.controller = controller;

    try {
      initScreen();
      this.painter = screen.newTextGraphics();
      this.menu = new GameMenu(screen);
      this.drawer = new GameRenderer(screen, controller);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  public void run() throws IOException {
    boolean runningMenu = true;
    while (runningMenu) {
      switch (menu.getChoice()) {
        case NEW_GAME:
          controller.nullSession();
          controller.createNewGame();
          gameCycle();
          break;
        case LOAD_GAME:
          if (controller.loadGame()) {
            gameCycle();
          }
          break;
        case SCOREBOARD:
          showScoreboard();
          while (getChar() != 27);
          break;
        case EXIT:
          runningMenu = false;
          break;
      }
    }

    screen.close();
  }

  private void showScoreboard() throws IOException {
    clearScreen();

    TerminalSize size = screen.getTerminalSize();
    int centerX = size.getColumns() / 2;
    int centerY = size.getRows() / 2;

    var topRecords = controller.getTopRecords();
    if (!topRecords.isEmpty()) {
      String topRecordsStr = "TOP RECORDS:";
      painter.putString(centerX - topRecordsStr.length() / 2,
              centerY - topRecords.size() / 2 - 2, topRecordsStr);

      int row = centerY - topRecords.size() / 2;
      for (int i = 0; i < topRecords.size(); i++) {
        String recordLine = String.format("%2d. %s", i + 1, topRecords.get(i));
        painter.putString(centerX - PresentationConstants.SCOREBOARD_MAX_WIDTH / 2, row + i, recordLine);
      }

      String exitStr = "Press ESC button to return to the menu";
      painter.putString(centerX - exitStr.length() / 2, row + topRecords.size() + 2, exitStr);

    } else {
      String noRecordsStr = "No records found";
      painter.putString(centerX - noRecordsStr.length() / 2, centerY, noRecordsStr);

      String exitStr = "Press ESC button to return to the menu";
      painter.putString(centerX - exitStr.length() / 2, centerY + 2, exitStr);
    }

    screen.refresh();
  }

  private void gameCycle() throws IOException {
    boolean playing = true;
    while (playing) {
      updateGameScreen();

      playing = handleUserInput();

      if (controller.isGameOver()) {
        playing = false;
        controller.addRecord();
        controller.nullSession();
        printGameoverScreen();
        while (getChar() != 10);
      } else if (controller.isVictory()) {
        playing = false;
        controller.addRecord();
        controller.nullSession();
        printVictoryScreen();
        while (getChar() != 10);
      }
    }
  }

  private boolean handleUserInput() throws IOException {
    boolean playing = true;

    switch (getChar()) {
      case 'w', 'W' -> {
        controller.movePlayer(Direction.UP);
        controller.makeStep();
      }
      case 'a', 'A' -> {
        controller.movePlayer(Direction.LEFT);
        controller.makeStep();
      }
      case 's', 'S' -> {
        controller.movePlayer(Direction.DOWN);
        controller.makeStep();
      }
      case 'd', 'D' -> {
        controller.movePlayer(Direction.RIGHT);
        controller.makeStep();
      }
      case 'h', 'H' -> chooseConsumable(FieldTypes.WEAPON);
      case 'j', 'J' -> chooseConsumable(FieldTypes.FOOD);
      case 'k', 'K' -> chooseConsumable(FieldTypes.ELIXIR);
      case 'e', 'E' -> chooseConsumable(FieldTypes.SCROLL);
      case 'l', 'L' -> throwWeapon();
      case 27 -> {
        playing = false;
        controller.saveGame();
      }
    }

    return playing;
  }

  private void chooseConsumable(FieldTypes type) throws IOException {
    var backpack = controller.getPlayer().getInventory();
    var items = switch (type) {
      case WEAPON -> backpack.getWeapons();
      case FOOD -> backpack.getFoods();
      case ELIXIR -> backpack.getElixirs();
      case SCROLL -> backpack.getScrolls();
      default -> null;
    };

    if (items != null) {
      printItemsMenu(type, items, false);

      int index = getChar() - '0';
      if (index == 0 && type == FieldTypes.WEAPON) {
        controller.setEmptyHands();
      } else {
        Item item = getItemFromBackpack(index, items);

        if (item != null) {
          controller.useItem(item);
        }
      }
    }
  }

  private void throwWeapon() throws IOException {
    var weapons = controller.getPlayer().getInventory().getWeapons();

    printItemsMenu(FieldTypes.WEAPON, weapons, true);

    int index = getChar() - '0';
    if (!weapons.isEmpty()) {
      var weapon = getItemFromBackpack(index, weapons);
      controller.getPlayer().throwWeapon(weapon);
    }
  }

  private void printItemsMenu(FieldTypes type, ArrayList<? extends Item> items, boolean throwing) throws IOException {
    clearScreen();

    TerminalSize size = screen.getTerminalSize();
    int currentRow = (size.getRows() - 10) / 2;
    int centerCol = size.getColumns() / 2;

    String headerStr = String.format("Choose %s:", type.toString().toLowerCase());
    painter.putString(centerCol - headerStr.length() / 2, currentRow - 2, headerStr);

    if (!items.isEmpty()) {
      int counter = 1;

      if (type == FieldTypes.WEAPON && !throwing) {
        String noWeaponStr = "0. Without weapon";
        painter.putString(centerCol - noWeaponStr.length() / 2, currentRow, noWeaponStr);
        currentRow++;
      }

      for (var item : items) {
        String itemStr = String.format("%d. %s:", counter, item.toString().toLowerCase());
        painter.putString(centerCol - itemStr.length() / 2, currentRow++, itemStr);
        counter++;
      }

      int minKey = (type == FieldTypes.WEAPON) ? 0 : 1;
      int maxKey = counter - 1;
      String chooseStr = String.format("Press %d-%d key to choose %s or any key to continue",
              minKey, maxKey, type.toString().toLowerCase());

      painter.putString(centerCol - chooseStr.length() / 2, currentRow + 1, chooseStr);
    } else {
      String noItemStr = String.format("You have no %s!", type.toString().toLowerCase());
      painter.putString(centerCol - noItemStr.length() / 2, currentRow, noItemStr);

      String exitStr = "Press any key to continue...";
      painter.putString(centerCol - exitStr.length() / 2, currentRow + 2, exitStr);
    }

    screen.refresh();
  }

  private Item getItemFromBackpack(int index, ArrayList<? extends Item> items) {
    Item item = null;

    if (index >= 1 && index <= items.size()) {
      item = items.get(index - 1);
    }

    return item;
  }

  private void updateGameScreen() throws IOException {
    clearScreen();
    drawer.print();
    screen.refresh();
  }

  private void initScreen() throws IOException {
    TerminalSize size = new TerminalSize(
            PresentationConstants.SCREEN_WIDTH,
            PresentationConstants.SCREEN_HEIGHT);

    DefaultTerminalFactory gameTerminalFactory = new DefaultTerminalFactory()
            .setInitialTerminalSize(size)
            .setTerminalEmulatorTitle(PresentationConstants.GAME_TITLE);

    Terminal terminal = gameTerminalFactory.createTerminal();

    this.screen = new TerminalScreen(terminal);

    screen.startScreen();
    screen.setCursorPosition(null);
  }

  private int getChar() throws IOException {
    int ch = -1;

    KeyStroke key = screen.readInput();
    if (key.getKeyType() == KeyType.Character) {
      ch = key.getCharacter();
    } else if (key.getKeyType() == KeyType.Escape) {
      ch = 27;
    } else if (key.getKeyType() == KeyType.Enter) {
      ch = 10;
    }

    return ch;
  }

  private void printGameoverScreen() throws IOException {
    clearScreen();

    TerminalSize size = screen.getTerminalSize();
    TerminalPosition pos = new TerminalPosition(
            (size.getColumns() - PresentationConstants.GAMEOVER_LOGO_WIDTH) / 2,
              ((size.getRows() - PresentationConstants.GAMEOVER_LOGO_HEIGHT) - 2) / 2);

    for (int i = 0; i < PresentationConstants.GAMEOVER_LOGO_HEIGHT; i++) {
      painter.putString(pos.withRelative(0, i), PresentationConstants.GAMEOVER_LOGO[i]);
    }

    String exitStr = "Press ENTER button to return to the menu";
    painter.putString(
            (size.getColumns() - exitStr.length()) / 2,
            pos.getRow() + PresentationConstants.GAMEOVER_LOGO_HEIGHT + 2, exitStr);

    screen.refresh();
  }

  private void printVictoryScreen() throws IOException {
    clearScreen();

    TerminalSize size = screen.getTerminalSize();
    TerminalPosition pos = new TerminalPosition(
            (size.getColumns() - PresentationConstants.WIN_LOGO_WIDTH) / 2,
            (size.getRows() - PresentationConstants.WIN_LOGO_HEIGHT) / 2);

    for (int i = 0; i < PresentationConstants.WIN_LOGO_HEIGHT; i++) {
      painter.putString(pos.withRelative(0, i), PresentationConstants.WIN_LOGO[i]);
    }

    String exitStr = "Press ENTER button to return to the menu";
    painter.putString(
            (size.getColumns() - exitStr.length()) / 2,
            pos.getRow() + PresentationConstants.WIN_LOGO_HEIGHT + 2, exitStr);

    screen.refresh();
  }

  private void clearScreen() {
    TerminalSize size = screen.getTerminalSize();

    for (int i = 0; i < size.getRows(); ++i) {
      for (int j = 0; j < size.getColumns(); ++j) {
        painter.setCharacter(j, i, ' ');
      }
    }
  }
}
