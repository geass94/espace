package ge.boxwood.espace.controllers;

import ge.boxwood.espace.ErrorStalker.StepLoggerService;
import ge.boxwood.espace.config.providers.DeviceProvider;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.models.UserTokenState;
import ge.boxwood.espace.security.TokenHelper;
import ge.boxwood.espace.security.auth.JwtAuthenticationRequest;
import ge.boxwood.espace.services.UserService;
import ge.boxwood.espace.services.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// "/auth" this is route group
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AuthenticationController {

    @Autowired
    TokenHelper tokenHelper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private StepLoggerService stepLoggerService;


    //    "/login" this goes after /auth and redirects to login method
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest,
            HttpServletResponse response,
            Device device
    ) throws AuthenticationException, IOException {

        // Perform the security
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        // Inject into security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // token creation
        User user = (User) authentication.getPrincipal();
        String jws = tokenHelper.generateToken(user, device);
        String jwsr = tokenHelper.generateRefreshToken(user, device);

        int expiresIn = tokenHelper.getExpiredIn(device);
        // Return the token
        UserTokenState userTokenState = new UserTokenState(jws, jwsr, expiresIn, user.getSmsActive() || user.getEmailActive());
        HashMap params = new HashMap();
        params.put("token", userTokenState.getAccess_token());
        params.put("refresh_token", userTokenState.getRefresh_token());
        params.put("isUserActive", userTokenState.isUserActivated());
        params.put("token_expires_in", userTokenState.getExpires_in());
        stepLoggerService.logStep("AuthenticationController", "login", params);
        return ResponseEntity.ok(userTokenState);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<?> refreshAuthenticationToken(
            HttpServletRequest request,
            Device device
    ) {
        String authToken = tokenHelper.getToken(request);
        String username = tokenHelper.getUsernameFromToken(authToken);
        User user = (User) userDetailsService.loadUserByUsername(username);
        if (authToken != null && user != null) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                    user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jws = tokenHelper.generateToken(user, device);
            String jwsr = tokenHelper.generateRefreshToken(user, device);
            UserTokenState userTokenState = new UserTokenState(jws, jwsr, tokenHelper.getExpiredIn(device), user.getSmsActive() || user.getEmailActive());
            HashMap params = new HashMap();
            params.put("token", userTokenState.getAccess_token());
            params.put("refresh_token", userTokenState.getRefresh_token());
            params.put("isUserActive", userTokenState.isUserActivated());
            params.put("token_expires_in", userTokenState.getExpires_in());
            stepLoggerService.logStep("AuthenticationController", "refresh IF", params);
            return ResponseEntity.ok(userTokenState);
        } else {
            UserTokenState userTokenState = new UserTokenState();
            HashMap params = new HashMap();
            params.put("token", userTokenState.getAccess_token());
            params.put("refresh_token", userTokenState.getRefresh_token());
            params.put("isUserActive", userTokenState.isUserActivated());
            params.put("token_expires_in", userTokenState.getExpires_in());
            stepLoggerService.logStep("AuthenticationController", "refresh ELSE", params);
            return ResponseEntity.accepted().body(userTokenState);
        }
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    public ResponseEntity<?> changePassword(@RequestBody PasswordChanger passwordChanger) {
        userDetailsService.changePassword(passwordChanger.oldPassword, passwordChanger.newPassword);
        Map<String, String> result = new HashMap<>();
        result.put("result", "success");
        return ResponseEntity.accepted().body(result);
    }

    static class PasswordChanger {
        public String oldPassword;
        public String newPassword;
    }
}