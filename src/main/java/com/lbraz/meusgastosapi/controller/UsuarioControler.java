package com.lbraz.meusgastosapi.controller;

import com.lbraz.meusgastosapi.domain.service.UsuarioService;
import com.lbraz.meusgastosapi.dto.usuario.UsuarioRequestDto;
import com.lbraz.meusgastosapi.dto.usuario.UsuarioResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*") // A anotação @CrossOrigin("*") serve para habilitar a política de compartilhamento de recursos entre
                    // origens (CORS) em um controlador REST do Spring Framework.
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControler {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> obterTodos(){
        return ResponseEntity.ok(usuarioService.obterTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> obterPorId(@PathVariable Long id){
        return ResponseEntity.ok(usuarioService.obterPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDto> cadastrar(@RequestBody UsuarioRequestDto dto){
        UsuarioResponseDto usuario = usuarioService.cadastrar(dto);
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> atualizar(@PathVariable Long id, @RequestBody UsuarioRequestDto dto){
        UsuarioResponseDto usuario = usuarioService.atualizar(id, dto);
//        return new ResponseEntity<>(usuario, HttpStatus.OK);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id){
        usuarioService.deletar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
