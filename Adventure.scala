package o1.adventure

import scala.util.Random


/** The class `Adventure` represents text adventure games. An adventure consists of a player and
  * a number of areas that make up the game world. It provides methods for playing the game one
  * turn at a time and for checking the state of the game.
  *
  * N.B. This version of the class has a lot of "hard-coded" information which pertain to a very
  * specific adventure game that involves a small trip through a twisted forest. All newly created
  * instances of class `Adventure` are identical to each other. To create other kinds of adventure
  * games, you will need to modify or replace the source code of this class. */
class Adventure {

  /** The title of the adventure game. */
  val title = "A Forest Nightmare"

  private val northCape      = new Area("North Cape", "A small cape in the north.")
  private val northCoast     = new Area("North Coast", "The water looks kinda reddish...")
  private val campfire       = new Area("Campfire", "You are at the campfire. There is a smell of meat in the air. Someone was grilling.")
  private val woods          = new Area("Woods", "A lot of evergreen trees here.")
  private val swamp          = new Area("Swamp", "A silent swamp. No signs of life forms.")
  private val campingArea    = new Area("Camping Area", "You are at the camping area. There are some tents but where are the hikers?")
  private val northernForest = new Area("Northern Forest", "You are at the northern part of the forest.")
  private val cabin          = new Area("Cabin", "An old abondoned cabin made of woods.")
  private val island         = new Area("Island", "You are on a small island.")
  private val bridge         = new Area("Bridge", "A bridge to an island.")
  private val westernForest  = new Area("Western Forest", "You are at the western part of the forest.")
  private val forestHeart    = new Area("Forest Heart", "You are in the middle of the forest.")
  private val easternForest  = new Area("Eastern Forest", "You are at the eastern part of the forest.")
  private val shore          = new Area("Shore", "A small shore at the edge of the forest.")
  private val southernForest = new Area("Southern Forest", "You are at the southern part of the forest.")
  private val cemetery       = new Area("Cemetery", "Looks like the murderer has created a cemetery for its victims. Lets hope you won't end up here.")
  private val southCape      = new Area("South Cape", "A small cape in the south.")
  private val southCoast     = new Area("South Coast", "The water looks kinda reddish...")
  private val dirtRoad       = new Area("Dirt Road", "Drive south to reach the south exit!")
  private val bushes         = new Area("Bushes", "A place of big bushes.")
  private val shack          = new Area("Shack", "You entered a dark, creepy shack. The place is dirty and smells awful.")
  private val southExit      = new Area("South Exit", "Your only path to escape.")


  private val gameArea = Vector[Area](northCape, northCoast, campfire, woods, swamp, campingArea, northernForest, cabin, island, bridge,
    westernForest, forestHeart, easternForest, shore, southernForest, cemetery, southCape, southCoast, dirtRoad, bushes, shack, southExit)

  private val itemsInGame = Vector[Item](
    new Item("bandage", "Bandage to stop bleedings."),
    new Item("battery", "A car battery."),
    new Item("firstaid", "First Aid to heal wounds."),
    new Item("gasoline", "Gasoline to fill the tank of a car."),
    new Item("keys", "Car keys to drive a car."),
    new Item("knife", "A sharp knife."),
    new Item("map", "A map of the game area."),
    new Item("penknife", "A sharp penknife."),
    new Item("pocketknife", "A sharp pocketknife.")
  )

       northCape.setNeighbors(Vector(                           "east" -> northCoast                                                         ))
      northCoast.setNeighbors(Vector(                           "east" -> campfire,                                  "west" -> northCape     ))
        campfire.setNeighbors(Vector(                           "east" -> woods,          "south" -> campingArea,    "west" -> northCoast    ))
           woods.setNeighbors(Vector(                           "east" -> swamp,          "south" -> northernForest, "west" -> campfire      ))
           swamp.setNeighbors(Vector(                                                     "south" -> cabin,          "west" -> woods         ))
     campingArea.setNeighbors(Vector("north" -> campfire,       "east" -> northernForest, "south" -> westernForest                           ))
  northernForest.setNeighbors(Vector("north" -> woods,          "east" -> cabin,          "south" -> forestHeart,    "west" -> campingArea   ))
           cabin.setNeighbors(Vector("north" -> swamp,                                    "south" -> easternForest,  "west" -> northernForest))
          island.setNeighbors(Vector(                           "east" -> bridge                                                             ))
          bridge.setNeighbors(Vector(                           "east" -> westernForest,                             "west" -> island        ))
   westernForest.setNeighbors(Vector("north" -> campingArea,    "east" -> forestHeart,    "south" -> shore,          "west" -> bridge        ))
     forestHeart.setNeighbors(Vector("north" -> northernForest, "east" -> easternForest,  "south" -> southernForest, "west" -> westernForest ))
   easternForest.setNeighbors(Vector("north" -> cabin,                                    "south" -> cemetery,       "west" -> forestHeart   ))
           shore.setNeighbors(Vector("north" -> westernForest,  "east" -> southernForest, "south" -> dirtRoad                                ))
  southernForest.setNeighbors(Vector("north" -> forestHeart,    "east" -> cemetery,       "south" -> bushes,         "west" -> shore         ))
        cemetery.setNeighbors(Vector("north" -> easternForest,                            "south" -> shack,          "west" -> southernForest))
       southCape.setNeighbors(Vector(                           "east" -> southCoast                                                         ))
      southCoast.setNeighbors(Vector(                           "east" -> dirtRoad,                                  "west" -> southCape     ))
        dirtRoad.setNeighbors(Vector("north" -> shore,          "east" -> bushes,         "south" -> southExit,      "west" -> southCoast    ))
          bushes.setNeighbors(Vector("north" -> southernForest, "east" -> shack,                                     "west" -> dirtRoad      ))
           shack.setNeighbors(Vector("north" -> cemetery,                                                            "west" -> bushes        ))
       southExit.setNeighbors(Vector("north" -> dirtRoad                                                                                     ))


  this.itemsInGame.foreach(this.gameArea(Random.nextInt(this.gameArea.size)).addItem(_))

  val carLocations = this.gameArea.diff(Vector(bridge, cabin, campfire, dirtRoad, shack, southExit))

  carLocations(Random.nextInt(carLocations.size)).addCar(new Car("car", "An abandoned old car."))


  /** The character that the player controls in the game. */
  val player = new Player(campfire)

  val murderer = new Killer(shack, player)

  /** The number of turns that have passed since the start of the game. */
  var turnCount = 0
  /** The maximum number of turns that this adventure game allows before time runs out. */
  val timeLimit = 200


  /** Determines if the adventure is complete, that is, if the player has won. */
  def isComplete = {
    southExit.car match {
      case Some(car) => true
      case None => false
    }
  }

  /** Determines whether the player has won, lost, or quit, thereby ending the game. */
  def isOver = this.isComplete || this.player.hasQuit || this.turnCount == this.timeLimit || !this.player.isAlive

  /** Returns a message that is to be displayed to the player at the beginning of the game. */
  def welcomeMessage = {
    this.player.help()
  }


  /** Returns a message that is to be displayed to the player at the end of the game. The message
    * will be different depending on whether or not the player has completed their quest. */
  def goodbyeMessage = {
    if (this.isComplete) {
      "Congratulations on your escaping. The nightmare is now over. You survived!"
    } else if (!this.player.isAlive) {
      "You died...rest in peace.\nGame over!"
    } else if (this.turnCount == this.timeLimit) {
      "Oh no! Time's up. Starved of entertainment, you collapse and weep like a child.\nGame over!"
    } else {  // game over due to player quitting
      "Quitter!"
    }
  }


  /** Plays a turn by executing the given in-game command, such as "go west". Returns a textual
    * report of what happened, or an error message if the command was unknown. In the latter
    * case, no turns elapse. */
  def playTurn(command: String) = {
    val action = new Action(command)
    val outcomeReport = action.execute(this.player)
    if (outcomeReport.isDefined) {
      this.murderer.move()
      this.turnCount += 1
      if(this.player.isHurt) {
        this.player.loseBlood()
      }
    }
    outcomeReport.getOrElse("Unknown command: \"" + command + "\".") + "\n" + this.murderer.reportOutcome + "\n" + this.player.isBleeding
  }


}

