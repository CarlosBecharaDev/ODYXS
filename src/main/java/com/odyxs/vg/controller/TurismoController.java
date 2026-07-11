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
public class TurismoController {

    @Autowired private EventoService eventoService;

    // ── Eventos ───────────────────────────────────────────────

    @GetMapping("/eventos")
    public String eventos(
            @RequestParam(required = false) String desde,
            Model model, HttpSession session) {
        if (session.getAttribute("usuarioId") == null) return "redirect:/login";

        model.addAttribute("eventos", eventoService.obtenerAprobados(desde));
        model.addAttribute("desde", desde);
        return "eventos";
    }

    // ── Proponer evento (usuarios) ────────────────────────────

    @GetMapping("/proponer-evento")
    public String proponerEventoForm(HttpSession session) {
        if (session.getAttribute("usuarioId") == null) return "redirect:/login";
        return "proponer-evento";
    }

    @PostMapping("/proponer-evento")
    public String proponerEvento(@RequestParam String nombre,
                                 @RequestParam(required = false) String descripcion,
                                 @RequestParam String fecha,
                                 @RequestParam(required = false) String lugar,
                                 @RequestParam(required = false) MultipartFile imagen,
                                 HttpSession session) {
        if (session.getAttribute("usuarioId") == null) return "redirect:/login";

        eventoService.guardar(nombre, descripcion, fecha, lugar, false, imagen);
        return "redirect:/eventos";
    }
}
