package o1.adventure


import o1.{Pic, show}

import scala.collection.mutable.Map



/** A `Player` object represents a player character controlled by the real-life user of the program.
  *
  * A player object's state is mutable: the player's location and possessions can change, for instance.
  *
  * @param startingArea  the initial location of the player */
class Player(startingArea: Area) {

  private var quitCommandGiven = false             // one-way flag

  private val playerInventory = Map[String, Item]()

  private var blood = 5000 // the players blood amount in milliliters

  private var currentLocation = startingArea  // gatherer: changes in relation to the previous location

  var isHurt = false
  var isAlive = true
  var inCar = false


  /** Determines if the player has indicated a desire to quit the game. */
  def hasQuit = this.quitCommandGiven


  /** Returns the current location of the player. */
  def location = this.currentLocation

  /** Tries to pick up an item of the given name. This is successful if such an item is located in the player's current location
    * and the player is not inside a car. If so, the item is added to the player's inventory.  */
  def get(itemName: String) = {
    if(!this.inCar) {
      if(this.currentLocation.contains(itemName)) {
        val item = this.currentLocation.removeItem(itemName).get
        this.playerInventory += item.name -> item
        "You picked up the " + itemName + "."
      } else {
        "There is no " + itemName + " here to pick up."
      }
    } else {
      "You have to get outside the car to do so!"
    }
  }

  /** Tries to drop an item of the given name. This is successful if such an item is currently in the player's possession
    * and the player is not inside a car.
    * If so, the item is removed from the player's inventory and placed in the area. */
  def drop(itemName: String) = {
    if(!this.inCar) {
      this.playerInventory.get(itemName) match {
        case Some(item) =>
          this.playerInventory.remove(itemName)
          this.currentLocation.addItem(item)
          "You drop the " + itemName + "."
        case None => "You don't have that!"
      }
    } else {
      "You have to get outside the car to do so!"
    }
  }

  def has(itemName: String) = this.playerInventory.contains(itemName)

  /** Causes the player to examine the item of the given name. */
  def examine(itemName: String) = {
    this.playerInventory.get(itemName) match {
      case Some(item) =>
        "You look closely at the " + itemName + ".\n" + item.description
      case None => "If you want to examine something, you need to pick it up first."
    }
  }

  /** Causes the player to list what they are carrying.
    * Returns a listing of the player's possessions or a statement indicating that the player is carrying nothing. */
  def inventory() = {
    if(this.playerInventory.nonEmpty) {
      val itemList = this.playerInventory.keys.mkString("\n")
      "You are carrying:\n" + itemList
    } else {
      "You are empty-handed."
    }
  }

  /** Attempts to move the player in the given direction. This is successful if there
    * is an exit from the player's current location towards the direction name
    * and the player is not inside a car. */
  def go(direction: String) = {
    if(!this.inCar) {
      val destination = this.location.neighbor(direction)
      this.currentLocation = destination.getOrElse(this.currentLocation)
      if (destination.isDefined) {
        "You go " + direction + "."
      } else {
        "You can't go " + direction + "."
      }
    } else {
      "You have to get outside the car to do so!"
    }
  }

  /** Causes the player to rest for a short while (this has no substantial effect in game terms).
    * Returns a description of what happened. */
  def rest() = {
    "You rest for a while. Better get a move on, though."
  }

  /** If the player is hurt, he/she loses 250 ml blood each turn.
    * If there is no blood left in the player, he/she dies and the game is over. */
  def loseBlood() = {
    this.blood -= 250
    if(this.blood == 0) {
      isAlive = false
    }
  }

  def bleedingTurns = this.blood / 250 //returns how many turns the player will survive if bleeding

  def isBleeding = {
    if(isHurt && isAlive) {
      "You are hurt! You'll bleed to death in " + this.bleedingTurns + " turns!"
    } else {
      ""
    }
  }

  /** If the player is hurt, he/she can heal himself/herself by using first aid or bandage.
    * The used item is removed from the player's inventory and can't be used again. */
  def heal() = {
    if(isHurt) {
      this.playerInventory.get("firstaid") match {
        case Some(aid) =>
          this.playerInventory.remove(aid.name)
          this.isHurt = false
          "You healed yourself successfully. You're not bleeding anymore."
        case None =>
          this.playerInventory.get("bandage") match {
            case Some (bandage) =>
              this.playerInventory.remove(bandage.name)
              this.isHurt = false
              "You healed yourself successfully. You're not bleeding anymore."
            case None =>
              "You have neither bandage nor firstaid. Get some to heal yourself."
          }
      }
    } else {
      "You are not hurt. Good for you!"
    }
  }

  /** The player can add items to a car to fix it if there is a car in the player's current location.
    * Only a battery, gasoline and keys can be added. If all of them have been added, the car is fixed. */
   def add(carPart: String) = {
    if(this.currentLocation.car.isEmpty) {
      "There's no car here!"
    } else if(carPart != "battery" && carPart != "gasoline" && carPart != "keys") {
      carPart + " is not a car part!"
    } else {
      this.playerInventory.get(carPart) match {
        case Some(carItem) =>
          this.playerInventory.remove(carItem.name)
          this.currentLocation.car.foreach(_.carParts += carItem.name -> carItem)
          carItem + " added successfully!"
        case None => "You don't have the " + carPart + "!"
      }
    }
  }

  /** A fixed car can be started and used for escaping. If the car is not fixed, returns a list of items
    * already in the car and a list of items which are not yet in the car and are therefore still needed.
    * If the car is started successfully, the player gets inside the car. */
  def start() = {
    if(this.currentLocation.car.isEmpty) {
      "There's no car here!"
    } else if(this.currentLocation.car.exists(_.isFixed)) {
      this.inCar = true
      "You started the car successfully! You are inside the car. You can now drive away!"
    } else {
      this.currentLocation.car match {
        case Some(vehicle) =>
          val partsNeeded = vehicle.partsNeeded
          var carParts = vehicle.carParts.keys.toVector
          var neededParts = vehicle.neededParts
          "Unable to start the car. The car needs " + partsNeeded.mkString(" and ") + " to be started.\nAlready in the car: " + carParts.mkString(" ") + "\nNeeded: " + neededParts.mkString(" ")
        case None =>
          "Unable to start the car."
      }
    }
  }

  /** If the player is inside a car, he/she can drive it to the given direction assuming there is an exit in that direction. */
  def drive(direction: String) = {
    if(this.inCar) {
      val destination = this.location.neighbor(direction)
      if (destination.isDefined) {
        val movingCar = this.currentLocation.removeCar("car")
        this.currentLocation = destination.getOrElse(this.currentLocation)
        movingCar.foreach(this.currentLocation.addCar(_))
        "You drove " + direction + "."
      } else {
        "You can't drive " + direction + "."
      }
    } else {
      "You are not in a car!"
    }
  }

  /** Shows the map of the game area if the player has got the map item. */
  def map() = {
    if(this.playerInventory.contains("map")) {
      show(Pic("map.gif"))
      "You looked at the map."
    } else {
      "You don't have a map!"
    }
  }

  def help() = {
    val guide = Vector[String](
    "There is a murderer somewhere around here. Try to escape.",
    "You can fight back if you have a knife but you will lose it and get hurt.",
    "If you are hurt, use command heal to stop your bleeding. You need bandage or firstaid to do so.",
    "There is one broken car. You can use it to escape but you have to fix it first.",
    "Fix the car by adding battery, gasoline and keys. Type add and the name of the item to be added.",
    "Use command start to start the car and type drive and a direction to drive from an area to another.",
    "Use command map to take a look at the map if you have one.",
    "Other commands: go direction, get item name, drop item name, examine item name, inventory, rest, quit",
    "You win the game if you drive to the south exit. Good luck!")
    guide.mkString("\n")
  }

  /** Signals that the player wants to quit the game. Returns a description of what happened within
    * the game as a result (which is the empty string, in this case). */
  def quit() = {
    this.quitCommandGiven = true
    ""
  }


  /** Returns a brief description of the player's state, for debugging purposes. */
  override def toString = "Now at: " + this.location.name


}


