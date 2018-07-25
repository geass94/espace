package ge.boxwood.espace.controllers


import ge.boxwood.espace.ErrorStalker.StepLoggerService
import ge.boxwood.espace.models.User
import ge.boxwood.espace.models.UserTokenState
import ge.boxwood.espace.security.TokenHelper
import ge.boxwood.espace.services.UserService
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.mobile.device.Device
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping(UserController.ENTRY_POINT)
open class UserController (val userService: UserService,
                           val tokenHelper: TokenHelper,
                           val authenticationManager: AuthenticationManager,
                           val stepLoggerService: StepLoggerService) {

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun loadById(@PathVariable id: Long?): User {
        var params = hashMapOf("userId" to id)
        stepLoggerService.logStep("UserController", "loadById", params)
        return this.userService!!.getOne(id)
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    open fun loadAll(): List<User> {
        return this.userService!!.all
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'GUEST')")
    open fun loadProfile(@RequestHeader(value = "Authorization") token: String): User {
        val accessToken = token.toString().substring(7)
        val username = tokenHelper!!.getUsernameFromToken(accessToken)
        var params = hashMapOf("access_token" to accessToken, "username" to username)
        stepLoggerService.logStep("UserController", "loadProfile", params)
        return userService!!.getByUsername(username)
    }



//    @PutMapping("/")

    @PostMapping(consumes = arrayOf(APPLICATION_JSON_UTF8_VALUE), path = arrayOf("/signup"))
    open fun add(@RequestBody @Valid user: User, response: HttpServletResponse, device: Device): Any {
        var user = user
        val rawPassowrd = user.password
//        user.authIds = ArrayList(Arrays.asList(1L))
        user = userService!!.create(user)


        val authentication = authenticationManager!!.authenticate(
                UsernamePasswordAuthenticationToken(
                        user.username,
                        rawPassowrd
                )
        )

        // Inject into security context
        SecurityContextHolder.getContext().authentication = authentication
        // token creation
        val autoUser = authentication.principal as User
        val jws = tokenHelper!!.generateToken(autoUser, device)
        val jwsr = tokenHelper.generateRefreshToken(autoUser, device)
        val expiresIn = tokenHelper.getExpiredIn(device)
        // Return the token
        return ResponseEntity.ok(UserTokenState(jws, jwsr, expiresIn.toLong(), false))
    }

    @PostMapping("/check-username")
    open fun checkUsername(@RequestBody user: User): Boolean{
        return userService.checkUsername(user)
    }

    @PostMapping("/check-email")
    open fun checkEmail(@RequestBody user: User): Boolean{
        return userService.checkEmail(user)
    }

    @PostMapping("/check-phone")
    open fun checkPhone(@RequestBody user: User): Boolean{
        return userService.checkPhone(user)
    }

    @PutMapping("/update/{id}", consumes = arrayOf(APPLICATION_JSON_UTF8_VALUE), produces = arrayOf(APPLICATION_JSON_UTF8_VALUE))
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'GUEST')")
    open fun update(@RequestBody user: User, response: HttpServletResponse, @PathVariable("id") id: Long): Any{
        var updatedUser: User = userService.update(user, id)
        if(updatedUser != null)
        {
            return ResponseEntity.ok(user)
        }
        else
        {
            throw RuntimeException("Error while updating!")
        }
    }

    /*
        *  We are not using userService.findByUsername here(we could),
        *  so it is good that we are making sure that the user has role "ROLE_USER"
        *  to access this endpoint.
        */
    @RequestMapping("/whoami")
    @PreAuthorize("hasAuthority('USER')")
    fun user(user: Principal): User {
        return this.userService!!.getByUsername(user.name)
    }

    companion object {
        const val ENTRY_POINT = "/users"
    }
}