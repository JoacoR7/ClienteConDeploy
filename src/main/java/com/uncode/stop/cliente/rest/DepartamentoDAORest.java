package com.uncode.stop.cliente.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.uncode.stop.cliente.dtos.DepartamentoDTO;
import com.uncode.stop.cliente.dtos.ProvinciaDTO;
import com.uncode.stop.cliente.entities.ResponseWrapper;

@Service
public class DepartamentoDAORest {
	
	@Autowired
	private RestTemplate restTemplate;

	private final String baseUri = "http://200.45.208.176:9000/departamentos";

	public void crear(DepartamentoDTO departamento) {
		String uri = baseUri;
		restTemplate.postForEntity(uri, departamento, DepartamentoDTO.class);

	}

	public void modificar(DepartamentoDTO departamento) {

		String uri = baseUri + "/" + departamento.getId();
		restTemplate.put(uri, departamento);

	}

	public void eliminar(UUID id) {

		String uri = baseUri + "/" + id;
		restTemplate.delete(uri);
	}

	public DepartamentoDTO buscar(UUID id) {

		String uri = baseUri + "/" + id;

		ResponseEntity<DepartamentoDTO> response = restTemplate.getForEntity(uri, DepartamentoDTO.class);
		DepartamentoDTO departamento = response.getBody();

		return departamento;
	}

	public List<DepartamentoDTO> listar() {
		String uri = baseUri;

		// Deserializa la respuesta en el formato esperado (ResponseWrapper<PaisDTO>)
		ResponseEntity<ResponseWrapper<DepartamentoDTO>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<ResponseWrapper<DepartamentoDTO>>() {
				});

		// Obtén la lista de países desde el "content" del ResponseWrapper
		List<DepartamentoDTO> listaDepartamentos = response.getBody().getContent();
		return listaDepartamentos;
	}
	
	public List<DepartamentoDTO> listarDepartamentosPorProvincia(UUID provinciaId) {
	    String uri = "http://200.45.208.176:9000/departamentos/listarDepartamentosPorProvincia/" + provinciaId;

	    ResponseEntity<List<DepartamentoDTO>> response = restTemplate.exchange(
	            uri, 
	            HttpMethod.GET, 
	            null, 
	            new ParameterizedTypeReference<List<DepartamentoDTO>>() {}
	    );

	    return response.getBody();
	}

}
