package reader

trait ItemService {
  def find(id: Int): Item
}

object ItemService {
  def fromItemServiceConfig: Reader[ItemServiceConfig, ItemService] = Reader { config =>
    new ItemService {
      def find(id: Int): Item = Item(id, s"${config.namePrefix} $id")
    }
  }
}
