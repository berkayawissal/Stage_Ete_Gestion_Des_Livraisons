package com.example.demo.service;

import com.example.demo.auth.AuthenticationRequest;
import com.example.demo.auth.AuthenticationResponse;
import com.example.demo.auth.RegistrationRequest;
import com.example.demo.config.JwtService;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthenticationService {
  private final RoleRepository roleRepository;
  private final UserRepository repository;
  private final DistributeurRepository distributeurRepository;
  private final TokenRepository tokenRepository;
  private final PointDeVenteRepository venteRepository;
  private final EndUsersRepository endUsersRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final LivreurRepository livreurRepository;
  private final AdminRepository adminRepository;

  public AuthenticationResponse register(RegistrationRequest request) {
    Set<String> strRoles = request.getRoles();
    log.info("ROLES: " + (request.getRoles()));
      //System.err.println(strRoles[]);
    Set<Role> roles = new HashSet<>();
    //if role = "ADMIN" => adminRepository.save(request) :: dans le table admin
      if (strRoles == null) {
          Role userRole = roleRepository.findByName("USER")
                  .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
      } else {
          strRoles.forEach(role -> {

                  Role adminRole = roleRepository.findByName(role)
                          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);

          });
      }
      log.info("ROLES: " +(request.getRoles()));
    var  user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .fullname(request.getFullname())
            .build();
    user.setRoles(roles);

    if (strRoles.contains("LIVREUR")) {
      Livreur livreur = new Livreur();
      livreur.setEmail(request.getEmail());
      livreur.setPassword(passwordEncoder.encode(request.getPassword()));
      livreur.setFullname(request.getFullname());
     // user = livreur.getIdAdmin();
      livreurRepository.save(livreur);
    }
      if (strRoles.contains("DISTRIBUTEUR"))  {
          Distributeur distributeur = new Distributeur();
          distributeur.setEmail(request.getEmail());
          distributeur.setPassword(passwordEncoder.encode(request.getPassword()));
          distributeur.setFullname(request.getFullname());
         // user = distributeur.getIdAdmin();
      distributeurRepository.save(distributeur);
    }
    if (strRoles.contains("END_USER"))  {
      EndUsers endUsers = new EndUsers();
      endUsers.setEmail(request.getEmail());
      endUsers.setPassword(passwordEncoder.encode(request.getPassword()));
      endUsers.setFullname(request.getFullname());
      // user = distributeur.getIdAdmin();
      endUsersRepository.save(endUsers);
    }
      if (strRoles.contains("POINT_DE_VENTE")){
      PointDeVente pointDeVente = new PointDeVente();
      pointDeVente.setEmail(request.getEmail());
      pointDeVente.setPassword(passwordEncoder.encode(request.getPassword()));
      pointDeVente.setFullname(request.getFullname());
      //user = pointDeVente.getIdAdmin();
      venteRepository.save(pointDeVente);
    }
      if (strRoles.contains("ADMIN")){
      Admin admin = new Admin();
      admin.setPassword(passwordEncoder.encode(request.getPassword()));
      admin.setEmail(request.getEmail());
      admin.setFullname(request.getFullname());
      adminRepository.save(admin);
    }
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(savedUser);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    log.info("userEmail: " + request.getEmail() + request.getPassword());
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
   // saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
}
 