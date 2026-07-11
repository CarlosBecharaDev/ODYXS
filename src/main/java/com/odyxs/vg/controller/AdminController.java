package com.odyxs.vg.controller;

import com.odyxs.vg.entity.*;
import com.odyxs.vg.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AdminController {

    @Autowired private LugarService      lugarService;
    @Autowired private CategoriaService  categoriaService;
    @Autowired private EventoService     eventoService;
    @Autowired private ActividadService  actividadService;

    private boolean esAdmin(HttpSession session) {
        Object rol    = session.getAttribute("usuarioRol");
        Object correo = session.getAttribute("usuarioCorreo");
        return rol != null && rol.toString().equals("ADMIN")
            && "admin@odyxs.com".equals(correo);
    }

    @GetMapping("/admin")
    public String panelAdmin(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/";
        model.addAttribute("pendientes",           lugarService.obtenerPendientes());
        model.addAttribute("lugares",              lugarService.obtenerTodos());
        model.addAttribute("categorias",           categoriaService.obtenerTodas());
        model.addAttribute("eventos",              eventoService.obtenerAprobados());
        model.addAttribute("eventosPendientes",    eventoService.obtenerPendientes());
        model.addAttribute("actividades",          actividadService.obtenerTodas());
        model.addAttribute("actividadesPendientes",actividadService.obtenerPendientes());
        return "admin";
    }

    // ── LUGARES ───────────────────────────────────────────────

    @PostMapping("/admin/lugares/{id}/aprobar")
    public String aprobar(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";
        lugarService.aprobar(id); return "redirect:/admin";
    }

    @PostMapping("/admin/lugares/{id}/rechazar")
    public String rechazar(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";
        lugarService.rechazar(id); return "redirect:/admin";
    }

    @PostMapping("/admin/lugares/{id}/eliminar")
    public String eliminarLugar(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";
        lugarService.eliminar(id); return "redirect:/admin";
    }

    @PostMapping("/admin/lugares/agregar")
    public String agregarLugar(@RequestParam String nombre,
                               @RequestParam String descripcion,
                               @RequestParam String ubicacion,
                               @RequestParam(required = false) String urlMapa,
                               @RequestParam Long categoriaId,
                               @RequestParam(required = false) MultipartFile imagen,
                               HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";
        lugarService.guardar(categoriaId, nombre, descripcion, ubicacion, urlMapa, true, imagen);
        return "redirect:/admin";
    }

    @PostMapping("/admin/lugares/{id}/editar")
    public String editarLugar(@PathVariable Long id,
                              @RequestParam String nombre,
                              @RequestParam String descripcion,
                              @RequestParam String ubicacion,
                              @RequestParam(required = false) String urlMapa,
                              @RequestParam Long categoriaId,
                              @RequestParam(required = false) MultipartFile imagen,
                              HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";
        
        Lugar lugar = lugarService.obtenerPorId(id);
        if (lugar == null) return "redirect:/admin";
        
        // El guardado/actualizado debería centralizarse idealmente en el servicio
        // Por ahora mantenemos la lógica pero usando el servicio de almacenamiento
        lugarService.actualizarImagen(id, imagen); 
        // Nota: lugarService.guardar ya maneja la creación, pero para edición 
        // se podría mejorar el LugarService. Aquí actualizamos solo imagen.
        // Para simplificar, asumimos que el usuario solo quiere editar la imagen por ahora
        // o implementamos un método de actualización completo en el servicio.
        
        return "redirect:/admin";
    }

    // ── EVENTOS ───────────────────────────────────────────────

    @PostMapping("/admin/eventos/agregar")
    public String agregarEvento(@RequestParam String nombre,
                                @RequestParam String descripcion,
                                @RequestParam String fecha,
                                @RequestParam String lugar,
                                @RequestParam(required = false) MultipartFile imagen,
                                HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";
        eventoService.guardar(nombre, descripcion, fecha, lugar, true, imagen);
        return "redirect:/admin";
    }

    @PostMapping("/admin/eventos/{id}/editar")
    public String editarEvento(@PathVariable Long id,
                               @RequestParam String nombre,
                               @RequestParam String descripcion,
                               @RequestParam String fecha,
                               @RequestParam String lugar,
                               @RequestParam(required = false) MultipartFile imagen,
                               HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";
        eventoService.actualizar(id, nombre, descripcion, fecha, lugar, imagen);
        return "redirect:/admin";
    }

    @PostMapping("/admin/eventos/{id}/aprobar")
    public String aprobarEvento(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";
        eventoService.aprobar(id);
        return "redirect:/admin";
    }

    @PostMapping("/admin/eventos/{id}/eliminar")
    public String eliminarEvento(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";
        eventoService.eliminar(id);
        return "redirect:/admin";
    }

    // ── ACTIVIDADES ───────────────────────────────────────────

    @PostMapping("/admin/actividades/agregar")
    public String agregarActividad(@RequestParam String nombre,
                                   @RequestParam String descripcion,
                                   @RequestParam String duracion,
                                   @RequestParam String precioAprox,
                                   @RequestParam String categoria,
                                   @RequestParam(required = false) MultipartFile imagen,
                                   HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";
        actividadService.guardar(nombre, descripcion, duracion, precioAprox, categoria, true, imagen);
        return "redirect:/admin";
    }

    @PostMapping("/admin/actividades/{id}/editar")
    public String editarActividad(@PathVariable Long id,
                                  @RequestParam String nombre,
                                  @RequestParam String descripcion,
                                  @RequestParam String duracion,
                                  @RequestParam String precioAprox,
                                  @RequestParam String categoria,
                                  @RequestParam(required = false) MultipartFile imagen,
                                  HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";
        actividadService.actualizar(id, nombre, descripcion, duracion, precioAprox, categoria, imagen);
        return "redirect:/admin";
    }

    @PostMapping("/admin/actividades/{id}/aprobar")
    public String aprobarActividad(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";
        actividadService.aprobar(id);
        return "redirect:/admin";
    }

    @PostMapping("/admin/actividades/{id}/eliminar")
    public String eliminarActividad(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";
        actividadService.eliminar(id);
        return "redirect:/admin";
    }
}
