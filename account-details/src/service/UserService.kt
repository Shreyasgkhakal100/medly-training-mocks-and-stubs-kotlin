package service

import exceptions.NotFoundException
import models.EmailBody
import repository.UserRepository

class UserService(private val userRepository: UserRepository?, private val emailService: EmailService?) {
    fun sendWelcomeEmail(email: String) = emailService?.send(EmailBody("Welcome", "Welcome to the portal", email))

    fun sendRegisteredPhoneNumber(email: String) {
        try {
            val user = userRepository?.findByEmail(email)
            val emailBody = EmailBody(
                "Account Details", "Here is your Registered Phone Number: ${user?.phoneNumber}", email
            )
            emailService?.send(emailBody)
        } catch (exception: NotFoundException) {
            val emailBody = EmailBody(
                "Account Not Found", "We do not have a registered account matching your email address", email
            )
            emailService?.send(emailBody)
        }
    }
}
