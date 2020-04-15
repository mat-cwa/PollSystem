package github.com.matcwa.model.entity;

import github.com.matcwa.model.TokenType;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Table(name = "Token", schema = "POLL")
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;
    @OneToOne
    @JoinColumn(name = "fk_user")
    private User owner;
    @Enumerated(EnumType.STRING)
    private TokenType type;
    private Date createdAt;
    private boolean isActive=true;
    private boolean isConfirmed;

    public Token() {
    }

    public Token(String value, User owner, TokenType type, boolean isActive) {
        this.value = value;
        this.owner = owner;
        this.type = type;
        this.createdAt = new Date();
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token token = (Token) o;
        return isActive() == token.isActive() &&
                isConfirmed() == token.isConfirmed() &&
                Objects.equals(getId(), token.getId()) &&
                Objects.equals(getOwner(), token.getOwner()) &&
                getType() == token.getType();
    }

}
