package com.resume.backend.services;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.resume.backend.entity.UserEntity;
import com.resume.backend.repository.UserRepository;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	UserRepository userRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
   // @Cacheable(value = "userDetails", key = "#username")
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		UserEntity userEntity = userRepository.findByUserNameCaseSensitive(username);
        String password;
         if(userEntity == null) {
        	 throw new UsernameNotFoundException(username);
         }
         //User userEntity = findByUserName.get(0);
        // Collection<GrantedAuthority> authorities = new ArrayList();
        // String roles =userEntity.getRoles();
//		String roles = userEntity.getRoles().stream().map(role -> role.getRoleName()).collect(Collectors.joining(","));
//		String[] rolesArray = roles.split(",");
//         for(String role: rolesArray) {
//        	 GrantedAuthority authority= new SimpleGrantedAuthority(role.trim());
//        	 authorities.add(authority);
//         }
	//	System.out.println("Password encoder");
	//	System.out.println(passwordEncoder.encode("admin123"));
		//System.out.println("Base64");

	//	System.out.println(Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded()));

        if(userEntity.getPassword()==null || userEntity.getPassword().isEmpty()){
            password = "{noop}OAUTH2_USER";
        }else{
            password = userEntity.getPassword();
        }
		List<SimpleGrantedAuthority> authorities = userEntity.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getRoleName())) // ROLE_ADMIN
				.toList();
		UserDetails user = new User(userEntity.getUsername(),password,authorities);
		return user;
	}

}
