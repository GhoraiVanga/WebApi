package me.zhulin.shopapi.service.impl;


import me.zhulin.shopapi.api.CartController;
import me.zhulin.shopapi.entity.Cart;
import me.zhulin.shopapi.entity.OrderMain;
import me.zhulin.shopapi.entity.ProductInOrder;
import me.zhulin.shopapi.entity.ProductInfo;
import me.zhulin.shopapi.entity.User;
import me.zhulin.shopapi.repository.CartRepository;
import me.zhulin.shopapi.repository.OrderRepository;
import me.zhulin.shopapi.repository.ProductInOrderRepository;
import me.zhulin.shopapi.repository.UserRepository;
import me.zhulin.shopapi.service.CartService;
import me.zhulin.shopapi.service.ProductService;
import me.zhulin.shopapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created By Zhu Lin on 3/11/2018.
 */
@Service
public class CartServiceImpl implements CartService
{
	
	@Autowired
    EmailService service;
    @Autowired
    ProductService productService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductInOrderRepository productInOrderRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    UserService userService;

    @Override
    public Cart getCart(User user) {
        return user.getCart();
    }

    @Override
    @Transactional
    public void mergeLocalCart(Collection<ProductInOrder> productInOrders, User user) {
        Cart finalCart = user.getCart();
        System.out.println("finalcart " + finalCart);
        productInOrders.forEach(productInOrder -> 
        {
            Set<ProductInOrder> set = finalCart.getProducts();
            System.out.println("set " + set);
            Optional<ProductInOrder> old = set.stream().filter(e -> e.getProductId().equals(productInOrder.getProductId())).findFirst();
            System.out.println("old " + old);
            ProductInOrder prod;
          
            
            if (old.isPresent()) 
            {
                prod = old.get();
                prod.setCount(productInOrder.getCount() + prod.getCount());
            } else {
                prod = productInOrder;
                prod.setCart(finalCart);
                finalCart.getProducts().add(prod);
            }
            productInOrderRepository.save(prod);
        });
        cartRepository.save(finalCart);

    }

    @Override
    @Transactional
    public void delete(String itemId, User user) {
        var op = user.getCart().getProducts().stream().filter(e -> itemId.equals(e.getProductId())).findFirst();
        op.ifPresent(productInOrder -> {
            productInOrder.setCart(null);
            productInOrderRepository.deleteById(productInOrder.getId());
        });
    }



    @Override
    @Transactional
    public void checkout(User user) {
        System.out.println("user" + user);
        OrderMain order = new OrderMain(user);
     //   ProductInOrder po=new ProductInOrder();
       //     System.out.println("po" + po.getProductPrice());
        System.out.println("order" + order);
      
        orderRepository.save(order);
       
        
        System.out.println(order.getCreateTime());
        Map<String, Object> model = new HashMap<>();
		model.put("Name", order.getBuyerName());
		model.put("location", "West Bengal , Palashpai");
		model.put("Mobile", order.getBuyerPhone());
		model.put("OrderAmount", order.getOrderAmount());
	   //model.put("Order" , order.getProducts());
		model.put("Address", order.getBuyerAddress());
		//System.out.println(" Order Amount " + order.getOrderAmount());
		
		
    
        // clear cart's foreign key & set order's foreign key& decrease stock//
        user.getCart().getProducts().forEach(productInOrder ->
        {
        	model.put("ProductName", productInOrder.getProductName());
        	model.put("Total", productInOrder.getCount());
        	System.out.println("productInOrder.getCount() " + productInOrder.getCount());
            productInOrder.setCart(null);
            productInOrder.setOrderMain(order);
            productService.decreaseStock(productInOrder.getProductId(), productInOrder.getCount());
            productInOrderRepository.save(productInOrder);
        });
        service.sendEmail(order, model);
        System.out.println(" Order Amount " + order.getOrderAmount());
    }
    
}
