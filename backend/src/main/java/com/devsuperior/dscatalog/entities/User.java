package com.devsuperior.dscatalog.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tb_user")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String firstName;
	private String lastName;
	@Column(unique = true)
	private String email;
	private String password;

	// Set -> Não aceita repetições, por isso não é usado um List.
	//        É uma interface, por isso é instanciado com o HashSet, que é a implementação do Set.
	// @ManyToMany     -> Informa que muitos registros de uma entidade estão relacionados com muitos registros de outra entidade.
	// @JoinTable      -> Cria uma tabela normatizada entre dois objetos, definidos nos parâmetros "name", "joinColumns" e "inverseJoinColumns". 
	// FetchType.EAGER -> Força o carregamento das dependências da entidade. Nesse caso, ao instanciar um usuário os perfis serão carregados automaticamente.
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "tb_user_role", 
	           joinColumns = @JoinColumn(name = "user_id"),
	           inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	
	public User() {
		
	}
	
	public User(Long id, String firstName, String lastName, String email, String password) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(id, other.id);
	}
}