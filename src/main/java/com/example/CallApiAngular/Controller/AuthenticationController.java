package com.example.CallApiAngular.Controller;


import com.example.CallApiAngular.DTO.Request.ApiResponse;
import com.example.CallApiAngular.DTO.Request.AuthenticationRequest;
import com.example.CallApiAngular.DTO.Request.IntrospectRequest;
import com.example.CallApiAngular.DTO.Response.AuthenticationResponse;
import com.example.CallApiAngular.DTO.Response.IntrospectResponse;
import com.example.CallApiAngular.Service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    private final  AuthenticationService authenticationService;

    @PostMapping("/log-in")//Check usename và password, nếu đúngđúng sẽ trả về true và 1 token
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        var result= authenticationService.authenticate(request);
       return  ApiResponse.<AuthenticationResponse>builder()
               .result(result)
               .build();
    }
    @PostMapping("/introspect")//Kiểm tra token với mật khẩu
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result= authenticationService.introspect(request);
        return  ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }
}
