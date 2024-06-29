package com.example.api.user;

import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.exception.CustomException;
import com.example.request.PasswordRequest;
import com.example.request.UserRequest;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.response.ResponseError;
import com.example.response.UserResponse;
import com.example.service.RefreshTokenService;
import com.example.service.implement.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController("user")
@RequestMapping("/api/user")
public class ApiUser {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private RefreshTokenService refreshTokenService;

    // CALL SUCCESS
    @PutMapping(value = "/update/profile")
    public ResponseEntity<?> updateInformation(@RequestBody UserRequest userRequest) throws ResponseError {
        UserResponse userResponse = userService.updateInformationUser(userRequest);

        ResponseData<UserResponse> response = new ResponseData<>();
        response.setMessage("Updated information success!!!");
        response.setSuccess(true);
        response.setStatus(HttpStatus.CREATED.value());
        response.setResults(userResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PostMapping("/logout")
    public ResponseEntity<?> userLogout() throws CustomException {
        // delete refresh token in database
        String refreshTokenCode = jwtProvider.getRefreshTokenCodeFromCookie(request, CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_USER);
        refreshTokenService.deleteRefreshTokenByRefreshTokenCode(refreshTokenCode);

        // delete token and refresh token on cookie
        ResponseCookie cookie = jwtProvider.cleanTokenCookie(CookieConstant.JWT_COOKIE_USER);
        ResponseCookie refreshTokenCookie = jwtProvider.cleanRefreshTokenCodeCookie(CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_USER);

        Response response = new Response();
        response.setMessage("Logout success !!!");
        response.setSuccess(true);
        response.setStatus(HttpStatus.OK.value());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    // CALL SUCCESS
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordRequest passwordRequest) throws ResponseError {
        Response response = userService.changePasswordUser(passwordRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
