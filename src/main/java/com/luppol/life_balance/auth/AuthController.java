package com.luppol.life_balance.auth;

import com.luppol.life_balance.auth.dto.AuthDto;
import com.luppol.life_balance.auth.dto.LoginDto;
import com.luppol.life_balance.auth.dto.PasswordChangeDto;
import com.luppol.life_balance.auth.service.IAuthService;
import com.luppol.life_balance.auth.service.IUserService;
import com.luppol.life_balance.dto.PersonReadDto;
import com.luppol.life_balance.auth.dto.UserCreateDto;
import com.luppol.life_balance.auth.dto.UserPasswordPutDto;
import com.luppol.life_balance.auth.dto.UserReadDto;
import com.luppol.life_balance.services.IPersonServiceRead;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequestMapping(AuthController.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication API")
public class AuthController {
    public static final String BASE_PATH = "/api/auth";

    private final IAuthService authService;
    private final IUserService userService;
    private final IPersonServiceRead personService;

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account and returns a JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered",
                    content = @Content(schema = @Schema(implementation = AuthDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Email/username already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthDto> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Registration payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserCreateDto.class))
            )
            @Valid @RequestBody UserCreateDto body
    ) {
        AuthDto token = userService.create(body);
        return ResponseEntity.created(URI.create(BASE_PATH + "/me")).body(token);
    }

    @Operation(
            summary = "Login",
            description = "Authenticates user by username or email and returns a JWT."
    )
    @ApiResponse(responseCode = "200", description = "Authenticated",
            content = @Content(schema = @Schema(implementation = AuthDto.class)))
    @PostMapping("/login")
    public ResponseEntity<AuthDto> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginDto.class))
            )
            @Valid @RequestBody LoginDto body
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
    public ResponseEntity<PersonReadDto> me(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(personService.getById(currentUser.pid()));
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
                    content = @Content(schema = @Schema(implementation = UserPasswordPutDto.class))
            )
            @Valid @RequestBody PasswordChangeDto body,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        authService.changePassword(currentUser.uid(), body);
        return ResponseEntity.ok().build();
    }
}
