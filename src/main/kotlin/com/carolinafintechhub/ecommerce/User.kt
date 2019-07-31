package com.carolinafintechhub.ecommerce

import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import javax.persistence.Entity

@Entity
class User

@Repository
interface UserRepository

@Service
class UserService

@Controller
class AuthController
