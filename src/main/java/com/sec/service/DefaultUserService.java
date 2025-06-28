package com.sec.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.sec.dto.UserRegisteredDTO;
import com.sec.model.User;


public interface DefaultUserService extends UserDetailsService{

	User save(UserRegisteredDTO userRegisteredDTO);

	String generateOtp(User user);



	
}
