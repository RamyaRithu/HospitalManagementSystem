package com.project.patientmanage.service;


import com.project.patientmanage.Rrepository.UserRepository;
import com.project.patientmanage.dto.UserRegistrationDto;
import com.project.patientmanage.model.Role;
import com.project.patientmanage.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements  UserService{
    //repository layer
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User save(UserRegistrationDto registrationDto) {
        //create new user object with registration dto
        System.out.println("email:"+registrationDto.getEmail());
        System.out.println("password "+registrationDto.getPassword());
        User user=new User(
                registrationDto.getFirstName(),
                registrationDto.getLastName(),
                passwordEncoder.encode(registrationDto.getPassword()),
                registrationDto.getEmail(),
                Arrays.asList(new Role("ROLE_USER")));

        return userRepository.save(user);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if(user==null){
            throw new UsernameNotFoundException("Invalid username or password");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),
                mapRolesAuthorites(user.getRoles()));
    }


    private Collection<? extends GrantedAuthority> mapRolesAuthorites(Collection<Role> roles){
        return  roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

}
