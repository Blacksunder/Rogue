package presentation.common;

import backend.domain.map.Constants;

public class PresentationConstants {
  public static final String GAME_TITLE = "ROGUE GAME";

  public static final int LOGO_WIDTH = 43;
  public static final int LOGO_HEIGHT = 6;
  public static final String[] GAME_LOGO = {
          "██████╗  ██████╗  ██████╗ ██╗   ██╗███████╗",
          "██╔══██╗██╔═══██╗██╔════╝ ██║   ██║██╔════╝",
          "██████╔╝██║   ██║██║  ███╗██║   ██║█████╗",
          "██╔══██╗██║   ██║██║   ██║██║   ██║██╔══╝",
          "██║  ██║╚██████╔╝╚██████╔╝╚██████╔╝███████╗",
          "╚═╝  ╚═╝ ╚═════╝  ╚═════╝  ╚═════╝ ╚══════╝"
  };

  public static final int GAMEOVER_LOGO_WIDTH = 64;
  public static final int GAMEOVER_LOGO_HEIGHT = 6;
  public static final String[] GAMEOVER_LOGO = {
          "██╗   ██╗ ██████╗ ██╗   ██╗    ██████╗ ███████╗ █████╗ ██████╗",
          "╚██╗ ██╔╝██╔═══██╗██║   ██║    ██╔══██╗██╔════╝██╔══██╗██╔══██╗",
          " ╚████╔╝ ██║   ██║██║   ██║    ██║  ██║█████╗  ███████║██║  ██║",
          "  ╚██╔╝  ██║   ██║██║   ██║    ██║  ██║██╔══╝  ██╔══██║██║  ██║",
          "   ██║   ╚██████╔╝╚██████╔╝    ██████╔╝███████╗██║  ██║██████╔╝",
          "   ╚═╝    ╚═════╝  ╚═════╝     ╚═════╝ ╚══════╝╚═╝  ╚═╝╚═════╝"
  };

  public static final int WIN_LOGO_WIDTH = 54;
  public static final int WIN_LOGO_HEIGHT = 6;
  public static final String[] WIN_LOGO = {
          "██╗   ██╗ ██████╗ ██╗   ██╗    ██╗    ██╗██╗███╗   ██╗",
          "╚██╗ ██╔╝██╔═══██╗██║   ██║    ██║    ██║██║████╗  ██║",
          " ╚████╔╝ ██║   ██║██║   ██║    ██║ █╗ ██║██║██╔██╗ ██║",
          "  ╚██╔╝  ██║   ██║██║   ██║    ██║███╗██║██║██║╚██╗██║",
          "   ██║   ╚██████╔╝╚██████╔╝    ╚███╔███╔╝██║██║ ╚████║",
          "   ╚═╝    ╚═════╝  ╚═════╝      ╚══╝╚══╝ ╚═╝╚═╝  ╚═══╝"
  };

  public static final String DEVS = "by gilmajac, jodyvole, seeiumjo";

  public static final int MSG_FIELD_HEIGHT = 5;

  public static final int MAP_FIELD_START_POSITION_Y = MSG_FIELD_HEIGHT + 1;
  public static final int MAP_FIELD_HEIGHT = Constants.HEIGHT;

  public static final int INFO_FIELD_START_POSITION_Y = MSG_FIELD_HEIGHT + Constants.HEIGHT + 1;
  public static final int INFO_FIELD_HEIGHT = 5;

  public static final int SCREEN_HEIGHT = MSG_FIELD_HEIGHT + MAP_FIELD_HEIGHT + INFO_FIELD_HEIGHT;
  public static final int SCREEN_WIDTH = Constants.WIDTH;

  public static final int SCOREBOARD_MAX_WIDTH = 70;
}
