import backend.Controller;
import backend.Model;
import presentation.View;

import java.io.IOException;

public class Main {
  public static void main(String[] args) {
      Model gameModel = new Model();
      Controller gameController = new Controller(gameModel);
      View app = new View(gameController);

      try {
          app.run();
      } catch (IOException e) {
          System.err.println(e.getMessage());
      }
  }
}
