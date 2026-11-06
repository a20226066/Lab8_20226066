package sw2.lab5.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sw2.lab5.Entity.Post;
import sw2.lab5.Entity.Usuario;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    // Para visitantes: solo los posts publicados
    List<Post> findByPublishedTrue();

    // Para un usuario: los publicados + sus propios borradores
    List<Post> findByPublishedTrueOrUser(Usuario user);
}
