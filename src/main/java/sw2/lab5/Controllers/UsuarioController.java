package sw2.lab5.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.UsuarioRepository;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    // Solo admin (protegido en WebSecurityConfig)
    @GetMapping("/list")
    public String listaUsuarios(Model model) {
        model.addAttribute("listausuarios", usuarioRepository.findAll());
        return "user/list";
    }

    // Mi perfil: user y admin, siempre el usuario de la sesion
    @GetMapping("/perfil")
    public String perfil(Model model, HttpSession session) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        model.addAttribute("usuario", usuarioRepository.findById(usuarioSesion.getId_user()).get());
        return "user/perfil";
    }

    // Guarda solo nombre, apellido, intro y perfil del usuario logueado
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("usuario") Usuario form, HttpSession session,
                          RedirectAttributes attr) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        Usuario usuario = usuarioRepository.findById(usuarioSesion.getId_user()).get();

        usuario.setFirstname(form.getFirstname());
        usuario.setLastname(form.getLastname());
        usuario.setIntro(form.getIntro());
        usuario.setProfile(form.getProfile());
        usuarioRepository.save(usuario);

        session.setAttribute("usuario", usuario); // actualiza el nombre en la navbar
        attr.addFlashAttribute("msg", "Perfil actualizado");
        return "redirect:/user/perfil";
    }
}
