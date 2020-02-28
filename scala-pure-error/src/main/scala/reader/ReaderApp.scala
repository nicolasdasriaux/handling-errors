package reader

import service._

object ReaderApp {
  def main(args: Array[String]): Unit = {
    val buildCustomerService: Reader[CustomerServiceConfig, CustomerService] = Reader(CustomerService.fromConfig)
    val buildItemService: Reader[ItemServiceConfig, ItemService] = Reader(ItemService.fromConfig)

    val program: Reader[GlobalConfig, (Customer, Item)] = for {
      customerService <- buildCustomerService.local[GlobalConfig](_.customerServiceConfig)
      itemService <- buildItemService.local[GlobalConfig](_.itemServiceConfig)

      customer = customerService.find(1)
      item = itemService.find(1)
    } yield (customer, item)

    val globalConfig = GlobalConfig(
      customerServiceConfig = CustomerServiceConfig(
        firstNamePrefix = "First Name",
        lastNamePrefix = "Last Name"
      ),
      itemServiceConfig = ItemServiceConfig(
        namePrefix = "Name"
      )
    )

    println(program.run(globalConfig))
    println(Reader[Any, String](_ => "ABC").run(()))
  }
}
