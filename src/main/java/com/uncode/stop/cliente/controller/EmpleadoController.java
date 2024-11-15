package com.uncode.stop.cliente.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;

import com.uncode.stop.cliente.dtos.EmpleadoDTO;
import com.uncode.stop.cliente.dtos.UnidadNegocioDTO;
import com.uncode.stop.cliente.dtos.UsuarioDTO;
import com.uncode.stop.cliente.enums.Rol;
import com.uncode.stop.cliente.enums.TipoEmpleado;
import com.uncode.stop.cliente.services.EmpleadoService;
import com.uncode.stop.cliente.services.UnidadNegocioService;

@Controller
@RequestMapping("/empleado")
public class EmpleadoController {

	@Autowired
	private EmpleadoService empleadoService;

	@Autowired
	private UnidadNegocioService unidadNegocioService;

	@GetMapping("/listarEmpleados")
	public String iniciarEmpleados(ModelMap model) {
		List<EmpleadoDTO> empleados = empleadoService.listar();
		model.put("empleados", empleados);
		return "/empleado/empleado.html";
	}

	@GetMapping("/editar-empleado")
	public String editEmpleado(@RequestParam(value = "id", required = false) UUID id,
			@RequestParam(value = "accion") String accion,
			ModelMap model) {

		EmpleadoDTO empleado = null;
		List<UnidadNegocioDTO> unidades = unidadNegocioService.listar();

		if (id == null) {
			empleado = new EmpleadoDTO();
		} else {
			empleado = empleadoService.buscar(id);
		}
		model.put("empleado", empleado);
		model.put("unidades", unidades);
		model.put("tipoEmpleados", TipoEmpleado.values());
		model.put("accion", accion);
		return "/empleado/editEmpleado.html";
	}

	@PostMapping("/actualizar-empleado")
	public String actualizarLocalidad(@RequestParam(value = "id", required = false) UUID id,
			@RequestParam(value = "nombre") String nombre,
			@RequestParam(value = "apellido") String apellido,
			@RequestParam(value = "legajo") String legajo,
			@RequestParam(value = "unidadId") UUID unidadId,
			@RequestParam(value = "tipoEmpleado") TipoEmpleado tipoEmpleado,
			@RequestParam(value = "accion", defaultValue = "Crear") String accion,
			ModelMap model) {
		try {
			if (id == null) {
				empleadoService.crear(id, nombre, apellido, legajo, unidadId, tipoEmpleado);
			} else {
				empleadoService.modificar(id, nombre, apellido, legajo, unidadId, tipoEmpleado);
			}
		} catch (HttpStatusCodeException e) {
			EmpleadoDTO empleado = EmpleadoDTO.builder().nombre(nombre).apellido(apellido).legajo(legajo)
					.unidadDeNegocio(UnidadNegocioDTO.builder().id(unidadId).build()).tipoEmpleado(tipoEmpleado)
					.build();
			model.put("empleado", empleado);
			model.put("unidades", unidadNegocioService.listar());
			model.put("tipoEmpleados", TipoEmpleado.values());
			model.put("accion", accion);
			model.put("error", e.getMessage());
			return "/empleado/editEmpleado.html";
		}

		return "redirect:/empleado/listarEmpleados";
	}

	@PostMapping("/eliminar-empleado")
	public String eliminarLocalidad(@RequestParam("id") UUID id) {
		empleadoService.eliminar(id);
		return "redirect:/empleado/listarEmpleados";
	}

	@GetMapping("/editar-usuario")
	public String editUsuario(@RequestParam("id") UUID id,
			@RequestParam(value = "accion") String accion,
			ModelMap model) {

		EmpleadoDTO empleado = empleadoService.buscar(id);

		if (accion.equals("CrearUsuario")) {
			empleado.setUsuario(new UsuarioDTO());
		}

		model.put("empleado", empleado);
		model.put("accion", accion);
		model.put("roles", Rol.values());
		return "/usuario/editUsuario.html";
	}

	@PostMapping("/actualizar-usuario")
	public String editUsuario(@RequestParam(value = "id", required = false) UUID id,
			@RequestParam(value = "accion", required = false) String accion,
			@RequestParam(value = "cuenta") String cuenta,
			@RequestParam(value = "clave") String clave,
			@RequestParam(value = "confirmarClave") String confirmarClave,
			@RequestParam(value = "rol") Rol rol, ModelMap model) {
		try {

			if (accion.equals("CrearUsuario")) {
				empleadoService.crearUsuario(id, cuenta, clave, confirmarClave, rol);
			} else {
				empleadoService.modificarUsuario(id, cuenta, clave, confirmarClave, rol);
			}
		} catch (HttpStatusCodeException e) {
			EmpleadoDTO empleado = empleadoService.buscar(id);
			empleado.setUsuario(UsuarioDTO.builder().cuenta(cuenta).rol(rol).build());
			model.put("empleado", empleado);
			model.put("accion", accion);
			model.put("roles", Rol.values());
			model.put("error", e.getMessage());
			return "/usuario/editUsuario.html";
		}

		return "redirect:/empleado/listarEmpleados";
	}

}
