package com.lbraz.meusgastosapi.security;

import com.lbraz.meusgastosapi.domain.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${auth.jwt.secret}")
    private String jwtSecret;

    @Value("${auth.jwt-expiration-milliseg}")
    private Long jwtExpirationMilliseg;

    public String gerarToken(Authentication authentication){

        // Ele pega a data atual e soma mais 1 dia em milissegundos
        Date dataExpiracao = new Date(new Date().getTime() + jwtExpirationMilliseg);

        // Aqui pegamos o usuário atual da autenticação
        Usuario usuario = (Usuario) authentication.getPrincipal();

        try {
            // Aqui gera uma chave com base na secret
            Key secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes("UTF-8"));

            // Aqui gera o token
            return Jwts.builder()
                    .setSubject(usuario.getUsername()) // Define o assunto do token (normalmente o nome de usuário)
                    .setIssuedAt(new Date()) // Define a data de emissão do token (atual)
                    .setExpiration(dataExpiracao) // Define a data de expiração do token
                    .signWith(secretKey) // Assina o token com a chave secreta
                    .compact(); // Compacta o token em uma string JWT
        } catch (Exception e){
            System.out.println(e.getMessage());
            return ""; // Em caso de exceção, retorna uma string vazia
        }
    }


    // Em resumo, o método verifica a assinatura e decodifica as informações do token JWT, retornando as claims correspondentes.
    private Claims getClaims(String token){
        try {
            // Aqui gera uma chave com base na secret
            Key secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes("UTF-8"));

            // Aqui faz o parsing e verifica a assinatura do token
            /*Fazer o parsing do token" refere-se ao processo de analisar e interpretar a estrutura do token JWT para
            extrair as informações contidas nele.*/
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey) // Define a chave secreta para validar a assinatura do token
                    .build()
                    .parseClaimsJws(token) // Faz o parsing do token
                    .getBody(); // Obtém as informações do corpo (claims) do token

            return claims; // Retorna as claims extraídas do token
        } catch (Exception e){
            e.printStackTrace(); // Imprime o rastreamento da exceção em caso de erro
            return null; // Em caso de exceção, retorna nulo
        }
    }

    // Método que pega o email do usuário dentro do token
    public String getUsername(String token){
        // Obtém as claims do token
        Claims claims = getClaims(token);

        // Verifica se as claims são nulas
        if (claims == null) {
            return null;
        }

        // Retorna o assunto (usuário) contido nas claims
        return claims.getSubject();
    }

    // Método que verifica se um token JWT é válido comparando a data atual com a data de expiração do token
    public boolean isValidToken(String token){

        // Obtém as claims do token
        Claims claims = getClaims(token);

        // Verifica se as claims são nulas
        if (claims == null) {
            return false;
        }

        // Obtém o email do usuário das claims
        String email = claims.getSubject();

        // Obtém a data de expiração do token das claims
        Date dataExpiracao = claims.getExpiration();

        // Obtém a data atual
        Date agora = new Date(System.currentTimeMillis());

        // Verifica se a data atual não é nula e é anterior à data de expiração
        if (agora != null && agora.before(dataExpiracao)) {
            return true; // O token é válido
        }

        return false; // O token é inválido
    }

}
