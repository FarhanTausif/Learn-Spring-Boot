package com.example.RBACwithSpringSecurity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Getter
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    private String firstName;
    private String lastName;

    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;

    // Store roles as comma-separated string to avoid @ManyToMany performance issues
    @Column(name = "roles")
    private String rolesString = "USER"; // Default role

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (rolesString == null || rolesString.trim().isEmpty()) {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return Arrays.stream(rolesString.split(","))
            .map(String::trim)
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList());
    }

    // Helper methods for role management
    public List<String> getRoles() {
        if (rolesString == null || rolesString.trim().isEmpty()) {
            return List.of("USER");
        }
        return Arrays.stream(rolesString.split(","))
            .map(String::trim)
            .collect(Collectors.toList());
    }

    public void setRoles(List<String> roles) {
        this.rolesString = String.join(",", roles);
    }

    public void addRole(String role) {
        List<String> currentRoles = getRoles();
        if (!currentRoles.contains(role)) {
            currentRoles.add(role);
            setRoles(currentRoles);
        }
    }

    public void removeRole(String role) {
        List<String> currentRoles = getRoles();
        currentRoles.remove(role);
        setRoles(currentRoles);
    }

    public boolean hasRole(String role) {
        return getRoles().contains(role);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
