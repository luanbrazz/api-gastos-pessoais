package com.lbraz.meusgastosapi.security;

import com.lbraz.meusgastosapi.domain.model.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private JwtUtil jwtUtil;

    private UserDetailsSecurityServer userDetailsSecurityServer;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                                  UserDetailsSecurityServer userDetailsSecurityServer) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.userDetailsSecurityServer = userDetailsSecurityServer;
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
//            UsuarioResponseDto usuarioDto = usuarioService.obterPorEmail(email);

            // Mapeia o DTO do usuário para a entidade Usuario
//            Usuario usuario = modelMapper.map(usuarioDto, Usuario.class);


            Usuario usuario = (Usuario) userDetailsSecurityServer.loadUserByUsername(email);
            // Cria uma instância de UsernamePasswordAuthenticationToken com o usuário, nenhuma senha e as autoridades do usuário
            return new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        }

        return null;
    }
}
