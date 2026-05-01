package com.fabulous.orchestrator.service;

import com.fabulous.orchestrator.dto.AuthResponse;
import com.fabulous.orchestrator.dto.LoginRequest;
import com.fabulous.orchestrator.dto.RegisterRequest;
import com.fabulous.orchestrator.model.User;
import com.fabulous.orchestrator.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * 1. Check if email/mobile already exists
     * 2. If not, create new User with encoded password
     * 3. Save to DB
     * 4. Return AuthResponse with JWT and user info
     */
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        logger.info("Inside register method of AuthService with email: {}", req.email());

        try {
            if (userRepository.existsByEmail(req.email()) || userRepository.existsByMobileNumber(req.mobileNumber())) {
                logger.error("Registration failed: email or mobile already exists for email: {}, mobile: {}", req.email(), req.mobileNumber());

                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "An account with this email/mobile already exists");
            }
            User user = new User();
            user.setFirstName(req.firstName());
            user.setLastName(req.lastName());
            user.setMobileNumber(req.mobileNumber());
            user.setEmail(req.email());
            user.setPasswordHash(passwordEncoder.encode(req.password()));
//            user.setRole(Role.CUSTOMER);
            userRepository.save(user);

            logger.info("User registered successfully with email: {}", req.email());

            return buildResponse(user);
        } catch (Exception e) {
            logger.error("Unexpected error during registration: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
        }
    }

    /**
     * 1. Look up user by email
     * 2. If not found, throw 401
     * 3. If found but disabled, throw 403
     * 4. If found and enabled, check password
     * 5. If password doesn't match, throw 401
     * 6. If password matches, return AuthResponse with JWT and user info
     */
    public AuthResponse login(LoginRequest req) {
        logger.info("Inside login method of AuthService with email: {}", req.email());

        try {
            User user = userRepository.findByEmail(req.email())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED, "Invalid email or password"));

            if (!user.isEnabled()) {
                logger.error("Login failed: account is disabled for email: {}", req.email());

                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is disabled");
            }

            if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
                logger.error("Login failed: invalid password for email: {}", req.email());

                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
            }

            logger.info("User logged in successfully with email: {}", req.email());

            return buildResponse(user);
        } catch (Exception e) {
            logger.error("Unexpected error during login: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
        }
    }

    /**
     * Helper method to build AuthResponse from User entity
     * 1. Generate JWT token for the user
     * 2. Create and return AuthResponse with token and user info
     * Note: This method assumes the user is already authenticated and valid
     * @param user
     * @return
     */
    private AuthResponse buildResponse(User user) {
        logger.info("Building AuthResponse for user with email: {}", user.getEmail());
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(
                token,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getRole().name(),
                jwtService.extractExpiration(token).getTime()
        );
    }
}
