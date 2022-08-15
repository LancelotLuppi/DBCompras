package br.com.dbc.vemser.dbcompras.security;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.service.UsuarioService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final UsuarioService usuarioService;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String expiration;

    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String KEY_CARGOS = "cargos";


    public String generateToken(UsuarioEntity usuarioEntity) {

        Date now = new Date();
        Date exp = new Date(now.getTime() + Long.parseLong(expiration));

        List<String> listCargos = List.of(usuarioEntity.getCargos().getName().getRole());

        String token = Jwts.builder()
                .setIssuer("dbccompras-api")
                .claim(Claims.ID, usuarioEntity.getIdUser())
                .claim(KEY_CARGOS, listCargos)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        return TOKEN_PREFIX + token;
    }


    public UsernamePasswordAuthenticationToken isValid(String token){
        if(token == null) {
            return null;
        }
        Claims body = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody();

        Integer idUsuario = body.get(Claims.ID, Integer.class);

        if(idUsuario != null){
            List<String> cargos = body.get(KEY_CARGOS, List.class);
            System.out.println(cargos);
            List<SimpleGrantedAuthority> cargosGrantedAuthority = cargos.stream()
                    .map(cargo -> new SimpleGrantedAuthority(cargo))
                    .toList();
            return new UsernamePasswordAuthenticationToken(
                    idUsuario,
                    null,
                    cargosGrantedAuthority
            );
        }
        return null;
    }
}