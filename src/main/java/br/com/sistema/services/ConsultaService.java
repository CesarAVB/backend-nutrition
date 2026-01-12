package br.com.sistema.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sistema.dtos.AvaliacaoFisicaDTO;
import br.com.sistema.dtos.ComparativoConsultasDTO;
import br.com.sistema.dtos.ConsultaDetalhadaDTO;
import br.com.sistema.dtos.ConsultaResumoDTO;
import br.com.sistema.dtos.DiferencasDTO;
import br.com.sistema.dtos.QuestionarioEstiloVidaDTO;
import br.com.sistema.dtos.RegistroFotograficoDTO;
import br.com.sistema.exceptions.BusinessException;
import br.com.sistema.exceptions.ResourceNotFoundException;
import br.com.sistema.models.AvaliacaoFisica;
import br.com.sistema.models.Consulta;
import br.com.sistema.models.Paciente;
import br.com.sistema.models.QuestionarioEstiloVida;
import br.com.sistema.models.RegistroFotografico;
import br.com.sistema.repositories.AvaliacaoFisicaRepository;
import br.com.sistema.repositories.ConsultaRepository;
import br.com.sistema.repositories.PacienteRepository;
import br.com.sistema.repositories.QuestionarioEstiloVidaRepository;
import br.com.sistema.repositories.RegistroFotograficoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultaService {

	private final ConsultaRepository consultaRepository;
	private final PacienteRepository pacienteRepository;
	private final AvaliacaoFisicaRepository avaliacaoFisicaRepository;
	private final QuestionarioEstiloVidaRepository questionarioRepository;
	private final RegistroFotograficoRepository registroFotograficoRepository;

	@Transactional
	public ConsultaResumoDTO criarConsulta(Long pacienteId) {
		Paciente paciente = pacienteRepository.findById(pacienteId)
				.orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));

		Consulta consulta = new Consulta();
		consulta.setPaciente(paciente);
		consulta.setDataConsulta(LocalDateTime.now());

		Consulta saved = consultaRepository.save(consulta);
		return converterParaResumoDTO(saved);
	}

	@Transactional(readOnly = true)
	public List<ConsultaResumoDTO> listarConsultasPorPaciente(Long pacienteId) {
		return consultaRepository.findByPacienteIdOrderByDataConsultaDesc(pacienteId).stream()
				.map(this::converterParaResumoDTO).toList();
	}

	@Transactional(readOnly = true)
	public ConsultaDetalhadaDTO buscarConsultaCompleta(Long consultaId) {
		Consulta consulta = consultaRepository.findById(consultaId)
				.orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));

		return converterParaDetalhadaDTO(consulta);
	}

	@Transactional(readOnly = true)
	public ComparativoConsultasDTO compararConsultas(Long pacienteId, Long consultaInicialId, Long consultaFinalId) {
		Consulta consultaInicial = consultaRepository.findById(consultaInicialId)
				.orElseThrow(() -> new ResourceNotFoundException("Consulta inicial não encontrada"));

		Consulta consultaFinal = consultaRepository.findById(consultaFinalId)
				.orElseThrow(() -> new ResourceNotFoundException("Consulta final não encontrada"));

		if (!consultaInicial.getPaciente().getId().equals(pacienteId)
				|| !consultaFinal.getPaciente().getId().equals(pacienteId)) {
			throw new BusinessException("Consultas não pertencem ao mesmo paciente");
		}

		ComparativoConsultasDTO comparativo = new ComparativoConsultasDTO();
		comparativo.setConsultaInicial(converterParaDetalhadaDTO(consultaInicial));
		comparativo.setConsultaFinal(converterParaDetalhadaDTO(consultaFinal));
		comparativo.setDiferencas(calcularDiferencas(consultaInicial.getId(), consultaFinal.getId()));

		return comparativo;
	}

	private DiferencasDTO calcularDiferencas(Long consultaInicialId, Long consultaFinalId) {
		DiferencasDTO diferencas = new DiferencasDTO();

		AvaliacaoFisica avalInicial = avaliacaoFisicaRepository.findByConsultaId(consultaInicialId).orElse(null);
		AvaliacaoFisica avalFinal = avaliacaoFisicaRepository.findByConsultaId(consultaFinalId).orElse(null);

		if (avalInicial == null || avalFinal == null) {
			return diferencas; // Retorna vazio se não houver avaliações
		}

		// Composição Corporal
		diferencas.setDiferencaPeso(calcularDiferenca(avalFinal.getPesoAtual(), avalInicial.getPesoAtual()));
		diferencas.setDiferencaPercentualGordura(
				calcularDiferenca(avalFinal.getPercentualGordura(), avalInicial.getPercentualGordura()));
		diferencas.setDiferencaMassaMagra(calcularDiferenca(avalFinal.getMassaMagra(), avalInicial.getMassaMagra()));
		diferencas.setDiferencaMassaGorda(calcularDiferenca(avalFinal.getMassaGorda(), avalInicial.getMassaGorda()));
		diferencas.setDiferencaImc(calcularDiferenca(avalFinal.getImc(), avalInicial.getImc()));

		// Perímetros
		Map<String, Double> diferencasPerimetros = new HashMap<>();
		diferencasPerimetros.put("ombro",
				calcularDiferenca(avalFinal.getPerimetroOmbro(), avalInicial.getPerimetroOmbro()));
		diferencasPerimetros.put("torax",
				calcularDiferenca(avalFinal.getPerimetroTorax(), avalInicial.getPerimetroTorax()));
		diferencasPerimetros.put("cintura",
				calcularDiferenca(avalFinal.getPerimetroCintura(), avalInicial.getPerimetroCintura()));
		diferencasPerimetros.put("abdominal",
				calcularDiferenca(avalFinal.getPerimetroAbdominal(), avalInicial.getPerimetroAbdominal()));
		diferencasPerimetros.put("quadril",
				calcularDiferenca(avalFinal.getPerimetroQuadril(), avalInicial.getPerimetroQuadril()));
		diferencasPerimetros.put("bracoDireitoRelax", calcularDiferenca(avalFinal.getPerimetroBracoDireitoRelax(),
				avalInicial.getPerimetroBracoDireitoRelax()));
		diferencasPerimetros.put("bracoDireitoContr", calcularDiferenca(avalFinal.getPerimetroBracoDireitoContr(),
				avalInicial.getPerimetroBracoDireitoContr()));
		diferencasPerimetros.put("bracoEsquerdoRelax", calcularDiferenca(avalFinal.getPerimetroBracoEsquerdoRelax(),
				avalInicial.getPerimetroBracoEsquerdoRelax()));
		diferencasPerimetros.put("bracoEsquerdoContr", calcularDiferenca(avalFinal.getPerimetroBracoEsquerdoContr(),
				avalInicial.getPerimetroBracoEsquerdoContr()));
		diferencasPerimetros.put("antebracoDireito", calcularDiferenca(avalFinal.getPerimetroAntebracoDireito(),
				avalInicial.getPerimetroAntebracoDireito()));
		diferencasPerimetros.put("antebracoEsquerdo", calcularDiferenca(avalFinal.getPerimetroAntebracoEsquerdo(),
				avalInicial.getPerimetroAntebracoEsquerdo()));
		diferencasPerimetros.put("coxa",
				calcularDiferenca(avalFinal.getPerimetroCoxa(), avalInicial.getPerimetroCoxa()));
		diferencasPerimetros.put("coxaDireita",
				calcularDiferenca(avalFinal.getPerimetroCoxaDireita(), avalInicial.getPerimetroCoxaDireita()));
		diferencasPerimetros.put("coxaEsquerda",
				calcularDiferenca(avalFinal.getPerimetroCoxaEsquerda(), avalInicial.getPerimetroCoxaEsquerda()));
		diferencasPerimetros.put("panturrilhaDireita", calcularDiferenca(avalFinal.getPerimetroPanturrilhaDireita(),
				avalInicial.getPerimetroPanturrilhaDireita()));
		diferencasPerimetros.put("panturrilhaEsquerda", calcularDiferenca(avalFinal.getPerimetroPanturrilhaEsquerda(),
				avalInicial.getPerimetroPanturrilhaEsquerda()));
		diferencas.setDiferencasPerimetros(diferencasPerimetros);

		// Dobras Cutâneas
		Map<String, Double> diferencasDobras = new HashMap<>();
		diferencasDobras.put("triceps", calcularDiferenca(avalFinal.getDobraTriceps(), avalInicial.getDobraTriceps()));
		diferencasDobras.put("peito", calcularDiferenca(avalFinal.getDobraPeito(), avalInicial.getDobraPeito()));
		diferencasDobras.put("axilarMedia",
				calcularDiferenca(avalFinal.getDobraAxilarMedia(), avalInicial.getDobraAxilarMedia()));
		diferencasDobras.put("subescapular",
				calcularDiferenca(avalFinal.getDobraSubescapular(), avalInicial.getDobraSubescapular()));
		diferencasDobras.put("abdominal",
				calcularDiferenca(avalFinal.getDobraAbdominal(), avalInicial.getDobraAbdominal()));
		diferencasDobras.put("supraIliaca",
				calcularDiferenca(avalFinal.getDobraSupraIliaca(), avalInicial.getDobraSupraIliaca()));
		diferencasDobras.put("coxa", calcularDiferenca(avalFinal.getDobraCoxa(), avalInicial.getDobraCoxa()));
		diferencas.setDiferencasDobras(diferencasDobras);

		return diferencas;
	}

	private Double calcularDiferenca(Double valorFinal, Double valorInicial) {
		if (valorFinal == null || valorInicial == null) {
			return null;
		}
		return valorFinal - valorInicial;
	}

	private ConsultaResumoDTO converterParaResumoDTO(Consulta consulta) {
		ConsultaResumoDTO dto = new ConsultaResumoDTO();
		dto.setId(consulta.getId());
		dto.setPacienteId(consulta.getPaciente().getId());
		dto.setNomePaciente(consulta.getPaciente().getNomeCompleto());
		dto.setDataConsulta(consulta.getDataConsulta());

		// Buscar dados relacionados
		try {
			avaliacaoFisicaRepository.findByConsultaId(consulta.getId()).ifPresent(avaliacao -> {
				dto.setPeso(avaliacao.getPesoAtual());
				dto.setPercentualGordura(avaliacao.getPercentualGordura());
			});
			dto.setTemAvaliacaoFisica(avaliacaoFisicaRepository.existsByConsultaId(consulta.getId()));
		} catch (Exception e) {
			dto.setTemAvaliacaoFisica(false);
		}

		// ✅ FORCE NATIVE QUERY PARA QUESTIONÁRIO
		try {
			Optional<QuestionarioEstiloVida> questionarioOpt = questionarioRepository
					.findByConsultaId(consulta.getId());
			if (questionarioOpt.isPresent()) {
				dto.setObjetivo(questionarioOpt.get().getObjetivo());
				dto.setTemQuestionario(true);
			} else {
				dto.setTemQuestionario(false);
			}
		} catch (Exception e) {
			System.err.println("Erro ao buscar questionário para consulta " + consulta.getId() + ": " + e.getMessage());
			dto.setTemQuestionario(false);
		}

		try {
			dto.setTemFotos(registroFotograficoRepository.existsByConsultaId(consulta.getId()));
		} catch (Exception e) {
			dto.setTemFotos(false);
		}

		return dto;
	}

	private ConsultaDetalhadaDTO converterParaDetalhadaDTO(Consulta consulta) {
		ConsultaDetalhadaDTO dto = new ConsultaDetalhadaDTO();
		dto.setId(consulta.getId());
		dto.setPacienteId(consulta.getPaciente().getId());
		dto.setNomePaciente(consulta.getPaciente().getNomeCompleto());
		dto.setDataConsulta(consulta.getDataConsulta());

		// Buscar avaliação física
		avaliacaoFisicaRepository.findByConsultaId(consulta.getId()).ifPresent(avaliacao -> {
			dto.setAvaliacaoFisica(converterAvaliacaoParaDTO(avaliacao));
		});

		// Buscar questionário
		questionarioRepository.findByConsultaId(consulta.getId()).ifPresent(questionario -> {
			dto.setQuestionario(converterQuestionarioParaDTO(questionario));
		});

		// Buscar fotos
		registroFotograficoRepository.findByConsultaId(consulta.getId()).ifPresent(registro -> {
			dto.setRegistroFotografico(converterRegistroParaDTO(registro));
		});

		return dto;
	}

	private AvaliacaoFisicaDTO converterAvaliacaoParaDTO(AvaliacaoFisica avaliacao) {
		AvaliacaoFisicaDTO dto = new AvaliacaoFisicaDTO();
		dto.setId(avaliacao.getId());
		dto.setConsultaId(avaliacao.getConsulta().getId());
		dto.setPerimetroOmbro(avaliacao.getPerimetroOmbro());
		dto.setPerimetroTorax(avaliacao.getPerimetroTorax());
		dto.setPerimetroCintura(avaliacao.getPerimetroCintura());
		dto.setPerimetroAbdominal(avaliacao.getPerimetroAbdominal());
		dto.setPerimetroQuadril(avaliacao.getPerimetroQuadril());
		dto.setPerimetroBracoDireitoRelax(avaliacao.getPerimetroBracoDireitoRelax());
		dto.setPerimetroBracoDireitoContr(avaliacao.getPerimetroBracoDireitoContr());
		dto.setPerimetroBracoEsquerdoRelax(avaliacao.getPerimetroBracoEsquerdoRelax());
		dto.setPerimetroBracoEsquerdoContr(avaliacao.getPerimetroBracoEsquerdoContr());
		dto.setPerimetroAntebracoDireito(avaliacao.getPerimetroAntebracoDireito());
		dto.setPerimetroAntebracoEsquerdo(avaliacao.getPerimetroAntebracoEsquerdo());
		dto.setPerimetroCoxa(avaliacao.getPerimetroCoxa());
		dto.setPerimetroCoxaDireita(avaliacao.getPerimetroCoxaDireita());
		dto.setPerimetroCoxaEsquerda(avaliacao.getPerimetroCoxaEsquerda());
		dto.setPerimetroPanturrilhaDireita(avaliacao.getPerimetroPanturrilhaDireita());
		dto.setPerimetroPanturrilhaEsquerda(avaliacao.getPerimetroPanturrilhaEsquerda());
		dto.setDobraTriceps(avaliacao.getDobraTriceps());
		dto.setDobraPeito(avaliacao.getDobraPeito());
		dto.setDobraAxilarMedia(avaliacao.getDobraAxilarMedia());
		dto.setDobraSubescapular(avaliacao.getDobraSubescapular());
		dto.setDobraAbdominal(avaliacao.getDobraAbdominal());
		dto.setDobraSupraIliaca(avaliacao.getDobraSupraIliaca());
		dto.setDobraCoxa(avaliacao.getDobraCoxa());
		dto.setPesoAtual(avaliacao.getPesoAtual());
		dto.setMassaMagra(avaliacao.getMassaMagra());
		dto.setMassaGorda(avaliacao.getMassaGorda());
		dto.setPercentualGordura(avaliacao.getPercentualGordura());
		dto.setImc(avaliacao.getImc());
		return dto;
	}

	private QuestionarioEstiloVidaDTO converterQuestionarioParaDTO(QuestionarioEstiloVida questionario) {
		QuestionarioEstiloVidaDTO dto = new QuestionarioEstiloVidaDTO();
		dto.setId(questionario.getId());
		dto.setConsultaId(questionario.getConsulta().getId());
		dto.setObjetivo(questionario.getObjetivo());
		dto.setFrequenciaTreino(questionario.getFrequenciaTreino());
		dto.setTempoTreino(questionario.getTempoTreino());
		dto.setCirurgias(questionario.getCirurgias());
		dto.setDoencas(questionario.getDoencas());
		dto.setHistoricoFamiliar(questionario.getHistoricoFamiliar());
		dto.setMedicamentos(questionario.getMedicamentos());
		dto.setSuplementos(questionario.getSuplementos());
		dto.setUsoAnabolizantes(questionario.getUsoAnabolizantes());
		dto.setCicloAnabolizantes(questionario.getCicloAnabolizantes());
		dto.setDuracaoAnabolizantes(questionario.getDuracaoAnabolizantes());
		dto.setFuma(questionario.getFuma());
		dto.setFrequenciaAlcool(questionario.getFrequenciaAlcool());
		dto.setFuncionamentoIntestino(questionario.getFuncionamentoIntestino());
		dto.setQualidadeSono(questionario.getQualidadeSono());
		dto.setIngestaoAguaDiaria(questionario.getIngestaoAguaDiaria());
		dto.setAlimentosNaoGosta(questionario.getAlimentosNaoGosta());
		dto.setFrutasPreferidas(questionario.getFrutasPreferidas());
		dto.setNumeroRefeicoesDesejadas(questionario.getNumeroRefeicoesDesejadas());
		dto.setHorarioMaiorFome(questionario.getHorarioMaiorFome());
		dto.setPressaoArterial(questionario.getPressaoArterial());
		dto.setIntolerancias(questionario.getIntolerancias());
		return dto;
	}

	private RegistroFotograficoDTO converterRegistroParaDTO(RegistroFotografico registro) {
		RegistroFotograficoDTO dto = new RegistroFotograficoDTO();
		dto.setId(registro.getId());
		dto.setConsultaId(registro.getConsulta().getId());
		dto.setFotoAnterior(registro.getFotoAnterior());
		dto.setFotoPosterior(registro.getFotoPosterior());
		dto.setFotoLateralEsquerda(registro.getFotoLateralEsquerda());
		dto.setFotoLateralDireita(registro.getFotoLateralDireita());
		return dto;
	}
	
	@Transactional
	public void deletarConsulta(Long consultaId) {
	    Consulta consulta = consultaRepository.findById(consultaId)
	            .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
	    
	    // Deletar entidades relacionadas primeiro (se necessário)
	    if (avaliacaoFisicaRepository.existsByConsultaId(consultaId)) {
	        avaliacaoFisicaRepository.deleteByConsultaId(consultaId);
	    }
	    
	    if (registroFotograficoRepository.existsByConsultaId(consultaId)) {
	        registroFotograficoRepository.deleteByConsultaId(consultaId);
	    }
	    
	    // Questionário precisa de @Modifying no Repository
	    consultaRepository.delete(consulta);
	}
	
	@Transactional(readOnly = true)
	public List<ConsultaResumoDTO> listarTodasConsultas() {
	    return consultaRepository.findAllByOrderByDataConsultaDesc().stream()
	            .map(this::converterParaResumo)
	            .collect(Collectors.toList());
	}

	private ConsultaResumoDTO converterParaResumo(Consulta consulta) {
	    ConsultaResumoDTO dto = new ConsultaResumoDTO();
	    dto.setId(consulta.getId());
	    dto.setPacienteId(consulta.getPaciente().getId());
	    dto.setNomePaciente(consulta.getPaciente().getNomeCompleto());
	    dto.setDataConsulta(consulta.getDataConsulta());
	    
	    // Verificar se tem avaliação física
	    boolean temAvaliacaoFisica = avaliacaoFisicaRepository.existsByConsultaId(consulta.getId());
	    dto.setTemAvaliacaoFisica(temAvaliacaoFisica);
	    
	    // Verificar se tem questionário
	    boolean temQuestionario = questionarioRepository.existsByConsultaId(consulta.getId());
	    dto.setTemQuestionario(temQuestionario);
	    
	    // Verificar se tem fotos
	    boolean temFotos = registroFotograficoRepository.existsByConsultaId(consulta.getId());
	    dto.setTemFotos(temFotos);
	    
	    // Buscar dados resumidos da avaliação física se existir
	    if (temAvaliacaoFisica) {
	        avaliacaoFisicaRepository.findByConsultaId(consulta.getId()).ifPresent(avaliacao -> {
	            dto.setPeso(avaliacao.getPesoAtual());
	            dto.setPercentualGordura(avaliacao.getPercentualGordura());
	        });
	    }
	    
	    // Buscar objetivo do questionário se existir
	    if (temQuestionario) {
	        questionarioRepository.findByConsultaId(consulta.getId()).ifPresent(questionario -> {
	            dto.setObjetivo(questionario.getObjetivo());
	        });
	    }
	    
	    return dto;
	}
	
	@Transactional
	public ConsultaResumoDTO atualizarDataConsulta(Long consultaId, LocalDateTime novaData) {
	    Consulta consulta = consultaRepository.findById(consultaId)
	            .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
	    
	    consulta.setDataConsulta(novaData);
	    Consulta updated = consultaRepository.save(consulta);
	    
	    return converterParaResumo(updated);
	}
}