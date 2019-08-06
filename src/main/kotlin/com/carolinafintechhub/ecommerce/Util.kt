package com.carolinafintechhub.ecommerce

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer as EUAC
import java.util.Optional

fun <T> Optional<T>.orNull(): T? = this.orElse(null)

inline operator fun HttpSecurity.invoke(block: HttpSecurity.() -> Unit) {
	this.block()
}

inline fun HttpSecurity.authorizeRequests(block: EUAC<*>.ExpressionInterceptUrlRegistry.() -> Unit) {
	authorizeRequests().block()
}

inline fun HttpSecurity.formLogin(block: FormLoginConfigurer<*>.() -> Unit) {
	formLogin().block()
}

inline fun HttpSecurity.logout(block: LogoutConfigurer<*>.() -> Unit) {
	logout().block()
}
