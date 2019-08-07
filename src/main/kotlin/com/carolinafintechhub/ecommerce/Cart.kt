package com.carolinafintechhub.ecommerce

import com.stripe.Stripe
import com.stripe.model.Charge
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import kotlin.math.sign

@Controller
@RequestMapping("/cart")
class CartController(
	val productService: ProductService,
	val userService: UserService,
	@get:ModelAttribute("publicKey")
	@Value("\${STRIPE_PUBLIC_KEY}")
	val stripePublicKey: String,
	@Value("\${STRIPE_SECRET_KEY}")
	stripeSecret: String
) {
	init {
		Stripe.apiKey = stripeSecret
	}
	
	@get:ModelAttribute("cart")
	val cart get() = userService.loggedInUser?.cart!!
	
	@get:ModelAttribute("total")
	val total get() = cart.entries.fold(0.0) { acc, e -> acc + e.key.price * e.value }
	
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
	
	@PostMapping("/checkout")
	fun checkout(@RequestParam stripeToken: String, model: Model): String {
		model["charge"] = Charge.create(mapOf(
			"amount" to (total * 100).toInt(),
			"source" to stripeToken,
			"currency" to "USD"
		))
		
		cart.clear()
		userService.updateCart(cart)
		
		return "result"
	}
}
