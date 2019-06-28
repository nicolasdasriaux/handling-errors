package reader

trait CustomerService {
  def find(id: Int): Customer
}

object CustomerService {
  def fromCustomerServiceConfig: Reader[CustomerServiceConfig, CustomerService] = Reader { config =>
    new CustomerService {
      override def find(id: Int): Customer = Customer(id, s"${config.firstNamePrefix} $id", s"${config.lastNamePrefix} $id")
    }
  }
}
