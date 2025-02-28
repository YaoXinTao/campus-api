package com.campus.api.controller.mini;

import com.campus.api.common.Result;
import com.campus.api.entity.ShoppingCart;
import com.campus.api.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "小程序-购物车接口", description = "小程序购物车相关接口")
@RestController
@RequestMapping("/api/v1/mini/shopping-cart")
@RequiredArgsConstructor
public class MiniShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "添加商品到购物车", description = "添加商品到购物车，如果已存在则更新数量")
    @PostMapping("/add")
    public Result<Void> addToCart(@RequestBody ShoppingCart cart) {
        shoppingCartService.addToCart(cart);
        return Result.success();
    }

    @Operation(summary = "更新购物车商品", description = "更新购物车商品信息（包括SKU、数量等）")
    @PutMapping("/{cartId}")
    public Result<Void> updateCartItem(@PathVariable Long cartId, @RequestBody ShoppingCart cart) {
        cart.setId(cartId);
        shoppingCartService.updateCartItem(cart);
        return Result.success();
    }

    @Operation(summary = "更新购物车商品数量", description = "更新购物车中指定商品的数量")
    @PutMapping("/quantity")
    public Result<Void> updateQuantity(@RequestParam Long cartId, @RequestParam Integer quantity) {
        shoppingCartService.updateQuantity(cartId, quantity);
        return Result.success();
    }

    @Operation(summary = "删除购物车商品", description = "从购物车中删除指定商品")
    @DeleteMapping("/{cartId}")
    public Result<Void> removeFromCart(@PathVariable Long cartId) {
        shoppingCartService.removeFromCart(cartId);
        return Result.success();
    }

    @Operation(summary = "更新商品选中状态", description = "更新购物车商品的选中状态")
    @PutMapping("/selected")
    public Result<Void> updateSelected(@RequestParam Long cartId, @RequestParam Integer selected) {
        shoppingCartService.updateSelected(cartId, selected);
        return Result.success();
    }

    @Operation(summary = "获取购物车列表", description = "获取当前用户的购物车列表")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> getCartList(@RequestParam Long userId) {
        List<ShoppingCart> cartList = shoppingCartService.getUserCart(userId);
        return Result.success(cartList);
    }
} 