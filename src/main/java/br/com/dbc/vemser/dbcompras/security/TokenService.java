package br.com.dbc.vemser.dbcompras.security;
import br.com.dbc.vemser.dbcompras.entity.CargoEntity;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.service.UsuarioService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NoArgsConstructor;
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

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String expiration;

    private static final String KEY_CARGOS = "cargos";


    public String generateToken(UsuarioEntity usuarioEntity) {

        Date now = new Date();
        Date exp = new Date(now.getTime() + Long.parseLong(expiration));

        List<String> listCargos = usuarioEntity.getCargos()
                .stream()
                .map(CargoEntity::getName)
                .toList();

        String token = Jwts.builder()
                .setIssuer("dbccompras-api")
                .claim(Claims.ID, usuarioEntity.getIdUser())
                .claim(KEY_CARGOS, listCargos)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        return TokenAuthenticationFilter.BEARER + token;
    }


    public UsernamePasswordAuthenticationToken isValid(String token) {
        if(token == null) {
            return null;
        }

        Claims body = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        Integer idUsuario = body.get(Claims.ID, Integer.class);

        if(idUsuario != null) {
            List<String> cargos = body.get(KEY_CARGOS, List.class);

            List<SimpleGrantedAuthority> cargosAuthority = cargos.stream()
                    .map(SimpleGrantedAuthority::new).toList();

            return new UsernamePasswordAuthenticationToken(idUsuario, null, cargosAuthority);
        }
        return null;
    }
}