package com.example.phongvanrestaurant.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return items;
    }

    public void addItem(Product product, int quantity) {
        this.items.add(new CartItem(product, quantity));
    }

    public void removeItem(Product product) {
        items.removeIf(cartItem -> cartItem.getProduct().getID() == product.getID());
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public void clearCart() {
        items.clear();
    }
}

