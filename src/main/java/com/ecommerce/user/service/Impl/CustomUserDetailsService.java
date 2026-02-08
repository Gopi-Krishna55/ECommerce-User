package com.ecommerce.user.service.Impl;

import com.ecommerce.user.entity.UserDetailsEntity;
import com.ecommerce.user.model.SecurityUserDetails;
import com.ecommerce.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserDetailsEntity user =
                userRepository.findByUserName(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "User not found: " + username));

        // ✅ Wrap entity → security user
        return new SecurityUserDetails(user);
    }
}
