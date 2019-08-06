package com.carolinafintechhub.ecommerce

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import kotlin.math.sign

@Controller
@RequestMapping("/cart")
class CartController(val productService: ProductService, val userService: UserService) {
	@get:ModelAttribute("cart")
	val cart get() = userService.loggedInUser?.cart!!
	
	@ModelAttribute("list")
	fun list() = ArrayList<Double>()
	
	// DRY CRUD
	
	/** &nbsp;R */
	@get:GetMapping
	val showCart = "cart"
	
	/** C UD */
	@RequestMapping(method = [RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE])
	fun setQuantity(
		@RequestParam id: ArrayList<Long>,
		@RequestParam quantity: ArrayList<Int?>?
	): String {
		for((i, q) in id to (quantity ?: arrayListOf(null))) {
			val p = productService.findById(i) ?: continue
			// if((q ?: 1) > 0) cart[p] = q ?: ((cart[p] ?: 0) + 1)
			// else cart -= p
			// More lines but more readable and still nice.
			when(q?.sign) {
				null -> cart[p] = (cart[p] ?: 0) + 1
				1 -> cart[p] = q
				else -> cart -= p
			}
		}
		userService.updateCart(cart)
		return "redirect:/cart"
	}
}
