package com.luppol.life_balance.auth;

import com.luppol.life_balance.auth.dto.*;
import com.luppol.life_balance.auth.service.IAuthService;
import com.luppol.life_balance.auth.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(AuthController.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication API")
public class AuthController {
    public static final String BASE_PATH = "/api/auth";

    private final IAuthService authService;
    private final IUserService userService;


    @Operation(
            summary = "Register new user",
            description = "Creates a new user account and returns a JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered",
                    content = @Content(schema = @Schema(implementation = AuthTokensDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Email/username already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthTokensDto> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Registration payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserCreateDto.class))
            )
            @Valid @RequestBody AuthRegisterDto body
    ) {
        authService.register(body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Login",
            description = "Authenticates user by username or email and returns a JWT."
    )
    @ApiResponse(responseCode = "200", description = "Authenticated",
            content = @Content(schema = @Schema(implementation = AuthTokensDto.class)))
    @PostMapping("/login")
    public ResponseEntity<AuthTokensDto> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthLoginDto.class))
            )
            @Valid @RequestBody AuthLoginDto body
    ) {
        return ResponseEntity.ok(authService.login(body));
    }

    @Operation(
            summary = "Current user",
            description = "Returns the profile of the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current user",
                    content = @Content(schema = @Schema(implementation = UserReadDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me")
    public ResponseEntity<UserReadDto> me(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(userService.getById(currentUser.uid()));
    }

    @Operation(
            summary = "Change password",
            description = "Changes password for the authenticated user (requires current password)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed"),
            @ApiResponse(responseCode = "400", description = "Weak password / invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Current password incorrect")
    })
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Password change payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthPasswordChangeDto.class))
            )
            @Valid @RequestBody AuthPasswordChangeDto body,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        authService.changePassword(currentUser.uid(), body);
        return ResponseEntity.ok().build();
    }
}
