package br.com.hackathon.sus.prenatal_auth.domain.entities;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class User {
    private Integer id;
    private String name;
    private String email;
    private String login;
    private String password;
    private Date lastUpdateDate;
    private Address address;
    private Set<Role> roles = new HashSet<>();

    public User(String name, String email, String login, String password, Address address) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.address = address;
        this.lastUpdateDate = new Date();
    }

    public User(Integer id, String name, String email, String login, String password, Address address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.address = address;
        this.lastUpdateDate = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getLogin() { return login; }

    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public Date getLastUpdateDate() { return lastUpdateDate; }

    public void setLastUpdateDate(Date lastUpdateDate) { this.lastUpdateDate = lastUpdateDate; }

    public Address getAddress() { return address; }

    public void setAddress(Address address) { this.address = address; }

    public Set<Role> getRoles() { return roles; }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public boolean hasRole(String roleName) {
        return this.roles.stream()
                .anyMatch(role -> role.getAuthority().equals(roleName));
    }
}
