package com.carolinafintechhub.ecommerce

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Product(
	val name: String = "",
	val brand: String = "",
	val category: String = "",
	val description: String = "",
	val image: String = "",
	val price: Double = 0.0,
	@GeneratedValue @Id val id: Long = 0
)

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
	override fun findAll(): List<Product>
	
	fun findByBrand(brand: String?): List<Product>
	fun findByCategory(category: String?): List<Product>
	fun findByBrandAndCategory(brand: String?, category: String?): List<Product>
	
	@Query("SELECT DISTINCT brand FROM Product p")
	fun findDistinctBrands(): List<String>
	
	@Query("SELECT DISTINCT category FROM Product p")
	fun findDistinctCategories(): List<String>
}

@Service
class ProductService(val productRepository: ProductRepository) {
	fun save(product: Product) = productRepository.save(product)
	
	fun findAll() = productRepository.findAll()
	fun findById(id: Long) = productRepository.findById(id).orNull()
	
	fun findByBrand(brand: String?) = productRepository.findByBrand(brand)
	fun findByCategory(category: String?) = productRepository.findByCategory(category)
	fun findByBrandAndCategory(brand: String?, category: String?) = productRepository.findByBrandAndCategory(brand, category)
	
	fun findDistinctBrands() = productRepository.findDistinctBrands()
	fun findDistinctCategories() = productRepository.findDistinctCategories()
	
	fun findByBrandAndOrCategory(brand: String?, category: String?) =
		when(null) {
			brand -> findByCategory(category).ifEmpty(::findAll)
			category -> findByBrand(brand)
			else -> findByBrandAndCategory(brand, category)
		}
}

@Controller
class ProductController(val productService: ProductService) {
	@GetMapping("/product/{id}")
	fun showProduct(@PathVariable id: Long, model: Model): String {
		model["product"] = productService.findById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
		return "product"
	}
	
	@GetMapping("/filter")
	fun filter(@RequestParam brand: String?, @RequestParam category: String?, model: Model): String {
		model["products"] = productService.findByBrandAndOrCategory(brand, category)
		return "main"
	}
	
	@ExceptionHandler(ResponseStatusException::class)
	fun fourOhFour(e: ResponseStatusException) = when(e.status) {
		HttpStatus.NOT_FOUND -> "404"
		else -> "redirect:/error"
	}
}
