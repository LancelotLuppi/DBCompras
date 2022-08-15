package br.com.dbc.vemser.dbcompras.entity;

import br.com.dbc.vemser.dbcompras.enums.CargoUsuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "office")
public class CargoEntity implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_CARGO")
    @SequenceGenerator(name = "SEQ_ID_CARGO", sequenceName = "seq_id_office", allocationSize = 1)
    @Column(name = "id_office")
    private Integer idCargo;

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_office",
            joinColumns = @JoinColumn(name = "id_office"),
            inverseJoinColumns = @JoinColumn(name = "id_user")
    )
    private Set<UsuarioEntity> usuarios;

    @Override
    public String getAuthority() {
        return name;
    }



}
