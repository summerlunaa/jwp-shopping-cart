package woowacourse.shoppingcart.ui;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import woowacourse.auth.support.AuthenticationPrincipal;
import woowacourse.shoppingcart.application.CustomerService;
import woowacourse.shoppingcart.domain.customer.Customer;
import woowacourse.shoppingcart.dto.CustomerProfileRequest;
import woowacourse.shoppingcart.dto.CustomerRequest;
import woowacourse.shoppingcart.dto.CustomerResponse;
import woowacourse.shoppingcart.dto.EmailUniqueCheckResponse;
import woowacourse.shoppingcart.dto.PasswordRequest;

@Controller
@RequestMapping("/api/members")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/check-email")
    public ResponseEntity<EmailUniqueCheckResponse> checkDuplicateEmail(@RequestParam final String email) {
        final EmailUniqueCheckResponse emailUniqueCheckResponse =
                new EmailUniqueCheckResponse(customerService.isUniqueEmail(email));
        return ResponseEntity.ok().body(emailUniqueCheckResponse);
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> signUp(@RequestBody @Valid final CustomerRequest customerRequest) {
        final Customer customer = customerService.signUp(customerRequest);
        final CustomerResponse customerResponse = new CustomerResponse(customer.getEmail(), customer.getNickname());
        return ResponseEntity.status(HttpStatus.CREATED).body(customerResponse);
    }

    @PostMapping("/auth/password-check")
    public ResponseEntity<Void> checkPassword(@AuthenticationPrincipal final String email,
                                              @RequestBody @Valid final PasswordRequest passwordRequest) {
        customerService.checkPassword(email, passwordRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/auth/me")
    public ResponseEntity<CustomerResponse> findProfile(@AuthenticationPrincipal final String email) {
        final Customer customer = customerService.findByEmail(email);
        final CustomerResponse customerResponse = new CustomerResponse(customer.getEmail(), customer.getNickname());
        return ResponseEntity.ok().body(customerResponse);
    }

    @PatchMapping("/auth/me")
    public ResponseEntity<Void> updateProfile(@AuthenticationPrincipal final String email,
                                              @RequestBody @Valid final CustomerProfileRequest customerProfileRequest) {
        customerService.updateProfile(email, customerProfileRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/auth/password")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal final String email,
                                               @RequestBody @Valid final PasswordRequest passwordRequest) {
        customerService.updatePassword(email, passwordRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/auth/me")
    public ResponseEntity<Void> signOut(@AuthenticationPrincipal final String email) {
        customerService.delete(email);
        return ResponseEntity.noContent().build();
    }
}
