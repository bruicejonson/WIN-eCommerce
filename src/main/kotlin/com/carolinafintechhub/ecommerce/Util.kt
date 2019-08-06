package com.carolinafintechhub.ecommerce

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer as EUAC
import java.util.Optional

fun <T> Optional<T>.orNull(): T? = this.orElse(null)

operator fun <A, B> Pair<Iterable<A>, Iterable<B>>.iterator(): Iterator<Pair<A, B>> {
	val a = first.iterator()
	val b = second.iterator()
	
	return object : Iterator<Pair<A, B>> {
		override fun hasNext() = a.hasNext() && b.hasNext()
		override fun next() = a.next() to b.next()
	}
}

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
