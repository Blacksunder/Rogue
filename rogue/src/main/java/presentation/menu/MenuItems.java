package presentation.menu;

public enum MenuItems {
  NEW_GAME("NEW GAME"),
  LOAD_GAME("LOAD GAME"),
  SCOREBOARD("SCOREBOARD"),
  EXIT("EXIT");

  private String item;

  MenuItems(String item) {
    this.item = item;
  }

  public String getItem() { return item; }
}
