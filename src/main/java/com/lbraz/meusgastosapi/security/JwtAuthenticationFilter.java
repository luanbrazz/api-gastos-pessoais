package com.lbraz.meusgastosapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lbraz.meusgastosapi.common.ConversorData;
import com.lbraz.meusgastosapi.domain.model.ErrorResposta;
import com.lbraz.meusgastosapi.domain.model.Usuario;
import com.lbraz.meusgastosapi.dto.usuario.LoginRequestDto;
import com.lbraz.meusgastosapi.dto.usuario.LoginResponseDto;
import com.lbraz.meusgastosapi.dto.usuario.UsuarioResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private JwtUtil jwtUtil;

    @Autowired
    private ModelMapper modelMapper;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super();
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;

        setFilterProcessesUrl("/api/auth");
    }


    @Override // Valida se a autenticação está ok
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Lê os dados de login do usuário a partir do corpo da requisição
            LoginRequestDto login = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            // Cria um objeto de autenticação com as credenciais fornecidas
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    login.getEmail(), login.getSenha());

            // Realiza a autenticação chamando o AuthenticationManager
            Authentication auth = authenticationManager.authenticate(authToken);
            return auth;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Usuário ou senha inválido(s)");
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authResult) throws IOException {
        // Obtém o objeto Usuario a partir da autenticação bem-sucedida
        Usuario usuario = (Usuario) authResult.getPrincipal();

        // Gera um novo token JWT com base na autenticação bem-sucedida
        String token = jwtUtil.gerarToken(authResult);

/*        UsuarioResponseDto usuarioResponse = new UsuarioResponseDto();
        usuarioResponse.setId(usuario.getId());
        usuarioResponse.setEmail(usuario.getEmail());
        usuarioResponse.setNome(usuario.getNome());
        usuarioResponse.setFoto(usuario.getFoto());
        usuarioResponse.setDataCadastro(usuario.getDataCadastro());*/

        // Mapeia o objeto Usuario para UsuarioResponseDto
        UsuarioResponseDto usuarioResponse = modelMapper.map(usuario, UsuarioResponseDto.class);

        // Cria um objeto LoginResponseDto contendo o token e o usuário
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setToken("Bearer " + token);
        loginResponseDto.setUsuario(usuarioResponse);

        // Define o tipo de conteúdo da resposta como JSON
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        // Escreve a resposta na saída da resposta HTTP
        response.getWriter().write(new Gson().toJson(loginResponseDto));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        // Obtém a data e hora atual como uma string
        String dataHora = ConversorData.converterDateParaDataEHora(new Date());

        // Cria um objeto ErrorResposta contendo os detalhes do erro de autenticação
        ErrorResposta erro = new ErrorResposta(dataHora, 401, "Not Authorized", failed.getMessage());

        // Define o status da resposta como 401 (Não Autorizado)
        response.setStatus(401);

        // Define o tipo de conteúdo da resposta como JSON
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        // Escreve a resposta de erro na saída da resposta HTTP
        response.getWriter().write(new Gson().toJson(erro));
    }

}

