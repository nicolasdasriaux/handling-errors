package pureerror.zio

import zio._
import zio.console._
import zio.duration.durationInt
import zio.{App, ExitCode, URIO, ZEnv, ZLayer, ZManaged}

import java.io.IOException

case class Customer(id: Int, firstName: String, lastName: String)

case class CustomerService(customersRef: Ref[List[Customer]], config: CustomerServiceConfig) {
  def insert(customer: Customer): UIO[Unit] = customersRef.update(customer :: _)
  def find(id: Int): UIO[Option[Customer]] = customersRef.get.map(_.find(_.id == id))
  def findAll(): UIO[List[Customer]] = customersRef.get.map(_.reverse)
}

case class CustomerServiceConfig(firstNamePrefix: String, lastNamePrefix: String)

case class Item(id: Int, name: String)

case class ItemService(config: ItemServiceConfig) {
  def find(id: Int): Item = Item(id, s"${config.namePrefix} $id")
}

case class ItemServiceConfig(namePrefix: String)

case class ECommerceService(customerService: CustomerService, itemService: ItemService)
case class GlobalConfig(customerServiceConfig: CustomerServiceConfig, itemServiceConfig: ItemServiceConfig)

object ZioModuleApp extends App {
  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    val globalConfig = GlobalConfig(
      customerServiceConfig = CustomerServiceConfig(
        firstNamePrefix = "First Name",
        lastNamePrefix = "Last Name"
      ),
      itemServiceConfig = ItemServiceConfig(
        namePrefix = "Name"
      )
    )

    val customerServiceConfigLayer: ULayer[Has[CustomerServiceConfig]] = ZLayer.succeed(globalConfig.customerServiceConfig)
    val itemServiceConfigLayer: ULayer[Has[ItemServiceConfig]] = ZLayer.succeed(globalConfig.itemServiceConfig)

    val customerServiceLayer: URLayer[Has[CustomerServiceConfig], Has[CustomerService]] = {
      for {
        customersRef <- Ref.make(List.empty[Customer])
        customerServiceConfig <- ZIO.service[CustomerServiceConfig]
      } yield CustomerService(customersRef, customerServiceConfig)
    }.toLayer

    val itemServiceLayer: URLayer[Has[ItemServiceConfig], Has[ItemService]] = (ItemService(_)).toLayer

    val eCommerceServiceLayer: URLayer[Has[ItemService] with Has[CustomerService], Has[ECommerceService]] = (ECommerceService(_, _)).toLayer

    val eCommerceServiceLayer2: URLayer[Has[ItemService] with Has[CustomerService], Has[ECommerceService]] = {
      for {
        customerService <- ZManaged.service[CustomerService]
        itemService <- ZManaged.service[ItemService]
      } yield ECommerceService(customerService, itemService)
    }.toLayer

    val eCommerceServiceLayer3: URLayer[Has[CustomerService] with Has[ItemService], Has[ECommerceService]] = (ECommerceService(_, _)).toLayer

    val value: ULayer[Has[CustomerService] with Has[ItemService] with Has[ECommerceService]] = (
      (customerServiceConfigLayer to customerServiceLayer) and
        (itemServiceConfigLayer to itemServiceLayer)
      ) andTo eCommerceServiceLayer

    case class Application(console: Console.Service, customerService: CustomerService) {
      def run: UIO[Unit] = for {
        _ <- customerService.insert(Customer(1, "Paul", "Johnson"))
        _ <- customerService.insert(Customer(2, "Bill", "Jackson"))
        _ <- customerService.insert(Customer(3, "Mary", "Stuart"))
        maybeCustomer <-
          customerService.find(2)
            .some.map(customer => s"${customer.firstName} ${customer.lastName}").option

        _ <- console.putStrLn(maybeCustomer.toString).orDie
        customers <- customerService.findAll()

        _ <- ZIO.foreach(customers) { customer =>
          console.putStrLn(customer.toString).orDie
        }
      } yield ()
    }

    val layer: ULayer[Has[CustomerService]] = customerServiceConfigLayer to customerServiceLayer

    val program: ZIO[Has[Console.Service] with Has[CustomerService], Nothing, Unit] = for {
      customerService <- ZIO.service[CustomerService]
      console <- ZIO.service[Console.Service]
      _ <- customerService.insert(Customer(1, "Paul", "Johnson"))
      _ <- customerService.insert(Customer(2, "Bill", "Jackson"))
      _ <- customerService.insert(Customer(3, "Mary", "Stuart"))
      maybeCustomer <-
        customerService.find(2)
          .some.map(customer => s"${customer.firstName} ${customer.lastName}").option

      _ <- console.putStrLn(maybeCustomer.toString).orDie
      customers <- customerService.findAll()

      _ <- ZIO.foreach(customers) { customer =>
        console.putStrLn(customer.toString).orDie
      }
    } yield ()

    program.provideCustomLayer(layer).exitCode


    val applicationLayer: URLayer[Has[Console.Service] with Has[CustomerService], Has[Application]] = (Application(_, _)).toLayer

    ???
  }
}

