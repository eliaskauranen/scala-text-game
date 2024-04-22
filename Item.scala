package o1.adventure

/** The class `Item` represents items in a text adventure game. Each item has a name
  * and a  *  longer description. (In later versions of the adventure game, items may
  * have other features as well.)
  *
  * N.B. It is assumed, but not enforced by this class, that items have unique names.
  * That is, no two items in a game world have the same name.
  *
  * @param name         the item's name
  * @param description  the item's description */
class Item(val name: String, val description: String) {

  /** Returns a short textual representation of the item (its name, that is). */
  override def toString = this.name


}

class Car(name: String, description: String) extends Item(name, description) {
  val partsNeeded = Vector[String]("battery", "gasoline", "keys")         // the names of the items a car needs to be started
  var carParts = Map[String, Item]()                                      // the items the player has put in to the car
  def neededParts = partsNeeded.diff(carParts.keys.toVector)              // returns the car items which are not in the car
  def isFixed = this.partsNeeded == this.carParts.keys.toVector.sorted    // returns true if the car has all of its parts and is ready to be started
}