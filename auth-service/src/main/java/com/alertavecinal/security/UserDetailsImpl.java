package com.alertavecinal.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.alertavecinal.entity.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String rol;
    private Boolean enabled;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(Usuario usuario) {
        String rol = usuario.getRol() != null ? usuario.getRol() : "ROLE_VECINO";
        GrantedAuthority authority = new SimpleGrantedAuthority(rol);
        boolean activo = usuario.getActivo() != null ? usuario.getActivo() : true;
        return new UserDetailsImpl(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getPassword(),
                rol,
                activo,
                Collections.singletonList(authority));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return enabled != null ? enabled : true; }
}
