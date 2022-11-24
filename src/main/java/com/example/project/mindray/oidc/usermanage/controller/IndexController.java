package com.example.project.mindray.oidc.usermanage.controller;

import com.nimbusds.jose.shaded.json.JSONArray;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home")
public class IndexController {

    @GetMapping("/index")
    public String index() {
        return "Hello World !";
    }


    @GetMapping("/userinfo")
    public OidcUser getOidcUserPrincipal(@AuthenticationPrincipal OidcUser principal) {
        System.out.println("/index/userinfo/OidcUser:"+principal);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Map claimsMap = jwt.getClaims();
        Map realmMap = (Map)claimsMap.get("realm_access");

        List<GrantedAuthority> list = new ArrayList();
        JSONArray roleArry = (JSONArray)realmMap.get("roles");
        for(Object str:roleArry){
            String temp = str.toString();
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(temp);
            list.add(simpleGrantedAuthority);
        }

        OidcIdToken oidcIdToken = new OidcIdToken(jwt.getTokenValue(),jwt.getIssuedAt(),jwt.getExpiresAt(),jwt.getClaims());
        principal = new DefaultOidcUser(list,oidcIdToken);

        return principal;
    }
}
