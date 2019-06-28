package reader

object ReaderApp {
  def main(args: Array[String]): Unit = {
    Reader((n: Int) => s"#$n")

    val program: Reader[GlobalConfig, (Customer, Item)] = for {
      customerService <- CustomerService.fromCustomerServiceConfig.local[GlobalConfig](_.customerServiceConfig)
      itemService <- ItemService.fromItemServiceConfig.local[GlobalConfig](_.itemServiceConfig)
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
  }
}
