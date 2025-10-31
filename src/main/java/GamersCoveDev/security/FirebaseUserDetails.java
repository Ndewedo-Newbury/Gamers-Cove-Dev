package GamersCoveDev.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class FirebaseUserDetails implements UserDetails {

    private final String uid;
    private final String email;
    private final String displayName;
    private final boolean emailVerified;

    public FirebaseUserDetails(String uid, String email, String displayName, boolean emailVerified) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.emailVerified = emailVerified;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return ""; // Firebase handles authentication, no password needed
    }

    @Override
    public String getUsername() {
        return uid;
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
        return true;
    }
}
