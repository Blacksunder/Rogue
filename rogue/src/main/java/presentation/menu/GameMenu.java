package presentation.menu;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import presentation.common.PresentationConstants;

import java.io.IOException;

public class GameMenu {
  private final Screen screen;
  private final TerminalSize size;

  private MenuItems currentItem = MenuItems.NEW_GAME;
  private static final MenuItems[] MENU_ITEMS = MenuItems.values();

  public GameMenu(Screen screen) {
    screen.clear();

    this.screen = screen;
    this.size = screen.getTerminalSize();
  }

  private void print() throws IOException {
    clearScreen();

    printLogo();
    printMenuItems();
    printDevs();

    screen.refresh();
  }

  private void printLogo() {
    TerminalPosition pos = new TerminalPosition(
            (size.getColumns() - PresentationConstants.LOGO_WIDTH) / 2,
            (size.getRows() - (PresentationConstants.LOGO_HEIGHT + MENU_ITEMS.length)) / 2);

    TextGraphics painter = screen.newTextGraphics();
    for (int i = 0; i < PresentationConstants.GAME_LOGO.length; i++) {
      painter.putString(pos.withRelative(2, i + 1), PresentationConstants.GAME_LOGO[i]);
    }
  }

  private void printDevs() {
    TerminalPosition pos = new TerminalPosition(
            (size.getColumns() - PresentationConstants.DEVS.length() - 1),
            (size.getRows() - 1));

    TextGraphics painter = screen.newTextGraphics();
    painter.putString(pos, PresentationConstants.DEVS);
  }

  private void printMenuItems() {
    TerminalPosition pos = new TerminalPosition(
            (size.getColumns() - 10) / 2,
            (size.getRows() - PresentationConstants.LOGO_HEIGHT) / 2 + PresentationConstants.LOGO_HEIGHT);

    TextGraphics painter = screen.newTextGraphics();
    for (int i = 0; i < MENU_ITEMS.length; i++) {
      boolean isSelected = (MENU_ITEMS[i] == currentItem);
      painter.setBackgroundColor(isSelected ? TextColor.ANSI.WHITE : TextColor.ANSI.BLACK);
      painter.setForegroundColor(isSelected ? TextColor.ANSI.BLACK : TextColor.ANSI.WHITE);

      painter.putString(pos.withRelative(2, i + 1), MENU_ITEMS[i].getItem());
    }
  }

  private void clearScreen() {
    TerminalPosition pos = new TerminalPosition(0, 0);
    TextGraphics painter = screen.newTextGraphics();

    for (int i = 0; i < size.getRows(); ++i) {
      for (int j = 0; j < size.getColumns(); ++j) {
        painter.setCharacter(pos.withRelative(j, i), ' ');
      }
    }
  }

  public MenuItems getChoice() throws IOException {
    MenuItems selectedItem = null;
    while (selectedItem == null) {
      print();

      switch (getChar()) {
        case 'w', 'W' -> currentItem = prevItem();
        case 's', 'S' -> currentItem = nextItem();
        case '\n' -> selectedItem = currentItem;
      }
    }

    screen.clear();

    return selectedItem;
  }

  private MenuItems prevItem() {
    return MENU_ITEMS[(currentItem.ordinal() + MENU_ITEMS.length - 1) % MENU_ITEMS.length];
  }

  private MenuItems nextItem() {
    return MENU_ITEMS[(currentItem.ordinal() + 1) % MENU_ITEMS.length];
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
}

