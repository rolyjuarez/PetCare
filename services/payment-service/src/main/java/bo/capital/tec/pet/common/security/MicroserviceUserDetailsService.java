package bo.capital.tec.pet.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MicroserviceUserDetailsService implements UserDetailsService {

    private final SecurityMapper securityMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioAuth usuario = securityMapper.selectUsuarioByUsername(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        List<String> roles = securityMapper.selectRolesByUsuarioId(usuario.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .map(a -> (SimpleGrantedAuthority) a)
                .toList();
        return new CustomUserDetails(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getPassword(),
                Boolean.TRUE.equals(usuario.getActivo()),
                Boolean.TRUE.equals(usuario.getBloqueado()),
                authorities);
    }
}
