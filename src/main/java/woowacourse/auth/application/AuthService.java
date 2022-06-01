package woowacourse.auth.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowacourse.auth.dto.TokenRequest;
import woowacourse.auth.support.JwtTokenProvider;
import woowacourse.shoppingcart.dao.CustomerDao;
import woowacourse.shoppingcart.dao.entity.CustomerEntity;
import woowacourse.shoppingcart.exception.InvalidCustomerException;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomerDao customerDao;

    public AuthService(JwtTokenProvider jwtTokenProvider, CustomerDao customerDao) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customerDao = customerDao;
    }

    public String createToken(TokenRequest tokenRequest) {
        customerDao.findByEmailAndPassword(tokenRequest.getEmail(), tokenRequest.getPassword())
                .orElseThrow(() -> new InvalidCustomerException("아이디나 비밀번호를 잘못 입력했습니다."));

        return jwtTokenProvider.createToken(tokenRequest.getEmail());
    }

    public String getNickname(TokenRequest tokenRequest) {
        CustomerEntity customerEntity = customerDao.findByEmailAndPassword(
                        tokenRequest.getEmail(),
                        tokenRequest.getPassword())
                .orElseThrow(() -> new InvalidCustomerException("아이디나 비밀번호를 잘못 입력했습니다."));

        return customerEntity.getNickname();
    }
}
