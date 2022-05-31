package woowacourse.shoppingcart.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowacourse.shoppingcart.dao.CustomerDao;
import woowacourse.shoppingcart.domain.Customer;
import woowacourse.shoppingcart.dto.CustomerRequest;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public boolean isDistinctEmail(String email) {
        return !customerDao.existEmail(email);
    }

    public Customer signUp(CustomerRequest customerRequest) {
        customerDao.save(
                customerRequest.getEmail(),
                customerRequest.getNickname(),
                customerRequest.getPassword());

        return customerRequest.toCustomer();
    }
}
