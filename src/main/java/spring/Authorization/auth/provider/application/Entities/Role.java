package spring.Authorization.auth.provider.application.Entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import spring.Authorization.auth.provider.application.Entities.Permission;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static spring.Authorization.auth.provider.application.Entities.Permission.*;

@RequiredArgsConstructor
public enum Role {

    USER(Collections.emptySet()),

    ADMIN(
            Set.of(
                    // * these are objects of the permission enum
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            )
    ),
    MANAGER(
            Set.of(
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            )
    );
    @Getter
    private final Set<Permission> permissions;
//  This method converts the set of permissions associated with a role into a list of SimpleGrantedAuthority objects, which also takes in anothe object which is the role that hte permissions (SimpleGrantedAuthority objects) are associated to which Spring Security uses to enforce access control.
    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = getPermissions()
                .stream()
//  The purpose of this code is to convert the set of permissions associated with a role into a list of SimpleGrantedAuthority objects, which are used by Spring Security to represent authorities granted to the user.
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
//  This way, the list of authorities includes both the specific permissions granted to the user and the role of the user
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }


}
