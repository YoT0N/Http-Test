package edu.ilkiv.lab5.config;

/*
  @author Bodya
  @project lab5
  @class AuditorAware
  version 1.0.0
  @since 24.04.2025 - 21:03 
*/


import java.util.Optional;

public class AuditorAwareImpl implements org.springframework.data.domain.AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        //return Optional.of("admin");
        return Optional.of(System.getProperty("user.name"));
    }
}
