package br.edu.fatecpg.backend_pulseira.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.edu.fatecpg.backend_pulseira.model.Endereco;
import br.edu.fatecpg.backend_pulseira.model.InformacoesMedicas;
import br.edu.fatecpg.backend_pulseira.model.PessoaMonitorada;
import br.edu.fatecpg.backend_pulseira.repository.PessoaMonitoradaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessoaMonitoradaService {

    // Sem 0/O/1/I para o código ser fácil de ler e ditar.
    private static final String ALFABETO_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int TAMANHO_CODIGO = 4;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private final PessoaMonitoradaRepository pessoaRepository;

    public PessoaMonitorada cadastrar(PessoaMonitorada pessoa) {
        if (pessoa.getNome() == null || pessoa.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da pessoa monitorada é obrigatório.");
        }
        if (pessoa.getDataNascimento() == null || pessoa.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Informe uma data de nascimento válida.");
        }
        pessoa.setAtiva(true);
        pessoa.setCriadoEm(Instant.now());
        // Sempre gerado aqui: o cliente não escolhe o próprio código de vínculo.
        pessoa.setCodigoVinculo(gerarCodigoUnico());
        return pessoaRepository.save(pessoa);
    }

    public PessoaMonitorada buscarAtiva(String pessoaId) {
        PessoaMonitorada pessoa = pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa monitorada não encontrada."));
        if (!Boolean.TRUE.equals(pessoa.getAtiva())) {
            throw new IllegalStateException("A pessoa monitorada está inativa.");
        }
        return pessoa;
    }

    public PessoaMonitorada buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Informe o código do paciente.");
        }
        String normalizado = codigo.trim().toUpperCase(Locale.ROOT);
        PessoaMonitorada pessoa = pessoaRepository.findByCodigoVinculo(normalizado)
                .orElseThrow(() -> new IllegalArgumentException("Código não encontrado. Confira e tente de novo."));
        if (!Boolean.TRUE.equals(pessoa.getAtiva())) {
            throw new IllegalStateException("A pessoa monitorada está inativa.");
        }
        return pessoa;
    }

    public PessoaMonitorada atualizarInformacoesMedicas(String pessoaId, InformacoesMedicas informacoes) {
        if (informacoes == null) {
            throw new IllegalArgumentException("Informe os dados de saúde.");
        }
        PessoaMonitorada pessoa = buscarAtiva(pessoaId);
        pessoa.setInformacoesMedicas(informacoes);
        return pessoaRepository.save(pessoa);
    }

    /** O endereço é opcional: com tudo em branco, o que estava guardado é removido. */
    public PessoaMonitorada atualizarEndereco(String pessoaId, Endereco informado) {
        PessoaMonitorada pessoa = buscarAtiva(pessoaId);
        pessoa.setEndereco(normalizarEndereco(informado));
        return pessoaRepository.save(pessoa);
    }

    private Endereco normalizarEndereco(Endereco informado) {
        if (informado == null) {
            return null;
        }
        Endereco endereco = new Endereco();
        endereco.setLogradouro(limpar(informado.getLogradouro(), 120, "A rua"));
        endereco.setNumero(limpar(informado.getNumero(), 10, "O número"));
        endereco.setComplemento(limpar(informado.getComplemento(), 60, "O complemento"));
        endereco.setBairro(limpar(informado.getBairro(), 60, "O bairro"));
        endereco.setCidade(limpar(informado.getCidade(), 60, "A cidade"));

        String estado = limpar(informado.getEstado(), 2, "O estado");
        if (estado != null && !estado.matches("[A-Za-z]{2}")) {
            throw new IllegalArgumentException("O estado deve ter 2 letras (ex.: SP).");
        }
        endereco.setEstado(estado == null ? null : estado.toUpperCase(Locale.ROOT));

        String cep = limpar(informado.getCep(), 9, "O CEP");
        if (cep != null) {
            cep = cep.replaceAll("\\D", "");
            if (cep.length() != 8) {
                throw new IllegalArgumentException("O CEP deve ter 8 números.");
            }
        }
        endereco.setCep(cep);

        boolean vazio = endereco.getLogradouro() == null && endereco.getNumero() == null
                && endereco.getComplemento() == null && endereco.getBairro() == null
                && endereco.getCidade() == null && endereco.getEstado() == null && endereco.getCep() == null;
        return vazio ? null : endereco;
    }

    private String limpar(String valor, int maximo, String campo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String limpo = valor.trim();
        if (limpo.length() > maximo) {
            throw new IllegalArgumentException(campo + " pode ter no máximo " + maximo + " caracteres.");
        }
        return limpo;
    }

    public Optional<PessoaMonitorada> buscarQualquer(String pessoaId) {
        return pessoaRepository.findById(pessoaId);
    }

    public void definirAtiva(String pessoaId, boolean ativa) {
        pessoaRepository.findById(pessoaId).ifPresent(pessoa -> {
            pessoa.setAtiva(ativa);
            pessoaRepository.save(pessoa);
        });
    }

    public void atualizarTelefone(String pessoaId, String telefone) {
        pessoaRepository.findById(pessoaId).ifPresent(pessoa -> {
            pessoa.setTelefone(telefone);
            pessoaRepository.save(pessoa);
        });
    }

    private String gerarCodigoUnico() {
        String codigo;
        do {
            StringBuilder sufixo = new StringBuilder();
            for (int i = 0; i < TAMANHO_CODIGO; i++) {
                sufixo.append(ALFABETO_CODIGO.charAt(ALEATORIO.nextInt(ALFABETO_CODIGO.length())));
            }
            codigo = "ZELO-" + sufixo;
        } while (pessoaRepository.existsByCodigoVinculo(codigo));
        return codigo;
    }
}
