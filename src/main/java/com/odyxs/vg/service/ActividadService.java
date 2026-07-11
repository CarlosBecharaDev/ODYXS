package com.odyxs.vg.service;

import com.odyxs.vg.entity.Actividad;
import com.odyxs.vg.repository.ActividadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ActividadService {

    @Autowired private ActividadRepository actividadRepository;
    @Autowired private StorageService storageService;

    public List<Actividad> obtenerTodas() {
        return actividadRepository.findAll();
    }

    public List<Actividad> obtenerAprobadas() {
        return actividadRepository.findByEstado(Actividad.Estado.APROBADO);
    }

    public List<Actividad> obtenerPendientes() {
        return actividadRepository.findByEstado(Actividad.Estado.PENDIENTE);
    }

    public List<Actividad> obtenerPorCategoria(Actividad.CategoriaActividad cat) {
        return actividadRepository.findByCategoriaAndEstado(cat, Actividad.Estado.APROBADO);
    }

    public Actividad obtenerPorId(Long id) {
        return actividadRepository.findById(id).orElse(null);
    }

    public String aprobar(Long id) {
        Actividad a = actividadRepository.findById(id).orElse(null);
        if (a == null) return "Actividad no encontrada.";
        a.setEstado(Actividad.Estado.APROBADO);
        actividadRepository.save(a);
        return "Actividad aprobada.";
    }

    public String rechazar(Long id) {
        Actividad a = actividadRepository.findById(id).orElse(null);
        if (a == null) return "Actividad no encontrada.";
        a.setEstado(Actividad.Estado.RECHAZADO);
        actividadRepository.save(a);
        return "Actividad rechazada.";
    }

    public String eliminar(Long id) {
        Actividad a = actividadRepository.findById(id).orElse(null);
        if (a == null) return "Actividad no encontrada.";
        storageService.borrarImagen(a.getImagenUrl());
        actividadRepository.deleteById(id);
        return "Actividad eliminada.";
    }

    public String guardar(String nombre, String descripcion, String duracion,
                          String precioAprox, String categoriaStr,
                          boolean esOficial, MultipartFile imagen) {
        try {
            Actividad.CategoriaActividad cat =
                Actividad.CategoriaActividad.valueOf(categoriaStr.toUpperCase());

            String imagenGuardada = null;
            if (imagen != null && !imagen.isEmpty()) {
                imagenGuardada = storageService.guardarImagen(imagen, "actividades");
                if (imagenGuardada == null) return "Error al guardar la imagen.";
            }

            Actividad a = new Actividad();
            a.setNombre(nombre);
            a.setDescripcion(descripcion);
            a.setDuracion(duracion);
            a.setPrecioAprox(precioAprox);
            a.setCategoria(cat);
            a.setEsOficial(esOficial);
            a.setEstado(esOficial ? Actividad.Estado.APROBADO : Actividad.Estado.PENDIENTE);
            a.setImagenUrl(imagenGuardada);
            actividadRepository.save(a);
            return "Actividad guardada.";
        } catch (IllegalArgumentException e) {
            return "Categoría inválida.";
        }
    }

    public String actualizar(Long id, String nombre, String descripcion, String duracion,
                             String precioAprox, String categoriaStr, MultipartFile imagen) {
        Actividad a = actividadRepository.findById(id).orElse(null);
        if (a == null) return "Actividad no encontrada.";
        try {
            a.setNombre(nombre);
            a.setDescripcion(descripcion);
            a.setDuracion(duracion);
            a.setPrecioAprox(precioAprox);
            a.setCategoria(Actividad.CategoriaActividad.valueOf(categoriaStr.toUpperCase()));
            if (imagen != null && !imagen.isEmpty()) {
                String url = storageService.guardarImagen(imagen, "actividades");
                if (url != null) {
                    storageService.borrarImagen(a.getImagenUrl()); // borrar imagen vieja solo si es local
                    a.setImagenUrl(url);
                }
            }
            actividadRepository.save(a);
            return "Actividad actualizada.";
        } catch (IllegalArgumentException e) {
            return "Categoría inválida.";
        }
    }
}
