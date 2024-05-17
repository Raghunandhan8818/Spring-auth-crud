package org.example.simplecrudapp.service;


import jakarta.servlet.http.HttpServletResponse;
import org.example.simplecrudapp.dto.ReqRes;
import org.example.simplecrudapp.entity.User;
import org.example.simplecrudapp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserManagementService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private HttpServletResponse httpServletResponse;

    public ReqRes register(ReqRes registrationRequest) {
        System.out.println("In SERVICE");
        System.out.println(registrationRequest);
        ReqRes response = new ReqRes();
        try {
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setName(registrationRequest.getName());
            user.setRole(registrationRequest.getRole());
            user.setCity(registrationRequest.getCity());
            User savedUser = userRepo.save(user);
            if (savedUser.getId() > 0) {
                response.setUser(savedUser);
                response.setMessage("User registered successfully");
                response.setStatusCode(200);
            }
            return response;
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }


    public ReqRes login(ReqRes loginRequest) {
        ReqRes response = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = userRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24 Hrs");
            response.setMessage("User logged in successfully");
            return response;

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }


    public ReqRes refreshToken(ReqRes refreshTokenRequest) {
        ReqRes response = new ReqRes();
        try {
            String ourEmail = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            User user = userRepo.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), user)) {
                var jwt = jwtUtils.generateToken(user);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            return response;

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    public ReqRes getAllUsers() {
        ReqRes response = new ReqRes();

        try {
            List<User> result = userRepo.findAll();
            if (!result.isEmpty()) {
                response.setUsers(result);
                response.setStatusCode(200);
                response.setMessage("Successful");
            } else {
                response.setStatusCode(404);
                response.setMessage("No users found");
            }
            return response;
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred: " + e.getMessage());
            return response;
        }
    }


    public ReqRes getUsersById(Integer id) {
        ReqRes response = new ReqRes();
        try {
            User usersById = userRepo.findById(Long.valueOf(id)).orElseThrow(() -> new RuntimeException("User Not found"));
            response.setUser(usersById);
            response.setStatusCode(200);
            response.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred: " + e.getMessage());
        }
        return response;
    }


    public ReqRes deleteUser(Integer userId) {
        ReqRes response = new ReqRes();
        try {
            Optional<User> userOptional = userRepo.findById(Long.valueOf(userId));
            if (userOptional.isPresent()) {
                userRepo.deleteById(Long.valueOf(userId));
                response.setStatusCode(200);
                response.setMessage("User deleted successfully");
            } else {
                response.setStatusCode(404);
                response.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return response;
    }

    public ReqRes updateUser(Integer userId, User updatedUser) {
        ReqRes response = new ReqRes();
        try {
            Optional<User> userOptional = userRepo.findById(Long.valueOf(userId));
            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setRole(updatedUser.getRole());

                // Check if password is present in the request
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    // Encode the password and update it
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                User savedUser = userRepo.save(existingUser);
                response.setUser(savedUser);
                response.setStatusCode(200);
                response.setMessage("User updated successfully");
            } else {
                response.setStatusCode(404);
                response.setMessage("User not found for update");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return response;
    }

    public ReqRes getMyInfo(String email){
        ReqRes response = new ReqRes();
        try {
            Optional<User> userOptional = userRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                response.setUser(userOptional.get());
                response.setStatusCode(200);
                response.setMessage("successful");
            } else {
                response.setStatusCode(404);
                response.setMessage("User not found for update");
            }

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return response;

    }
}




