package com.carolinafintechhub.ecommerce

import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import javax.persistence.Entity

@Entity
class Product

@Repository
interface ProductRepository

@Service
class ProductService

@Controller
class ProductController
