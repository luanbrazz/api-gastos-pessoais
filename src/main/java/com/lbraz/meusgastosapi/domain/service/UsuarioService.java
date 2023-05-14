package com.lbraz.meusgastosapi.domain.service;

import com.lbraz.meusgastosapi.domain.exception.ResourceBadRequestException;
import com.lbraz.meusgastosapi.domain.exception.ResourceNotFoundException;
import com.lbraz.meusgastosapi.domain.model.Usuario;
import com.lbraz.meusgastosapi.domain.repository.UsuarioRepository;
import com.lbraz.meusgastosapi.dto.usuario.UsuarioRequestDto;
import com.lbraz.meusgastosapi.dto.usuario.UsuarioResponseDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements ICRUDService<UsuarioRequestDto, UsuarioResponseDto> {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public List<UsuarioResponseDto> obterTodos() {

        List<Usuario> usuarios = usuarioRepository.findAll();

        List<UsuarioResponseDto> usuariosDto = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            UsuarioResponseDto dto = mapper.map(usuario, UsuarioResponseDto.class);
            usuariosDto.add(dto);
        }
        return usuariosDto;

        /*return usuarios.stream()
                .map(usuario -> mapper.map(usuario, UsuarioResponseDto.class))
                .collect(Collectors.toList());*/
    }

    @Override
    public UsuarioResponseDto obterPorId(Long id) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isEmpty()) {
            // lança uma exceção
            throw new ResourceNotFoundException("Não foi possível encontrar o usuário com o id: " + id);
        }
        return mapper.map(optUsuario.get(), UsuarioResponseDto.class);

    }

    public UsuarioResponseDto obterPorEmail(String email) {
        Optional<Usuario> optUsuario = usuarioRepository.findByEmail(email);

        if (optUsuario.isEmpty()) {
            // lança uma exceção
            throw new ResourceNotFoundException("Não foi possível encontrar o usuário com o e-mail: " + email);
        }
        return mapper.map(optUsuario.get(), UsuarioResponseDto.class);

    }

    @Override
    public UsuarioResponseDto cadastrar(UsuarioRequestDto dto) {

        validarUsuario(dto);

        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(dto.getEmail());

        if (optionalUsuario.isPresent()) {
            throw new ResourceBadRequestException("Já existe um usuário cadastrado com o email: " + dto.getEmail());
        }

        Usuario usuario = mapper.map(dto, Usuario.class);
        usuario.setId(null);
        usuario.setDataCadastro(new Date());
        // encoder em senha

        usuario = usuarioRepository.save(usuario);

        return mapper.map(usuario, UsuarioResponseDto.class);
    }

    @Override
    public UsuarioResponseDto atualizar(Long id, UsuarioRequestDto dto) {

        UsuarioResponseDto usuarioBanco = obterPorId(id);

        validarUsuario(dto);
        Usuario usuario = mapper.map(dto, Usuario.class);
        // encoder em senha

        usuario.setId(id);
        usuario.setDataInativacao(usuarioBanco.getDataInativacao());
        usuario.setDataCadastro(usuarioBanco.getDataCadastro());
        usuario = usuarioRepository.save(usuario);

        return mapper.map(usuario, UsuarioResponseDto.class);
    }

    @Override
    public void deletar(Long id) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isEmpty()) {
            // lança uma exceção
            throw new ResourceNotFoundException("Não foi possível encontrar o usuário com o id: " + id);
        }

        Usuario usuario = optUsuario.get();

        usuario.setDataInativacao(new Date());
        System.out.println("Data da inativação: " + usuario.getDataInativacao());

        usuarioRepository.save(usuario);

        /*usuarioRepository.deleteById(id);*/
    }

    public void validarUsuario(UsuarioRequestDto dto) {
        if (dto.getEmail() == null || dto.getSenha() == null) {
            throw new ResourceBadRequestException("E-mail e senha são obrigatorios");
        }
    }
}
