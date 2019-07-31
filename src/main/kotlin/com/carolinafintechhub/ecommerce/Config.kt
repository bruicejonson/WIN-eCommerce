package com.carolinafintechhub.ecommerce

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
// @EnableWebSecurity
class SecurityConfiguration : WebSecurityConfigurerAdapter()

@Configuration
class WebMvcConfiguration
