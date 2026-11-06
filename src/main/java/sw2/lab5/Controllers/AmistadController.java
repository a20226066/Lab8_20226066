package sw2.lab5.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sw2.lab5.Entity.Solicitud;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.SolicitudRepository;
import sw2.lab5.Repository.UsuarioRepository;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/amistad")
public class AmistadController {

    @Autowired
    SolicitudRepository solicitudRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    // Pagina de amistades del usuario logueado (user y admin)
    @GetMapping("")
    public String amistades(Model model, HttpSession session) {
        Usuario yo = (Usuario) session.getAttribute("usuario");

        model.addAttribute("recibidas", solicitudRepository.findByReceiverAndStatus(yo, "pendiente"));
        model.addAttribute("enviadas", solicitudRepository.findBySenderAndStatus(yo, "pendiente"));
        model.addAttribute("amigos", solicitudRepository.listarAmigos(yo.getId_user()));

        // Usuarios a los que se les puede enviar solicitud:
        // activos, que no sea yo y sin solicitud pendiente/aceptada conmigo
        List<Integer> idsOcupados = new ArrayList<>();
        idsOcupados.add(yo.getId_user());
        for (Solicitud s : solicitudRepository.listarActivas(yo.getId_user())) {
            idsOcupados.add(s.getSender().getId_user());
            idsOcupados.add(s.getReceiver().getId_user());
        }
        List<Usuario> disponibles = new ArrayList<>();
        for (Usuario u : usuarioRepository.findAll()) {
            if (u.isActive() && !idsOcupados.contains(u.getId_user())) {
                disponibles.add(u);
            }
        }
        model.addAttribute("disponibles", disponibles);
        return "amistad/lista";
    }

    @PostMapping("/enviar")
    public String enviar(@RequestParam("idReceptor") int idReceptor, HttpSession session,
                         RedirectAttributes attr) {
        Usuario yo = (Usuario) session.getAttribute("usuario");
        Optional<Usuario> optReceptor = usuarioRepository.findById(idReceptor);

        if (idReceptor == yo.getId_user()) {
            attr.addFlashAttribute("msgError", "No puedes enviarte una solicitud a ti mismo");
        } else if (optReceptor.isEmpty() || !optReceptor.get().isActive()) {
            attr.addFlashAttribute("msgError", "El usuario no existe");
        } else if (solicitudRepository.contarActivas(yo.getId_user(), idReceptor) > 0) {
            attr.addFlashAttribute("msgError", "Ya existe una solicitud pendiente entre ustedes o ya son amigos");
        } else {
            Usuario receptor = optReceptor.get();
            // Si antes me la rechazaron, reutilizo esa fila (la tabla no permite duplicados)
            Solicitud solicitud = solicitudRepository.findBySenderAndReceiver(yo, receptor);
            if (solicitud == null) {
                solicitud = new Solicitud();
                solicitud.setSender(yo);
                solicitud.setReceiver(receptor);
            }
            solicitud.setStatus("pendiente");
            solicitud.setCreated_at(LocalDateTime.now());
            solicitudRepository.save(solicitud);
            attr.addFlashAttribute("msg", "Solicitud enviada a " + receptor.getFirstname() + " " + receptor.getLastname());
        }
        return "redirect:/amistad";
    }

    // Aceptar: solo quien recibio la solicitud
    @PostMapping("/aceptar")
    public String aceptar(@RequestParam("id") int id, HttpSession session, RedirectAttributes attr) {
        Usuario yo = (Usuario) session.getAttribute("usuario");
        Solicitud solicitud = buscarPendiente(id);

        if (solicitud != null && solicitud.getReceiver().getId_user() == yo.getId_user()) {
            solicitud.setStatus("aceptada");
            solicitudRepository.save(solicitud);
            attr.addFlashAttribute("msg", "Solicitud aceptada");
        } else {
            attr.addFlashAttribute("msgError", "Solo quien recibió la solicitud puede aceptarla");
        }
        return "redirect:/amistad";
    }

    // Rechazar: solo quien recibio la solicitud
    @PostMapping("/rechazar")
    public String rechazar(@RequestParam("id") int id, HttpSession session, RedirectAttributes attr) {
        Usuario yo = (Usuario) session.getAttribute("usuario");
        Solicitud solicitud = buscarPendiente(id);

        if (solicitud != null && solicitud.getReceiver().getId_user() == yo.getId_user()) {
            solicitud.setStatus("rechazada");
            solicitudRepository.save(solicitud);
            attr.addFlashAttribute("msg", "Solicitud rechazada");
        } else {
            attr.addFlashAttribute("msgError", "Solo quien recibió la solicitud puede rechazarla");
        }
        return "redirect:/amistad";
    }

    // Cancelar: solo quien envio la solicitud (se borra)
    @PostMapping("/cancelar")
    public String cancelar(@RequestParam("id") int id, HttpSession session, RedirectAttributes attr) {
        Usuario yo = (Usuario) session.getAttribute("usuario");
        Solicitud solicitud = buscarPendiente(id);

        if (solicitud != null && solicitud.getSender().getId_user() == yo.getId_user()) {
            solicitudRepository.delete(solicitud);
            attr.addFlashAttribute("msg", "Solicitud cancelada");
        } else {
            attr.addFlashAttribute("msgError", "Solo quien envió la solicitud puede cancelarla");
        }
        return "redirect:/amistad";
    }

    // Devuelve la solicitud solo si existe y sigue pendiente
    private Solicitud buscarPendiente(int id) {
        Optional<Solicitud> opt = solicitudRepository.findById(id);
        if (opt.isPresent() && opt.get().getStatus().equals("pendiente")) {
            return opt.get();
        }
        return null;
    }
}
