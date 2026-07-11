package com.odyxs.vg.service;

import com.odyxs.vg.entity.Evento;
import com.odyxs.vg.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventoService {

    @Autowired private EventoRepository eventoRepository;
    @Autowired private StorageService storageService;

    public List<Evento> obtenerAprobados(String desde) {
        if (desde != null && !desde.isBlank()) {
            return eventoRepository.findByActivoTrueAndFechaGreaterThanEqualOrderByFechaAsc(
                        LocalDate.parse(desde));
        }
        return eventoRepository.findByActivoTrueOrderByFechaAsc();
    }

    public List<Evento> obtenerTodos() {
        return eventoRepository.findAll();
    }

    public List<Evento> obtenerAprobados() {
        return eventoRepository.findByActivoTrueOrderByFechaAsc();
    }

    public List<Evento> obtenerPendientes() {
        return eventoRepository.findByActivoFalseOrderByIdDesc();
    }

    public Evento obtenerPorId(Long id) {
        return eventoRepository.findById(id).orElse(null);
    }

    public String guardar(String nombre, String descripcion, String fecha, String lugar,
                          boolean activo, MultipartFile imagen) {
        Evento e = new Evento();
        e.setNombre(nombre);
        e.setDescripcion(descripcion);
        e.setFecha(LocalDate.parse(fecha));
        e.setLugar(lugar);
        e.setActivo(activo);

        if (imagen != null && !imagen.isEmpty()) {
            String url = storageService.guardarImagen(imagen, "eventos");
            if (url != null) e.setImagenUrl(url);
        }

        eventoRepository.save(e);
        return "Evento guardado.";
    }

    public String actualizar(Long id, String nombre, String descripcion, String fecha, String lugar,
                             MultipartFile imagen) {
        Evento e = eventoRepository.findById(id).orElse(null);
        if (e == null) return "Evento no encontrado.";

        e.setNombre(nombre);
        e.setDescripcion(descripcion);
        e.setFecha(LocalDate.parse(fecha));
        e.setLugar(lugar);

        if (imagen != null && !imagen.isEmpty()) {
            String url = storageService.guardarImagen(imagen, "eventos");
            if (url != null) {
                storageService.borrarImagen(e.getImagenUrl());
                e.setImagenUrl(url);
            }
        }

        eventoRepository.save(e);
        return "Evento actualizado.";
    }

    public String aprobar(Long id) {
        Evento e = eventoRepository.findById(id).orElse(null);
        if (e == null) return "Evento no encontrado.";
        e.setActivo(true);
        eventoRepository.save(e);
        return "Evento aprobado.";
    }

    public String eliminar(Long id) {
        Evento e = eventoRepository.findById(id).orElse(null);
        if (e == null) return "Evento no encontrado.";
        storageService.borrarImagen(e.getImagenUrl());
        eventoRepository.deleteById(id);
        return "Evento eliminado.";
    }
}
