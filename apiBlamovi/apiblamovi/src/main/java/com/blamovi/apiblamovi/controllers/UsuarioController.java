package com.blamovi.apiblamovi.controllers;

import com.blamovi.apiblamovi.dtos.UsuarioDto;
import com.blamovi.apiblamovi.models.UsuarioModel;
import com.blamovi.apiblamovi.repositories.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.blamovi.apiblamovi.services.FileUploadService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/usuarios", produces = {"application/json"})
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    FileUploadService fileUploadService;

    @GetMapping
    public ResponseEntity<List<UsuarioModel>> listarUsuarios(){
        return ResponseEntity.status(HttpStatus.OK).body(usuarioRepository.findAll());
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<Object> buscarUsuario(@PathVariable(value = "idUsuario")Integer id){
        Optional<UsuarioModel> usuarioBuscado = usuarioRepository.findById(id);

        if (usuarioBuscado.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(usuarioBuscado.get());
    }

    /*
    REQUISIÇÃO SEM IMAGEM
    @PostMapping
    public ResponseEntity<Object> criarUsuario(@RequestBody @Valid UsuarioDto usuarioDto){
        if (usuarioRepository.findByEmail(usuarioDto.email()) != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email já cadastrado!");
        }
        UsuarioModel novoUsuario = new UsuarioModel();
        BeanUtils.copyProperties(usuarioDto, novoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRepository.save(novoUsuario));
    } */

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Método para cadastrar um usuário", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cadastro foi efetuado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos!")
    })
    public ResponseEntity<Object> criarUsuario(@ModelAttribute @Valid UsuarioDto usuarioDto){
        if (usuarioRepository.findByEmail(usuarioDto.email()) != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email já cadastrado!");
        }

        UsuarioModel novoUsuario = new UsuarioModel();
        BeanUtils.copyProperties(usuarioDto, novoUsuario);

        String urlImagem;

        try{
            urlImagem = fileUploadService.fazerUpload(usuarioDto.foto());
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        novoUsuario.setFoto(urlImagem);

        //Criptografa senha
        String senhaCript = new BCryptPasswordEncoder().encode(usuarioDto.senha());
        novoUsuario.setSenha(senhaCript);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRepository.save(novoUsuario));
    }


    /*
    REQUISIÇÃO SEM IMAGEM
    @PutMapping("/{idUsuario}")
    public ResponseEntity<Object> editarUsuario(@PathVariable(value = "idUsuario") Integer id, @RequestBody @Valid UsuarioDto usuarioDto){
        Optional<UsuarioModel> usuarioBuscado = usuarioRepository.findById(id);

        if (usuarioBuscado.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado!");
        }
        UsuarioModel usuarioBd = usuarioBuscado.get();
        BeanUtils.copyProperties(usuarioDto, usuarioBd);

        return ResponseEntity.status(HttpStatus.OK).body(usuarioRepository.save(usuarioBd));
    }*/

    @PutMapping(value = "/{idUsuario}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> editarUsuario(@PathVariable(value = "idUsuario") int id, @ModelAttribute @Valid UsuarioDto usuarioDto){

        Optional<UsuarioModel> usuarioBuscado = usuarioRepository.findById(id);

        if (usuarioBuscado.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado!");
        }

        UsuarioModel usuarioBd = usuarioBuscado.get();
        BeanUtils.copyProperties(usuarioDto, usuarioBd);

        String urlImagem;

        try{
            urlImagem = fileUploadService.fazerUpload(usuarioDto.foto());
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        usuarioBd.setFoto(urlImagem);

        return ResponseEntity.status(HttpStatus.OK).body(usuarioRepository.save(usuarioBd));
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Object> deletarUsuario(@PathVariable(value = "idUsuario") Integer id){
        Optional<UsuarioModel> usuarioBuscado = usuarioRepository.findById(id);

        if (usuarioBuscado.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado!");
        }
        usuarioRepository.delete(usuarioBuscado.get());
        return ResponseEntity.status(HttpStatus.OK).body("Usuário deletado com sucesso!");
    }
}
