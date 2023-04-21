package com.example.demo.service;

import com.example.demo.model.DTOs.LoginDTO;
import com.example.demo.model.DTOs.UserRegisterDataDTO;
import com.example.demo.model.DTOs.UserWithoutPassDTO;
import com.example.demo.model.entities.User;
import com.example.demo.model.exceptions.BadRequestException;
import com.example.demo.model.exceptions.NotFoundException;
import com.example.demo.model.exceptions.UnauthorizedException;
import com.example.demo.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;



    public UserWithoutPassDTO register(UserRegisterDataDTO registerData){
        if (!registerData.getFullName().matches("^[A-Z][a-z]+(\\s[A-Z][a-z]+)?$")){
            throw new BadRequestException("Invalid full name!");
        }
        if (userRepository.existsByEmail(registerData.getEmail())){
            throw new BadRequestException("Email already exists!");
        }
        if (!registerData.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")){
            throw new BadRequestException("Invalid email!");
        }
        if (registerData.getPassword().length()<6){
            throw new BadRequestException("Pass should be at least 6 chars long.");
        }
        if (!String.valueOf(registerData.getAge()).matches("^([1-9]|[1-9][0-9]|1[0-4][0-9]|150)$")){
            throw new BadRequestException("Invalid age!");
        }
        String verCode = UUID.randomUUID().toString();
        User u = mapper.map(registerData,User.class);
        u.setRegisteredAt(LocalDateTime.now());
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        u.setVerCode(verCode);
        userRepository.save(u);
        new Thread(() -> sendVerificationCode(registerData.getEmail(),verCode)).start();
        return mapper.map(u,UserWithoutPassDTO.class);
    }
    public UserWithoutPassDTO verify(String code){
        Optional<User> opt = userRepository.getByVerCode(code);
        if (!opt.isPresent()){
            throw new BadRequestException("Incorrect code!");
        }
        User u = opt.get();
        if (u.isVerified()){
            throw new BadRequestException("Already verified!");
        }
        u.setVerified(true);
        userRepository.save(u);
        return mapper.map(u,UserWithoutPassDTO.class);
    }
    public UserWithoutPassDTO login(LoginDTO dto) {
        Optional<User> opt = userRepository.getByEmail(dto.getEmail());

        if (opt.isEmpty()){
            throw new UnauthorizedException("Wrong credentials");
        }
        if(!passwordEncoder.matches(dto.getPassword(), opt.get().getPassword())){
            throw new UnauthorizedException("Wrong credentials");
        }
        if (!opt.get().isVerified()){
            throw new UnauthorizedException("Wrong credentials");
        }
        if (opt.get().isBanned()){
            throw new UnauthorizedException("You've been banned!");
        }
        User u = opt.get();
        u.setLastLoginTime(LocalDateTime.now());
        userRepository.save(u);
        return mapper.map(u, UserWithoutPassDTO.class);
    }
    public UserWithoutPassDTO banUnban(int bannableUserId,int sessionId){
        Optional<User> admin = userRepository.findById(sessionId);
        if (!admin.get().isAdmin()) {
            throw new UnauthorizedException("No permission!");
        }
        Optional<User> bannableUser = userRepository.findById(bannableUserId);
        if (bannableUser.isEmpty()) {
            throw new NotFoundException("User doesn't exist!");
        }
        if (bannableUser.get().isAdmin()) {
            throw new UnauthorizedException("No permission!");
        }
        bannableUser.get().setBanned(!bannableUser.get().isBanned());
        userRepository.save(bannableUser.get());
        return mapper.map(bannableUser.get(),UserWithoutPassDTO.class);
    }


    private void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Account Verification");
        message.setText("Click the following link to verify your account: http://localhost:7373/users/verify?code=" + code);
        mailSender.send(message);
    }
    @Scheduled(fixedRate = 60000)
    public void deleteUnverifiedUsers() {
        long thresholdInSeconds = 900;
        LocalDateTime thresholdTime = LocalDateTime.now().minusSeconds(thresholdInSeconds);
        List<User> unverifiedUsers = userRepository.findUnverifiedUsersRegisteredBefore(thresholdTime);
        userRepository.deleteAll(unverifiedUsers);
    }




}
