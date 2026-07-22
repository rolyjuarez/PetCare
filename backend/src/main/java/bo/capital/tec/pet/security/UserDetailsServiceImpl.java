package bo.capital.tec.pet.security;

import bo.capital.tec.pet.usuario.entity.Usuario;
import bo.capital.tec.pet.usuario.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioMapper usuarioMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioMapper.findByUsername(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        List<String> roles = usuarioMapper.findRolesByUsuarioId(usuario.getId());
        List<String> permissions = usuarioMapper.findPermissionsByUsuarioId(usuario.getId());
        return CustomUserDetails.build(usuario, roles, permissions);
    }
}
