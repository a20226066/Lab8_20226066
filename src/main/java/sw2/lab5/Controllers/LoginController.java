package sw2.lab5.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.UsuarioRepository;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    // Spring Security llega aqui despues de un login correcto
    @GetMapping("/redirectByRol")
    public String redirectByRol(Authentication auth, HttpSession session) {
        String rol = "";
        for (GrantedAuthority role : auth.getAuthorities()) {
            rol = role.getAuthority();
            break;
        }

        // Guardamos el usuario y su rol en la sesion
        Usuario usuario = usuarioRepository.findByEmail(auth.getName());
        session.setAttribute("usuario", usuario);
        session.setAttribute("rol", rol);

        return "redirect:/post";
    }
}
