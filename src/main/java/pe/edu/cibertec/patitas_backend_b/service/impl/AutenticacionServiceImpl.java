package pe.edu.cibertec.patitas_backend_b.service.impl;

import pe.edu.cibertec.patitas_backend_b.dto.LoginRequestDTO;
import pe.edu.cibertec.patitas_backend_b.dto.LogoutRequestDTO;
import pe.edu.cibertec.patitas_backend_b.service.AutenticacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

@Service
public class AutenticacionServiceImpl implements AutenticacionService {

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public String[] validarUsuario(LoginRequestDTO loginRequestDTO) throws IOException {

        String[] datosUsuario = null;
        Resource resource = resourceLoader.getResource("classpath:usuarios.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(resource.getFile()))) {

            String linea;
            while ((linea = br.readLine()) != null) {

                String[] datos = linea.split(";");
                if (loginRequestDTO.tipoDocumento().equals(datos[0]) &&
                    loginRequestDTO.numeroDocumento().equals(datos[1]) &&
                    loginRequestDTO.password().equals(datos[2])) {

                    datosUsuario = new String[2];
                    datosUsuario[0] = datos[3]; // Recuperar nombre
                    datosUsuario[1] = datos[4]; // Recuperar email

                }

            }

        } catch (IOException e) {
            datosUsuario = null;
            throw new IOException(e);
        }

        return datosUsuario;
    }

    @Override
    public Date cerrarSesionUsuario(LogoutRequestDTO logoutRequestDTO) throws IOException {
        Date fechaLogout = new Date(); // Obtener la fecha actual

        // Ruta del archivo auditoria.txt
        String filePath = "src/main/resources/auditoria.txt";

        // Preparar la línea a escribir
        String registro = String.format("%s;%s;%s%n",
                logoutRequestDTO.tipoDocumento(),
                logoutRequestDTO.numeroDocumento(),
                fechaLogout.toString());

        // Escribir en el archivo auditoria.txt
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.write(registro); // Escribir la línea en el archivo
            System.out.println("Registro de auditoría: " + registro); // Mensaje de depuración
        } catch (IOException e) {
            System.out.println("Error al registrar en auditoría: " + e.getMessage());
            throw new IOException("Error al registrar en auditoría", e);
        }

        return fechaLogout; // Retornar la fecha de cierre de sesión
    }



}