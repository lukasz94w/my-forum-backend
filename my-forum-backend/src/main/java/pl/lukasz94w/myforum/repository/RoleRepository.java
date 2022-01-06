package pl.lukasz94w.myforum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lukasz94w.myforum.model.Role;
import pl.lukasz94w.myforum.model.enums.EnumeratedRole;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByEnumeratedRole(EnumeratedRole name);
}
