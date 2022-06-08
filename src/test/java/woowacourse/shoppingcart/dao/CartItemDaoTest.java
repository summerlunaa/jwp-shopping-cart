package woowacourse.shoppingcart.dao;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import woowacourse.shoppingcart.dao.entity.CartItemEntity;

@JdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class CartItemDaoTest {

    private final CartItemDao cartItemDao;

    @Autowired
    public CartItemDaoTest(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        cartItemDao = new CartItemDao(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @DisplayName("커스터머 아이디를 넣으면, 해당 커스터머가 장바구니에 담은 상품의 아이디 목록을 가져온다.")
    @Test
    void findProductIdsByCustomerId() {
        // given
        final Long customerId = 1L;

        // when
        final List<Long> productsIds = cartItemDao.findProductIdsByCustomerId(customerId);

        // then
        assertThat(productsIds).containsExactly(1L, 2L);
    }

    @DisplayName("Customer Id로 장바구니 목록을 조회한다.")
    @Test
    void findAllByCustomerId() {
        // given
        final Long customerId = 1L;

        // when
        final List<CartItemEntity> cartItemEntities = cartItemDao.findAllByCustomerId(customerId);

        // then
        assertThat(cartItemEntities)
                .hasSize(2)
                .extracting(CartItemEntity::getId, CartItemEntity::getCustomerId, CartItemEntity::getProductId,
                        CartItemEntity::getQuantity)
                .contains(
                        tuple(1L, 1L, 1L, 1),
                        tuple(2L, 1L, 2L, 1)
                );
    }

    @DisplayName("customerId와 productId로 장바구니를 조회한다")
    @Test
    void findByCustomerIdAndProductId() {
        // given
        final Long customerId = 1L;
        final Long productId = 1L;

        // when
        final CartItemEntity cartItemEntity = cartItemDao.findByCustomerIdAndProductId(customerId, productId);

        // then
        assertThat(cartItemEntity)
                .usingRecursiveComparison()
                .isEqualTo(new CartItemEntity(1L, 1L, 1L, 1));
    }

    @DisplayName("cartId로 장바구니를 조회한다.")
    @Test
    void findById() {
        // given
        final Long cartId = 1L;

        // when
        final CartItemEntity cartItemEntity = cartItemDao.findById(cartId);

        // then
        assertThat(cartItemEntity)
                .usingRecursiveComparison()
                .isEqualTo(new CartItemEntity(1L, 1L, 1L, 1));
    }

    @DisplayName("이미 카트에 담긴 상품인지 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"1,True", "10,False"})
    void existCartItem(final Long productId, final Boolean expected) {
        assertThat(cartItemDao.existCartItem(1L, productId)).isEqualTo(expected);
    }

    @DisplayName("카트에 아이템을 담으면, 담긴 카트 아이디를 반환한다. ")
    @Test
    void addCartItem() {
        // given
        final Long customerId = 1L;
        final Long productId = 1L;
        final int quantity = 1;

        // when & then
        assertDoesNotThrow(() -> cartItemDao.addCartItem(customerId, productId, quantity));
    }

    @DisplayName("장바구니에 담긴 아이템의 수량을 변경한다.")
    @Test
    void updateCartItem() {
        // given
        final Long customerId = 1L;
        final Long productId = 1L;
        final int quantity = 2;

        // when
        cartItemDao.updateCartItem(customerId, productId, quantity);

        // then
        CartItemEntity cartItemEntity = cartItemDao.findByCustomerIdAndProductId(customerId, productId);
        assertThat(cartItemEntity.getQuantity()).isEqualTo(quantity);
    }

    @DisplayName("Customer Id를 넣으면, 해당 장바구니 Id들을 가져온다.")
    @Test
    void deleteCartItem() {

        // given
        final Long cartId = 1L;

        // when
        cartItemDao.deleteCartItem(cartId);

        // then
        final Long customerId = 1L;
        final List<Long> productIds = cartItemDao.findProductIdsByCustomerId(customerId);

        assertThat(productIds).containsExactly(2L);
    }
}
