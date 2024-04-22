package o1.adventure

import scala.util.Random

class Killer(startingArea: Area, val victim: Player) {

  private var currentLocation = startingArea
  private var carAccident = false
  private def teleportTo = this.currentLocation.neighbors.toVector.flatMap(_._2.neighbors.toVector).map(_._2).filter(_ != this.currentLocation)

  /** Moves the killer each turn from one location to another.
    * If the victim moves to the area where the killer is, the turn is used to murder the victim instead.
    * If that's not the case, the killer changes its location so that the killer moves to a neighbor area
    * if the victim is in one of those areas. Otherwise the killer moves farther to a neighbor's neighbor.
    * If the killer moves to the area where the victim is, the killer tries to murder the victim.
    */

  def move() = {
    if(this.currentLocation == this.victim.location) {
      murder()
    } else {
      if(this.currentLocation.neighbors.exists(_._2 == this.victim.location)) {
        val neighbor = Random.shuffle(this.currentLocation.neighbors.toVector)
        this.currentLocation = neighbor.head._2
      } else {
        this.currentLocation = Random.shuffle(this.teleportTo).head
      }
      if(this.currentLocation == this.victim.location) {
        murder()
      }
    }
  }

  /** The killer tries to murder its victim.
    * If the victim is driving a car, the car crashes into the killer and is turned off. The victim manages to get out of the car and is not murdered in this turn.
    * If the victim is not driving, he/she can still survive by fighting back with a knife item. The victim gets hurt and loses the knife but is not murdered.
    * The same knife can't be used again so it disappears.
    * If the victim is neither driving nor has a knife item, the killer murders the victim successfully. GAME OVER!
    */

  def murder() = {
    if(this.victim.inCar) {
      this.victim.inCar = false
      this.carAccident = true
    } else if(this.victim.has("knife") || this.victim.has("penknife") || this.victim.has("pocketknife")) {
      this.victim.isHurt = true
      if(this.victim.has("knife")) {
        this.victim.drop("knife")
        this.victim.location.removeItem("knife")
      } else if(this.victim.has("penknife")) {
        this.victim.drop("penknife")
        this.victim.location.removeItem("penknife")
      } else {
        this.victim.drop("pocketknife")
        this.victim.location.removeItem("pocketknife")
      }
    } else {
      this.victim.isAlive = false
    }
  }

  //report at the end of a turn

  def reportOutcome = {
    if((this.teleportTo ++ this.currentLocation.neighbors.toVector.map(_._2)).contains(this.victim.location)) {
      "Watch out, the murderer is nearby and can sense your fear!"
    } else if(this.carAccident) {
      this.carAccident = false
      "You crashed into the murderer. Therefore the car stopped and turned off. You got outside. You can't start the car if the murderer is in the area. RUN!"
    } else if(this.currentLocation == this.victim.location && this.victim.isHurt && this.victim.isAlive) {
      "You got attacked by the murderer. You fought back with a knife but you got hurt. Run for your life!"
    } else if(this.victim.isHurt && this.victim.isAlive) {
      "You are bleeding. You have to heal yourself or you will bleed to death in " + this.victim.bleedingTurns + " turns!"
    } else if(!this.victim.isAlive) {
      "The murderer got you!"
    } else {
      "Your heart is beating."
    }
  }

}
