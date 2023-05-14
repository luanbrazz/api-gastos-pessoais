package com.lbraz.meusgastosapi.security;

import com.lbraz.meusgastosapi.domain.model.Usuario;
import com.lbraz.meusgastosapi.domain.service.UsuarioService;
import com.lbraz.meusgastosapi.dto.usuario.UsuarioResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ModelMapper modelMapper;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Obtém o cabeçalho "Authorization" da requisição
        String header = request.getHeader("Authorization");

        // Verifica se o cabeçalho existe e começa com "Bearer "
        if (header != null && header.startsWith("Bearer ")) {

            // Obtém a autenticação com base no token JWT
            UsernamePasswordAuthenticationToken auth = getAuthentication(header.substring(7));

            // Verifica se a autenticação é válida e está autenticada
            if (auth != null && auth.isAuthenticated()) {
                // Define a autenticação no contexto de segurança do Spring Security
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Continua o filtro na cadeia de filtros
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token){
        // Verifica se o token JWT é válido
        if (jwtUtil.isValidToken(token)) {
            // Obtém o email do usuário do token
            String email = jwtUtil.getUsername(token);

            // Obtém o usuário do banco de dados com base no email
            UsuarioResponseDto usuarioDto = usuarioService.obterPorEmail(email);

            // Mapeia o DTO do usuário para a entidade Usuario
            Usuario usuario = modelMapper.map(usuarioDto, Usuario.class);

            // Cria uma instância de UsernamePasswordAuthenticationToken com o usuário, nenhuma senha e as autoridades do usuário
            return new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        }

        return null;
    }
}
