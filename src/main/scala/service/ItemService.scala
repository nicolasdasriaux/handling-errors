package service

trait ItemService {
  def find(id: Int): Item
}

object ItemService {
  def apply(config: ItemServiceConfig): ItemService = new ItemService {
    def find(id: Int): Item = Item(id, s"${config.namePrefix} $id")
  }
}
