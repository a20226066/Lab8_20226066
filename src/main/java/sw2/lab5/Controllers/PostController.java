package sw2.lab5.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sw2.lab5.Entity.Post;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.PostRepository;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    PostRepository postRepository;

    // Listar: todos pueden entrar (anonimo, user, admin)
    @GetMapping("")
    public String listaPost(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String rol = (String) session.getAttribute("rol");

        List<Post> lista;
        if (usuario == null) {
            lista = postRepository.findByPublishedTrue();              // anonimo
        } else if ("admin".equals(rol)) {
            lista = postRepository.findAll();                          // admin ve todo
        } else {
            lista = postRepository.findByPublishedTrueOrUser(usuario); // user
        }
        model.addAttribute("lista", lista);
        return "post/lista";
    }

    // Crear: user y admin (protegido en WebSecurityConfig)
    @GetMapping("/create")
    public String nuevoPost(Model model) {
        model.addAttribute("post", new Post());
        return "post/form";
    }

    // Editar: solo el autor del post
    @GetMapping("/edit")
    public String editarPost(Model model, @RequestParam("id") int id,
                             HttpSession session, RedirectAttributes attr) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Optional<Post> optPost = postRepository.findById(id);

        if (optPost.isPresent() && esAutor(optPost.get(), usuario)) {
            model.addAttribute("post", optPost.get());
            return "post/form";
        }
        attr.addFlashAttribute("msgError", "No puedes editar ese post");
        return "redirect:/post";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("post") @Valid Post post, BindingResult result,
                          HttpSession session, RedirectAttributes attr) {
        if (result.hasErrors()) {
            return "post/form";
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (post.getId_Post() == 0) {
            // Nuevo post: el autor es el usuario de la sesion
            post.setUser(usuario);
            postRepository.save(post);
            attr.addFlashAttribute("msg", "Post creado");
        } else {
            // Edicion: verificamos que el post exista y que sea suyo
            Optional<Post> optPost = postRepository.findById(post.getId_Post());
            if (optPost.isPresent() && esAutor(optPost.get(), usuario)) {
                post.setUser(optPost.get().getUser());
                postRepository.save(post);
                attr.addFlashAttribute("msg", "Post actualizado");
            } else {
                attr.addFlashAttribute("msgError", "No puedes editar ese post");
            }
        }
        return "redirect:/post";
    }

    // Borrar (POST): el admin borra cualquiera, el user solo los suyos
    @PostMapping("/borrar")
    public String borrar(@RequestParam("id") int id, HttpSession session, RedirectAttributes attr) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String rol = (String) session.getAttribute("rol");
        Optional<Post> optPost = postRepository.findById(id);

        if (optPost.isPresent() && ("admin".equals(rol) || esAutor(optPost.get(), usuario))) {
            postRepository.deleteById(id);
            attr.addFlashAttribute("msg", "Post eliminado");
        } else {
            attr.addFlashAttribute("msgError", "No puedes eliminar ese post");
        }
        return "redirect:/post";
    }

    private boolean esAutor(Post post, Usuario usuario) {
        return usuario != null && post.getUser().getId_user() == usuario.getId_user();
    }
}
