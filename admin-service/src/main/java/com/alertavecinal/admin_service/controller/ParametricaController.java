package com.alertavecinal.admin_service.controller;

import com.alertavecinal.admin_service.entity.Parametrica;
import com.alertavecinal.admin_service.service.ParametricaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/parametricas")
public class ParametricaController {

    @Autowired
    private ParametricaService parametricaService;

    // GET: http://localhost:8083/api/admin/parametricas
    @GetMapping
    public ResponseEntity<List<Parametrica>> listarParametricas(@RequestParam(required = false) String tipo) {
        if (tipo != null && !tipo.isEmpty()) {
            return ResponseEntity.ok(parametricaService.obtenerPorTipo(tipo));
        }
        return ResponseEntity.ok(parametricaService.obtenerTodas());
    }

    // GET: http://localhost:8083/api/admin/parametricas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Parametrica> obtenerPorId(@PathVariable Long id) {
        return parametricaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: http://localhost:8083/api/admin/parametricas
    @PostMapping
    public ResponseEntity<Parametrica> crearParametrica(@RequestBody Parametrica parametrica) {
        Parametrica nuevaParametrica = parametricaService.crearParametrica(parametrica);
        return new ResponseEntity<>(nuevaParametrica, HttpStatus.CREATED);
    }

    // PUT: http://localhost:8083/api/admin/parametricas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Parametrica> actualizarParametrica(@PathVariable Long id, @RequestBody Parametrica parametrica) {
        return parametricaService.actualizarParametrica(id, parametrica)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: http://localhost:8083/api/admin/parametricas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarParametrica(@PathVariable Long id) {
        if (parametricaService.eliminarParametrica(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
