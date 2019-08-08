package com.carolinafintechhub.ecommerce

import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Entity
data class User(
	private var username: String = "",
	private var password: String = "",
	@GeneratedValue @Id val id: Long = 0
) : UserDetails {
	// So annoying that Kotlin getters and setters can't override these.
	@NotBlank override fun getUsername() = username
	@NotBlank override fun getPassword() = password
	fun setUsername(username: String) {
		this.username = username
	}
	fun setPassword(password: String) {
		this.password = password
	}
	
	@ElementCollection
	@Cascade(CascadeType.DELETE)
	val cart: MutableMap<Product, Int> = HashMap()
	
	override fun getAuthorities() = listOf(GrantedAuthority { "USER" })
	override fun isEnabled() = true
	override fun isCredentialsNonExpired() = true
	override fun isAccountNonExpired() = true
	override fun isAccountNonLocked() = true
}

@Repository
interface UserRepository : JpaRepository<User, Long> {
	fun findByUsername(username: String): User?
	
	/* Idea: The ability to use Kotlin's backticks to make these methods.
	
	fun `find by username` == fun findByUsername
	
	Spring Boot QL Queries could also work:
	fun `SELECT * FROM User u WHERE u.username = ?`
	
	 */
}

@Service
class UserService(
	val userRepository: UserRepository,
	val bCryptPasswordEncoder: BCryptPasswordEncoder
) : UserDetailsService {
	val loggedInUser
		get() = findByUsername(SecurityContextHolder.getContext().authentication.name)
	
	fun saveNew(user: User): User? {
		if(findByUsername(user.username) != null) return null
		user.password = bCryptPasswordEncoder.encode(user.password)
		return userRepository.save(user)
	}
	
	fun updateCart(cart: Map<Product, Int>) {
		(loggedInUser ?: return).cart += cart
		userRepository.save(loggedInUser as User)
	}
	
	fun findByUsername(username: String) = userRepository.findByUsername(username)
	override fun loadUserByUsername(username: String) = findByUsername(username)
}

@Controller
@RequestMapping("/auth")
class AuthController(val userService: UserService) {
	@ModelAttribute("user")
	fun blankUser() = User()
	
	@GetMapping
	fun sign(model: Model): String {
		if(userService.loggedInUser != null) return "redirect:/"
		model["formBean"] = User("", "")
		return "auth"
	}
	
	@PostMapping
	fun signInOrUp(
		@RequestParam type: String,
		@Valid user: User,
		result: BindingResult,
		request: HttpServletRequest
	): String {
		val password = user.password
		if(!result.hasErrors() && type == "up" && userService.saveNew(user) == null)
			result.rejectValue("username", "NotTaken", "already taken")
		
		if(result.hasErrors()) return "auth"
		
		try {
			request.login(user.username, password)
		} catch(e: ServletException) {
			when(e.message) {
				"Bad credentials" -> result.rejectValue("password", "BadPassword", "incorrect password")
				"UserDetailsService returned null, which is an interface contract violation" -> result.rejectValue("username", "UserNotFound", "incorrect username")
				else -> result.rejectValue("username", "UnknownError", "unknown error ")
			}
			return "auth"
		}
		return "redirect:/"
	}
}
