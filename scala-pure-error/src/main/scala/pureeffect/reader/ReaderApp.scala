package pureeffect.reader

import pureerror.service._

object ReaderApp {
  def main(args: Array[String]): Unit = {
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

    println(program.run(globalConfig))
  }
}
