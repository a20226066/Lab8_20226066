package sw2.lab5.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sw2.lab5.Entity.Solicitud;
import sw2.lab5.Entity.Usuario;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {

    // Solicitudes recibidas o enviadas segun su estado
    List<Solicitud> findByReceiverAndStatus(Usuario receiver, String status);

    List<Solicitud> findBySenderAndStatus(Usuario sender, String status);

    // Solicitud en una sola direccion (para reutilizar una rechazada)
    Solicitud findBySenderAndReceiver(Usuario sender, Usuario receiver);

    // Amistades aceptadas del usuario (las que envio y las que recibio)
    @Query(value = "SELECT * FROM friend_request WHERE status = 'aceptada' " +
            "AND (id_sender = ?1 OR id_receiver = ?1)", nativeQuery = true)
    List<Solicitud> listarAmigos(int idUsuario);

    // Cuenta solicitudes pendientes o aceptadas entre dos usuarios (en cualquier direccion)
    @Query(value = "SELECT COUNT(*) FROM friend_request WHERE status <> 'rechazada' " +
            "AND ((id_sender = ?1 AND id_receiver = ?2) OR (id_sender = ?2 AND id_receiver = ?1))",
            nativeQuery = true)
    int contarActivas(int idUsuario1, int idUsuario2);

    // Todas las solicitudes pendientes o aceptadas donde participa el usuario
    @Query(value = "SELECT * FROM friend_request WHERE status <> 'rechazada' " +
            "AND (id_sender = ?1 OR id_receiver = ?1)", nativeQuery = true)
    List<Solicitud> listarActivas(int idUsuario);
}
