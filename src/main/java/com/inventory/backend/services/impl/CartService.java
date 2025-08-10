package com.inventory.backend.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import com.inventory.backend.dao.impl.CartDao;
import com.inventory.backend.entities.Cart;
import com.inventory.backend.entities.Customer;
import com.inventory.backend.entities.Product;
import com.inventory.backend.services.IModelService;

@Service
public class CartService implements IModelService<Cart, Long> {

    private final CartDao cartDao;

    public CartService(CartDao cartDao) {
        this.cartDao = cartDao;
    }

    @Override
    public Optional<Cart> save(Cart cart) {
        cartDao.create(cart);
        return Optional.of(cart);
    }

    @Override
    public Optional<Cart> findById(Long id) {
        return Optional.ofNullable(cartDao.findById(id).orElse(null));
    }

    @Override
    public List<Cart> findAll() {
        return cartDao.findAll();
    }

    @Override
    public Optional<Cart> update(Cart cart, Long id) {
        throw new UnsupportedOperationException("Update method not implemented");
    }

    public void addProduct(Long cartId, Long productId, Integer quantity) {
        cartDao.addProduct(cartId, productId, quantity);
    }

    public void removeProduct(Long cartId, Long productId) {
        cartDao.removeProduct(cartId, productId);
    }

    // Retrieves cart ID by customer email
    public Long getCartIdByCustomerEmail(String email) {
        return cartDao.getCartIdByCustomerEmail(email);
    }

    @Override
    public void delete(Long id) {
        cartDao.delete(id);
    }

    // Get Cart object by Customer's email
    public Cart getCartByCustomerEmail(Customer customer) {
        Long cartId = getCartIdByCustomerEmail(customer.getEmailId());
        if (cartId != null) {
            return findById(cartId).orElse(null);  // Fetch the cart by its ID if found
        }
        return null;  // Return null if cart is not found
    }

    // Retrieves products from the customer's cart
    public Set<Triple<Product, Integer, java.sql.Date>> getCartProducts(Customer customer) {
        Cart cart = getCartByCustomerEmail(customer);  // Fetch the cart using the correct method
        
        if (cart != null) {
            return new HashSet<>(cart.getProducts());  // Return the products from the cart
        }
        
        return new HashSet<>();  // Return empty set if cart not found
    }
}
