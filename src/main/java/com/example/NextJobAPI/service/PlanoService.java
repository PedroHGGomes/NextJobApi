package com.example.NextJobAPI.service;

import com.example.NextJobAPI.dto.PlanoRequestDTO;
import com.example.NextJobAPI.dto.PlanoResponseDTO;
import com.example.NextJobAPI.exception.BusinessException;
import com.example.NextJobAPI.exception.ResourceNotFoundException;
import com.example.NextJobAPI.model.Plano;
import com.example.NextJobAPI.model.Usuario;
import com.example.NextJobAPI.repository.PlanoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanoService {
    
    private final PlanoRepository planoRepository;
    private final UsuarioService usuarioService;
    private final GroqAIService groqAIService;
      @Transactional
    @CacheEvict(value = "planos", allEntries = true)
    public PlanoResponseDTO criarPlano(PlanoRequestDTO request, String email) {
        log.info("Criando plano para usuário: {}", email);
        
        Usuario usuario = usuarioService.buscarOuCriarUsuario(email, email);
        
        Plano plano = new Plano();
        plano.setTitulo(request.getTitulo());
        plano.setDescricao(request.getDescricao());
        plano.setCategoria(request.getCategoria());
        plano.setPrioridade(request.getPrioridade());
        plano.setStatus("PROCESSANDO"); // Mudou para PROCESSANDO
        plano.setUsuario(usuario);
        
        Plano salvo = planoRepository.save(plano);
        log.info("Plano criado com ID: {}", salvo.getId());
        
        // Gera o conteúdo com IA de forma síncrona
        try {
            log.info("Gerando conteúdo com Groq AI...");
            log.info("Título: {}", request.getTitulo());
            log.info("Descrição (primeiros 100 chars): {}", request.getDescricao().substring(0, Math.min(100, request.getDescricao().length())));
            
            String conteudoIA = groqAIService.gerarPlanoCarreira(request.getTitulo(), request.getDescricao());
            
            if (conteudoIA == null || conteudoIA.isEmpty()) {
                throw new BusinessException("IA retornou conteúdo vazio");
            }
            
            salvo.setConteudoGerado(conteudoIA);
            salvo.setStatus("CONCLUIDO");
            salvo = planoRepository.save(salvo);
            
            log.info("Conteúdo gerado com sucesso pela IA - {} caracteres", conteudoIA.length());
        } catch (BusinessException e) {
            log.error("Erro de negócio ao gerar conteúdo com IA: {}", e.getMessage(), e);
            salvo.setStatus("ERRO");
            salvo.setConteudoGerado("Erro ao gerar plano com IA: " + e.getMessage() + "\n\nVerifique se a chave da API Groq está configurada corretamente.");
            salvo = planoRepository.save(salvo);
        } catch (Exception e) {
            log.error("Erro inesperado ao gerar conteúdo com IA: {}", e.getMessage(), e);
            salvo.setStatus("ERRO");
            salvo.setConteudoGerado("Erro inesperado ao gerar plano com IA: " + e.getMessage());
            salvo = planoRepository.save(salvo);
        }
        
        return toResponseDTO(salvo);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "planos", key = "#email")
    public List<PlanoResponseDTO> listarPlanos(String email) {
        log.info("Listando planos do usuário: {}", email);
        
        Usuario usuario = usuarioService.buscarOuCriarUsuario(email, email);
        
        return planoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "planos", key = "#id + '-' + #email")
    public PlanoResponseDTO buscarPorId(Long id, String email) {
        log.info("Buscando plano {} do usuário: {}", id, email);
        
        Usuario usuario = usuarioService.buscarOuCriarUsuario(email, email);
        
        Plano plano = planoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado com ID: " + id));
        
        return toResponseDTO(plano);
    }
    
    @Transactional(readOnly = true)
    public List<PlanoResponseDTO> listarPorStatus(String email, String status) {
        log.info("Listando planos do usuário {} com status: {}", email, status);
        
        Usuario usuario = usuarioService.buscarOuCriarUsuario(email, email);
        
        return planoRepository.findByUsuarioIdAndStatus(usuario.getId(), status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    @CacheEvict(value = "planos", allEntries = true)
    public PlanoResponseDTO atualizar(Long id, PlanoRequestDTO request, String email) {
        log.info("Atualizando plano {} do usuário: {}", id, email);
        
        Usuario usuario = usuarioService.buscarOuCriarUsuario(email, email);
        
        Plano plano = planoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado com ID: " + id));
        
        plano.setTitulo(request.getTitulo());
        plano.setDescricao(request.getDescricao());
        plano.setCategoria(request.getCategoria());
        plano.setPrioridade(request.getPrioridade());
        
        if (request.getStatus() != null) {
            plano.setStatus(request.getStatus());
        }
        
        Plano atualizado = planoRepository.save(plano);
        log.info("Plano {} atualizado com sucesso", id);
        
        return toResponseDTO(atualizado);
    }
    
    @Transactional
    @CacheEvict(value = "planos", allEntries = true)
    public void deletar(Long id, String email) {
        log.info("Deletando plano {} do usuário: {}", id, email);
        
        Usuario usuario = usuarioService.buscarOuCriarUsuario(email, email);
        
        Plano plano = planoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado com ID: " + id));
        
        planoRepository.delete(plano);
        log.info("Plano {} deletado com sucesso", id);
    }
    
    @Transactional
    @CacheEvict(value = "planos", allEntries = true)
    public void atualizarConteudoGerado(Long id, String conteudo) {
        log.info("Atualizando conteúdo gerado do plano: {}", id);
        
        Plano plano = planoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado com ID: " + id));
        
        plano.setConteudoGerado(conteudo);
        plano.setStatus("CONCLUIDO");
        
        planoRepository.save(plano);
        log.info("Conteúdo do plano {} atualizado com sucesso", id);
    }
    
    private PlanoResponseDTO toResponseDTO(Plano plano) {
        return new PlanoResponseDTO(
                plano.getId(),
                plano.getTitulo(),
                plano.getDescricao(),
                plano.getCategoria(),
                plano.getPrioridade(),
                plano.getStatus(),
                plano.getConteudoGerado(),
                plano.getUsuario().getEmail(),
                plano.getCriadoEm(),
                plano.getAtualizadoEm()
        );
    }
}
