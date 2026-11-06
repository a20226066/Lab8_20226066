package sw2.lab5.Entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "friend_request")
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_request;

    @ManyToOne
    @JoinColumn(name = "id_sender", nullable = false)
    private Usuario sender;      // quien envia

    @ManyToOne
    @JoinColumn(name = "id_receiver", nullable = false)
    private Usuario receiver;    // quien recibe

    @Column(nullable = false)
    private String status;       // pendiente, aceptada, rechazada

    @Column(nullable = false)
    private LocalDateTime created_at;

    public int getId_request() {
        return id_request;
    }

    public void setId_request(int id_request) {
        this.id_request = id_request;
    }

    public Usuario getSender() {
        return sender;
    }

    public void setSender(Usuario sender) {
        this.sender = sender;
    }

    public Usuario getReceiver() {
        return receiver;
    }

    public void setReceiver(Usuario receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}
