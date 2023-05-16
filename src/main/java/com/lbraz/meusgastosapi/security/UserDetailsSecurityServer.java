package com.lbraz.meusgastosapi.security;

import com.lbraz.meusgastosapi.domain.model.Usuario;
import com.lbraz.meusgastosapi.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailsSecurityServer implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> optUsuario = usuarioRepository.findByEmail(username);

        if (optUsuario.isEmpty()) {
            throw new UsernameNotFoundException("Usuário ou senha inválidos");
        }

        return optUsuario.get();
    }
}
