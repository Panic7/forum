package com.example.forum.security;

import com.example.forum.model.User;
import com.example.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        if(username.contains("@")) {
            user = userRepository.findByEmail(username).orElseThrow(() -> {
                throw new UsernameNotFoundException("User Not Found with email " + username);
            });
        } else {
            user = userRepository.findByUsername(username).orElseThrow(() -> {
                throw new UsernameNotFoundException("User Not Found with username " + username);
            });
        }

        return UserDetailsImpl.create(user);
    }
}
