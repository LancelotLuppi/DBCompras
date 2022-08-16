package br.com.dbc.vemser.dbcompras.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "usuario")
public class UsuarioEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USER")
    @SequenceGenerator(name = "SEQ_USER", sequenceName = "seq_id_user", allocationSize = 1)
    @Column(name = "id_user", insertable = false, updatable = false)
    private Integer idUser;

    @Column(name = "name")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "enable")
    private boolean enable;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        mappedBy = "usuario")
    public Set<CompraEntity> compras;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "usuario")
    public Set<CotacaoEntity> cotacoes;



    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_office",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_office")
    )
    private Set<CargoEntity> cargos;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return cargos;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }
}
