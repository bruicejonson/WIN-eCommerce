package com.carolinafintechhub.ecommerce

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
	val userService: UserService,
	val bCryptPasswordEncoder: BCryptPasswordEncoder
) : WebSecurityConfigurerAdapter() {
	override fun configure(auth: AuthenticationManagerBuilder) {
		auth.userDetailsService<UserDetailsService>(userService).passwordEncoder(bCryptPasswordEncoder)
	}
	
	override fun configure(http: HttpSecurity) {
		http {
			authorizeRequests {
				antMatchers("/cart/**").authenticated()
				antMatchers("/console/**").permitAll()
			}
			formLogin {
				loginPage("/auth")
				loginProcessingUrl("/login")
			}
			logout {
				logoutRequestMatcher(AntPathRequestMatcher("/signout"))
				logoutSuccessUrl("/")
			}
			
			headers().frameOptions().disable()
			csrf().disable()
		}
	}
}

@Configuration
class WebMvcConfiguration {
	@Bean
	fun passwordEncoder() = BCryptPasswordEncoder()
}
