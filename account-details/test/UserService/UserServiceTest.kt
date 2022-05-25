import exceptions.NotFoundException
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import models.EmailBody
import models.User
import repository.UserRepository
import service.EmailService
import service.UserService

class UserServiceTest: StringSpec() {
    private val mockEmailService =  mockk<EmailService>( relaxed = true)
    private val mockUserRepository = mockk<UserRepository>()

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        super.afterEach(testCase, result)
        clearAllMocks()
    }

    init {
        "should send mail" {
            val userService: UserService = UserService(userRepository = null, mockEmailService)
            userService.sendWelcomeEmail("test@gmail.com")
            val expectedMailBody: EmailBody = EmailBody("Welcome", "Welcome to the portal", "test@gmail.com")
            verify(exactly = 1) { mockEmailService.send(expectedMailBody) }
        }

        "should send account details if user is present" {
            every { mockUserRepository.findByEmail("present@a.com") } returns User("1234567890", "present@a.com", "present member")
            val userService: UserService = UserService(mockUserRepository, mockEmailService)
            userService.sendRegisteredPhoneNumber("present@a.com")
            val expectedMailBody: EmailBody = EmailBody("Account Details", "Here is your Registered Phone Number: 1234567890", "present@a.com")
            verify(exactly = 1) { mockEmailService.send(expectedMailBody) }
        }

        "should send mail with error message if user is not present" {
            every { mockUserRepository.findByEmail("absent@a.com") } throws NotFoundException()
            val userService: UserService = UserService(mockUserRepository, mockEmailService)
            userService.sendRegisteredPhoneNumber("absent@a.com")
            val expectedMailBody: EmailBody = EmailBody(
                "Account Not Found",
                "We do not have a registered account matching your email address",
                "absent@a.com"
            )
            verify(exactly = 1) { mockEmailService.send(expectedMailBody) }
        }
    }
}
