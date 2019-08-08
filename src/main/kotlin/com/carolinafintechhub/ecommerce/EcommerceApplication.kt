package com.carolinafintechhub.ecommerce

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute

@SpringBootApplication
class EcommerceApplication

fun main(args: Array<String>) {
	runApplication<EcommerceApplication>(*args)
}

@Controller
@ControllerAdvice
class MainController(val productService: ProductService) {
	@get:ModelAttribute("products")
	val products get() = productService.findAll()
	
	@get:ModelAttribute("brands")
	val brands get() = productService.findDistinctBrands()
	
	@get:ModelAttribute("categories")
	val categories get() = productService.findDistinctCategories()
	
	@get:GetMapping("/")
	val index = "main"
	
	@get:GetMapping("/about")
	val about = "about"
	
	@get:GetMapping("/404")
	val fourOhFour = "404"
}
