package br.com.salazar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class RestService {
    @Autowired
    private RestTemplate restTemplate;

    public void fazerChamadaRest() {
        log.debug("Iniciando chamada REST");
        // Implemente sua lógica de chamada REST aqui
    }
}
