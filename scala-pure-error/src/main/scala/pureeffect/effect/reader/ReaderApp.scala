package pureeffect.effect.reader

case class Customer(id: Int, firstName: String, lastName: String)

trait CustomerService {
  def find(id: Int): Customer
}

object CustomerService {
  def fromConfig(config: CustomerServiceConfig): CustomerService = new CustomerService {
    def find(id: Int): Customer = Customer(id, s"${config.firstNamePrefix} $id", s"${config.lastNamePrefix} $id")
  }
}

case class CustomerServiceConfig(firstNamePrefix: String, lastNamePrefix: String)

case class Item(id: Int, name: String)

trait ItemService {
  def find(id: Int): Item
}

object ItemService {
  def fromConfig(config: ItemServiceConfig): ItemService = new ItemService {
    def find(id: Int): Item = Item(id, s"${config.namePrefix} $id")
  }
}

case class ItemServiceConfig(namePrefix: String)
case class GlobalConfig(customerServiceConfig: CustomerServiceConfig, itemServiceConfig: ItemServiceConfig)

object ReaderApp {
  val globalConfig = GlobalConfig(
    customerServiceConfig = CustomerServiceConfig(
      firstNamePrefix = "First Name",
      lastNamePrefix = "Last Name"
    ),
    itemServiceConfig = ItemServiceConfig(
      namePrefix = "Name"
    )
  )

  val buildCustomerService: Reader[CustomerServiceConfig, CustomerService] = Reader.access(CustomerService.fromConfig)
  val buildItemService: Reader[ItemServiceConfig, ItemService] = Reader.access(ItemService.fromConfig)

  val program: Reader[GlobalConfig, (Customer, Item)] = for {
    customerService <- buildCustomerService.provideSome[GlobalConfig](_.customerServiceConfig)
    itemService <- buildItemService.provideSome[GlobalConfig](_.itemServiceConfig)

    customer = customerService.find(1)
    item = itemService.find(1)
  } yield (customer, item)

  def main(args: Array[String]): Unit = {
    val customerAndItem: (Customer, Item) = program.run(globalConfig)
    println(customerAndItem)
  }
}
