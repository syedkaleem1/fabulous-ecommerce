package com.fabulous.orchestrator.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CUSTOMER;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() { createdAt = Instant.now(); }

    public Long getId()                      { return id; }
    public String getFirstName()             { return firstName; }
    public void setFirstName(String v)       { this.firstName = v; }
    public String getLastName()              { return lastName; }
    public void setLastName(String v)        { this.lastName = v; }
    public String getMobileNumber()         { return mobileNumber; }
    public void setMobileNumber(String v)   { this.mobileNumber = v; }
    public String getEmail()                 { return email; }
    public void setEmail(String v)           { this.email = v; }
    public String getPasswordHash()          { return passwordHash; }
    public void setPasswordHash(String v)    { this.passwordHash = v; }
    public Role getRole()                    { return role; }
    public void setRole(Role v)              { this.role = v; }
    public boolean isEnabled()               { return enabled; }
    public void setEnabled(boolean v)        { this.enabled = v; }
    public Instant getCreatedAt()            { return createdAt; }
}
