package service

trait CustomerService {
  def find(id: Int): Customer
}

object CustomerService {
  def fromConfig(config: CustomerServiceConfig): CustomerService = new CustomerService {
    def find(id: Int): Customer = Customer(id, s"${config.firstNamePrefix} $id", s"${config.lastNamePrefix} $id")
  }
}
