package pe.edu.cibertec.cl1_renzo_frontend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import pe.edu.cibertec.cl1_renzo_frontend.dto.BuscarRequestDTO;
import pe.edu.cibertec.cl1_renzo_frontend.dto.BuscarResponseDTO;
import pe.edu.cibertec.cl1_renzo_frontend.viewmodel.BuscarModel;

@Controller
@RequestMapping("/buscar")
public class BuscarController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/inicio")
    public String inicio(Model model){
        BuscarModel buscarModel = new BuscarModel("0", "", "", "", "", "", "");
        model.addAttribute("buscarModel", buscarModel);
        return "inicio";
    }

    @PostMapping("/verificar")
    public String verificar(@RequestParam("placa") String placa, Model model){;

        //Validamos el campo
        if (placa == null || placa.trim().isEmpty() || !placa.matches("^[a-zA-Z0-9-]{8}$")) {
            BuscarModel buscarModel = new BuscarModel("1", "Debe ingresar una placa correcta", "", "", "", "", "");
            model.addAttribute("buscarModel", buscarModel);
            return "inicio";
        }

        try{

            //Invocar API de validación de usuario
            String endpoint = "http://localhost:8081/verificacion/buscar";
            BuscarRequestDTO buscarRequestDTO = new BuscarRequestDTO(placa);
            BuscarResponseDTO buscarResponseDTO = restTemplate.postForObject(endpoint, buscarRequestDTO, BuscarResponseDTO.class);

            //Validar respuesta
            if(buscarResponseDTO.codigo().equals("0")){
                BuscarModel buscarModel = new BuscarModel("0", "", buscarResponseDTO.marca(), buscarResponseDTO.modelo(), buscarResponseDTO.asientos(), buscarResponseDTO.precio(), buscarResponseDTO.color());
                model.addAttribute("buscarModel", buscarModel);
                return "info";
            } else {
                BuscarModel buscarModel = new BuscarModel("2", "No se encontró un vehículo para la placa ingresada", "", "", "", "", "");
                model.addAttribute("buscarModel", buscarModel);
                return "inicio";
            }

        } catch (Exception e){
            BuscarModel buscarModel = new BuscarModel("9", "Ocurrió un problema en la autenticación", "", "", "", "", "");
            model.addAttribute("buscarModel", buscarModel);
            System.out.println(e.getMessage());
            return "inicio";
        }
    }
}
