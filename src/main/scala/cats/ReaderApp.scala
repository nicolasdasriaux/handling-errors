package cats

import cats.data.Reader
import service._

object ReaderApp {
  def main(args: Array[String]): Unit = {
    val globalConfig = GlobalConfig(
      CustomerServiceConfig("First Name", "Last Name"),
      ItemServiceConfig("Item")
    )

    val program: Reader[GlobalConfig, (Customer, Item)] = for {
      customerService <- Reader(CustomerService.apply).local[GlobalConfig](_.customerServiceConfig)
      itemService <- Reader(ItemService.apply).local[GlobalConfig](_.itemServiceConfig)
      customer = customerService.find(1)
      item = itemService.find(2)
    } yield (customer, item)

    val result: (Customer, Item) = program.run(globalConfig)
    println(result)
  }
}
