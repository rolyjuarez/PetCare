package bo.capital.tec.pet.common.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private boolean activo;
    private boolean bloqueado;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public boolean isEnabled() {
        return activo;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !bloqueado;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
